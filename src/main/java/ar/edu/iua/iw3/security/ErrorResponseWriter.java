package ar.edu.iua.iw3.security;

import java.io.IOException;

import org.springframework.http.MediaType;

import ar.edu.iua.iw3.util.StandartResponse;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Escribe la respuesta de error estandar de la API ({ message, code, devInfo }) desde los filtros de seguridad,
 * donde no hay controller ni ResponseEntity. El JSON se arma a mano porque el proyecto no usa ObjectMapper y
 * Spring Boot 4 trae dos Jackson.
 */
final class ErrorResponseWriter {

	private ErrorResponseWriter() {
	}

	static void write(HttpServletResponse res, StandartResponse body) throws IOException {
		res.setStatus(body.getHttpStatus().value());
		res.setContentType(MediaType.APPLICATION_JSON_VALUE);
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write("{\"message\":" + quote(body.getMessage()) + ",\"code\":" + body.getCode()
				+ ",\"devInfo\":" + quote(body.getDevInfo()) + "}");
	}

	/** Texto como literal JSON (null incluido); escapa lo que romperia el documento. */
	static String quote(String text) {
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
