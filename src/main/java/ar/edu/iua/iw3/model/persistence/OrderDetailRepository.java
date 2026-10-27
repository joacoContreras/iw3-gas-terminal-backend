package ar.edu.iua.iw3.model.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
	/** Una pagina de las lecturas de una orden, de la mas antigua a la mas reciente (orden fijo por id). */
	@Query("select d from OrderDetail d where d.order = ?1 order by d.id asc")
	Page<OrderDetail> findByOrder(Order order, Pageable pageable);
}
