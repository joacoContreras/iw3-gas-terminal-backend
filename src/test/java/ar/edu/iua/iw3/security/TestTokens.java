package ar.edu.iua.iw3.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import ar.edu.iua.iw3.auth.filters.AuthConstants;

/** Tokens JWT para las pruebas: iguales a los del login (sujeto, roles, email y vencimiento), firmados con la clave que se indique. */
final class TestTokens {

	private TestTokens() {
	}

	/** Token valido por 1 hora firmado con la clave por defecto del repositorio. */
	static String of(String... roles) {
		return of(AuthConstants.SECRET, 3_600_000L, roles);
	}

	static String of(String secret, long validForMillis, String... roles) {
		return JWT.create().withSubject("usuario-de-prueba").withClaim("roles", List.of(roles))
				.withClaim("email", "prueba@iw3.local").withExpiresAt(new Date(System.currentTimeMillis() + validForMillis))
				.sign(Algorithm.HMAC512(secret.getBytes(StandardCharsets.UTF_8)));
	}
}
