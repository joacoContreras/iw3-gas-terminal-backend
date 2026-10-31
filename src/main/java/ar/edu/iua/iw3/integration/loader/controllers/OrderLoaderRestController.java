package ar.edu.iua.iw3.integration.loader.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.iua.iw3.auth.User;
import ar.edu.iua.iw3.business.DetailResult;
import ar.edu.iua.iw3.business.IOrderBusiness;
import ar.edu.iua.iw3.business.IOrderDetailBusiness;
import ar.edu.iua.iw3.business.exception.BadRequestException;
import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.business.exception.ConflictException;
import ar.edu.iua.iw3.business.exception.ForbiddenException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.controllers.BaseRestController;
import ar.edu.iua.iw3.controllers.Constants;
import ar.edu.iua.iw3.integration.loader.model.ActivationRequest;
import ar.edu.iua.iw3.integration.loader.model.ActivationResponse;
import ar.edu.iua.iw3.integration.loader.model.DetailRequest;
import ar.edu.iua.iw3.integration.loader.model.DetailResponse;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.util.IStandartResponseBusiness;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Puntos 3 y 4 de la consigna: el sistema de carga se activa con la contraseña, recibe el preset,
 * envía las lecturas del caudalímetro y cierra la orden.
 */
@RestController
@RequestMapping(Constants.URL_INTEGRATION_LOADER)
@Tag(name = "Integración sistema de carga", description = "Activación de la carga y recepción de lecturas del caudalímetro")
public class OrderLoaderRestController extends BaseRestController {

	@Autowired
	private IOrderBusiness orderBusiness;

	@Autowired
	private IOrderDetailBusiness orderDetailBusiness;

	@Autowired
	private IStandartResponseBusiness response;

	@Operation(summary = "Activa la carga de una orden",
			description = "Comprueba que la orden exista, esté en estado 2 y que la contraseña de activación sea correcta. "
					+ "Devuelve el preset (kg a cargar) y registra activationDateTime la primera vez. Es idempotente: "
					+ "activar otra vez con la contraseña correcta devuelve el mismo preset. No cambia el estado.")
	@ApiResponse(responseCode = "200", description = "Carga activada; devuelve el preset",
			content = @Content(schema = @Schema(implementation = ActivationResponse.class)))
	@ApiResponse(responseCode = "400", description = "Falta orderNumber o password")
	@ApiResponse(responseCode = "403", description = "La contraseña de activación es incorrecta")
	@ApiResponse(responseCode = "404", description = "La orden no existe")
	@ApiResponse(responseCode = "409", description = "La orden no está en estado 2")
	@PostMapping(value = "/activation", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> activate(@RequestBody ActivationRequest body) {
		try {
			Order order = orderBusiness.activate(body.getOrderNumber(), body.getPassword());
			return new ResponseEntity<>(new ActivationResponse(order.getPreset()), HttpStatus.OK);
		} catch (BadRequestException e) {
			return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()),
					HttpStatus.BAD_REQUEST);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (ConflictException e) {
			return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
		} catch (ForbiddenException e) {
			return new ResponseEntity<>(response.build(HttpStatus.FORBIDDEN, e, e.getMessage()), HttpStatus.FORBIDDEN);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(summary = "Registra una lectura del caudalímetro",
			description = "Recibe los datos en tiempo real del caudalímetro mientras la orden está en estado 2 y activada. "
					+ "Si la lectura es válida, la guarda en el histórico y actualiza la cabecera (últimos valores, inicio y fin de carga) con la hora de recepción del servidor. "
					+ "Si es inválida, la descarta sin guardarla y devuelve el motivo.")
	@ApiResponse(responseCode = "200", description = "Lectura procesada (guardada o descartada)",
			content = @Content(schema = @Schema(implementation = DetailResponse.class)))
	@ApiResponse(responseCode = "400", description = "Faltan campos obligatorios o formato inválido")
	@ApiResponse(responseCode = "404", description = "La orden no existe")
	@ApiResponse(responseCode = "409", description = "La orden no está en estado 2 o no está activada")
	@PostMapping(value = "/orders/{orderNumber}/details", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addDetail(@PathVariable Integer orderNumber, @RequestBody DetailRequest body) {
		try {
			DetailResult result = orderDetailBusiness.add(orderNumber, body.getAccumulatedMass(),
					body.getDensity(), body.getTemperature(), body.getFlowRate());
			return new ResponseEntity<>(new DetailResponse(result.isSaved(), result.getReason()), HttpStatus.OK);
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

	@Operation(summary = "Cierra la orden para carga",
			description = "Punto 4: pasa la orden del estado 2 al 3 (Cerrada para carga). Desde ese momento se rechazan "
					+ "nuevas lecturas del caudalímetro (409). Registra el cambio en el historial de estados con el usuario "
					+ "del token. No lleva cuerpo. No es idempotente: cerrar otra vez responde 409.")
	@ApiResponse(responseCode = "200", description = "Orden cerrada; devuelve la orden en estado 3",
			content = @Content(schema = @Schema(implementation = Order.class)))
	@ApiResponse(responseCode = "401", description = "Falta el token o es inválido")
	@ApiResponse(responseCode = "404", description = "La orden no existe")
	@ApiResponse(responseCode = "409", description = "La orden no está en estado 2 (por ejemplo, ya está cerrada)")
	@ApiResponse(responseCode = "500", description = "Error interno")
	@PostMapping(value = "/orders/{orderNumber}/close", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> close(@PathVariable Integer orderNumber) {
		try {
			User user = getUserLogged();
			Order order = orderBusiness.close(orderNumber, user == null ? null : user.getUsername());
			return new ResponseEntity<>(order, HttpStatus.OK);
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
