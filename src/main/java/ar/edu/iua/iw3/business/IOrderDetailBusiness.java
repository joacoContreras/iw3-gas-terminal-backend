package ar.edu.iua.iw3.business;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ar.edu.iua.iw3.business.exception.BadRequestException;
import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.business.exception.ConflictException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.model.OrderDetail;

/** Interfaz de negocio para el procesamiento de lecturas del caudalimetro. */
public interface IOrderDetailBusiness {

	/**
	 * Procesa una lectura: si es valida la guarda y actualiza la cabecera;
	 * si es invalida la descarta devolviendo el motivo del descarte.
	 */
	DetailResult add(Integer orderNumber, Double accumulatedMass, Double density, Double temperature,
			Double flowRate, LocalDateTime timestamp)
			throws NotFoundException, BadRequestException, ConflictException, BusinessException;

	/** Una pagina de las lecturas guardadas de una orden, por id ascendente (decision 11). */
	Page<OrderDetail> list(Integer orderNumber, Pageable pageable) throws NotFoundException, BusinessException;

}
