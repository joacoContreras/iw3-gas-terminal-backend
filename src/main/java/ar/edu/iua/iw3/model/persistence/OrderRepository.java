package ar.edu.iua.iw3.model.persistence;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	Optional<Order> findByOrderNumber(Integer orderNumber);

	/** Una pagina de ordenes, de la mas reciente a la mas antigua. */
	Page<Order> findAllByOrderByOrderNumberDesc(Pageable pageable);
}
