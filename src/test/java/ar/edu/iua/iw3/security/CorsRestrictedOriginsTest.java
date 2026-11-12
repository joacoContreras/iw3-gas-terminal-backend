package ar.edu.iua.iw3.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ar.edu.iua.iw3.auth.IUserBusiness;
import ar.edu.iua.iw3.auth.filters.JwtSecret;
import ar.edu.iua.iw3.business.IOrderBusiness;
import ar.edu.iua.iw3.business.IOrderDetailBusiness;
import ar.edu.iua.iw3.controllers.OrderRestController;
import ar.edu.iua.iw3.util.StandartResponseBusiness;

/** CORS_ALLOWED_ORIGINS limita los origenes que pueden llamar a la API (lista separada por coma, con espacios tolerados). */
@WebMvcTest(controllers = OrderRestController.class)
@Import({ SecurityConfiguration.class, RestAccessDeniedHandler.class, RestAuthenticationEntryPoint.class, JwtSecret.class,
		StandartResponseBusiness.class })
@TestPropertySource(properties = "cors.allowed-origins=http://localhost:3000, https://app.iw3.example")
class CorsRestrictedOriginsTest {

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private IUserBusiness userBusiness;
	@MockitoBean
	private IOrderBusiness orderBusiness;
	@MockitoBean
	private IOrderDetailBusiness orderDetailBusiness;

	private MockHttpServletResponse preflightFrom(String origin) throws Exception {
		return mvc.perform(options("/api/v1/orders").header(HttpHeaders.ORIGIN, origin)
				.header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")).andReturn().getResponse();
	}

	@Test
	void un_origen_de_la_lista_se_acepta_y_la_respuesta_lo_devuelve_tal_cual() throws Exception {
		MockHttpServletResponse res = preflightFrom("http://localhost:3000");

		assertEquals(200, res.getStatus());
		assertEquals("http://localhost:3000", res.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
	}

	@Test
	void el_segundo_origen_de_la_lista_tambien_se_acepta_aunque_tenga_espacios_en_la_configuracion() throws Exception {
		MockHttpServletResponse res = preflightFrom("https://app.iw3.example");

		assertEquals(200, res.getStatus());
		assertEquals("https://app.iw3.example", res.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
	}

	@Test
	void un_origen_ajeno_se_rechaza() throws Exception {
		MockHttpServletResponse res = preflightFrom("https://sitio-malicioso.example");

		assertEquals(403, res.getStatus());
		assertNull(res.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
	}

	@Test
	void un_pedido_comun_desde_un_origen_ajeno_no_recibe_cabeceras_cors() throws Exception {
		MockHttpServletResponse res = mvc.perform(get("/api/v1/orders").header(HttpHeaders.ORIGIN, "https://sitio-malicioso.example"))
				.andReturn().getResponse();

		assertNull(res.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
	}
}
