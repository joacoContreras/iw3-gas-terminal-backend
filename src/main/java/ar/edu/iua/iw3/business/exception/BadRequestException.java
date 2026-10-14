package ar.edu.iua.iw3.business.exception;

import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Datos de entrada invalidos (campo obligatorio ausente, valor fuera de rango).
 * Extiende la convencion de la catedra (NotFound, Found, Business) para poder
 * responder 400 en lugar de 500.
 */
@NoArgsConstructor
public class BadRequestException extends Exception {

	private static final long serialVersionUID = 1L;

	@Builder
	public BadRequestException(String message, Throwable ex) {
		super(message, ex);
	}

	public BadRequestException(String message) {
		super(message);
	}

	public BadRequestException(Throwable ex) {
		super(ex);
	}

}
