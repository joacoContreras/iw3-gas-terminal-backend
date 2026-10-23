package ar.edu.iua.iw3.integration.tms.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Cuerpo del pedido de la balanza (TMS) con la tara de la orden (punto 2). */
@Getter
@Setter
@NoArgsConstructor
public class TareRequest {

	@Schema(description = "Peso del camión vacío, en kg", example = "14500.0")
	private double weight;

}
