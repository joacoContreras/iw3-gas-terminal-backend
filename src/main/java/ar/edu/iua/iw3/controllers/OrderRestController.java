package ar.edu.iua.iw3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.iua.iw3.business.IOrderBusiness;
import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.util.IStandartResponseBusiness;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/** Consulta de ordenes: sirve para verificar lo recibido y, mas adelante, para el frontend. */
@RestController
@RequestMapping(Constants.URL_ORDERS)
@Tag(name = "Órdenes", description = "Consulta de órdenes de carga")
public class OrderRestController extends BaseRestController {

	@Autowired
	private IOrderBusiness orderBusiness;

	@Autowired
	private IStandartResponseBusiness response;

	@Operation(summary = "Lista las órdenes, de la más reciente a la más antigua")
	@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list() {
		try {
			return new ResponseEntity<>(orderBusiness.list(), HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(summary = "Obtiene una orden por su número")
	@GetMapping(value = "/{orderNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> load(@PathVariable Integer orderNumber) {
		try {
			return new ResponseEntity<>(orderBusiness.load(orderNumber), HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

	@Operation(summary = "Historial de cambios de estado de una orden")
	@GetMapping(value = "/{orderNumber}/status-log", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> statusLog(@PathVariable Integer orderNumber) {
		try {
			return new ResponseEntity<>(orderBusiness.listStatusLog(orderNumber), HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}
}
