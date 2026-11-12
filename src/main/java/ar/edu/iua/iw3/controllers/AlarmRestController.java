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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.iua.iw3.auth.User;
import ar.edu.iua.iw3.business.IAlarmBusiness;
import ar.edu.iua.iw3.business.IAlarmConfigBusiness;
import ar.edu.iua.iw3.business.exception.BadRequestException;
import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.business.exception.ConflictException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.model.AlarmAckRequest;
import ar.edu.iua.iw3.model.AlarmConfig;
import ar.edu.iua.iw3.model.AlarmStatus;
import ar.edu.iua.iw3.model.TemperatureAlarm;
import ar.edu.iua.iw3.util.IStandartResponseBusiness;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/** Alarmas de temperatura (examen final, punto 3): consulta, aceptacion y configuracion de umbral y correos. */
@RestController
@RequestMapping(Constants.URL_ALARMS)
@Tag(name = "Alarmas", description = "Alarma de temperatura: consulta, aceptación y configuración")
public class AlarmRestController extends BaseRestController {

	private static final int MAX_PAGE_SIZE = 100;

	@Autowired
	private IAlarmBusiness alarmBusiness;

	@Autowired
	private IAlarmConfigBusiness configBusiness;

	@Autowired
	private IStandartResponseBusiness response;

	@Operation(summary = "Lista las alarmas paginadas, de la más reciente a la más antigua",
			description = "Filtros opcionales: status (PENDING o ACKNOWLEDGED) y orderNumber. También page (desde 0) y size "
					+ "(por defecto 20, máximo 100). El orden es fijo por fecha de disparo descendente.")
	@ApiResponse(responseCode = "200", description = "Página de alarmas (content + page)")
	@ApiResponse(responseCode = "400", description = "status inválido")
	@ApiResponse(responseCode = "401", description = "Falta el token o es inválido")
	@ApiResponse(responseCode = "403", description = "El rol del usuario no permite este endpoint")
	@ApiResponse(responseCode = "500", description = "Error interno")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_OPERADOR')")
	@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list(@RequestParam(required = false) String status,
			@RequestParam(required = false) Integer orderNumber,
			@ParameterObject @PageableDefault(size = 20) Pageable pageable) {
		AlarmStatus filter = null;
		if (status != null && !status.isBlank()) {
			try {
				filter = AlarmStatus.valueOf(status.trim().toUpperCase());
			} catch (IllegalArgumentException e) {
				return new ResponseEntity<>(
						response.build(HttpStatus.BAD_REQUEST, e, "status debe ser PENDING o ACKNOWLEDGED"),
						HttpStatus.BAD_REQUEST);
			}
		}
		try {
			// Se descarta el sort del cliente (el orden lo fija el repositorio) y se acota el tamaño
			int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);
			return new ResponseEntity<>(
					new PagedModel<>(alarmBusiness.list(filter, orderNumber, PageRequest.of(pageable.getPageNumber(), size))),
					HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(summary = "Acepta una alarma pendiente",
			description = "Solo la puede aceptar un usuario autorizado (ADMIN o SUPERVISOR). Queda registrado quién (sale del token), "
					+ "cuándo y la observación opcional. Una vez aceptada, una nueva superación del umbral en esa orden vuelve a alarmar.")
	@ApiResponse(responseCode = "200", description = "La alarma aceptada",
			content = @Content(schema = @Schema(implementation = TemperatureAlarm.class)))
	@ApiResponse(responseCode = "400", description = "Observación demasiado larga")
	@ApiResponse(responseCode = "401", description = "Falta el token o es inválido")
	@ApiResponse(responseCode = "403", description = "El rol del usuario no permite este endpoint")
	@ApiResponse(responseCode = "404", description = "La alarma no existe")
	@ApiResponse(responseCode = "409", description = "La alarma ya fue aceptada")
	@ApiResponse(responseCode = "500", description = "Error interno")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
	@PostMapping(value = "/{id}/acknowledge", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> acknowledge(@PathVariable Long id,
			@RequestBody(required = false) AlarmAckRequest body) {
		try {
			User user = getUserLogged();
			return new ResponseEntity<>(alarmBusiness.acknowledge(id, user == null ? null : user.getUsername(),
					body == null ? null : body.getObservation()), HttpStatus.OK);
		} catch (BadRequestException e) {
			return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()),
					HttpStatus.BAD_REQUEST);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (ConflictException e) {
			return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(summary = "Obtiene la configuración de la alarma",
			description = "Umbral de temperatura (°C) y correos de destino. Si nadie la configuró, rige el umbral por defecto y no hay destinatarios.")
	@ApiResponse(responseCode = "200", description = "La configuración vigente",
			content = @Content(schema = @Schema(implementation = AlarmConfig.class)))
	@ApiResponse(responseCode = "401", description = "Falta el token o es inválido")
	@ApiResponse(responseCode = "403", description = "El rol del usuario no permite este endpoint")
	@ApiResponse(responseCode = "500", description = "Error interno")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERVISOR')")
	@GetMapping(value = "/config", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getConfig() {
		try {
			return new ResponseEntity<>(configBusiness.get(), HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(summary = "Configura el umbral y los correos de destino",
			description = "Reemplaza la configuración. temperatureThreshold es obligatorio y finito; recipientEmails es obligatorio "
					+ "(puede ser una lista vacía), sin repetidos, con formato de correo válido y como máximo 20.")
	@ApiResponse(responseCode = "200", description = "La configuración guardada",
			content = @Content(schema = @Schema(implementation = AlarmConfig.class)))
	@ApiResponse(responseCode = "400", description = "Datos ausentes o inválidos")
	@ApiResponse(responseCode = "401", description = "Falta el token o es inválido")
	@ApiResponse(responseCode = "403", description = "El rol del usuario no permite este endpoint")
	@ApiResponse(responseCode = "500", description = "Error interno")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping(value = "/config", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateConfig(@RequestBody AlarmConfig body) {
		try {
			return new ResponseEntity<>(configBusiness.update(body), HttpStatus.OK);
		} catch (BadRequestException e) {
			return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()),
					HttpStatus.BAD_REQUEST);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
