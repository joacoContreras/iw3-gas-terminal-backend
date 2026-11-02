package ar.edu.iua.iw3.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.OrderStatus;
import ar.edu.iua.iw3.model.persistence.OrderRepository;
import ar.edu.iua.iw3.model.persistence.OrderStatusLogRepository;

/**
 * Pruebas unitarias de la validacion del peso de la tara (Punto 2, arreglo H-26) con Mockito.
 */
@ExtendWith(MockitoExtension.class)
class OrderBusinessTareTest {

	private static final Integer NUMBER = 2001;

	@Mock
	private OrderRepository orderDAO;

	@Mock
	private OrderStatusLogRepository statusLogDAO;

	@InjectMocks
	private OrderBusiness orderBusiness;

	private Order pendingOrder() {
		Order o = new Order();
		o.setId(1L);
		o.setOrderNumber(NUMBER);
		o.setStatus(OrderStatus.PENDING_INITIAL_WEIGHING);
		when(orderDAO.findByOrderNumber(NUMBER)).thenReturn(Optional.of(o));
		return o;
	}

	/** Un peso invalido da 400 con el mensaje esperado y no guarda nada. */
	private void assertBadWeight(double weight, String expectedMessage) {
		Order o = pendingOrder();
		BadRequestException e = assertThrows(BadRequestException.class,
				() -> orderBusiness.registerTare(NUMBER, weight, "admin"));
		assertTrue(e.getMessage().contains(expectedMessage), e.getMessage());
		assertEquals(OrderStatus.PENDING_INITIAL_WEIGHING, o.getStatus());
		assertNull(o.getTare());
		verify(orderDAO, never()).save(any());
		verify(statusLogDAO, never()).save(any());
	}

	@Test
	void registerTare_infiniteWeight_throwsBadRequestWithoutSaving() {
		assertBadWeight(Double.POSITIVE_INFINITY, "weight debe ser un número finito");
	}

	@Test
	void registerTare_zeroNegativeAndNaN_keepTheOriginalMessage() {
		assertBadWeight(0.0, "weight debe ser mayor que 0");
		assertBadWeight(-5.0, "weight debe ser mayor que 0");
		assertBadWeight(Double.NaN, "weight debe ser mayor que 0");
	}

	@Test
	void registerTare_negativeInfinity_keepsTheOriginalMessage() {
		assertBadWeight(Double.NEGATIVE_INFINITY, "weight debe ser mayor que 0");
	}

	@Test
	void registerTare_validWeight_movesToStatus2() throws Exception {
		Order o = pendingOrder();
		when(orderDAO.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

		Order result = orderBusiness.registerTare(NUMBER, 14500.0, "admin");

		assertEquals(OrderStatus.INITIAL_WEIGHING_REGISTERED, result.getStatus());
		assertEquals(14500.0, result.getTare());
		assertNotNull(result.getActivationPassword());
		assertEquals(o, result);
		verify(statusLogDAO).save(any());
	}
}
