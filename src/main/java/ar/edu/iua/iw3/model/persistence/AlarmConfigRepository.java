package ar.edu.iua.iw3.model.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.model.AlarmConfig;

/** La configuracion es una sola fila (AlarmConfig.SINGLE_ID): se lee y se guarda con findById y save. */
@Repository
public interface AlarmConfigRepository extends JpaRepository<AlarmConfig, Long> {
}
