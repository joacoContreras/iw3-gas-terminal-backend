package ar.edu.iua.iw3.model.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	Optional<Order> findByOrderNumber(Integer orderNumber);

	List<Order> findAllByOrderByOrderNumberDesc();
}
