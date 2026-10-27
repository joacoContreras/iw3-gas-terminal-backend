package ar.edu.iua.iw3.business;

import lombok.Getter;

/**
 * Resultado del procesamiento de una lectura del caudalimetro.
 * Indica si fue guardada o descartada junto con el motivo en caso de descarte.
 */
@Getter
public class DetailResult {

	private final boolean saved;
	private final String reason;

	private DetailResult(boolean saved, String reason) {
		this.saved = saved;
		this.reason = reason;
	}

	public static DetailResult saved() {
		return new DetailResult(true, null);
	}

	public static DetailResult discarded(String reason) {
		return new DetailResult(false, reason);
	}

}
