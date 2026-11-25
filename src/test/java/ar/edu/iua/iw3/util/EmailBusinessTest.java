package ar.edu.iua.iw3.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import ar.edu.iua.iw3.business.exception.BusinessException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/** EmailBusiness: remitente, destinatario y traduccion de errores del servidor de correo, sin SMTP real. */
@ExtendWith(MockitoExtension.class)
class EmailBusinessTest {

	@Mock
	private JavaMailSender sender;

	@InjectMocks
	private EmailBusiness business;

	@BeforeEach
	void remitenteDeRespaldo() {
		ReflectionTestUtils.setField(business, "defaultFrom", "alarmas@iw3.local");
	}

	private SimpleMailMessage sent() {
		ArgumentCaptor<SimpleMailMessage> c = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(sender).send(c.capture());
		return c.getValue();
	}

	@Test
	void con_cuenta_smtp_el_remitente_es_esa_cuenta() throws Exception {
		ReflectionTestUtils.setField(business, "username", "planta@gmail.com");

		business.sendSimpleMessage("dest@x.com", "Asunto", "Texto");

		SimpleMailMessage m = sent();
		assertEquals("planta@gmail.com", m.getFrom());
		assertArrayEquals(new String[] { "dest@x.com" }, m.getTo());
		assertEquals("Asunto", m.getSubject());
		assertEquals("Texto", m.getText());
	}

	@Test
	void sin_cuenta_smtp_usa_el_remitente_de_respaldo_porque_javamail_no_envia_sin_remitente() throws Exception {
		ReflectionTestUtils.setField(business, "username", "");

		business.sendSimpleMessage("dest@x.com", "Asunto", "Texto");

		assertEquals("alarmas@iw3.local", sent().getFrom());
	}

	@Test
	void un_fallo_del_servidor_de_correo_se_informa_como_business_exception() {
		ReflectionTestUtils.setField(business, "username", "");
		doThrow(new MailSendException("sin conexion")).when(sender).send(any(SimpleMailMessage.class));

		BusinessException e = assertThrows(BusinessException.class,
				() -> business.sendSimpleMessage("dest@x.com", "Asunto", "Texto"));

		assertEquals("sin conexion", e.getMessage());
	}

	private MimeMessage mimeMessage() {
		MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
		when(sender.createMimeMessage()).thenReturn(message);
		return message;
	}

	private void textParts(Part part, Map<String, Part> parts) throws Exception {
		if (part.getContent() instanceof Multipart multipart) {
			for (int i = 0; i < multipart.getCount(); i++) textParts(multipart.getBodyPart(i), parts);
		} else if (part.isMimeType("text/plain")) parts.put("plain", part);
		else if (part.isMimeType("text/html")) parts.put("html", part);
	}

	@Test
	void correo_html_incluye_alternativa_de_texto_y_caracteres_utf8() throws Exception {
		ReflectionTestUtils.setField(business, "username", "planta@gmail.com");
		MimeMessage message = mimeMessage();

		business.sendHtmlMessage("dest@x.com", "Alerta de temperatura · Orden #5001", "Temperatura: 35,2 °C",
				"<p>Temperatura: <strong>35,2 °C</strong></p>");

		verify(sender).send(message);
		message.saveChanges();
		InternetAddress from = (InternetAddress) message.getFrom()[0];
		assertEquals("planta@gmail.com", from.getAddress());
		assertEquals("AetherLoad · Alertas operativas", from.getPersonal());
		assertEquals("dest@x.com", message.getAllRecipients()[0].toString());
		assertEquals("Alerta de temperatura · Orden #5001", message.getSubject());
		Map<String, Part> parts = new HashMap<>();
		textParts(message, parts);
		assertEquals(2, parts.size());
		assertEquals("Temperatura: 35,2 °C", parts.get("plain").getContent());
		assertEquals("<p>Temperatura: <strong>35,2 °C</strong></p>", parts.get("html").getContent());
		for (Part part : parts.values()) assertTrue(part.getContentType().toLowerCase().contains("charset=utf-8"));
	}

	@Test
	void correo_html_conserva_remitente_de_respaldo() throws Exception {
		MimeMessage message = mimeMessage();
		business.sendHtmlMessage("dest@x.com", "Alerta", "Texto", "<p>Texto</p>");
		assertEquals("alarmas@iw3.local", ((InternetAddress) message.getFrom()[0]).getAddress());
	}

	@Test
	void fallo_del_envio_html_se_informa_como_business_exception() {
		mimeMessage();
		doThrow(new MailSendException("sin conexion")).when(sender).send(any(MimeMessage.class));
		BusinessException e = assertThrows(BusinessException.class,
				() -> business.sendHtmlMessage("dest@x.com", "Alerta", "Texto", "<p>Texto</p>"));
		assertEquals("sin conexion", e.getMessage());
	}
}
