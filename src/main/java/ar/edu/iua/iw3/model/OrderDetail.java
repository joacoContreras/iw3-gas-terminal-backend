package ar.edu.iua.iw3.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
 * Una lectura valida del caudalimetro (punto 3): el historico de la carga. La cabecera
 * (Order) guarda solo los ultimos valores; aca queda cada lectura que no se descarto.
 */
@Entity
@Table(name = "order_details")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** LAZY y sin publicar: el detalle no necesita cargar ni mostrar la orden completa. */
	@JsonIgnore
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_order", nullable = false)
	private Order order;

	/** Masa acumulada (kg), creciente. */
	@Column(nullable = false)
	private double accumulatedMass;

	/** Densidad, entre 0 y 1 (sin incluirlos). */
	@Column(nullable = false)
	private double density;

	/** Temperatura (grados C). */
	@Column(nullable = false)
	private double temperature;

	/** Caudal (kg/h). */
	@Column(nullable = false)
	private double flowRate;

	/** Estampa de tiempo de la lectura. La columna no se llama "timestamp" para no usar la palabra del SQL. */
	@Column(name = "reading_timestamp", nullable = false)
	private LocalDateTime timestamp;
}
