package ar.edu.iua.iw3.business;

import java.security.SecureRandom;

/**
 * Genera la contrasena de activacion de la orden (punto 2): un entero de 5
 * digitos, de 10000 a 99999. Se usa SecureRandom y no Random porque la
 * contrasena es un secreto entre la balanza y el sistema de carga.
 */
final class ActivationPasswordGenerator {

	private static final int MIN = 10000;
	private static final int RANGE = 90000; // 10000..99999

	private static final SecureRandom RANDOM = new SecureRandom();

	private ActivationPasswordGenerator() {
	}

	static int generate() {
		return MIN + RANDOM.nextInt(RANGE);
	}
}
