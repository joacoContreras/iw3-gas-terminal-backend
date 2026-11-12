package ar.edu.iua.iw3.model.persistence;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.iua.iw3.model.AlarmStatus;
import ar.edu.iua.iw3.model.TemperatureAlarm;
import jakarta.persistence.LockModeType;

@Repository
public interface TemperatureAlarmRepository extends JpaRepository<TemperatureAlarm, Long> {

	/** Regla de no reenvio: ¿la orden ya tiene una alarma en ese estado? (se usa con PENDING) */
	boolean existsByOrderNumberAndStatus(Integer orderNumber, AlarmStatus status);

	/** Una pagina de alarmas, de la mas reciente a la mas antigua; los filtros nulos no filtran. */
	@Query("select a from TemperatureAlarm a where (:status is null or a.status = :status) "
			+ "and (:orderNumber is null or a.orderNumber = :orderNumber) order by a.triggeredAt desc, a.id desc")
	Page<TemperatureAlarm> search(@Param("status") AlarmStatus status, @Param("orderNumber") Integer orderNumber,
			Pageable pageable);

	/**
	 * Registra la fecha de envio del correo con un UPDATE puntual (no lee y guarda la entidad): asi un aviso que
	 * termina justo cuando alguien acepta la alarma no pisa los datos de la aceptacion.
	 */
	@Modifying
	@Transactional
	@Query("update TemperatureAlarm a set a.emailSentAt = ?2 where a.id = ?1")
	int markEmailSent(Long id, LocalDateTime when);

	/** Busca la alarma bloqueando la fila: dos aceptaciones simultaneas se serializan (la segunda recibe 409). */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select a from TemperatureAlarm a where a.id = ?1")
	Optional<TemperatureAlarm> findByIdForUpdate(Long id);
}
