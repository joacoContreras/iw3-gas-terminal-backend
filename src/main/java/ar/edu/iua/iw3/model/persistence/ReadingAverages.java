package ar.edu.iua.iw3.model.persistence;

/**
 * Proyeccion con los promedios simples de las lecturas guardadas de una orden. Los nombres de los
 * getters tienen que coincidir con los alias de la consulta de OrderDetailRepository.averagesByOrder.
 * Si la orden no tiene lecturas, los tres valores son nulos.
 */
public interface ReadingAverages {

	Double getTemperature();

	Double getDensity();

	Double getFlowRate();
}
