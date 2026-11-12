package ar.edu.iua.iw3.events;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.iua.iw3.business.IAlarmBusiness;
import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.util.EmailBusiness;

/** El aviso por correo: un fallo se registra pero nunca se propaga, y se envia un correo por destinatario. */
@ExtendWith(MockitoExtension.class)
class TemperatureAlarmEventListenerTest {

	private static final String SUBJECT = "[IW3] Alarma de temperatura: orden 5001";
	private static final String TEXT = "texto";

	@Mock
	private EmailBusiness emailBusiness;

	@Mock
	private IAlarmBusiness alarmBusiness;

	@InjectMocks
	private TemperatureAlarmEventListener listener;

	private static TemperatureAlarmEvent event(String... to) {
		return new TemperatureAlarmEvent("test", 7L, List.of(to), SUBJECT, TEXT);
	}

	@Test
	void envia_un_correo_a_cada_destinatario_y_registra_el_envio() throws Exception {
		listener.onTemperatureAlarm(event("a@x.com", "b@x.com"));

		verify(emailBusiness).sendSimpleMessage("a@x.com", SUBJECT, TEXT);
		verify(emailBusiness).sendSimpleMessage("b@x.com", SUBJECT, TEXT);
		verify(alarmBusiness).markEmailSent(7L);
	}

	@Test
	void si_un_destinatario_falla_se_avisa_a_los_demas_y_igual_se_registra_el_envio() throws Exception {
		doThrow(BusinessException.builder().message("rechazado").build()).when(emailBusiness)
				.sendSimpleMessage(eq("mala@x.com"), any(), any());

		assertDoesNotThrow(() -> listener.onTemperatureAlarm(event("mala@x.com", "buena@x.com")));

		verify(emailBusiness).sendSimpleMessage("buena@x.com", SUBJECT, TEXT);
		verify(alarmBusiness).markEmailSent(7L);
	}

	@Test
	void si_no_sale_ningun_correo_no_se_propaga_el_error_ni_se_registra_envio() throws Exception {
		doThrow(BusinessException.builder().message("sin SMTP").build()).when(emailBusiness).sendSimpleMessage(any(),
				any(), any());

		assertDoesNotThrow(() -> listener.onTemperatureAlarm(event("a@x.com", "b@x.com")));

		verify(emailBusiness, times(2)).sendSimpleMessage(any(), any(), any());
		verify(alarmBusiness, never()).markEmailSent(any());
	}

	@Test
	void un_error_inesperado_del_servidor_de_correo_tampoco_se_propaga() throws Exception {
		doThrow(new IllegalStateException("caido")).when(emailBusiness).sendSimpleMessage(any(), any(), any());

		assertDoesNotThrow(() -> listener.onTemperatureAlarm(event("a@x.com")));

		verify(alarmBusiness, never()).markEmailSent(any());
	}

	@Test
	void si_falla_el_registro_del_envio_no_se_propaga() throws Exception {
		doThrow(BusinessException.builder().message("sin base").build()).when(alarmBusiness).markEmailSent(7L);

		assertDoesNotThrow(() -> listener.onTemperatureAlarm(event("a@x.com")));

		verify(emailBusiness).sendSimpleMessage("a@x.com", SUBJECT, TEXT);
	}

	@Test
	void sin_destinatarios_no_hace_nada() {
		listener.onTemperatureAlarm(event());

		verifyNoInteractions(emailBusiness, alarmBusiness);
	}
}
