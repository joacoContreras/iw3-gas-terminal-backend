package ar.edu.iua.iw3.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

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
}
