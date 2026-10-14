package ar.edu.iua.iw3.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.OrderStatusLog;
import ar.edu.iua.iw3.model.persistence.OrderRepository;
import ar.edu.iua.iw3.model.persistence.OrderStatusLogRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderBusiness implements IOrderBusiness {

	@Autowired
	private OrderRepository orderDAO;

	@Autowired
	private OrderStatusLogRepository statusLogDAO;

	@Override
	public List<Order> list() throws BusinessException {
		try {
			return orderDAO.findAllByOrderByOrderNumberDesc();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	public Order load(Integer orderNumber) throws NotFoundException, BusinessException {
		Optional<Order> r = findByNumber(orderNumber);
		if (r.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra la Orden numero=" + orderNumber).build();
		}
		return r.get();
	}

	@Override
	public List<OrderStatusLog> listStatusLog(Integer orderNumber) throws NotFoundException, BusinessException {
		Order order = load(orderNumber);
		try {
			return statusLogDAO.findByOrder(order);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	/** Consulta por numero de orden envolviendo el error tecnico en BusinessException. */
	private Optional<Order> findByNumber(Integer orderNumber) throws BusinessException {
		if (orderNumber == null) {
			return Optional.empty();
		}
		try {
			return orderDAO.findByOrderNumber(orderNumber);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}
}
