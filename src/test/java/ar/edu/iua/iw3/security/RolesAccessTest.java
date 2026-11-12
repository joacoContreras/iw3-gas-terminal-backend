package ar.edu.iua.iw3.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import ar.edu.iua.iw3.auth.IUserBusiness;
import ar.edu.iua.iw3.auth.controller.AuthRestController;
import ar.edu.iua.iw3.auth.filters.AuthConstants;
import ar.edu.iua.iw3.auth.filters.JwtSecret;
import ar.edu.iua.iw3.business.IAlarmBusiness;
import ar.edu.iua.iw3.business.IAlarmConfigBusiness;
import ar.edu.iua.iw3.business.IOrderBusiness;
import ar.edu.iua.iw3.business.IOrderDetailBusiness;
import ar.edu.iua.iw3.controllers.AlarmRestController;
import ar.edu.iua.iw3.controllers.OrderRestController;
import ar.edu.iua.iw3.integration.loader.controllers.OrderLoaderRestController;
import ar.edu.iua.iw3.integration.sap.controllers.OrderSapRestController;
import ar.edu.iua.iw3.integration.tms.controllers.OrderTmsRestController;
import ar.edu.iua.iw3.util.StandartResponseBusiness;
import jakarta.servlet.ServletException;

/**
 * Pruebas de la matriz de roles (examen final, punto 2). Recorren la cadena de seguridad real
 * (filtro JWT + @PreAuthorize + manejo del 403) con tokens firmados con la clave del proyecto;
 * la capa de negocio se reemplaza por dobles, asi que no hace falta la base de datos.
 *
 * Que el pedido pase la seguridad significa que llega al controller: aunque despues el negocio
 * simulado no devuelva nada util, no puede ser 401 ni 403.
 */
@WebMvcTest(controllers = { OrderRestController.class, OrderSapRestController.class, OrderTmsRestController.class,
		OrderLoaderRestController.class, AuthRestController.class, AlarmRestController.class })
@Import({ SecurityConfiguration.class, RestAccessDeniedHandler.class, RestAuthenticationEntryPoint.class, JwtSecret.class,
		StandartResponseBusiness.class })
class RolesAccessTest {

	private static final String ADMIN = "ROLE_ADMIN";
	private static final String SUPERVISOR = "ROLE_SUPERVISOR";
	private static final String OPERADOR = "ROLE_OPERADOR";
	private static final String SAP = "ROLE_SAP";
	private static final String TMS = "ROLE_TMS";
	private static final String LOADER = "ROLE_LOADER";
	/** Rol que existe en la base desde el seed original pero que no figura en la matriz. */
	private static final String USER = "ROLE_USER";

	private static final List<String> ALL_ROLES = List.of(ADMIN, SUPERVISOR, OPERADOR, SAP, TMS, LOADER, USER);

	/** Un endpoint y los roles que la matriz aprobada autoriza a consumirlo. */
	private record Endpoint(String name, HttpMethod method, String path, String body, Set<String> allowed) {
		Endpoint(String name, HttpMethod method, String path, Set<String> allowed) {
			this(name, method, path, "{}", allowed);
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static final Set<String> READERS = Set.of(ADMIN, SUPERVISOR, OPERADOR);

	private static final List<Endpoint> MATRIX = List.of(
			// Order tiene "preset" primitivo: sin el campo, el binding falla con 400 antes de llegar a la comprobacion de rol
			new Endpoint("POST sap/orders", HttpMethod.POST, "/api/v1/integration/sap/orders", "{\"preset\":1}",
					Set.of(ADMIN, SAP)),
			new Endpoint("POST tms/tare", HttpMethod.POST, "/api/v1/integration/tms/orders/1/tare", Set.of(ADMIN, TMS)),
			new Endpoint("POST tms/final-weight", HttpMethod.POST, "/api/v1/integration/tms/orders/1/final-weight",
					Set.of(ADMIN, TMS)),
			new Endpoint("POST loader/activation", HttpMethod.POST, "/api/v1/integration/loader/activation",
					Set.of(ADMIN, LOADER)),
			new Endpoint("POST loader/details", HttpMethod.POST, "/api/v1/integration/loader/orders/1/details",
					Set.of(ADMIN, LOADER)),
			new Endpoint("POST loader/close", HttpMethod.POST, "/api/v1/integration/loader/orders/1/close",
					Set.of(ADMIN, SUPERVISOR, OPERADOR, LOADER)),
			new Endpoint("GET orders", HttpMethod.GET, "/api/v1/orders", READERS),
			new Endpoint("GET orders/{n}", HttpMethod.GET, "/api/v1/orders/1", READERS),
			new Endpoint("GET orders/{n}/status-log", HttpMethod.GET, "/api/v1/orders/1/status-log", READERS),
			new Endpoint("GET orders/{n}/details", HttpMethod.GET, "/api/v1/orders/1/details", READERS),
			new Endpoint("GET orders/{n}/reconciliation", HttpMethod.GET, "/api/v1/orders/1/reconciliation", READERS),
			new Endpoint("GET demo/encodepass", HttpMethod.GET, "/demo/encodepass?password=x", Set.of(ADMIN)),
			// Alarmas de temperatura (examen final, Fase 2): ver la matriz en docs/final-fase-1-roles.md
			new Endpoint("GET alarms", HttpMethod.GET, "/api/v1/alarms", READERS),
			new Endpoint("POST alarms/{id}/acknowledge", HttpMethod.POST, "/api/v1/alarms/1/acknowledge",
					Set.of(ADMIN, SUPERVISOR)),
			new Endpoint("GET alarms/config", HttpMethod.GET, "/api/v1/alarms/config", Set.of(ADMIN, SUPERVISOR)),
			new Endpoint("PUT alarms/config", HttpMethod.PUT, "/api/v1/alarms/config",
					"{\"temperatureThreshold\":30,\"recipientEmails\":[]}", Set.of(ADMIN)));

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private IUserBusiness userBusiness;
	@MockitoBean
	private IOrderBusiness orderBusiness;
	@MockitoBean
	private IOrderDetailBusiness orderDetailBusiness;
	@MockitoBean
	private IAlarmBusiness alarmBusiness;
	@MockitoBean
	private IAlarmConfigBusiness alarmConfigBusiness;

	/** Token igual al que emite el login: sujeto, roles, email y vencimiento a 1 hora. */
	private static String token(String role) {
		return JWT.create().withSubject("usuario-de-prueba").withClaim("roles", List.of(role))
				.withClaim("email", "prueba@iw3.local").withExpiresAt(new Date(System.currentTimeMillis() + 3_600_000))
				.sign(Algorithm.HMAC512(AuthConstants.SECRET.getBytes()));
	}

	private static MockHttpServletRequestBuilder build(Endpoint e) {
		// El binding del cuerpo ocurre antes de la comprobacion de rol: el cuerpo tiene que ser enlazable
		return request(e.method(), e.path()).contentType(MediaType.APPLICATION_JSON).content(e.body());
	}

	/**
	 * Ejecuta el pedido y devuelve el estado HTTP. Si el negocio simulado hace fallar al controller
	 * (ServletException), el pedido ya habia pasado la seguridad: se informa como 200.
	 */
	private int statusOf(MockHttpServletRequestBuilder req) throws Exception {
		try {
			return mvc.perform(req).andReturn().getResponse().getStatus();
		} catch (ServletException e) {
			return 200;
		}
	}

	static Stream<Arguments> endpointsPorRol() {
		return MATRIX.stream().flatMap(e -> ALL_ROLES.stream().map(r -> Arguments.of(e, r)));
	}

	static Stream<Endpoint> endpoints() {
		return MATRIX.stream();
	}

	@ParameterizedTest(name = "{0} con {1}")
	@MethodSource("endpointsPorRol")
	void cadaRolSoloAccedeAlQueLaMatrizLePermite(Endpoint e, String role) throws Exception {
		int status = statusOf(build(e).header("Authorization", "Bearer " + token(role)));
		if (e.allowed().contains(role)) {
			assertNotEquals(401, status, role + " debe poder consumir " + e);
			assertNotEquals(403, status, role + " debe poder consumir " + e);
		} else {
			assertEquals(403, status, role + " no debe poder consumir " + e);
		}
	}

	@ParameterizedTest(name = "{0} sin token")
	@MethodSource("endpoints")
	void sinTokenSigueSiendo401(Endpoint e) throws Exception {
		assertEquals(401, statusOf(build(e)), e + " sin token");
	}

	@Test
	void elTokenDeUnRolDesconocidoDa403() throws Exception {
		int status = statusOf(request(HttpMethod.GET, "/api/v1/orders").header("Authorization",
				"Bearer " + token("ROLE_INVENTADO")));
		assertEquals(403, status);
	}

	@Test
	void el403TieneElFormatoEstandarConMensaje() throws Exception {
		MvcResult res = mvc.perform(request(HttpMethod.POST, "/api/v1/integration/sap/orders")
				.contentType(MediaType.APPLICATION_JSON).content("{\"preset\":1}")
				.header("Authorization", "Bearer " + token(OPERADOR))).andReturn();
		assertEquals(403, res.getResponse().getStatus());
		assertTrue(res.getResponse().getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE),
				"el 403 debe responder JSON");
		String body = res.getResponse().getContentAsString();
		assertTrue(body.contains("\"code\" : 403") || body.contains("\"code\":403"), "debe llevar code 403: " + body);
		assertTrue(body.contains("\"message\""), "debe llevar message: " + body);
	}
}
