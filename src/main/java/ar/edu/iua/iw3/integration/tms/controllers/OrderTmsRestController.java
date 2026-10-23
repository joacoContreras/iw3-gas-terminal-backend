package ar.edu.iua.iw3.integration.tms.controllers;

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
import ar.edu.iua.iw3.business.IOrderBusiness;
import ar.edu.iua.iw3.business.exception.BadRequestException;
import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.business.exception.ConflictException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.controllers.BaseRestController;
import ar.edu.iua.iw3.controllers.Constants;
import ar.edu.iua.iw3.integration.tms.model.TareRequest;
import ar.edu.iua.iw3.integration.tms.model.TareResponse;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.util.IStandartResponseBusiness;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/** Punto 2 de la consigna: la balanza (TMS) envia la tara y recibe la contraseña de activacion. */
@RestController
@RequestMapping(Constants.URL_INTEGRATION_TMS + "/orders")
@Tag(name = "Integración TMS (balanza)", description = "Pesaje inicial y final de la orden")
public class OrderTmsRestController extends BaseRestController {

	@Autowired
	private IOrderBusiness orderBusiness;

	@Autowired
	private IStandartResponseBusiness response;

	@Operation(summary = "Registra la tara (pesaje inicial)",
			description = "Guarda el peso del camión vacío y la fecha del pesaje, genera la contraseña de activación "
					+ "de 5 dígitos y pasa la orden del estado 1 al 2. Solo para órdenes pendientes de pesaje inicial.")
	@ApiResponse(responseCode = "200", description = "Tara registrada; devuelve la contraseña de activación",
			content = @Content(schema = @Schema(implementation = TareResponse.class)))
	@ApiResponse(responseCode = "400", description = "Tara inválida (debe ser mayor que 0)")
	@ApiResponse(responseCode = "404", description = "La orden no existe")
	@ApiResponse(responseCode = "409", description = "La orden no está pendiente de pesaje inicial")
	@PostMapping(value = "/{orderNumber}/tare", consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> registerTare(@PathVariable Integer orderNumber, @RequestBody TareRequest body) {
		try {
			User user = getUserLogged();
			Order order = orderBusiness.registerTare(orderNumber, body.getWeight(),
					user == null ? null : user.getUsername());
			return new ResponseEntity<>(new TareResponse(order.getActivationPassword()), HttpStatus.OK);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (BadRequestException e) {
			return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()),
					HttpStatus.BAD_REQUEST);
		} catch (ConflictException e) {
			return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
