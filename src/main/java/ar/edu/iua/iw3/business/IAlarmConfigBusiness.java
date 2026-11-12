package ar.edu.iua.iw3.business;

import ar.edu.iua.iw3.business.exception.BadRequestException;
import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.model.AlarmConfig;

/** Interfaz de negocio de la configuracion de la alarma de temperatura (umbral y correos de destino). */
public interface IAlarmConfigBusiness {

	/** La configuracion vigente; si nadie la guardo todavia, el umbral por defecto y sin destinatarios. */
	AlarmConfig get() throws BusinessException;

	/**
	 * Reemplaza la configuracion. Valida (400): umbral presente y finito, lista de correos presente, cada
	 * correo con formato valido, sin repetidos y como maximo 20. Copia solo los campos permitidos.
	 */
	AlarmConfig update(AlarmConfig in) throws BadRequestException, BusinessException;
}
