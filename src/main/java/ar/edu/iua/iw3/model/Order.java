package ar.edu.iua.iw3.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Orden de carga: nucleo del proceso. Concentra los datos que llegan del sistema
 * externo (punto 1) y los eventos del ciclo de carga.
 * La tabla se llama "orders" porque ORDER es palabra reservada de SQL.
 */
@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Order {

	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Numero de orden: identificador unico (entero). */
	@Column(nullable = false, unique = true)
	private Integer orderNumber;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_truck", nullable = false)
	private Truck truck;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_driver", nullable = false)
	private Driver driver;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_customer", nullable = false)
	private Customer customer;

	@ManyToOne(optional = false)
	@JoinColumn(name = "id_product", nullable = false)
	private Product product;

	/** Fecha y hora de carga prevista (turno de carga). */
	@Column(nullable = false)
	private LocalDateTime plannedLoadDateTime;

	/** Cantidad de kilogramos a cargar. */
	@Column(nullable = false)
	private double preset;

	/** Estado actual (1 a 4). Lo asigna el backend; el sistema externo no lo envia. */
	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	@Column(nullable = false)
	private OrderStatus status;

	/** Momento en que se recibe la orden desde el sistema externo. */
	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	@Column(nullable = false)
	private LocalDateTime receptionDateTime;
}
