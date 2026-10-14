package ar.edu.iua.iw3.integration.sap.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.iua.iw3.auth.User;
import ar.edu.iua.iw3.business.IOrderBusiness;
import ar.edu.iua.iw3.business.exception.BadRequestException;
import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.business.exception.FoundException;
import ar.edu.iua.iw3.controllers.BaseRestController;
import ar.edu.iua.iw3.controllers.Constants;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.util.IStandartResponseBusiness;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/** Punto 1 de la consigna: recepcion de la orden y sus entidades desde el sistema externo (SAP). */
@RestController
@RequestMapping(Constants.URL_INTEGRATION_SAP + "/orders")
@Tag(name = "Integración SAP", description = "Recepción de órdenes de carga desde el sistema externo")
public class OrderSapRestController extends BaseRestController {

	@Autowired
	private IOrderBusiness orderBusiness;

	@Autowired
	private IStandartResponseBusiness response;

	@Operation(summary = "Recibe una orden de carga",
			description = "Sincroniza camión, chofer, cliente y producto por código externo y crea la orden en estado 1 "
					+ "(pendiente de pesaje inicial). El estado y la fecha de recepción los asigna el backend.")
	@ApiResponse(responseCode = "201", description = "Orden creada; el header location apunta a la orden")
	@ApiResponse(responseCode = "302", description = "Ya existe una orden con ese número (convención de la cátedra)")
	@ApiResponse(responseCode = "400", description = "Datos obligatorios ausentes o fuera de rango")
	@PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> add(@RequestBody Order order) {
		try {
			User user = getUserLogged();
			Order res = orderBusiness.add(order, user == null ? null : user.getUsername());
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("location", Constants.URL_ORDERS + "/" + res.getOrderNumber());
			return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
		} catch (BadRequestException e) {
			return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()),
					HttpStatus.BAD_REQUEST);
		} catch (FoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), HttpStatus.FOUND);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
