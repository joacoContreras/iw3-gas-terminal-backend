package ar.edu.iua.iw3.business.exception;

import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Operacion no permitida aunque el usuario este autenticado (por ejemplo, la contrasena
 * de activacion es incorrecta). Extiende la convencion de la catedra (NotFound, Found,
 * Business) y se traduce a 403.
 */
@NoArgsConstructor
public class ForbiddenException extends Exception {

	private static final long serialVersionUID = 1L;

	@Builder
	public ForbiddenException(String message, Throwable ex) {
		super(message, ex);
	}

	public ForbiddenException(String message) {
		super(message);
	}

	public ForbiddenException(Throwable ex) {
		super(ex);
	}

}
