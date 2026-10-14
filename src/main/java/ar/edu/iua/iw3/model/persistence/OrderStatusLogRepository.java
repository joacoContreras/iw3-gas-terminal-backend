package ar.edu.iua.iw3.model.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.OrderStatusLog;

@Repository
public interface OrderStatusLogRepository extends JpaRepository<OrderStatusLog, Long> {
	/** Historial de estados de una orden, del mas antiguo al mas reciente. */
	@Query("select l from OrderStatusLog l where l.order = ?1 order by l.id asc")
	List<OrderStatusLog> findByOrder(Order order);
}
