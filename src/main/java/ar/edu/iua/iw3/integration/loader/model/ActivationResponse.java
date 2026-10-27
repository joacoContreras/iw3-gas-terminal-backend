package ar.edu.iua.iw3.integration.loader.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Respuesta a la activacion del sistema de carga: el preset (kg a cargar) de la orden. */
@Getter
@AllArgsConstructor
public class ActivationResponse {

	@Schema(description = "Kilogramos a cargar (preset de la orden)", example = "15000.0")
	private Double preset;

}
