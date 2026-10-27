package ar.edu.iua.iw3.model.persistence;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

import ar.edu.iua.iw3.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	Optional<Order> findByOrderNumber(Integer orderNumber);

	/**
	 * Igual que findByOrderNumber pero bloquea la fila hasta el fin de la transaccion. Serializa las lecturas
	 * simultaneas de una misma orden: sin el bloqueo, los INSERT del detalle y el UPDATE de la cabecera se
	 * cruzan y MySQL aborta una con deadlock.
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select o from Order o where o.orderNumber = ?1")
	Optional<Order> findByOrderNumberForUpdate(Integer orderNumber);

	/** Una pagina de ordenes, de la mas reciente a la mas antigua. */
	Page<Order> findAllByOrderByOrderNumberDesc(Pageable pageable);
}
