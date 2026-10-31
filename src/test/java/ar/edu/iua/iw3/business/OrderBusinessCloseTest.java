package ar.edu.iua.iw3.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.iua.iw3.business.exception.ConflictException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.OrderStatus;
import ar.edu.iua.iw3.model.OrderStatusLog;
import ar.edu.iua.iw3.model.persistence.OrderRepository;
import ar.edu.iua.iw3.model.persistence.OrderStatusLogRepository;

/**
 * Pruebas unitarias de las reglas del cierre (Punto 4) con Mockito: los repositorios se
 * reemplazan por dobles, asi que no hace falta la base de datos.
 */
@ExtendWith(MockitoExtension.class)
class OrderBusinessCloseTest {

	private static final Integer NUMBER = 4001;

	@Mock
	private OrderRepository orderDAO;

	@Mock
	private OrderStatusLogRepository statusLogDAO;

	@InjectMocks
	private OrderBusiness orderBusiness;

	/** Orden minima con el numero de prueba y el estado pedido. */
	private Order order(OrderStatus status) {
		Order o = new Order();
		o.setId(1L);
		o.setOrderNumber(NUMBER);
		o.setStatus(status);
		return o;
	}

	@Test
	void close_orderNotFound_throwsNotFound() {
		when(orderDAO.findByOrderNumberForUpdate(NUMBER)).thenReturn(Optional.empty());
		NotFoundException e = assertThrows(NotFoundException.class, () -> orderBusiness.close(NUMBER, "admin"));
		assertTrue(e.getMessage().contains("No se encuentra la Orden numero=" + NUMBER));
		verify(orderDAO, never()).saveAndFlush(any());
		verify(statusLogDAO, never()).save(any());
	}

	@Test
	void close_status1_throwsConflict() {
		assertConflict(OrderStatus.PENDING_INITIAL_WEIGHING, "estado 1");
	}

	@Test
	void close_status3_alreadyClosed_throwsConflict() {
		assertConflict(OrderStatus.CLOSED_FOR_LOADING, "estado 3");
	}

	@Test
	void close_status4_throwsConflict() {
		assertConflict(OrderStatus.FINISHED, "estado 4");
	}

	/** Fuera del estado 2 el cierre da 409 y no escribe nada. */
	private void assertConflict(OrderStatus status, String expectedFragment) {
		when(orderDAO.findByOrderNumberForUpdate(NUMBER)).thenReturn(Optional.of(order(status)));
		ConflictException e = assertThrows(ConflictException.class, () -> orderBusiness.close(NUMBER, "admin"));
		assertTrue(e.getMessage().contains("no se puede cerrar"));
		assertTrue(e.getMessage().contains(expectedFragment));
		verify(orderDAO, never()).saveAndFlush(any());
		verify(statusLogDAO, never()).save(any());
	}

	/** Prepara una orden en estado 2 y hace que saveAndFlush devuelva la misma orden. */
	private Order mockStatus2() {
		Order o = order(OrderStatus.INITIAL_WEIGHING_REGISTERED);
		when(orderDAO.findByOrderNumberForUpdate(NUMBER)).thenReturn(Optional.of(o));
		when(orderDAO.saveAndFlush(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
		return o;
	}

	@Test
	void close_status2_movesTo3AndLogsOneRow() throws Exception {
		mockStatus2();
		Order result = orderBusiness.close(NUMBER, "admin");

		assertEquals(OrderStatus.CLOSED_FOR_LOADING, result.getStatus());
		verify(orderDAO).saveAndFlush(result);
		// Se usa la consulta con bloqueo, nunca la comun
		verify(orderDAO, never()).findByOrderNumber(any());

		ArgumentCaptor<OrderStatusLog> captor = ArgumentCaptor.forClass(OrderStatusLog.class);
		verify(statusLogDAO, times(1)).save(captor.capture());
		OrderStatusLog row = captor.getValue();
		assertEquals(OrderStatus.INITIAL_WEIGHING_REGISTERED, row.getFromStatus());
		assertEquals(OrderStatus.CLOSED_FOR_LOADING, row.getToStatus());
		assertEquals("admin", row.getActor());
		assertEquals("Orden cerrada para carga", row.getObservations());
		assertNotNull(row.getDateTime());
		assertSame(result, row.getOrder());
	}

	@Test
	void close_nullOrBlankActor_logsSistema() throws Exception {
		mockStatus2();
		orderBusiness.close(NUMBER, null);
		mockStatus2();
		orderBusiness.close(NUMBER, "  ");

		ArgumentCaptor<OrderStatusLog> captor = ArgumentCaptor.forClass(OrderStatusLog.class);
		verify(statusLogDAO, times(2)).save(captor.capture());
		captor.getAllValues().forEach(row -> assertEquals("sistema", row.getActor()));
	}

	@Test
	void close_doesNotChangeLoadingDatesNorLastValues() throws Exception {
		Order o = mockStatus2();
		LocalDateTime start = LocalDateTime.of(2026, 10, 30, 10, 0, 0);
		LocalDateTime end = LocalDateTime.of(2026, 10, 30, 10, 30, 0);
		o.setActivationPassword(48213);
		o.setLoadingStartDateTime(start);
		o.setLoadingEndDateTime(end);
		o.setLastAccumulatedMass(1500.0);
		o.setLastDensity(0.52);
		o.setLastTemperature(18.4);
		o.setLastFlowRate(3600.0);

		Order result = orderBusiness.close(NUMBER, "admin");

		assertEquals(start, result.getLoadingStartDateTime());
		assertEquals(end, result.getLoadingEndDateTime());
		assertEquals(1500.0, result.getLastAccumulatedMass());
		assertEquals(0.52, result.getLastDensity());
		assertEquals(18.4, result.getLastTemperature());
		assertEquals(3600.0, result.getLastFlowRate());
		assertEquals(48213, result.getActivationPassword());
	}
}
