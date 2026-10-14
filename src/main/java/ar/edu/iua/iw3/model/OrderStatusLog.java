package ar.edu.iua.iw3.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
 * Registro de auditoria de un cambio de estado de la orden: cuando ocurrio,
 * quien lo realizo (usuario o proceso) y una observacion opcional.
 */
@Entity
@Table(name = "order_status_logs")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderStatusLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne(optional = false)
	@JoinColumn(name = "id_order", nullable = false)
	private Order order;

	/** Estado anterior; es null en la creacion de la orden. */
	@Column(name = "from_status")
	private OrderStatus fromStatus;

	@Column(name = "to_status", nullable = false)
	private OrderStatus toStatus;

	@Column(nullable = false)
	private LocalDateTime dateTime;

	/** Usuario autenticado o proceso que realizo la transicion. */
	@Column(length = 100, nullable = false)
	private String actor;

	@Column(length = 255)
	private String observations;
}
