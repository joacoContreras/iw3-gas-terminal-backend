package ar.edu.iua.iw3.integration.loader.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Lectura enviada por el caudalimetro en tiempo real (punto 3).
 * La estampa de tiempo la asigna el servidor al recibirla; un timestamp enviado por el cliente se ignora.
 * Campos numericos en Double para distinguir un campo ausente (400) de uno informado (D4).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailRequest {

	@Schema(description = "Masa acumulada (kg), creciente", example = "1250.5")
	private Double accumulatedMass;

	@Schema(description = "Densidad del producto, entre 0 y 1 sin incluirlos", example = "0.52")
	private Double density;

	@Schema(description = "Temperatura actual (grados C)", example = "18.4")
	private Double temperature;

	@Schema(description = "Caudal masico (kg/h), mayor que 0", example = "3600.0")
	private Double flowRate;

}
