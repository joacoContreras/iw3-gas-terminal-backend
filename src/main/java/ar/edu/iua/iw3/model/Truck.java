package ar.edu.iua.iw3.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Camion. Llega desde el sistema externo identificado por su codigo externo. */
@Entity
@Table(name = "trucks")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Truck {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Codigo con el que el sistema externo identifica al camion. */
	@Column(length = 100, nullable = false, unique = true)
	private String externalCode;

	@Column(length = 20, nullable = false)
	private String plate;

	@Column(length = 255)
	private String description;

	/** Cisternado: detalle del volumen de cada cisterna. */
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_truck", nullable = false)
	@OrderBy("number ASC")
	private List<TruckTank> tanks = new ArrayList<>();
}
