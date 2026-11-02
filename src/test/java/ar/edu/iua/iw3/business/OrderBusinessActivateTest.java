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

import ar.edu.iua.iw3.business.exception.BadRequestException;
import ar.edu.iua.iw3.business.exception.ConflictException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.OrderStatus;
import ar.edu.iua.iw3.model.persistence.OrderRepository;
import ar.edu.iua.iw3.model.persistence.OrderStatusLogRepository;

/**
 * Pruebas unitarias de la activacion (Punto 3, arreglo H-25: busca la orden con bloqueo) con Mockito.
 */
@ExtendWith(MockitoExtension.class)
class OrderBusinessActivateTest {

	private static final Integer NUMBER = 3001;
	private static final Integer PASSWORD = 48213;

	@Mock
	private OrderRepository orderDAO;

	@Mock
	private OrderStatusLogRepository statusLogDAO;

	@InjectMocks
	private OrderBusiness orderBusiness;

	/** Orden en el estado pedido, devuelta por la consulta CON bloqueo. */
	private Order order(OrderStatus status) {
		Order o = new Order();
		o.setId(1L);
		o.setOrderNumber(NUMBER);
		o.setStatus(status);
		o.setActivationPassword(PASSWORD);
		when(orderDAO.findByOrderNumberForUpdate(NUMBER)).thenReturn(Optional.of(o));
		return o;
	}

	@Test
	void activate_missingFields_throwsBadRequestWithoutQuerying() {
		assertThrows(BadRequestException.class, () -> orderBusiness.activate(null, PASSWORD));
		assertThrows(BadRequestException.class, () -> orderBusiness.activate(NUMBER, null));
		verify(orderDAO, never()).findByOrderNumberForUpdate(any());
		verify(orderDAO, never()).findByOrderNumber(any());
	}

	@Test
	void activate_orderNotFound_throwsNotFound() {
		when(orderDAO.findByOrderNumberForUpdate(NUMBER)).thenReturn(Optional.empty());
		NotFoundException e = assertThrows(NotFoundException.class, () -> orderBusiness.activate(NUMBER, PASSWORD));
		assertTrue(e.getMessage().contains("No se encuentra la Orden numero=" + NUMBER));
		verify(orderDAO, never()).saveAndFlush(any());
	}

	@Test
	void activate_statusNot2_throwsConflict() {
		for (OrderStatus status : new OrderStatus[] { OrderStatus.PENDING_INITIAL_WEIGHING,
				OrderStatus.CLOSED_FOR_LOADING, OrderStatus.FINISHED }) {
			order(status);
			ConflictException e = assertThrows(ConflictException.class, () -> orderBusiness.activate(NUMBER, PASSWORD));
			assertTrue(e.getMessage().contains("no está lista para activar"));
			assertTrue(e.getMessage().contains("estado " + status.getCode()));
		}
		verify(orderDAO, never()).saveAndFlush(any());
	}

}
