package ar.edu.iua.iw3.integration.loader.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.iua.iw3.business.IOrderBusiness;
import ar.edu.iua.iw3.business.exception.BadRequestException;
import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.business.exception.ConflictException;
import ar.edu.iua.iw3.business.exception.ForbiddenException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.controllers.BaseRestController;
import ar.edu.iua.iw3.controllers.Constants;
import ar.edu.iua.iw3.integration.loader.model.ActivationRequest;
import ar.edu.iua.iw3.integration.loader.model.ActivationResponse;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.util.IStandartResponseBusiness;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/** Punto 3 de la consigna: el sistema de carga se activa con la contraseña y recibe el preset. */
@RestController
@RequestMapping(Constants.URL_INTEGRATION_LOADER)
@Tag(name = "Integración sistema de carga", description = "Activación de la carga y recepción de lecturas del caudalímetro")
public class OrderLoaderRestController extends BaseRestController {

	@Autowired
	private IOrderBusiness orderBusiness;

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

}
