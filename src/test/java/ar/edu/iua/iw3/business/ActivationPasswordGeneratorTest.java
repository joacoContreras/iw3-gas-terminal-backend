package ar.edu.iua.iw3.business;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/** Prueba unitaria: no levanta Spring ni necesita la base de datos. */
class ActivationPasswordGeneratorTest {

	private static final int ATTEMPTS = 10_000;

	@Test
	void generatesFiveDigitPasswords() {
		for (int i = 0; i < ATTEMPTS; i++) {
			int password = ActivationPasswordGenerator.generate();
			assertTrue(password >= 10000 && password <= 99999, "Fuera de rango: " + password);
		}
	}

	@Test
	void doesNotAlwaysReturnTheSameValue() {
		Set<Integer> distinct = new HashSet<>();
		for (int i = 0; i < ATTEMPTS; i++) {
			distinct.add(ActivationPasswordGenerator.generate());
		}
		assertTrue(distinct.size() > 1, "Siempre devolvio el mismo valor");
	}
}
