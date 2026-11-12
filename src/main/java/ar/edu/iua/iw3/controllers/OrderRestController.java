package ar.edu.iua.iw3.controllers;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.iua.iw3.business.IOrderBusiness;
import ar.edu.iua.iw3.business.IOrderDetailBusiness;
import ar.edu.iua.iw3.business.Reconciliation;
import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.business.exception.ConflictException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.OrderStatus;
import ar.edu.iua.iw3.util.IStandartResponseBusiness;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

	/** Campos por los que se puede ordenar el listado: lista cerrada, el cliente no elige columnas arbitrarias. */
	static final Set<String> SORTABLE = Set.of("orderNumber", "plannedLoadDateTime", "receptionDateTime", "status",
			"preset");

	@Autowired
	private IOrderBusiness orderBusiness;

	@Autowired
	private IOrderDetailBusiness orderDetailBusiness;

	@Autowired
	private IStandartResponseBusiness response;

	@Operation(summary = "Lista las órdenes paginadas, con filtro por estado y orden elegible",
			description = "Parámetros opcionales: status (1 a 4), page (desde 0, por defecto 0), size (por defecto 20, máximo 100) y "
					+ "sort con el formato campo,dirección (por ejemplo sort=plannedLoadDateTime,desc). Campos permitidos: orderNumber, "
					+ "plannedLoadDateTime, receptionDateTime, status y preset. Sin sort: número de orden descendente (la más reciente primero). "
					+ "Si se ordena por otro campo, el número de orden descendente desempata para que la paginación sea estable.")
	@ApiResponse(responseCode = "200", description = "Página de órdenes (content + page)")
	@ApiResponse(responseCode = "400", description = "status fuera de 1 a 4 o sort por un campo no permitido")
	@ApiResponse(responseCode = "401", description = "Falta el token o es inválido")
	@ApiResponse(responseCode = "500", description = "Error interno")
	@ApiResponse(responseCode = "403", description = "El rol del usuario no permite este endpoint")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_OPERADOR')")
	@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list(
			@Parameter(description = "Filtra por estado de la orden (1 a 4)") @RequestParam(required = false) String status,
			@ParameterObject @PageableDefault(size = 20) Pageable pageable) {
		// Validaciones fuera del try: si no, el 400 saldria como 500
		OrderStatus filter = null;
		if (status != null && !status.isBlank()) {
			try {
				filter = OrderStatus.fromCode(Integer.parseInt(status.trim()));
			} catch (IllegalArgumentException e) {
				return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, "status debe ser un número de 1 a 4"),
						HttpStatus.BAD_REQUEST);
			}
		}
		List<String> invalid = pageable.getSort().stream().map(Sort.Order::getProperty)
				.filter(p -> !SORTABLE.contains(p)).toList();
		if (!invalid.isEmpty()) {
			return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, null,
					"sort inválido: " + invalid + ". Campos permitidos: " + new TreeSet<>(SORTABLE)), HttpStatus.BAD_REQUEST);
		}
		try {
			// Se acota el tamaño; sin sort, la orden mas reciente primero; con sort, el numero de orden desempata
			int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
			Sort sort = pageable.getSort().isSorted() ? pageable.getSort() : Sort.by(Sort.Direction.DESC, "orderNumber");
			if (sort.getOrderFor("orderNumber") == null) {
				sort = sort.and(Sort.by(Sort.Direction.DESC, "orderNumber"));
			}
			// PagedModel fija la estructura del JSON: content + page (size, number, totalElements, totalPages)
			return new ResponseEntity<>(
					new PagedModel<>(orderBusiness.list(filter, PageRequest.of(pageable.getPageNumber(), size, sort))),
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
	@ApiResponse(responseCode = "403", description = "El rol del usuario no permite este endpoint")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_OPERADOR')")
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
	@ApiResponse(responseCode = "403", description = "El rol del usuario no permite este endpoint")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_OPERADOR')")
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
	@ApiResponse(responseCode = "403", description = "El rol del usuario no permite este endpoint")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_OPERADOR')")
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
	@ApiResponse(responseCode = "403", description = "El rol del usuario no permite este endpoint")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_OPERADOR')")
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
