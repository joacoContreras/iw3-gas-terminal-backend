package ar.edu.iua.iw3.business;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.Truck;
import ar.edu.iua.iw3.model.TruckTank;

/**
 * Validacion de la orden que llega del sistema externo (punto 1).
 * Devuelve la lista de problemas encontrados; vacia significa que es valida.
 * La catedra no usa Bean Validation: las reglas se escriben a mano en la capa de negocio.
 */
final class OrderValidator {

	private OrderValidator() {
	}

	static List<String> validate(Order order) {
		List<String> errors = new ArrayList<>();
		if (order == null) {
			errors.add("Falta el cuerpo de la orden");
			return errors;
		}
		if (order.getOrderNumber() == null || order.getOrderNumber() <= 0) {
			errors.add("orderNumber debe ser un entero mayor que 0");
		}
		if (order.getPlannedLoadDateTime() == null) {
			errors.add("plannedLoadDateTime es obligatorio");
		}
		if (order.getPreset() <= 0) {
			errors.add("preset debe ser mayor que 0 (kg)");
		}
		validateTruck(order.getTruck(), errors);
		return errors;
	}

	private static void validateTruck(Truck t, List<String> errors) {
		if (t == null) {
			errors.add("truck es obligatorio");
			return;
		}
		text(t.getExternalCode(), "truck.externalCode", 100, errors);
		text(t.getPlate(), "truck.plate", 20, errors);
		if (t.getTanks() == null || t.getTanks().isEmpty()) {
			errors.add("truck.tanks debe tener al menos una cisterna");
			return;
		}
		Set<Integer> numbers = new HashSet<>();
		for (TruckTank tank : t.getTanks()) {
			if (tank == null || tank.getNumber() <= 0 || tank.getCapacityLiters() <= 0) {
				errors.add("cada cisterna requiere number y capacityLiters mayores que 0");
			} else if (!numbers.add(tank.getNumber())) {
				errors.add("truck.tanks tiene el numero de cisterna " + tank.getNumber() + " repetido");
			}
		}
	}


	/** Texto obligatorio, no vacio y de largo acotado al de la columna. */
	private static void text(String value, String field, int max, List<String> errors) {
		if (value == null || value.isBlank()) {
			errors.add(field + " es obligatorio");
		} else if (value.trim().length() > max) {
			errors.add(field + " supera el maximo de " + max + " caracteres");
		}
	}
}
