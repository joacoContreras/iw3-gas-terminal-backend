package ar.edu.iua.iw3.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import ar.edu.iua.iw3.business.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

/**
 * Envio de correos por SMTP (clase 9 de la catedra). Cambios respecto del original: el remitente sale de la
 * configuracion (spring.mail.username) y no de un dominio fijo, y el paquete de excepciones es el del TP.
 * Las credenciales llegan por variables de entorno (SMTP_USER y SMTP_PASS): no estan en el repositorio.
 */
@Component
@Slf4j
public class EmailBusiness {

	@Autowired
	private JavaMailSender emailSender;

	/** Cuenta SMTP configurada: es el remitente (Gmail la usa siempre como remitente real). */
	@Value("${spring.mail.username:}")
	private String username;

	/** Remitente de respaldo cuando no hay cuenta SMTP (JavaMail no puede enviar sin remitente). */
	@Value("${alarm.mail.from:alarmas@iw3.local}")
	private String defaultFrom;

	public void sendSimpleMessage(String to, String subject, String text) throws BusinessException {
		log.trace("Enviando mail subject={} a: {}", subject, to);
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(username != null && !username.isBlank() ? username : defaultFrom);
			message.setTo(to);
			message.setSubject(subject);
			message.setText(text);
			emailSender.send(message);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}
	}
}
