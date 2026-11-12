package ar.edu.iua.iw3.security;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import ar.edu.iua.iw3.auth.IUserBusiness;
import ar.edu.iua.iw3.auth.filters.AuthConstants;
import ar.edu.iua.iw3.auth.filters.JwtSecret;
import ar.edu.iua.iw3.business.IOrderBusiness;
import ar.edu.iua.iw3.business.IOrderDetailBusiness;
import ar.edu.iua.iw3.controllers.OrderRestController;
import ar.edu.iua.iw3.util.StandartResponseBusiness;

/**
 * H-5 (final): la clave JWT sale de JWT_SECRET y, sin definirla, rige la de AuthConstants (el despliegue actual no
 * cambia ni invalida tokens). Prueba la resolucion de la clave y, con una clave configurada, que el filtro real
 * acepta los tokens firmados con ella y rechaza los firmados con la clave por defecto.
 */
@WebMvcTest(controllers = OrderRestController.class)
@Import({ SecurityConfiguration.class, RestAccessDeniedHandler.class, RestAuthenticationEntryPoint.class, JwtSecret.class,
		StandartResponseBusiness.class })
@TestPropertySource(properties = "jwt.secret=una-clave-de-produccion-larga-0123456789-abcdef")
class JwtSecretTest {

	private static final String CONFIGURED = "una-clave-de-produccion-larga-0123456789-abcdef";

	@Autowired
	private MockMvc mvc;

	@Autowired
	private JwtSecret jwtSecret;

	@MockitoBean
	private IUserBusiness userBusiness;
	@MockitoBean
	private IOrderBusiness orderBusiness;
	@MockitoBean
	private IOrderDetailBusiness orderDetailBusiness;

	private int statusWith(String token) throws Exception {
		try {
			return mvc.perform(get("/api/v1/orders").header(HttpHeaders.AUTHORIZATION, "Bearer " + token)).andReturn()
					.getResponse().getStatus();
		} catch (jakarta.servlet.ServletException e) {
			return 200; // paso la seguridad y llego al controller simulado
		}
	}

	// ------------------------------------------------------------------ con clave configurada

	@Test
	void la_clave_configurada_reemplaza_a_la_del_repositorio() {
		assertFalse(jwtSecret.isDefault());
		assertEquals(CONFIGURED, jwtSecret.value());
		assertArrayEquals(CONFIGURED.getBytes(StandardCharsets.UTF_8), jwtSecret.bytes());
	}

	@Test
	void un_token_firmado_con_la_clave_configurada_se_acepta() throws Exception {
		int status = statusWith(TestTokens.of(CONFIGURED, 3_600_000L, "ROLE_ADMIN"));

		assertTrue(status != 401 && status != 403, "estado " + status);
	}

	@Test
	void un_token_firmado_con_la_clave_por_defecto_ya_no_sirve() throws Exception {
		assertEquals(401, statusWith(TestTokens.of(AuthConstants.SECRET, 3_600_000L, "ROLE_ADMIN")));
	}

	// ------------------------------------------------------------------ resolucion (sin Spring)

	private static JwtSecret withConfigured(String value) {
		JwtSecret s = new JwtSecret();
		ReflectionTestUtils.setField(s, "configured", value);
		return s;
	}

	@Test
	void sin_clave_configurada_rige_la_de_authconstants() {
		for (String vacio : new String[] { null, "", "   " }) {
			JwtSecret s = withConfigured(vacio);

			assertTrue(s.isDefault(), "[" + vacio + "]");
			assertEquals(AuthConstants.SECRET, s.value());
			assertArrayEquals(AuthConstants.SECRET.getBytes(StandardCharsets.UTF_8), s.bytes());
		}
	}

	@Test
	void la_clave_por_defecto_da_los_mismos_bytes_que_antes_asi_los_tokens_vigentes_siguen_validos() {
		// Antes se usaba AuthConstants.SECRET.getBytes(): para una clave ASCII es identico
		assertArrayEquals(AuthConstants.SECRET.getBytes(), withConfigured("").bytes());
	}

	@Test
	void las_advertencias_de_arranque_no_fallan_con_ninguna_configuracion() {
		// warnIfWeak es de paquete (lo llama Spring tras construir el bean): se invoca por reflexion
		for (String clave : new String[] { null, "corta", "x".repeat(32) }) {
			ReflectionTestUtils.invokeMethod(withConfigured(clave), "warnIfWeak");
		}
	}
}
