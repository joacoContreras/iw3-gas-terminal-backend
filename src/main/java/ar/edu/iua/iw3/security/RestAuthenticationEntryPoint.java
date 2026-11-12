package ar.edu.iua.iw3.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import ar.edu.iua.iw3.util.IStandartResponseBusiness;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Respuesta 401 cuando no hay token o es invalido o vencio. Responde con el JSON estandar de la API y, a proposito,
 * SIN el header WWW-Authenticate: con httpBasic el 401 traia "WWW-Authenticate: Basic" y el navegador abria su
 * dialogo nativo de usuario y contraseña cuando el token vencia, en lugar de dejar que el frontend vuelva al login.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	static final String MESSAGE = "Falta el token o es inválido";

	@Autowired
	private IStandartResponseBusiness response;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse res, AuthenticationException ex)
			throws IOException {
		ErrorResponseWriter.write(res, response.build(HttpStatus.UNAUTHORIZED, ex, MESSAGE));
	}
}
