package ar.edu.iua.iw3.business;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.iua.iw3.business.exception.BadRequestException;
import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.business.exception.ConflictException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.OrderDetail;
import ar.edu.iua.iw3.model.OrderStatus;
import ar.edu.iua.iw3.model.persistence.OrderDetailRepository;
import ar.edu.iua.iw3.model.persistence.OrderRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de recepcion y persistencia de lecturas del caudalimetro.
 * Administra el descarte de lecturas y la actualizacion atomica de la cabecera.
 */
@Service
@Slf4j
public class OrderDetailBusiness implements IOrderDetailBusiness {

	@Autowired
	private IOrderBusiness orderBusiness;

	@Autowired
	private OrderRepository orderDAO;

	@Autowired
	private OrderDetailRepository orderDetailDAO;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public DetailResult add(Integer orderNumber, Double accumulatedMass, Double density, Double temperature,
			Double flowRate, LocalDateTime timestamp)
			throws NotFoundException, BadRequestException, ConflictException, BusinessException {
		// (a) Buscar la orden: si no existe lanza NotFoundException (404)
		Order order = orderBusiness.load(orderNumber);

		// (b) Algun campo obligatorio ausente -> 400
		if (accumulatedMass == null || density == null || temperature == null || flowRate == null) {
			throw BadRequestException.builder()
					.message("accumulatedMass, density, temperature y flowRate son obligatorios").build();
		}

		// (c) Estado distinto de 2 o no activada -> 409
		if (order.getStatus() != OrderStatus.INITIAL_WEIGHING_REGISTERED || order.getActivationDateTime() == null) {
			throw ConflictException.builder()
					.message("La orden " + orderNumber + " no acepta lecturas: debe estar en estado 2 y activada (estado "
							+ order.getStatus().getCode() + ", activada=" + (order.getActivationDateTime() != null) + ")")
					.build();
		}

		// (d) Validar motivos de descarte contra la masa anterior de la cabecera
		List<String> reasons = OrderDetailValidator.discardReasons(accumulatedMass, density, flowRate,
				order.getLastAccumulatedMass());
		if (!reasons.isEmpty()) {
			return DetailResult.discarded(String.join("; ", reasons));
		}

		// Lectura valida: persistir detalle y actualizar cabecera
		try {
			LocalDateTime estampa = (timestamp != null) ? timestamp : LocalDateTime.now();
			OrderDetail detail = new OrderDetail(null, order, accumulatedMass, density, temperature, flowRate, estampa);
			orderDetailDAO.save(detail);

			order.setLastAccumulatedMass(accumulatedMass);
			order.setLastDensity(density);
			order.setLastTemperature(temperature);
			order.setLastFlowRate(flowRate);
			if (order.getLoadingStartDateTime() == null) {
				order.setLoadingStartDateTime(estampa);
			}
			order.setLoadingEndDateTime(estampa);
			orderDAO.saveAndFlush(order);

			return DetailResult.saved();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

}
