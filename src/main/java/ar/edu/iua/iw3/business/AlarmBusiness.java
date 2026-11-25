package ar.edu.iua.iw3.business;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.iua.iw3.business.exception.BadRequestException;
import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.business.exception.ConflictException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.events.TemperatureAlarmEvent;
import ar.edu.iua.iw3.model.AlarmConfig;
import ar.edu.iua.iw3.model.AlarmStatus;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.TemperatureAlarm;
import ar.edu.iua.iw3.model.persistence.TemperatureAlarmRepository;
import ar.edu.iua.iw3.util.TemperatureAlarmEmail;
import lombok.extern.slf4j.Slf4j;

/**
 * Ciclo de vida de las alarmas de temperatura: crearlas al superar el umbral (una sola pendiente por orden),
 * avisar por correo mediante un evento y aceptarlas.
 */
@Service
@Slf4j
public class AlarmBusiness implements IAlarmBusiness {

	static final int MAX_OBSERVATION = 500;

	@Autowired
	private TemperatureAlarmRepository alarmDAO;

	@Autowired
	private IAlarmConfigBusiness configBusiness;

	@Autowired
	private ApplicationEventPublisher publisher;

	@Override
	public Optional<TemperatureAlarm> evaluate(Order order, double temperature) throws BusinessException {
		try {
			AlarmConfig config = configBusiness.get();
			double threshold = config.getTemperatureThreshold();
			// Se alarma solo cuando la temperatura SUPERA el umbral (igual no alarma)
			if (!(temperature > threshold)) {
				return Optional.empty();
			}
			// No reenvio: mientras la orden tenga una alarma pendiente no se crea ni se avisa otra
			if (alarmDAO.existsByOrderNumberAndStatus(order.getOrderNumber(), AlarmStatus.PENDING)) {
				return Optional.empty();
			}
			TemperatureAlarm alarm = alarmDAO.saveAndFlush(new TemperatureAlarm(null, order, order.getOrderNumber(),
					temperature, threshold, AlarmStatus.PENDING, LocalDateTime.now(), null, null, null, null));
			log.warn("Alarma de temperatura: orden {} a {} °C (umbral {} °C)", alarm.getOrderNumber(), temperature,
					threshold);

			List<String> recipients = List.copyOf(config.getRecipientEmails());
			if (recipients.isEmpty()) {
				log.warn("La alarma {} no se avisa por correo: no hay destinatarios configurados", alarm.getId());
			} else {
				// El listener atiende el evento DESPUES del commit y en otro hilo: la lectura no espera al correo
				TemperatureAlarmEmail email = TemperatureAlarmEmail.from(order, alarm);
				publisher.publishEvent(new TemperatureAlarmEvent(this, alarm.getId(), recipients, email.subject(),
						email.text(), email.html()));
			}
			return Optional.of(alarm);
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	public Page<TemperatureAlarm> list(AlarmStatus status, Integer orderNumber, Pageable pageable)
			throws BusinessException {
		try {
			return alarmDAO.search(status, orderNumber, pageable);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public TemperatureAlarm acknowledge(Long id, String actor, String observation)
			throws BadRequestException, NotFoundException, ConflictException, BusinessException {
		// Comprobaciones fuera del try: si no, el 400, el 404 y el 409 saldrian como 500
		if (actor == null || actor.isBlank()) {
			throw BadRequestException.builder().message("No se pudo identificar al usuario que acepta la alarma").build();
		}
		if (observation != null && observation.length() > MAX_OBSERVATION) {
			throw BadRequestException.builder()
					.message("observation admite como máximo " + MAX_OBSERVATION + " caracteres").build();
		}
		TemperatureAlarm alarm = findForUpdate(id);
		if (alarm.getStatus() == AlarmStatus.ACKNOWLEDGED) {
			throw ConflictException.builder().message("La alarma " + id + " ya fue aceptada por "
					+ alarm.getAcknowledgedBy()).build();
		}
		try {
			alarm.setStatus(AlarmStatus.ACKNOWLEDGED);
			alarm.setAcknowledgedBy(actor);
			alarm.setAcknowledgedAt(LocalDateTime.now());
			alarm.setObservation(observation == null || observation.isBlank() ? null : observation.trim());
			return alarmDAO.saveAndFlush(alarm);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	private TemperatureAlarm findForUpdate(Long id) throws NotFoundException, BusinessException {
		Optional<TemperatureAlarm> r;
		try {
			r = alarmDAO.findByIdForUpdate(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		// El 404 va fuera del try: si no, saldria como 500
		if (r.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra la Alarma id=" + id).build();
		}
		return r.get();
	}

	@Override
	public void markEmailSent(Long alarmId) throws BusinessException {
		try {
			alarmDAO.markEmailSent(alarmId, LocalDateTime.now());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}
}
