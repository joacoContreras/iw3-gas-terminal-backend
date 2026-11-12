package ar.edu.iua.iw3.auth.filters;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Clave con la que se firman y verifican los JWT (HS512). Se toma de la propiedad jwt.secret (variable de entorno
 * JWT_SECRET) y, si no esta definida, de AuthConstants.SECRET: asi el despliegue actual sigue funcionando sin
 * cambios y los tokens ya emitidos no se invalidan. Es el unico lugar que resuelve el valor, para que el login y el
 * filtro usen siempre la misma clave.
 *
 * Cambiar la clave invalida todos los tokens vigentes: los usuarios tienen que volver a iniciar sesion.
 */
@Component
@Slf4j
public class JwtSecret {

	/** Longitud minima recomendada para HS512 (la clave por defecto es mucho mas corta). */
	static final int RECOMMENDED_LENGTH = 32;

	@Value("${jwt.secret:}")
	private String configured;

	public boolean isDefault() {
		return configured == null || configured.isBlank();
	}

	public String value() {
		return isDefault() ? AuthConstants.SECRET : configured;
	}

	public byte[] bytes() {
		return value().getBytes(StandardCharsets.UTF_8);
	}

	@PostConstruct
	void warnIfWeak() {
		if (isDefault()) {
			log.warn("JWT: se usa la clave por defecto del repositorio. Definí la variable de entorno JWT_SECRET antes de un despliegue real");
		} else if (configured.length() < RECOMMENDED_LENGTH) {
			log.warn("JWT: la clave configurada tiene {} caracteres; se recomiendan al menos {}", configured.length(),
					RECOMMENDED_LENGTH);
		}
	}
}
