package ar.edu.iua.iw3.business;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.iua.iw3.business.exception.ConflictException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.OrderStatus;
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
}
