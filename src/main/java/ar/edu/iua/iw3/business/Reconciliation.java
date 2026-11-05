package ar.edu.iua.iw3.business;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Conciliacion de una orden finalizada (punto 5): compara lo pesado por la balanza con lo medido
 * por el caudalimetro. No se guarda: se calcula cada vez, con una sola funcion para el POST del
 * pesaje final y el GET de la consulta. Los valores no se redondean.
 */
@Getter
@AllArgsConstructor
public class Reconciliation {

	@Schema(description = "Número de la orden", example = "90001")
	private Integer orderNumber;

	@Schema(description = "Pesaje inicial (tara), en kg", example = "14500.0")
	private Double tare;

	@Schema(description = "Pesaje final, en kg", example = "16490.0")
	private Double finalWeight;

	@Schema(description = "Producto cargado: última masa acumulada válida, en kg (0.0 si no hubo lecturas)", example = "2000.0")
	private Double loadedProduct;

	@Schema(description = "Neto por balanza: pesaje final − pesaje inicial, en kg", example = "1990.0")
	private Double scaleNet;

	@Schema(description = "Diferencia entre balanza y caudalímetro: neto por balanza − producto cargado, en kg", example = "-10.0")
	private Double difference;

	@Schema(description = "Promedio simple de temperatura de las lecturas guardadas (null si no hubo lecturas)", example = "21.0")
	private Double averageTemperature;

	@Schema(description = "Promedio simple de densidad de las lecturas guardadas (null si no hubo lecturas)", example = "0.5")
	private Double averageDensity;

	@Schema(description = "Promedio simple de caudal de las lecturas guardadas, en kg/h (null si no hubo lecturas)", example = "3300.0")
	private Double averageFlowRate;
}
