package ar.edu.iua.iw3.model;

/**
 * Estado de una alarma de temperatura: pendiente hasta que un usuario autorizado la acepta.
 * Se guarda y se publica por su nombre (PENDING / ACKNOWLEDGED).
 */
public enum AlarmStatus {
	PENDING, ACKNOWLEDGED
}
