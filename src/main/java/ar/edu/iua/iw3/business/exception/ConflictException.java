package ar.edu.iua.iw3.business.exception;

import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Estado incorrecto para la operacion pedida (por ejemplo, registrar la tara de
 * una orden que ya no esta pendiente de pesaje inicial). Extiende la convencion
 * de la catedra (NotFound, Found, Business) y se traduce a 409.
 */
@NoArgsConstructor
public class ConflictException extends Exception {

	private static final long serialVersionUID = 1L;

	@Builder
	public ConflictException(String message, Throwable ex) {
		super(message, ex);
	}

	public ConflictException(String message) {
		super(message);
	}

	public ConflictException(Throwable ex) {
		super(ex);
	}

}
