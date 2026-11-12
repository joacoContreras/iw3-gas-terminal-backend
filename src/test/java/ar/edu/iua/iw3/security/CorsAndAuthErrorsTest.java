package ar.edu.iua.iw3.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;

import java.util.Base64;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ar.edu.iua.iw3.auth.IUserBusiness;
import ar.edu.iua.iw3.auth.Role;
import ar.edu.iua.iw3.auth.User;
import ar.edu.iua.iw3.auth.controller.AuthRestController;
import ar.edu.iua.iw3.auth.filters.JwtSecret;
import ar.edu.iua.iw3.business.IOrderBusiness;
import ar.edu.iua.iw3.business.IOrderDetailBusiness;
import ar.edu.iua.iw3.controllers.OrderRestController;
import ar.edu.iua.iw3.util.StandartResponseBusiness;

/**
 * Fase 3 del final: CORS dentro de la cadena de seguridad y errores 401 sin el header WWW-Authenticate.
 * Antes el preflight (OPTIONS) del navegador, que no lleva token, caia en el 401 y el frontend no podia llamar a la API.
 * Corre con la configuracion por defecto (CORS abierto); los origenes restringidos se prueban en CorsRestrictedOriginsTest.
 */
@WebMvcTest(controllers = { OrderRestController.class, AuthRestController.class })
@Import({ SecurityConfiguration.class, RestAccessDeniedHandler.class, RestAuthenticationEntryPoint.class, JwtSecret.class,
		StandartResponseBusiness.class })
class CorsAndAuthErrorsTest {

	private static final String ORIGIN = "http://localhost:3000";

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private IUserBusiness userBusiness;
	@MockitoBean
	private IOrderBusiness orderBusiness;
	@MockitoBean
	private IOrderDetailBusiness orderDetailBusiness;

	private MockHttpServletResponse preflight(String path, String method, String headers) throws Exception {
		var req = options(path).header(HttpHeaders.ORIGIN, ORIGIN).header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, method);
		if (headers != null) {
			req = req.header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, headers);
		}
		return mvc.perform(req).andReturn().getResponse();
	}

	// ------------------------------------------------------------------ CORS

	@Test
	void el_preflight_del_frontend_se_responde_sin_token() throws Exception {
		MockHttpServletResponse res = preflight("/api/v1/orders", "GET", "authorization,content-type");

		assertEquals(200, res.getStatus(), "el preflight no lleva token: no puede ser 401");
		assertEquals("*", res.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
		assertTrue(res.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS).contains("GET"));
		String allowedHeaders = res.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS).toLowerCase();
		assertTrue(allowedHeaders.contains("authorization") && allowedHeaders.contains("content-type"), allowedHeaders);
		assertEquals("3600", res.getHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE));
	}

	@Test
	void el_preflight_tambien_funciona_en_el_login_y_en_los_metodos_de_escritura() throws Exception {
		assertEquals(200, preflight("/api/v1/login", "POST", "content-type").getStatus());
		assertEquals(200, preflight("/api/v1/alarms/config", "PUT", "authorization,content-type").getStatus());
		assertEquals(200, preflight("/api/v1/integration/loader/orders/1/close", "POST", "authorization").getStatus());
	}

	@Test
	void un_metodo_que_la_api_no_usa_se_rechaza_en_el_preflight() throws Exception {
		assertEquals(403, preflight("/api/v1/orders", "PATCH", null).getStatus());
	}

	@Test
	void una_cabecera_que_la_api_no_usa_se_rechaza_en_el_preflight() throws Exception {
		assertEquals(403, preflight("/api/v1/orders", "GET", "x-cabecera-rara").getStatus());
	}

	@Test
	void las_respuestas_de_error_llevan_las_cabeceras_cors_para_que_el_frontend_pueda_leer_el_401() throws Exception {
		MockHttpServletResponse res = mvc.perform(get("/api/v1/orders").header(HttpHeaders.ORIGIN, ORIGIN)).andReturn()
				.getResponse();

		assertEquals(401, res.getStatus());
		assertEquals("*", res.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN),
				"sin esta cabecera el navegador oculta el 401 y el frontend no puede cerrar la sesion");
	}

	@Test
	void el_header_location_se_expone_para_el_alta_de_ordenes() throws Exception {
		MockHttpServletResponse res = mvc.perform(get("/api/v1/orders").header(HttpHeaders.ORIGIN, ORIGIN)).andReturn()
				.getResponse();

		assertTrue(String.valueOf(res.getHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS)).contains("Location"));
	}

	// ------------------------------------------------------------------ 401 sin WWW-Authenticate

	private void assertUnauthorizedJsonWithoutBasicChallenge(MockHttpServletResponse res) throws Exception {
		assertEquals(401, res.getStatus());
		assertNotNull(res.getContentType());
		assertTrue(res.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE), res.getContentType());
		assertFalse(res.containsHeader(HttpHeaders.WWW_AUTHENTICATE),
				"WWW-Authenticate: Basic hace que el navegador abra su dialogo de usuario y contraseña");
		String body = res.getContentAsString();
		assertTrue(body.contains("\"code\":401"), body);
		assertTrue(body.contains("\"message\":\"" + RestAuthenticationEntryPoint.MESSAGE + "\""), body);
	}

	@Test
	void sin_token_el_401_es_json_estandar_y_no_pide_credenciales_basic() throws Exception {
		assertUnauthorizedJsonWithoutBasicChallenge(mvc.perform(get("/api/v1/orders")).andReturn().getResponse());
	}

	@Test
	void con_un_token_basura_el_401_es_igual() throws Exception {
		assertUnauthorizedJsonWithoutBasicChallenge(mvc.perform(get("/api/v1/orders")
				.header(HttpHeaders.AUTHORIZATION, "Bearer esto.no.es-un-jwt-valido")).andReturn().getResponse());
	}

	@Test
	void con_un_token_vencido_el_401_es_igual_y_el_frontend_puede_volver_al_login() throws Exception {
		String vencido = TestTokens.of("MyVerySecretKey", -60_000L, "ROLE_ADMIN");

		assertUnauthorizedJsonWithoutBasicChallenge(mvc.perform(get("/api/v1/orders")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + vencido)).andReturn().getResponse());
	}

	@Test
	void un_token_firmado_con_otra_clave_se_rechaza() throws Exception {
		String ajeno = TestTokens.of("una-clave-que-no-es-la-del-servidor-0123456789", 3_600_000L, "ROLE_ADMIN");

		assertEquals(401, mvc.perform(get("/api/v1/orders").header(HttpHeaders.AUTHORIZATION, "Bearer " + ajeno))
				.andReturn().getResponse().getStatus());
	}

	@Test
	void la_autenticacion_http_basic_ya_no_existe_la_api_solo_acepta_jwt() throws Exception {
		// Un usuario valido en el negocio: con httpBasic habilitado, usuario y contraseña en cada pedido autenticarian
		User admin = new User();
		admin.setUsername("admin");
		admin.setEnabled(true);
		admin.setPassword(new BCryptPasswordEncoder().encode("admin123"));
		Role role = new Role();
		role.setName("ROLE_ADMIN");
		admin.setRoles(Set.of(role));
		when(userBusiness.load("admin")).thenReturn(admin);
		String basic = Base64.getEncoder().encodeToString("admin:admin123".getBytes());

		assertUnauthorizedJsonWithoutBasicChallenge(mvc.perform(get("/api/v1/orders")
				.header(HttpHeaders.AUTHORIZATION, "Basic " + basic)).andReturn().getResponse());
	}
}
