package ar.edu.iua.iw3.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;

import ar.edu.iua.iw3.business.exception.BadRequestException;
import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.business.exception.ConflictException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.events.TemperatureAlarmEvent;
import ar.edu.iua.iw3.model.AlarmConfig;
import ar.edu.iua.iw3.model.AlarmStatus;
import ar.edu.iua.iw3.model.Customer;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.Product;
import ar.edu.iua.iw3.model.TemperatureAlarm;
import ar.edu.iua.iw3.model.Truck;
import ar.edu.iua.iw3.model.persistence.TemperatureAlarmRepository;

/**
 * Pruebas unitarias de las alarmas de temperatura (examen final, punto 3) con Mockito: sin base de datos
 * ni servidor de correo. Cubren cuando se alarma, la regla de no reenvio y la aceptacion.
 */
@ExtendWith(MockitoExtension.class)
class AlarmBusinessTest {

	private static final Integer NUMBER = 5001;

	@Mock
	private TemperatureAlarmRepository alarmDAO;

	@Mock
	private IAlarmConfigBusiness configBusiness;

	@Mock
	private ApplicationEventPublisher publisher;

	@InjectMocks
	private AlarmBusiness business;

	private static Order order() {
		Order o = new Order();
		o.setOrderNumber(NUMBER);
		Truck t = new Truck();
		t.setPlate("AB123CD");
		o.setTruck(t);
		Customer c = new Customer();
		c.setCompanyName("Gas del Sur SA");
		o.setCustomer(c);
		Product p = new Product();
		p.setName("GLP");
		o.setProduct(p);
		return o;
	}

	private static AlarmConfig config(double threshold, String... emails) {
		AlarmConfig c = new AlarmConfig();
		c.setTemperatureThreshold(threshold);
		c.setRecipientEmails(List.of(emails));
		return c;
	}

	private static TemperatureAlarm alarm(AlarmStatus status) {
		return new TemperatureAlarm(9L, null, NUMBER, 35.2, 30.0, status, LocalDateTime.now(), null,
				status == AlarmStatus.ACKNOWLEDGED ? "supervisor" : null, null, null);
	}

	/** Simula el INSERT: la base le asigna el id. */
	private void stubSaveAssigningId() {
		when(alarmDAO.saveAndFlush(any(TemperatureAlarm.class))).thenAnswer(i -> {
			TemperatureAlarm a = i.getArgument(0);
			a.setId(7L);
			return a;
		});
	}

	// ------------------------------------------------------------------ cuando se alarma

	@Test
	void superarElUmbralCreaUnaAlarmaPendienteYPublicaUnSoloEvento() throws Exception {
		when(configBusiness.get()).thenReturn(config(30.0, "a@x.com", "b@x.com"));
		when(alarmDAO.existsByOrderNumberAndStatus(NUMBER, AlarmStatus.PENDING)).thenReturn(false);
		stubSaveAssigningId();

		Optional<TemperatureAlarm> res = business.evaluate(order(), 35.2);

		assertTrue(res.isPresent());
		TemperatureAlarm a = res.get();
		assertEquals(AlarmStatus.PENDING, a.getStatus());
		assertEquals(NUMBER, a.getOrderNumber());
		assertEquals(35.2, a.getTemperature());
		assertEquals(30.0, a.getThreshold());
		assertNotNull(a.getTriggeredAt());
		assertNull(a.getEmailSentAt());
		assertNull(a.getAcknowledgedBy());

		ArgumentCaptor<TemperatureAlarmEvent> ev = ArgumentCaptor.forClass(TemperatureAlarmEvent.class);
		verify(publisher).publishEvent(ev.capture());
		assertEquals(7L, ev.getValue().getAlarmId());
		assertEquals(List.of("a@x.com", "b@x.com"), ev.getValue().getRecipients());
		assertTrue(ev.getValue().getSubject().contains("5001"));
		String text = ev.getValue().getText();
		assertTrue(text.contains("AB123CD") && text.contains("Gas del Sur SA") && text.contains("GLP"), text);
		assertTrue(text.contains("35,2") && text.contains("30,0"), text);
	}

	@Test
	void unaTemperaturaIgualAlUmbralNoAlarma() throws Exception {
		when(configBusiness.get()).thenReturn(config(30.0, "a@x.com"));

		assertTrue(business.evaluate(order(), 30.0).isEmpty());

		verifyNoInteractions(alarmDAO, publisher);
	}

	@Test
	void unaTemperaturaPorDebajoDelUmbralNoAlarma() throws Exception {
		when(configBusiness.get()).thenReturn(config(30.0, "a@x.com"));

		assertTrue(business.evaluate(order(), 12.5).isEmpty());

		verifyNoInteractions(alarmDAO, publisher);
	}

	// ------------------------------------------------------------------ no reenvio

	@Test
	void conUnaAlarmaPendienteDeLaOrdenNoSeCreaNiSeAvisaOtra() throws Exception {
		when(configBusiness.get()).thenReturn(config(30.0, "a@x.com"));
		when(alarmDAO.existsByOrderNumberAndStatus(NUMBER, AlarmStatus.PENDING)).thenReturn(true);

		assertTrue(business.evaluate(order(), 40.0).isEmpty());

		verify(alarmDAO, never()).saveAndFlush(any());
		verifyNoInteractions(publisher);
	}

	@Test
	void aceptadaLaAlarmaUnaNuevaSuperacionVuelveAAlarmar() throws Exception {
		when(configBusiness.get()).thenReturn(config(30.0, "a@x.com"));
		// Primero hay una pendiente; despues de aceptarla ya no hay ninguna pendiente
		when(alarmDAO.existsByOrderNumberAndStatus(NUMBER, AlarmStatus.PENDING)).thenReturn(true, false);
		stubSaveAssigningId();

		assertTrue(business.evaluate(order(), 40.0).isEmpty());
		assertTrue(business.evaluate(order(), 41.0).isPresent());

		verify(publisher).publishEvent(any(TemperatureAlarmEvent.class));
	}

	// ------------------------------------------------------------------ correo

	@Test
	void sinDestinatariosLaAlarmaSeCreaPeroNoSePublicaElEvento() throws Exception {
		when(configBusiness.get()).thenReturn(config(30.0));
		when(alarmDAO.existsByOrderNumberAndStatus(NUMBER, AlarmStatus.PENDING)).thenReturn(false);
		stubSaveAssigningId();

		assertTrue(business.evaluate(order(), 35.0).isPresent());

		verifyNoInteractions(publisher);
	}

	@Test
	void siFallaLaConfiguracionSePropagaComoBusinessException() throws Exception {
		when(configBusiness.get()).thenThrow(BusinessException.builder().message("sin base").build());

		assertThrows(BusinessException.class, () -> business.evaluate(order(), 35.0));
		verifyNoInteractions(publisher);
	}

	@Test
	void siFallaLaBaseAlCrearLaAlarmaNoSePublicaElEventoYSeInformaBusinessException() throws Exception {
		when(configBusiness.get()).thenReturn(config(30.0, "a@x.com"));
		when(alarmDAO.existsByOrderNumberAndStatus(NUMBER, AlarmStatus.PENDING)).thenReturn(false);
		when(alarmDAO.saveAndFlush(any())).thenThrow(new IllegalStateException("caido"));

		assertThrows(BusinessException.class, () -> business.evaluate(order(), 35.0));
		verifyNoInteractions(publisher);
	}

	@Test
	void markEmailSentRegistraLaFechaConUnUpdatePuntual() throws Exception {
		business.markEmailSent(7L);

		verify(alarmDAO).markEmailSent(any(Long.class), any(LocalDateTime.class));
		verify(alarmDAO, never()).save(any());
	}

	// ------------------------------------------------------------------ aceptacion

	@Test
	void aceptarUnaAlarmaPendienteRegistraUsuarioFechaYObservacion() throws Exception {
		TemperatureAlarm pending = alarm(AlarmStatus.PENDING);
		when(alarmDAO.findByIdForUpdate(9L)).thenReturn(Optional.of(pending));
		when(alarmDAO.saveAndFlush(pending)).thenReturn(pending);

		TemperatureAlarm res = business.acknowledge(9L, "supervisor", "  Se revisó el sensor  ");

		assertSame(pending, res);
		assertEquals(AlarmStatus.ACKNOWLEDGED, res.getStatus());
		assertEquals("supervisor", res.getAcknowledgedBy());
		assertNotNull(res.getAcknowledgedAt());
		assertEquals("Se revisó el sensor", res.getObservation());
	}

	@Test
	void laObservacionEsOpcionalYUnaObservacionEnBlancoSeGuardaNula() throws Exception {
		TemperatureAlarm pending = alarm(AlarmStatus.PENDING);
		when(alarmDAO.findByIdForUpdate(9L)).thenReturn(Optional.of(pending));
		when(alarmDAO.saveAndFlush(pending)).thenReturn(pending);

		assertNull(business.acknowledge(9L, "admin", "   ").getObservation());
	}

	@Test
	void aceptarUnaAlarmaInexistenteDa404() {
		when(alarmDAO.findByIdForUpdate(99L)).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> business.acknowledge(99L, "admin", null));
		verify(alarmDAO, never()).saveAndFlush(any());
	}

	@Test
	void aceptarDosVecesDa409YNoCambiaLosDatosDeLaPrimeraAceptacion() {
		TemperatureAlarm done = alarm(AlarmStatus.ACKNOWLEDGED);
		when(alarmDAO.findByIdForUpdate(9L)).thenReturn(Optional.of(done));

		ConflictException e = assertThrows(ConflictException.class, () -> business.acknowledge(9L, "admin", null));

		assertTrue(e.getMessage().contains("supervisor"), e.getMessage());
		assertEquals("supervisor", done.getAcknowledgedBy());
		verify(alarmDAO, never()).saveAndFlush(any());
	}

	@Test
	void sinUsuarioIdentificadoDa400AntesDeTocarLaBase() {
		assertThrows(BadRequestException.class, () -> business.acknowledge(9L, null, null));
		assertThrows(BadRequestException.class, () -> business.acknowledge(9L, "  ", null));

		verifyNoInteractions(alarmDAO);
	}

	@Test
	void unaObservacionDeMasDe500CaracteresDa400() {
		String larga = "x".repeat(AlarmBusiness.MAX_OBSERVATION + 1);

		assertThrows(BadRequestException.class, () -> business.acknowledge(9L, "admin", larga));

		verifyNoInteractions(alarmDAO);
	}

	@Test
	void unaObservacionDeExactamente500CaracteresEsValida() throws Exception {
		TemperatureAlarm pending = alarm(AlarmStatus.PENDING);
		when(alarmDAO.findByIdForUpdate(9L)).thenReturn(Optional.of(pending));
		when(alarmDAO.saveAndFlush(pending)).thenReturn(pending);

		assertEquals(500, business.acknowledge(9L, "admin", "x".repeat(500)).getObservation().length());
	}

	// ------------------------------------------------------------------ consulta

	@Test
	void listDelegaLosFiltrosYLaPaginaAlRepositorio() throws Exception {
		PageRequest page = PageRequest.of(0, 20);
		when(alarmDAO.search(AlarmStatus.PENDING, NUMBER, page)).thenReturn(new PageImpl<>(List.of(alarm(AlarmStatus.PENDING))));

		assertEquals(1, business.list(AlarmStatus.PENDING, NUMBER, page).getTotalElements());
		verify(alarmDAO).search(AlarmStatus.PENDING, NUMBER, page);
		verify(alarmDAO, never()).findByIdForUpdate(anyLong());
	}
}
