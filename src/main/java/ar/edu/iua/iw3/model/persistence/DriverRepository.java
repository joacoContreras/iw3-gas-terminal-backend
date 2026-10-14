package ar.edu.iua.iw3.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.model.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
	/** Busca por el codigo con el que el sistema externo identifica a la entidad. */
	Optional<Driver> findByExternalCode(String externalCode);
}
