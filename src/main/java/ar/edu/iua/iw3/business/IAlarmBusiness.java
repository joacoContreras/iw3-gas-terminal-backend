package ar.edu.iua.iw3.business;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ar.edu.iua.iw3.business.exception.BadRequestException;
import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.business.exception.ConflictException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.model.AlarmStatus;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.TemperatureAlarm;

/** Interfaz de negocio de las alarmas de temperatura (examen final, punto 3). */
public interface IAlarmBusiness {

	/**
	 * Evalua la temperatura de una lectura valida ya guardada: si supera el umbral y la orden no tiene una
	 * alarma pendiente, crea la alarma y publica el evento que dispara el correo. Debe llamarse dentro de la
	 * transaccion de la lectura (que ya tiene bloqueada la orden): asi la comprobacion y el alta no se cruzan.
	 *
	 * @return la alarma creada, o vacio si no corresponde alarmar
	 */
	Optional<TemperatureAlarm> evaluate(Order order, double temperature) throws BusinessException;

	/** Una pagina de alarmas, de la mas reciente a la mas antigua; status y orderNumber son filtros opcionales. */
	Page<TemperatureAlarm> list(AlarmStatus status, Integer orderNumber, Pageable pageable) throws BusinessException;

	/**
	 * Acepta una alarma pendiente. El usuario sale del token (actor), no del cuerpo. Orden de comprobaciones:
	 * 400 (actor u observacion invalidos) → 404 (no existe) → 409 (ya estaba aceptada).
	 */
	TemperatureAlarm acknowledge(Long id, String actor, String observation)
			throws BadRequestException, NotFoundException, ConflictException, BusinessException;

	/** Registra que el correo de la alarma salio (fecha y hora de envio). */
	void markEmailSent(Long alarmId) throws BusinessException;
}
