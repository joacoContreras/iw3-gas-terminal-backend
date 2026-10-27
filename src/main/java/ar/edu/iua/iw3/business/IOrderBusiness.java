package ar.edu.iua.iw3.business;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ar.edu.iua.iw3.business.exception.BadRequestException;
import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.business.exception.ConflictException;
import ar.edu.iua.iw3.business.exception.FoundException;
import ar.edu.iua.iw3.business.exception.ForbiddenException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.OrderStatusLog;

public interface IOrderBusiness {
	public Page<Order> list(Pageable pageable) throws BusinessException;

	public Order load(Integer orderNumber) throws NotFoundException, BusinessException;

	public List<OrderStatusLog> listStatusLog(Integer orderNumber) throws NotFoundException, BusinessException;

	/**
	 * Recibe una orden del sistema externo (punto 1): valida, sincroniza las entidades
	 * por codigo externo, la crea en estado 1 y registra el cambio de estado.
	 * actor es el usuario o proceso que realiza la operacion.
	 */
	public Order add(Order order, String actor) throws FoundException, BadRequestException, BusinessException;

	/**
	 * Registra la tara enviada por la balanza (punto 2): guarda el peso, genera la contraseña
	 * de activación, pasa la orden del estado 1 al 2 y registra el cambio de estado.
	 */
	public Order registerTare(Integer orderNumber, double weight, String actor)
			throws NotFoundException, BadRequestException, ConflictException, BusinessException;

	/**
	 * Activa la carga con la contrasena de la orden (punto 3): comprueba que la orden exista,
	 * este en estado 2 y que la contrasena sea correcta, y guarda activationDateTime (solo la
	 * primera vez: es idempotente). Devuelve la orden para que el controller informe el preset.
	 * No cambia el estado ni escribe en el historial de estados.
	 */
	public Order activate(Integer orderNumber, Integer password)
			throws NotFoundException, BadRequestException, ConflictException, ForbiddenException, BusinessException;
}
