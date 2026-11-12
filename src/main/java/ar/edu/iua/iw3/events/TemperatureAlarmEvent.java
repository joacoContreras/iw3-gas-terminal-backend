package ar.edu.iua.iw3.events;

import java.util.List;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

/**
 * Evento de Spring (patron Observador, clase 9 de la catedra): se publica cuando se crea una alarma de
 * temperatura. Lleva todo lo necesario para avisar (destinatarios, asunto y texto), asi el que lo atiende
 * no tiene que volver a leer la base ni tocar entidades fuera de la transaccion.
 */
@Getter
public class TemperatureAlarmEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	private final Long alarmId;
	private final List<String> recipients;
	private final String subject;
	private final String text;

	public TemperatureAlarmEvent(Object source, Long alarmId, List<String> recipients, String subject, String text) {
		super(source);
		this.alarmId = alarmId;
		this.recipients = recipients;
		this.subject = subject;
		this.text = text;
	}
}
