package ar.edu.iua.iw3.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * Alarma de temperatura (examen final): se crea cuando una lectura valida supera el umbral y queda
 * PENDING hasta que un usuario autorizado la acepta. Mientras una orden tiene una alarma pendiente no se
 * crea ni se avisa otra de esa orden.
 */
@Entity
@Table(name = "temperature_alarms")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TemperatureAlarm {

	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** LAZY y sin publicar: la alarma se identifica hacia afuera por orderNumber. */
	@JsonIgnore
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_order", nullable = false)
	private Order order;

	/** Numero de la orden (el numero es inmutable, por eso se guarda tambien aca para consultar sin join). */
	@Column(nullable = false)
	private Integer orderNumber;

	/** Temperatura de la lectura que disparo la alarma (grados C). */
	@Column(nullable = false)
	private double temperature;

	/** Umbral vigente cuando se disparo (grados C). */
	@Column(nullable = false)
	private double threshold;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private AlarmStatus status;

	/** Momento en que se disparo (hora del servidor). */
	@Column(nullable = false)
	private LocalDateTime triggeredAt;

	/** Momento en que salio el correo; nulo si todavia no salio o si no se pudo enviar. */
	@Column
	private LocalDateTime emailSentAt;

	/** Usuario que acepto la alarma (sale del token). Nulo mientras este pendiente. */
	@Column(length = 100)
	private String acknowledgedBy;

	@Column
	private LocalDateTime acknowledgedAt;

	/** Observacion opcional de quien acepto. */
	@Column(length = 500)
	private String observation;
}
