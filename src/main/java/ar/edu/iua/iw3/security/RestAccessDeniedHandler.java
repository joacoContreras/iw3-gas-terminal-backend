package ar.edu.iua.iw3.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import ar.edu.iua.iw3.util.IStandartResponseBusiness;
import ar.edu.iua.iw3.util.StandartResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Respuesta 403 cuando un usuario autenticado intenta consumir un endpoint que su rol no permite
 * (@PreAuthorize). La excepcion AccessDeniedException la lanza el proxy de seguridad antes de entrar
 * al metodo del controller, asi que su try/catch no la ve: la atrapa el filtro de seguridad y llega
 * aca. Responde con el mismo JSON estandar del resto de la API ({ message, code, devInfo }).
 *
 * El mensaje no revela que roles se exigen ni cuales tiene el usuario.
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

	static final String MESSAGE = "No tiene permisos para realizar esta operación";

	@Autowired
	private IStandartResponseBusiness response;

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse res, AccessDeniedException ex)
			throws IOException {
		StandartResponse body = response.build(HttpStatus.FORBIDDEN, ex, MESSAGE);
		res.setStatus(HttpStatus.FORBIDDEN.value());
		res.setContentType(MediaType.APPLICATION_JSON_VALUE);
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write("{\"message\":" + quote(body.getMessage()) + ",\"code\":" + body.getCode()
				+ ",\"devInfo\":" + quote(body.getDevInfo()) + "}");
	}

	/** Texto como literal JSON (null incluido); escapa lo que romperia el documento. */
	private static String quote(String text) {
		if (text == null) {
			return "null";
		}
		StringBuilder sb = new StringBuilder("\"");
		for (char c : text.toCharArray()) {
			switch (c) {
			case '"' -> sb.append("\\\"");
			case '\\' -> sb.append("\\\\");
			case '\n' -> sb.append("\\n");
			case '\r' -> sb.append("\\r");
			case '\t' -> sb.append("\\t");
			default -> sb.append(c < 0x20 ? String.format("\\u%04x", (int) c) : String.valueOf(c));
			}
		}
		return sb.append('"').toString();
	}
}
