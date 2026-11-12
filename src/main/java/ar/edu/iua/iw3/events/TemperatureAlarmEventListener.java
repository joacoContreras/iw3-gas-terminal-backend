package ar.edu.iua.iw3.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import ar.edu.iua.iw3.business.IAlarmBusiness;
import ar.edu.iua.iw3.util.EmailBusiness;
import lombok.extern.slf4j.Slf4j;

/**
 * Atiende la alarma de temperatura enviando el correo a cada destinatario.
 *
 * - AFTER_COMMIT: el correo sale solo si la lectura y la alarma quedaron confirmadas (con rollback no sale nada).
 * - @Async: corre en otro hilo; la respuesta al caudalimetro no espera al servidor SMTP.
 * - Un fallo nunca se propaga: se registra y la lectura ya esta guardada. Se envia uno por destinatario para
 *   que una direccion rechazada no impida avisar a las demas.
 *
 * Importante: no se usa el multicaster asincrono global de la catedra, porque con el un
 * @TransactionalEventListener no se dispara (el listener correria fuera de la transaccion).
 */
@Component
@Slf4j
public class TemperatureAlarmEventListener {

	@Autowired
	private EmailBusiness emailBusiness;

	@Autowired
	private IAlarmBusiness alarmBusiness;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
	public void onTemperatureAlarm(TemperatureAlarmEvent event) {
		int sent = 0;
		for (String to : event.getRecipients()) {
			try {
				emailBusiness.sendSimpleMessage(to, event.getSubject(), event.getText());
				sent++;
			} catch (Exception e) {
				log.error("No se pudo enviar la alarma {} a {}: {}", event.getAlarmId(), to, e.getMessage());
			}
		}
		if (sent == 0) {
			log.warn("La alarma {} quedó sin aviso por correo: no se pudo enviar a ningún destinatario",
					event.getAlarmId());
			return;
		}
		try {
			alarmBusiness.markEmailSent(event.getAlarmId());
		} catch (Exception e) {
			log.error("No se pudo registrar el envío de la alarma {}: {}", event.getAlarmId(), e.getMessage());
		}
	}
}
