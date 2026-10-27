package ar.edu.iua.iw3.integration.loader.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Lectura enviada por el caudalimetro en tiempo real (punto 3).
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

	@Schema(description = "Estampa de tiempo de la lectura (opcional; si falta se usa la hora del servidor)",
			example = "2026-10-28T10:15:30")
	private LocalDateTime timestamp;

}
