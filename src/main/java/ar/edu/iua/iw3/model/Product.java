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

/**
 * Producto a cargar (gas liquido). Llega desde el sistema externo identificado
 * por su codigo externo.
 * La tabla se llama gas_products porque en las bases creadas con el demo de la
 * catedra ya existe una tabla "products" con otro esquema.
 */
@Entity
@Table(name = "gas_products")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 100, nullable = false, unique = true)
	private String externalCode;

	@Column(nullable = false)
	private String name;

	/** Descripcion: opcional segun la consigna. */
	@Column(length = 255)
	private String description;
}
