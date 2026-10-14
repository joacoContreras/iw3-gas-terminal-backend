package ar.edu.iua.iw3.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	/** Busca por el codigo con el que el sistema externo identifica a la entidad. */
	Optional<Customer> findByExternalCode(String externalCode);
}
