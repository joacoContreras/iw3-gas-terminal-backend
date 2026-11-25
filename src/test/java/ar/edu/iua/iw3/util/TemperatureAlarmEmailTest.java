package ar.edu.iua.iw3.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ar.edu.iua.iw3.model.AlarmStatus;
import ar.edu.iua.iw3.model.Customer;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.Product;
import ar.edu.iua.iw3.model.TemperatureAlarm;
import ar.edu.iua.iw3.model.Truck;

class TemperatureAlarmEmailTest {

	private static Order order(String customerName) {
		Order order = new Order();
		Truck truck = new Truck();
		truck.setPlate("AB123CD");
		Customer customer = new Customer();
		customer.setCompanyName(customerName);
		Product product = new Product();
		product.setName("GLP");
		order.setTruck(truck);
		order.setCustomer(customer);
		order.setProduct(product);
		return order;
	}

	private static TemperatureAlarm alarm() {
		return new TemperatureAlarm(7L, null, 5001, 35.2, 30.0, AlarmStatus.PENDING,
				LocalDateTime.of(2026, 11, 25, 14, 30, 0), null, null, null, null);
	}

	@Test
	void ambas_versiones_incluyen_datos_operativos_hora_argentina_y_accion_requerida() {
		TemperatureAlarmEmail email = TemperatureAlarmEmail.from(order("Gas del Sur SA"), alarm());
		assertEquals("[AetherLoad] Alerta de temperatura | Orden #5001", email.subject());
		for (String body : new String[] { email.text(), email.html() }) {
			for (String value : new String[] { "5001", "35,2 °C", "30,0 °C", "AB123CD", "Gas del Sur SA", "GLP",
					"25/11/2026 14:30:00", "UTC−03:00", "Acción requerida", "Pendiente de aceptación",
					"mientras la alarma permanezca pendiente", "No responda a este correo." }) {
				assertTrue(body.contains(value), value);
			}
		}
	}

	@Test
	void datos_del_cliente_se_muestran_como_texto_y_no_como_html() {
		String name = "Gas & Sur <script>alert(1)</script>";
		TemperatureAlarmEmail email = TemperatureAlarmEmail.from(order(name), alarm());
		assertTrue(email.text().contains(name));
		assertTrue(email.html().contains("Gas &amp; Sur &lt;script&gt;alert(1)&lt;/script&gt;"));
		assertFalse(email.html().contains("<script>"));
	}
}
