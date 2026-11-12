package ar.edu.iua.iw3.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Cuerpo opcional de la aceptacion de una alarma. El usuario NO viaja aca: sale del token. */
@Getter
@Setter
@NoArgsConstructor
public class AlarmAckRequest {

	@Schema(description = "Observación opcional de quien acepta (máximo 500 caracteres)", example = "Se revisó el sensor")
	private String observation;
}
