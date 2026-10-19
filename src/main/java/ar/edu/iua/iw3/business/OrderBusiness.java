package ar.edu.iua.iw3.business;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.iua.iw3.business.exception.BadRequestException;
import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.business.exception.FoundException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.model.Customer;
import ar.edu.iua.iw3.model.Driver;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.OrderStatus;
import ar.edu.iua.iw3.model.OrderStatusLog;
import ar.edu.iua.iw3.model.Product;
import ar.edu.iua.iw3.model.Truck;
import ar.edu.iua.iw3.model.TruckTank;
import ar.edu.iua.iw3.model.persistence.CustomerRepository;
import ar.edu.iua.iw3.model.persistence.DriverRepository;
import ar.edu.iua.iw3.model.persistence.OrderRepository;
import ar.edu.iua.iw3.model.persistence.OrderStatusLogRepository;
import ar.edu.iua.iw3.model.persistence.ProductRepository;
import ar.edu.iua.iw3.model.persistence.TruckRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderBusiness implements IOrderBusiness {

	@Autowired
	private OrderRepository orderDAO;

	@Autowired
	private OrderStatusLogRepository statusLogDAO;

	@Autowired
	private TruckRepository truckDAO;

	@Autowired
	private DriverRepository driverDAO;

	@Autowired
	private CustomerRepository customerDAO;

	@Autowired
	private ProductRepository productDAO;

	@Override
	public Page<Order> list(Pageable pageable) throws BusinessException {
		try {
			return orderDAO.findAllByOrderByOrderNumberDesc(pageable);
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

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Order add(Order order, String actor) throws FoundException, BadRequestException, BusinessException {
		List<String> errors = OrderValidator.validate(order);
		if (!errors.isEmpty()) {
			throw BadRequestException.builder().message(String.join("; ", errors)).build();
		}
		if (findByNumber(order.getOrderNumber()).isPresent()) {
			throw FoundException.builder().message("Se encontró la Orden numero=" + order.getOrderNumber()).build();
		}
		try {
			// Se copian solo los campos permitidos: estado y fecha de recepcion los fija el backend
			Order o = new Order();
			o.setOrderNumber(order.getOrderNumber());
			o.setPlannedLoadDateTime(order.getPlannedLoadDateTime());
			o.setPreset(order.getPreset());
			o.setTruck(syncTruck(order.getTruck()));
			o.setDriver(syncDriver(order.getDriver()));
			o.setCustomer(syncCustomer(order.getCustomer()));
			o.setProduct(syncProduct(order.getProduct()));
			o.setStatus(OrderStatus.PENDING_INITIAL_WEIGHING);
			o.setReceptionDateTime(LocalDateTime.now());
			o = orderDAO.save(o);
			logStatus(o, null, o.getStatus(), actor, "Orden recibida desde el sistema externo");
			return o;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	/** Registra un cambio de estado en el log de auditoria de la orden. */
	private void logStatus(Order order, OrderStatus from, OrderStatus to, String actor, String observations) {
		String who = (actor == null || actor.isBlank()) ? "sistema" : actor;
		statusLogDAO.save(new OrderStatusLog(null, order, from, to, LocalDateTime.now(), who, observations));
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

	/*
	 * Sincronizacion de las entidades externas: se busca por codigo externo y, si
	 * existe, se actualizan sus datos; si no, se crea. El sistema externo es la
	 * fuente de verdad, y nunca se usa un id que envie el cliente.
	 */

	private Truck syncTruck(Truck in) {
		String code = in.getExternalCode().trim();
		Truck t = truckDAO.findByExternalCode(code).orElseGet(Truck::new);
		t.setExternalCode(code);
		t.setPlate(in.getPlate().trim());
		t.setDescription(in.getDescription());
		List<TruckTank> incoming = new ArrayList<>();
		for (TruckTank tank : in.getTanks()) {
			incoming.add(new TruckTank(null, tank.getNumber(), tank.getCapacityLiters()));
		}
		incoming.sort(Comparator.comparingInt(TruckTank::getNumber));
		if (!sameTanks(t.getTanks(), incoming)) {
			t.getTanks().clear();
			t.getTanks().addAll(incoming);
		}
		return truckDAO.save(t);
	}

	/** Compara el cisternado actual con el recibido (ambos ordenados por numero). */
	private boolean sameTanks(List<TruckTank> current, List<TruckTank> incoming) {
		if (current.size() != incoming.size()) {
			return false;
		}
		for (int i = 0; i < current.size(); i++) {
			if (current.get(i).getNumber() != incoming.get(i).getNumber()
					|| current.get(i).getCapacityLiters() != incoming.get(i).getCapacityLiters()) {
				return false;
			}
		}
		return true;
	}

	private Driver syncDriver(Driver in) {
		String code = in.getExternalCode().trim();
		Driver d = driverDAO.findByExternalCode(code).orElseGet(Driver::new);
		d.setExternalCode(code);
		d.setName(in.getName().trim());
		d.setLastName(in.getLastName().trim());
		d.setDocument(in.getDocument());
		return driverDAO.save(d);
	}

	private Customer syncCustomer(Customer in) {
		String code = in.getExternalCode().trim();
		Customer c = customerDAO.findByExternalCode(code).orElseGet(Customer::new);
		c.setExternalCode(code);
		c.setCompanyName(in.getCompanyName().trim());
		c.setContact(in.getContact());
		return customerDAO.save(c);
	}

	private Product syncProduct(Product in) {
		String code = in.getExternalCode().trim();
		Product p = productDAO.findByExternalCode(code).orElseGet(Product::new);
		p.setExternalCode(code);
		p.setName(in.getName().trim());
		p.setDescription(in.getDescription());
		return productDAO.save(p);
	}
}
