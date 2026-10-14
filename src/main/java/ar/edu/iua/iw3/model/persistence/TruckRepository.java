package ar.edu.iua.iw3.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.model.Truck;

@Repository
public interface TruckRepository extends JpaRepository<Truck, Long> {
	/** Busca por el codigo con el que el sistema externo identifica a la entidad. */
	Optional<Truck> findByExternalCode(String externalCode);
}
