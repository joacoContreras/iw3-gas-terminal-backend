package ar.edu.iua.iw3.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import ar.edu.iua.iw3.business.exception.BadRequestException;
import ar.edu.iua.iw3.model.AlarmConfig;
import ar.edu.iua.iw3.model.persistence.AlarmConfigRepository;

/** Pruebas de la configuracion de la alarma (umbral y correos): validaciones y normalizacion, sin base de datos. */
@ExtendWith(MockitoExtension.class)
class AlarmConfigBusinessTest {

	@Mock
	private AlarmConfigRepository configDAO;

	@InjectMocks
	private AlarmConfigBusiness business;

	@BeforeEach
	void umbralPorDefecto() {
		ReflectionTestUtils.setField(business, "defaultThreshold", 30.0);
	}

	private static AlarmConfig body(Double threshold, List<String> emails) {
		AlarmConfig c = new AlarmConfig();
		c.setTemperatureThreshold(threshold);
		c.setRecipientEmails(emails);
		return c;
	}

	private String badRequestMessage(AlarmConfig in) {
		BadRequestException e = assertThrows(BadRequestException.class, () -> business.update(in));
		verify(configDAO, never()).saveAndFlush(any());
		return e.getMessage();
	}

	// ------------------------------------------------------------------ lectura

	@Test
	void sinConfiguracionGuardadaRigeElUmbralPorDefectoYNoHayDestinatarios() throws Exception {
		when(configDAO.findById(AlarmConfig.SINGLE_ID)).thenReturn(Optional.empty());

		AlarmConfig c = business.get();

		assertEquals(30.0, c.getTemperatureThreshold());
		assertTrue(c.getRecipientEmails().isEmpty());
		verify(configDAO, never()).save(any());
	}

	@Test
	void conConfiguracionGuardadaSeDevuelveLaGuardada() throws Exception {
		AlarmConfig saved = body(25.5, List.of("a@x.com"));
		when(configDAO.findById(AlarmConfig.SINGLE_ID)).thenReturn(Optional.of(saved));

		assertEquals(25.5, business.get().getTemperatureThreshold());
	}

	// ------------------------------------------------------------------ actualizacion valida

	@Test
	void unaConfiguracionValidaSeGuardaRecortadaYSinRepetidosIgnorandoMayusculas() throws Exception {
		when(configDAO.saveAndFlush(any(AlarmConfig.class))).thenAnswer(i -> i.getArgument(0));

		AlarmConfig res = business.update(body(28.0, Arrays.asList("  Ana@Planta.com ", "ana@planta.com", "luis@planta.com")));

		assertEquals(28.0, res.getTemperatureThreshold());
		assertEquals(List.of("Ana@Planta.com", "luis@planta.com"), res.getRecipientEmails());
		assertEquals(AlarmConfig.SINGLE_ID, res.getId());
	}

	@Test
	void unaListaVaciaDeCorreosEsValida() throws Exception {
		when(configDAO.saveAndFlush(any(AlarmConfig.class))).thenAnswer(i -> i.getArgument(0));

		assertTrue(business.update(body(30.0, List.of())).getRecipientEmails().isEmpty());
	}

	@Test
	void veinteCorreosEsElMaximoPermitido() throws Exception {
		when(configDAO.saveAndFlush(any(AlarmConfig.class))).thenAnswer(i -> i.getArgument(0));
		List<String> veinte = new ArrayList<>();
		for (int i = 0; i < AlarmConfigBusiness.MAX_RECIPIENTS; i++) {
			veinte.add("u" + i + "@x.com");
		}

		assertEquals(20, business.update(body(30.0, veinte)).getRecipientEmails().size());
	}

	@Test
	void elUmbralPuedeSerNegativoOCero() throws Exception {
		when(configDAO.saveAndFlush(any(AlarmConfig.class))).thenAnswer(i -> i.getArgument(0));

		assertEquals(-5.0, business.update(body(-5.0, List.of())).getTemperatureThreshold());
		assertEquals(0.0, business.update(body(0.0, List.of())).getTemperatureThreshold());
	}

	// ------------------------------------------------------------------ 400

	@Test
	void sinUmbralDa400() {
		assertTrue(badRequestMessage(body(null, List.of())).contains("temperatureThreshold"));
	}

	@Test
	void unUmbralNoFinitoDa400() {
		assertTrue(badRequestMessage(body(Double.NaN, List.of())).contains("temperatureThreshold"));
		assertTrue(badRequestMessage(body(Double.POSITIVE_INFINITY, List.of())).contains("temperatureThreshold"));
	}

	@Test
	void sinListaDeCorreosDa400() {
		assertTrue(badRequestMessage(body(30.0, null)).contains("recipientEmails"));
	}

	@Test
	void unCuerpoSinElCampoRecipientEmailsLlegaConNullYSeRechazaSinBorrarLosDestinatarios() {
		// Es lo que hace Jackson cuando el JSON solo trae el umbral: la lista no debe tener un valor por defecto
		AlarmConfig soloUmbral = new AlarmConfig();
		soloUmbral.setTemperatureThreshold(25.0);

		assertTrue(badRequestMessage(soloUmbral).contains("recipientEmails"));
	}

	@Test
	void unCuerpoAusenteDa400ConAmbosMotivos() {
		String msg = badRequestMessage(null);

		assertTrue(msg.contains("temperatureThreshold") && msg.contains("recipientEmails"), msg);
	}

	@Test
	void unCorreoConFormatoInvalidoDa400() {
		for (String malo : List.of("sin-arroba", "a@b", "con espacio@x.com", "a@@x.com", "a@x.com,b@x.com")) {
			assertTrue(badRequestMessage(body(30.0, List.of(malo))).contains("correo inválido"), malo);
		}
	}

	@Test
	void unCorreoVacioONuloDa400() {
		assertTrue(badRequestMessage(body(30.0, Arrays.asList("a@x.com", " "))).contains("vacío"));
		assertTrue(badRequestMessage(body(30.0, Arrays.asList("a@x.com", null))).contains("vacío"));
	}

	@Test
	void masDeVeinteCorreosDa400() {
		List<String> muchos = new ArrayList<>();
		for (int i = 0; i <= AlarmConfigBusiness.MAX_RECIPIENTS; i++) {
			muchos.add("u" + i + "@x.com");
		}

		assertTrue(badRequestMessage(body(30.0, muchos)).contains("máximo 20"));
	}

	@Test
	void todosLosErroresSeInformanJuntosEnUnSoloMensaje() {
		String msg = badRequestMessage(body(null, List.of("mal", "peor")));

		assertTrue(msg.contains("temperatureThreshold") && msg.contains("mal") && msg.contains("peor"), msg);
	}
}
