package ar.edu.iua.iw3.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Pruebas unitarias de las reglas de descarte del caudalimetro (JUnit 5 puro). */
class OrderDetailValidatorTest {

	@Test
	void discardReasons_validReadings_returnsEmptyList() {
		assertTrue(OrderDetailValidator.discardReasons(100.0, 0.52, 3600.0, null).isEmpty());
		assertTrue(OrderDetailValidator.discardReasons(250.0, 0.52, 3600.0, 100.0).isEmpty());
	}

	@Test
	void discardReasons_equalMass_isValid() {
		assertTrue(OrderDetailValidator.discardReasons(250.0, 0.52, 3600.0, 250.0).isEmpty());
	}

	@Test
	void discardReasons_flowRateZeroOrNegative_discards() {
		List<String> zero = OrderDetailValidator.discardReasons(100.0, 0.5, 0.0, null);
		assertEquals("el caudal (flowRate) debe ser mayor que 0", zero.get(0));
		List<String> negative = OrderDetailValidator.discardReasons(100.0, 0.5, -5.0, null);
		assertEquals("el caudal (flowRate) debe ser mayor que 0", negative.get(0));
	}

	@Test
	void discardReasons_accumulatedMassZeroOrNegative_discards() {
		List<String> zero = OrderDetailValidator.discardReasons(0.0, 0.5, 1000.0, null);
		assertEquals("la masa acumulada (accumulatedMass) debe ser mayor que 0", zero.get(0));
		List<String> negative = OrderDetailValidator.discardReasons(-1.0, 0.5, 1000.0, null);
		assertEquals("la masa acumulada (accumulatedMass) debe ser mayor que 0", negative.get(0));
	}

	@Test
	void discardReasons_massLessThanPrevious_discards() {
		List<String> reasons = OrderDetailValidator.discardReasons(200.0, 0.5, 1000.0, 250.0);
		assertEquals("la masa acumulada es menor que la anterior (250.0)", reasons.get(0));
	}

	@Test
	void discardReasons_densityOutOfRange_discards() {
		assertEquals("la densidad (density) debe estar entre 0 y 1, sin incluirlos",
				OrderDetailValidator.discardReasons(100.0, 0.0, 1000.0, null).get(0));
		assertEquals("la densidad (density) debe estar entre 0 y 1, sin incluirlos",
				OrderDetailValidator.discardReasons(100.0, 1.0, 1000.0, null).get(0));
		assertEquals("la densidad (density) debe estar entre 0 y 1, sin incluirlos",
				OrderDetailValidator.discardReasons(100.0, 1.5, 1000.0, null).get(0));
		assertEquals("la densidad (density) debe estar entre 0 y 1, sin incluirlos",
				OrderDetailValidator.discardReasons(100.0, -0.2, 1000.0, null).get(0));
	}

	@Test
	void discardReasons_nanValues_discards() {
		assertEquals("el caudal (flowRate) debe ser mayor que 0",
				OrderDetailValidator.discardReasons(100.0, 0.5, Double.NaN, null).get(0));
		assertEquals("la masa acumulada (accumulatedMass) debe ser mayor que 0",
				OrderDetailValidator.discardReasons(Double.NaN, 0.5, 1000.0, null).get(0));
		assertEquals("la densidad (density) debe estar entre 0 y 1, sin incluirlos",
				OrderDetailValidator.discardReasons(100.0, Double.NaN, 1000.0, null).get(0));
	}

	@Test
	void discardReasons_infiniteValues_discardsWithoutError() {
		String reason = "los valores no pueden ser infinitos";
		assertEquals(List.of(reason), OrderDetailValidator.discardReasons(Double.POSITIVE_INFINITY, 0.5, 1000.0, null));
		assertEquals(List.of(reason), OrderDetailValidator.discardReasons(100.0, 0.5, Double.POSITIVE_INFINITY, null));
		assertEquals(List.of(reason), OrderDetailValidator.discardReasons(100.0, Double.NEGATIVE_INFINITY, 1000.0, null));
		assertEquals(List.of(reason),
				OrderDetailValidator.discardReasons(100.0, 0.5, Double.POSITIVE_INFINITY, 1000.0, null));
	}

	@Test
	void discardReasons_multipleInvalidConditions_accumulatesAllReasons() {
		List<String> reasons = OrderDetailValidator.discardReasons(-10.0, 1.5, -5.0, null);
		assertEquals(3, reasons.size());
		assertTrue(reasons.contains("el caudal (flowRate) debe ser mayor que 0"));
		assertTrue(reasons.contains("la masa acumulada (accumulatedMass) debe ser mayor que 0"));
		assertTrue(reasons.contains("la densidad (density) debe estar entre 0 y 1, sin incluirlos"));
	}

}
