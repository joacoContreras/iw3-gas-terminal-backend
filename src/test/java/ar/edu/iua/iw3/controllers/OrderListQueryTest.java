package ar.edu.iua.iw3.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ar.edu.iua.iw3.auth.IUserBusiness;
import ar.edu.iua.iw3.auth.filters.AuthConstants;
import ar.edu.iua.iw3.auth.filters.JwtSecret;
import ar.edu.iua.iw3.business.IOrderBusiness;
import ar.edu.iua.iw3.business.IOrderDetailBusiness;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.OrderStatus;
import ar.edu.iua.iw3.security.RestAccessDeniedHandler;
import ar.edu.iua.iw3.security.RestAuthenticationEntryPoint;
import ar.edu.iua.iw3.security.SecurityConfiguration;
import ar.edu.iua.iw3.util.StandartResponseBusiness;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

/**
 * GET /orders con filtro por estado y orden elegible (final, consigna 4b): que recibe el negocio segun los
 * parametros y que se rechaza con 400. El negocio es un doble: no hace falta la base de datos.
 */
@WebMvcTest(controllers = OrderRestController.class)
@Import({ SecurityConfiguration.class, RestAccessDeniedHandler.class, RestAuthenticationEntryPoint.class, JwtSecret.class,
		StandartResponseBusiness.class })
class OrderListQueryTest {

	@Autowired
	private MockMvc mvc;

	@MockitoBean
	private IUserBusiness userBusiness;
	@MockitoBean
	private IOrderBusiness orderBusiness;
	@MockitoBean
	private IOrderDetailBusiness orderDetailBusiness;

	private static String adminToken() {
		return JWT.create().withSubject("u").withClaim("roles", List.of("ROLE_ADMIN"))
				.withExpiresAt(new java.util.Date(System.currentTimeMillis() + 3_600_000))
				.sign(Algorithm.HMAC512(AuthConstants.SECRET.getBytes()));
	}

	@BeforeEach
	void negocioDevuelveUnaPaginaVacia() throws Exception {
		when(orderBusiness.list(any(), any())).thenReturn(Page.<Order>empty());
	}

	private MockHttpServletResponse call(String query) throws Exception {
		return mvc.perform(get("/api/v1/orders" + query).header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken()))
				.andReturn().getResponse();
	}

	private Pageable pageableSent(OrderStatus expectedStatus) throws Exception {
		ArgumentCaptor<Pageable> p = ArgumentCaptor.forClass(Pageable.class);
		verify(orderBusiness).list(org.mockito.ArgumentMatchers.eq(expectedStatus), p.capture());
		return p.getValue();
	}

	// ------------------------------------------------------------------ valores por defecto

	@Test
	void sin_parametros_no_filtra_y_ordena_por_numero_de_orden_descendente_como_antes() throws Exception {
		assertEquals(200, call("").getStatus());

		Pageable p = pageableSent(null);
		assertEquals(0, p.getPageNumber());
		assertEquals(20, p.getPageSize());
		assertEquals(Sort.by(Sort.Direction.DESC, "orderNumber"), p.getSort());
	}

	@Test
	void el_tamanio_se_acota_a_100() throws Exception {
		call("?size=500");

		assertEquals(100, pageableSent(null).getPageSize());
	}

	// ------------------------------------------------------------------ filtro por estado

	@Test
	void el_filtro_status_llega_al_negocio_como_el_estado_correspondiente() throws Exception {
		for (OrderStatus s : OrderStatus.values()) {
			org.mockito.Mockito.clearInvocations(orderBusiness);
			assertEquals(200, call("?status=" + s.getCode()).getStatus(), "status=" + s.getCode());
			pageableSent(s);
		}
	}

	@Test
	void un_status_con_espacios_se_tolera_y_uno_vacio_equivale_a_no_filtrar() throws Exception {
		// El valor se pasa como parametro (no en la URL) para que MockMvc no lo codifique dos veces
		assertEquals(200, mvc.perform(get("/api/v1/orders").param("status", " 2 ")
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken())).andReturn().getResponse().getStatus());
		pageableSent(OrderStatus.INITIAL_WEIGHING_REGISTERED);

		org.mockito.Mockito.clearInvocations(orderBusiness);
		assertEquals(200, call("?status=").getStatus());
		pageableSent(null);
	}

	@Test
	void un_status_invalido_da_400_con_el_json_estandar_y_no_llega_al_negocio() throws Exception {
		for (String malo : new String[] { "0", "5", "99", "-1", "abc", "2.5" }) {
			MockHttpServletResponse res = call("?status=" + malo);

			assertEquals(400, res.getStatus(), "status=" + malo);
			String body = res.getContentAsString();
			assertTrue(body.contains("\"code\":400") || body.contains("\"code\" : 400"), body);
			assertTrue(body.contains("status debe ser un número de 1 a 4"), body);
		}
		verify(orderBusiness, never()).list(any(), any());
	}

	// ------------------------------------------------------------------ orden elegible

	@Test
	void un_orden_por_un_campo_permitido_se_aplica_y_el_numero_de_orden_desempata() throws Exception {
		assertEquals(200, call("?sort=plannedLoadDateTime,asc").getStatus());

		Sort sort = pageableSent(null).getSort();
		assertEquals(Sort.Direction.ASC, sort.getOrderFor("plannedLoadDateTime").getDirection());
		assertEquals(Sort.Direction.DESC, sort.getOrderFor("orderNumber").getDirection(),
				"sin desempate la paginacion no es estable cuando hay valores repetidos");
		assertEquals(2, sort.stream().count());
	}

	@Test
	void ordenar_por_numero_de_orden_no_agrega_un_desempate_repetido() throws Exception {
		call("?sort=orderNumber,asc");

		Sort sort = pageableSent(null).getSort();
		assertEquals(1, sort.stream().count());
		assertEquals(Sort.Direction.ASC, sort.getOrderFor("orderNumber").getDirection());
	}

	@Test
	void todos_los_campos_de_la_lista_cerrada_se_aceptan() throws Exception {
		for (String campo : OrderRestController.SORTABLE) {
			org.mockito.Mockito.clearInvocations(orderBusiness);
			assertEquals(200, call("?sort=" + campo + ",desc").getStatus(), campo);
		}
	}

	@Test
	void el_orden_se_combina_con_el_filtro() throws Exception {
		call("?status=2&sort=status,desc");

		Pageable p = pageableSent(OrderStatus.INITIAL_WEIGHING_REGISTERED);
		assertEquals(Sort.Direction.DESC, p.getSort().getOrderFor("status").getDirection());
	}

	@Test
	void un_campo_fuera_de_la_lista_da_400_y_no_llega_al_negocio() throws Exception {
		for (String malo : new String[] { "truck.plate", "activationPassword", "id", "password", "customer" }) {
			MockHttpServletResponse res = call("?sort=" + malo);

			assertEquals(400, res.getStatus(), "sort=" + malo);
			assertTrue(res.getContentAsString().contains("sort inválido"), res.getContentAsString());
			assertTrue(res.getContentAsString().contains("Campos permitidos"), res.getContentAsString());
		}
		verify(orderBusiness, never()).list(any(), any());
	}

	@Test
	void un_solo_campo_invalido_entre_varios_rechaza_todo_el_pedido() throws Exception {
		assertEquals(400, call("?sort=preset,asc&sort=activationPassword,desc").getStatus());
		verify(orderBusiness, never()).list(any(), any());
	}

	@Test
	void el_pedido_sigue_exigiendo_token() throws Exception {
		assertEquals(401, mvc.perform(get("/api/v1/orders?status=2")).andReturn().getResponse().getStatus());
		assertNull(mvc.perform(get("/api/v1/orders")).andReturn().getResponse().getHeader(HttpHeaders.WWW_AUTHENTICATE));
	}
}
