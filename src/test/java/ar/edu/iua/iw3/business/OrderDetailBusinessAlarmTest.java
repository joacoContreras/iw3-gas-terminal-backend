package ar.edu.iua.iw3.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.OrderDetail;
import ar.edu.iua.iw3.model.OrderStatus;
import ar.edu.iua.iw3.model.persistence.OrderDetailRepository;
import ar.edu.iua.iw3.model.persistence.OrderRepository;

/**
 * La alarma de temperatura se evalua con la lectura ya guardada y nunca debe impedir guardarla
 * (D-4 del plan del final). Se prueba el enganche en OrderDetailBusiness.add, sin base de datos.
 */
@ExtendWith(MockitoExtension.class)
class OrderDetailBusinessAlarmTest {

	private static final Integer NUMBER = 4001;

	@Mock
	private IOrderBusiness orderBusiness;

	@Mock
	private OrderRepository orderDAO;

	@Mock
	private OrderDetailRepository orderDetailDAO;

	@Mock
	private IAlarmBusiness alarmBusiness;

	@InjectMocks
	private OrderDetailBusiness business;

	private Order order;

	@BeforeEach
	void ordenActivadaEnEstado2() {
		order = new Order();
		order.setOrderNumber(NUMBER);
		order.setStatus(OrderStatus.INITIAL_WEIGHING_REGISTERED);
		order.setActivationDateTime(LocalDateTime.now());
		when(orderDAO.findByOrderNumberForUpdate(NUMBER)).thenReturn(Optional.of(order));
	}

	@Test
	void una_lectura_valida_se_guarda_y_luego_se_evalua_la_alarma_con_su_temperatura() throws Exception {
		DetailResult res = business.add(NUMBER, 100.0, 0.5, 35.2, 3000.0);

		assertTrue(res.isSaved());
		verify(orderDetailDAO).save(any(OrderDetail.class));
		verify(orderDAO).saveAndFlush(order);
		verify(alarmBusiness).evaluate(order, 35.2);
		assertEquals(35.2, order.getLastTemperature());
	}

	@Test
	void si_la_alarma_falla_la_lectura_se_guarda_igual_y_se_responde_guardada() throws Exception {
		doThrow(BusinessException.builder().message("alarma caida").build()).when(alarmBusiness).evaluate(any(),
				anyDouble());

		DetailResult res = business.add(NUMBER, 100.0, 0.5, 35.2, 3000.0);

		assertTrue(res.isSaved());
		verify(orderDetailDAO).save(any(OrderDetail.class));
		verify(orderDAO).saveAndFlush(order);
		assertEquals(100.0, order.getLastAccumulatedMass());
	}

	@Test
	void si_la_alarma_falla_con_un_error_inesperado_la_lectura_tambien_se_guarda() throws Exception {
		doThrow(new IllegalStateException("inesperado")).when(alarmBusiness).evaluate(any(), anyDouble());

		assertTrue(business.add(NUMBER, 100.0, 0.5, 35.2, 3000.0).isSaved());

		verify(orderDAO).saveAndFlush(order);
	}

	@Test
	void una_lectura_descartada_no_evalua_la_alarma() throws Exception {
		// Caudal 0: se descarta (consigna) y no debe disparar ninguna alarma
		DetailResult res = business.add(NUMBER, 100.0, 0.5, 99.0, 0.0);

		assertFalse(res.isSaved());
		verifyNoInteractions(alarmBusiness);
		verify(orderDetailDAO, never()).save(any());
	}
}
