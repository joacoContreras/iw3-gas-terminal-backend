package ar.edu.iua.iw3.integration.tms.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cuerpo del pedido de la balanza (TMS) con el pesaje final de la orden (punto 5). Es double (y no Double),
 * como TareRequest: un campo ausente llega como 0.0 y se rechaza con 400. No lleva fecha: la estampa del
 * pesaje es la hora de recepcion del servidor.
 */
@Getter
@Setter
@NoArgsConstructor
public class FinalWeightRequest {

	@Schema(description = "Peso del camión cargado, en kg", example = "16490.0")
	private double weight;

}
