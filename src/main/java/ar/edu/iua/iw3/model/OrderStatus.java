package ar.edu.iua.iw3.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Estados de la orden segun la consigna. Los cambios son secuenciales y
 * unidireccionales: una orden nunca retrocede a un estado anterior.
 */
public enum OrderStatus {
	PENDING_INITIAL_WEIGHING(1, "Pendiente de pesaje inicial"),
	INITIAL_WEIGHING_REGISTERED(2, "Con pesaje inicial registrado"),
	CLOSED_FOR_LOADING(3, "Cerrada para carga"),
	FINISHED(4, "Finalizada");

	private final int code;
	private final String description;

	OrderStatus(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/** Numero de estado (1 a 4): es el valor que se guarda en la base y se publica en el JSON. */
	@JsonValue
	public int getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public static OrderStatus fromCode(int code) {
		for (OrderStatus s : values()) {
			if (s.code == code) {
				return s;
			}
		}
		throw new IllegalArgumentException("Estado de orden inexistente: " + code);
	}
}
