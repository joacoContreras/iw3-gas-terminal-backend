package ar.edu.iua.iw3.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

import ar.edu.iua.iw3.business.exception.BadRequestException;
import ar.edu.iua.iw3.business.exception.ConflictException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.OrderStatus;
import ar.edu.iua.iw3.model.OrderStatusLog;
import ar.edu.iua.iw3.model.persistence.OrderDetailRepository;
import ar.edu.iua.iw3.model.persistence.OrderRepository;
import ar.edu.iua.iw3.model.persistence.OrderStatusLogRepository;
import ar.edu.iua.iw3.model.persistence.ReadingAverages;

/**
 * Pruebas unitarias del pesaje final y la conciliacion (Punto 5) con Mockito: los repositorios se
 * reemplazan por dobles, asi que no hace falta la base de datos.
 */
@ExtendWith(MockitoExtension.class)
class OrderBusinessFinalWeightTest {

	private static final Integer NUMBER = 5001;
	private static final double TARE = 14500.0;

	@Mock
	private OrderRepository orderDAO;

	@Mock
	private OrderStatusLogRepository statusLogDAO;

	@Mock
	private OrderDetailRepository orderDetailDAO;

	@InjectMocks
	private OrderBusiness orderBusiness;

	/** Orden con tara 14500 en el estado pedido, devuelta por la consulta CON bloqueo (pesaje final). */
	private Order lockedOrder(OrderStatus status) {
		Order o = new Order();
		o.setId(1L);
		o.setOrderNumber(NUMBER);
		o.setStatus(status);
		o.setTare(TARE);
		when(orderDAO.findByOrderNumberForUpdate(NUMBER)).thenReturn(Optional.of(o));
		return o;
	}

	/** Un pesaje rechazado no escribe nada: ni la orden ni el historial. */
	private void verifyNothingWritten() {
		verify(orderDAO, never()).saveAndFlush(any());
		verify(statusLogDAO, never()).save(any());
	}

	@Test
	void registerFinalWeight_orderNotFound_throwsNotFound() {
		when(orderDAO.findByOrderNumberForUpdate(NUMBER)).thenReturn(Optional.empty());
		NotFoundException e = assertThrows(NotFoundException.class,
				() -> orderBusiness.registerFinalWeight(NUMBER, 16490.0, "admin"));
		assertTrue(e.getMessage().contains("No se encuentra la Orden numero=" + NUMBER));
		verifyNothingWritten();
	}

	@Test
	void registerFinalWeight_zeroNegativeNaNAndInfinite_throwBadRequest() {
		double[] invalid = { 0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY };
		for (double weight : invalid) {
			Order o = lockedOrder(OrderStatus.CLOSED_FOR_LOADING);
			BadRequestException e = assertThrows(BadRequestException.class,
					() -> orderBusiness.registerFinalWeight(NUMBER, weight, "admin"));
			assertTrue(e.getMessage().startsWith("weight debe ser"), e.getMessage());
			assertEquals(OrderStatus.CLOSED_FOR_LOADING, o.getStatus());
			assertNull(o.getFinalWeight());
		}
		verifyNothingWritten();
	}

	@Test
	void registerFinalWeight_statusNot3_throwsConflict() {
		OrderStatus[] others = { OrderStatus.PENDING_INITIAL_WEIGHING, OrderStatus.INITIAL_WEIGHING_REGISTERED,
				OrderStatus.FINISHED };
		for (OrderStatus status : others) {
			lockedOrder(status);
			ConflictException e = assertThrows(ConflictException.class,
					() -> orderBusiness.registerFinalWeight(NUMBER, 16490.0, "admin"));
			assertTrue(e.getMessage().contains("no admite pesaje final"));
			assertTrue(e.getMessage().contains("estado " + status.getCode()));
		}
		verifyNothingWritten();
	}

	@Test
	void registerFinalWeight_noTare_throwsConflict() {
		Order o = lockedOrder(OrderStatus.CLOSED_FOR_LOADING);
		o.setTare(null);
		ConflictException e = assertThrows(ConflictException.class,
				() -> orderBusiness.registerFinalWeight(NUMBER, 16490.0, "admin"));
		assertTrue(e.getMessage().contains("no tiene tara registrada"));
		verifyNothingWritten();
	}

	@Test
	void registerFinalWeight_weightLessThanTare_throwsBadRequest() {
		Order o = lockedOrder(OrderStatus.CLOSED_FOR_LOADING);
		BadRequestException e = assertThrows(BadRequestException.class,
				() -> orderBusiness.registerFinalWeight(NUMBER, 14000.0, "admin"));
		assertTrue(e.getMessage().contains("no puede ser menor que la tara (14500.0 kg)"));
		assertEquals(OrderStatus.CLOSED_FOR_LOADING, o.getStatus());
		verifyNothingWritten();
	}

	/** Promedios de prueba (la proyeccion es una interfaz). */
	private static ReadingAverages averages(Double temperature, Double density, Double flowRate) {
		return new ReadingAverages() {
			public Double getTemperature() { return temperature; }
			public Double getDensity() { return density; }
			public Double getFlowRate() { return flowRate; }
		};
	}

	/** Orden cerrada con el ejemplo de la division (tara 14500, masa 2000, promedios 21.0 / 0.5 / 3300.0). */
	private Order closedOrderWithExample() {
		Order o = lockedOrder(OrderStatus.CLOSED_FOR_LOADING);
		o.setLastAccumulatedMass(2000.0);
		when(orderDAO.saveAndFlush(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
		when(orderDetailDAO.averagesByOrder(o)).thenReturn(averages(21.0, 0.5, 3300.0));
		return o;
	}

	@Test
	void registerFinalWeight_valid_movesTo4SavesWeightAndLogsOneRow() throws Exception {
		closedOrderWithExample();
		LocalDateTime before = LocalDateTime.now();

		Reconciliation r = orderBusiness.registerFinalWeight(NUMBER, 16490.0, "admin");

		// Conciliacion del ejemplo, sin redondeo
		assertEquals(NUMBER, r.getOrderNumber());
		assertEquals(14500.0, r.getTare());
		assertEquals(16490.0, r.getFinalWeight());
		assertEquals(2000.0, r.getLoadedProduct());
		assertEquals(1990.0, r.getScaleNet());
		assertEquals(-10.0, r.getDifference());
		assertEquals(21.0, r.getAverageTemperature());
		assertEquals(0.5, r.getAverageDensity());
		assertEquals(3300.0, r.getAverageFlowRate());

		ArgumentCaptor<Order> saved = ArgumentCaptor.forClass(Order.class);
		verify(orderDAO).saveAndFlush(saved.capture());
		assertEquals(OrderStatus.FINISHED, saved.getValue().getStatus());
		assertEquals(16490.0, saved.getValue().getFinalWeight());
		assertNotNull(saved.getValue().getFinalWeighingDateTime());
		assertTrue(!saved.getValue().getFinalWeighingDateTime().isBefore(before));
		verify(orderDAO, never()).findByOrderNumber(any());

		ArgumentCaptor<OrderStatusLog> log = ArgumentCaptor.forClass(OrderStatusLog.class);
		verify(statusLogDAO, times(1)).save(log.capture());
		assertEquals(OrderStatus.CLOSED_FOR_LOADING, log.getValue().getFromStatus());
		assertEquals(OrderStatus.FINISHED, log.getValue().getToStatus());
		assertEquals("admin", log.getValue().getActor());
		assertEquals("Pesaje final registrado", log.getValue().getObservations());
	}

	@Test
	void registerFinalWeight_nullOrBlankActor_logsSistema() throws Exception {
		closedOrderWithExample();
		orderBusiness.registerFinalWeight(NUMBER, 16490.0, null);
		closedOrderWithExample();
		orderBusiness.registerFinalWeight(NUMBER, 16490.0, "  ");

		ArgumentCaptor<OrderStatusLog> log = ArgumentCaptor.forClass(OrderStatusLog.class);
		verify(statusLogDAO, times(2)).save(log.capture());
		log.getAllValues().forEach(row -> assertEquals("sistema", row.getActor()));
	}

	@Test
	void registerFinalWeight_weightEqualToTareWithoutReadings_isValid() throws Exception {
		Order o = lockedOrder(OrderStatus.CLOSED_FOR_LOADING);
		when(orderDAO.saveAndFlush(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
		// Sin lecturas: lastAccumulatedMass nula y la consulta de promedios no devuelve nada

		Reconciliation r = orderBusiness.registerFinalWeight(NUMBER, TARE, "admin");

		assertEquals(OrderStatus.FINISHED, o.getStatus());
		assertEquals(0.0, r.getLoadedProduct());
		assertEquals(0.0, r.getScaleNet());
		assertEquals(0.0, r.getDifference());
		assertNull(r.getAverageTemperature());
		assertNull(r.getAverageDensity());
		assertNull(r.getAverageFlowRate());
	}

	/** Orden para la consulta (sin bloqueo): la busca la consulta comun. */
	private Order queryOrder(OrderStatus status) {
		Order o = new Order();
		o.setId(1L);
		o.setOrderNumber(NUMBER);
		o.setStatus(status);
		o.setTare(TARE);
		o.setFinalWeight(16490.0);
		o.setLastAccumulatedMass(2000.0);
		when(orderDAO.findByOrderNumber(NUMBER)).thenReturn(Optional.of(o));
		return o;
	}

	@Test
	void reconciliation_orderNotFound_throwsNotFound() {
		when(orderDAO.findByOrderNumber(NUMBER)).thenReturn(Optional.empty());
		assertThrows(NotFoundException.class, () -> orderBusiness.reconciliation(NUMBER));
	}

	@Test
	void reconciliation_statusNot4_throwsConflict() {
		OrderStatus[] others = { OrderStatus.PENDING_INITIAL_WEIGHING, OrderStatus.INITIAL_WEIGHING_REGISTERED,
				OrderStatus.CLOSED_FOR_LOADING };
		for (OrderStatus status : others) {
			queryOrder(status);
			ConflictException e = assertThrows(ConflictException.class, () -> orderBusiness.reconciliation(NUMBER));
			assertTrue(e.getMessage().contains("solo está disponible para órdenes en estado 4"));
			assertTrue(e.getMessage().contains("en estado " + status.getCode()));
		}
	}

	@Test
	void reconciliation_status4_returnsTheSameValuesWithoutLockingNorWriting() throws Exception {
		Order o = queryOrder(OrderStatus.FINISHED);
		when(orderDetailDAO.averagesByOrder(o)).thenReturn(averages(21.0, 0.5, 3300.0));

		Reconciliation r = orderBusiness.reconciliation(NUMBER);

		assertEquals(2000.0, r.getLoadedProduct());
		assertEquals(1990.0, r.getScaleNet());
		assertEquals(-10.0, r.getDifference());
		assertEquals(21.0, r.getAverageTemperature());
		assertEquals(0.5, r.getAverageDensity());
		assertEquals(3300.0, r.getAverageFlowRate());
		verify(orderDAO, never()).findByOrderNumberForUpdate(any());
		verifyNothingWritten();
	}
}
