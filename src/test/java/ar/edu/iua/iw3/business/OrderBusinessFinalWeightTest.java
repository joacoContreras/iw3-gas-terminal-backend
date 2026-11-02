package ar.edu.iua.iw3.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

import ar.edu.iua.iw3.business.exception.BadRequestException;
import ar.edu.iua.iw3.business.exception.ConflictException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.OrderStatus;
import ar.edu.iua.iw3.model.persistence.OrderDetailRepository;
import ar.edu.iua.iw3.model.persistence.OrderRepository;
import ar.edu.iua.iw3.model.persistence.OrderStatusLogRepository;

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
}
