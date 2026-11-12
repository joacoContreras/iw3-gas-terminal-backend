package ar.edu.iua.iw3.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Configuracion de la alarma de temperatura: umbral y correos de destino. Es una unica fila (id fijo):
 * la configuracion es global. Mientras nadie la guarde rige el umbral por defecto y no hay destinatarios.
 *
 * Sin constructor con todos los campos a proposito: asi Jackson enlaza por setters y un campo ausente
 * llega como null (se rechaza con 400) en vez de fallar antes de validar.
 */
@Entity
@Table(name = "alarm_config")
@NoArgsConstructor
@Getter
@Setter
public class AlarmConfig {

	public static final Long SINGLE_ID = 1L;

	@JsonIgnore
	@Id
	private Long id = SINGLE_ID;

	/** Umbral en grados C: se alarma cuando la temperatura lo supera estrictamente. Double para distinguir ausente. */
	@Column(nullable = false)
	private Double temperatureThreshold;

	/**
	 * Correos de destino de la alarma (puede estar vacio: la alarma se crea pero no se envia nada).
	 * Sin valor inicial a proposito: si el JSON no trae el campo debe llegar como null y rechazarse con 400; con una
	 * lista vacia por defecto, un PUT que solo trae el umbral borraria los destinatarios sin avisar.
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "alarm_config_recipients", joinColumns = @JoinColumn(name = "id_config"))
	@Column(name = "email", nullable = false, length = 255)
	private List<String> recipientEmails;
}
