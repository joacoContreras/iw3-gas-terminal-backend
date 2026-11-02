package ar.edu.iua.iw3.controllers;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.iua.iw3.business.IOrderBusiness;
import ar.edu.iua.iw3.business.IOrderDetailBusiness;
import ar.edu.iua.iw3.business.Reconciliation;
import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.business.exception.ConflictException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.util.IStandartResponseBusiness;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/** Consulta de ordenes: sirve para verificar lo recibido y, mas adelante, para el frontend. */
@RestController
@RequestMapping(Constants.URL_ORDERS)
@Tag(name = "Órdenes", description = "Consulta de órdenes de carga")
public class OrderRestController extends BaseRestController {

	private static final int MAX_PAGE_SIZE = 100;

	@Autowired
	private IOrderBusiness orderBusiness;

	@Autowired
	private IOrderDetailBusiness orderDetailBusiness;

	@Autowired
	private IStandartResponseBusiness response;

	@Operation(summary = "Lista las órdenes paginadas, de la más reciente a la más antigua",
			description = "Parámetros opcionales: page (desde 0, por defecto 0) y size (por defecto 20, máximo 100). "
					+ "El orden es fijo por número de orden descendente.")
	@ApiResponse(responseCode = "200", description = "Página de órdenes (content + page)")
	@ApiResponse(responseCode = "401", description = "Falta el token o es inválido")
	@ApiResponse(responseCode = "500", description = "Error interno")
	@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list(@ParameterObject @PageableDefault(size = 20) Pageable pageable) {
		try {
			// Se descarta el sort del cliente (el orden lo fija el repositorio) y se acota el tamaño
			int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
			// PagedModel fija la estructura del JSON: content + page (size, number, totalElements, totalPages)
			return new ResponseEntity<>(
					new PagedModel<>(orderBusiness.list(PageRequest.of(pageable.getPageNumber(), size))),
					HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(summary = "Obtiene una orden por su número")
	@ApiResponse(responseCode = "200", description = "La orden",
			content = @Content(schema = @Schema(implementation = Order.class)))
	@ApiResponse(responseCode = "401", description = "Falta el token o es inválido")
	@ApiResponse(responseCode = "404", description = "La orden no existe")
	@ApiResponse(responseCode = "500", description = "Error interno")
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
	@ApiResponse(responseCode = "200", description = "Historial de la orden, del más antiguo al más reciente")
	@ApiResponse(responseCode = "401", description = "Falta el token o es inválido")
	@ApiResponse(responseCode = "404", description = "La orden no existe")
	@ApiResponse(responseCode = "500", description = "Error interno")
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

	@Operation(summary = "Lista paginada de las lecturas guardadas de una orden (histórico del caudalímetro)",
			description = "Parámetros opcionales: page (desde 0, por defecto 0) y size (por defecto 20, máximo 100). "
					+ "El orden es fijo por id ascendente (de la lectura más antigua a la más reciente).")
	@ApiResponse(responseCode = "200", description = "Página de lecturas (content + page)")
	@ApiResponse(responseCode = "401", description = "Falta el token o es inválido")
	@ApiResponse(responseCode = "404", description = "La orden no existe")
	@ApiResponse(responseCode = "500", description = "Error interno")
	@GetMapping(value = "/{orderNumber}/details", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> details(@PathVariable Integer orderNumber,
			@ParameterObject @PageableDefault(size = 20) Pageable pageable) {
		try {
			// Igual que list: se descarta el sort del cliente y se acota el tamaño
			int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
			return new ResponseEntity<>(
					new PagedModel<>(orderDetailBusiness.list(orderNumber, PageRequest.of(pageable.getPageNumber(), size))),
					HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		}
	}

	@Operation(summary = "Conciliación de una orden finalizada",
			description = "Punto 5: pesaje inicial y final, producto cargado, neto por balanza, diferencia con el caudalímetro "
					+ "y promedios de temperatura, densidad y caudal de las lecturas guardadas. Se calcula en cada consulta "
					+ "(no se guarda) y solo está disponible para órdenes en estado 4.")
	@ApiResponse(responseCode = "200", description = "Conciliación de la orden",
			content = @Content(schema = @Schema(implementation = Reconciliation.class)))
	@ApiResponse(responseCode = "401", description = "Falta el token o es inválido")
	@ApiResponse(responseCode = "404", description = "La orden no existe")
	@ApiResponse(responseCode = "409", description = "La orden no está en estado 4")
	@ApiResponse(responseCode = "500", description = "Error interno")
	@GetMapping(value = "/{orderNumber}/reconciliation", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> reconciliation(@PathVariable Integer orderNumber) {
		try {
			return new ResponseEntity<>(orderBusiness.reconciliation(orderNumber), HttpStatus.OK);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (ConflictException e) {
			return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
