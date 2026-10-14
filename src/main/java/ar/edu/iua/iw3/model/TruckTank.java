package ar.edu.iua.iw3.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Una cisterna del camion (parte del "cisternado" de la consigna). */
@Entity
@Table(name = "truck_tanks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TruckTank {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Numero de la cisterna dentro del camion (1, 2, ...). */
	@Column(name = "tank_number", nullable = false)
	private int number;

	/** Volumen de la cisterna en litros. */
	@Column(nullable = false)
	private double capacityLiters;
}
