package ar.edu.iua.iw3.integration.tms.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Respuesta a la balanza (TMS) al registrar la tara: la contraseña de activación de la orden. */
@Getter
@AllArgsConstructor
public class TareResponse {

	@Schema(description = "Contraseña de activación de 5 dígitos", example = "48213")
	private Integer activationPassword;

}
