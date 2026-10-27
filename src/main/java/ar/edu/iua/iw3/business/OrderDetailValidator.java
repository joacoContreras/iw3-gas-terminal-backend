package ar.edu.iua.iw3.business;

import java.util.ArrayList;
import java.util.List;

/**
 * Reglas de descarte de lecturas del caudalimetro (punto 3 de la consigna).
 * Clase final con visibilidad de paquete: funcion pura sin efectos secundarios.
 */
final class OrderDetailValidator {

	private OrderDetailValidator() {
	}

	/**
	 * Evalua todas las condiciones de descarte de una lectura del caudalimetro.
	 * Devuelve la lista con los motivos encontrados (vacia si la lectura es valida).
	 * Se usa la forma !(x > 0) para que valores NaN tambien resulten descartados.
	 *
	 * @param accumulatedMass masa acumulada recibida (kg)
	 * @param density         densidad recibida (kg/m3)
	 * @param flowRate        caudal recibido (kg/h)
	 * @param previousMass    ultima masa acumulada valida de la cabecera (null si es la primera)
	 * @return lista de motivos de descarte; vacia si la lectura es valida
	 */
	static List<String> discardReasons(double accumulatedMass, double density, double flowRate, Double previousMass) {
		List<String> reasons = new ArrayList<>();

		if (!(flowRate > 0)) {
			reasons.add("el caudal (flowRate) debe ser mayor que 0");
		}
		if (!(accumulatedMass > 0)) {
			reasons.add("la masa acumulada (accumulatedMass) debe ser mayor que 0");
		}
		if (previousMass != null && accumulatedMass < previousMass) {
			reasons.add("la masa acumulada es menor que la anterior (" + previousMass + ")");
		}
		if (!(density > 0 && density < 1)) {
			reasons.add("la densidad (density) debe estar entre 0 y 1, sin incluirlos");
		}

		return reasons;
	}

}
