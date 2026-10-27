package ar.edu.iua.iw3.integration.loader.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cuerpo del pedido de activacion del sistema de carga (punto 3): numero de orden y contrasena.
 * Son Integer (y no int) para distinguir un campo ausente (null, 400) de uno informado.
 */
@Getter
@Setter
@NoArgsConstructor
public class ActivationRequest {

	@Schema(description = "Número de la orden a activar", example = "90001")
	private Integer orderNumber;

	@Schema(description = "Contraseña de activación de 5 dígitos que recibió la balanza", example = "48213")
	private Integer password;

}
