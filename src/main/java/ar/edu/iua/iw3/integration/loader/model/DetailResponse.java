package ar.edu.iua.iw3.integration.loader.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Respuesta a la recepcion de una lectura del caudalimetro.
 * Siempre HTTP 200, indicando si fue guardada o descartada con su motivo (D3).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailResponse {

	@Schema(description = "Indica si la lectura fue guardada (true) o descartada (false)", example = "true")
	private boolean saved;

	@Schema(description = "Motivo del descarte en caso de lectura invalida (null si fue guardada)",
			example = "la densidad (density) debe estar entre 0 y 1, sin incluirlos")
	private String reason;

}
