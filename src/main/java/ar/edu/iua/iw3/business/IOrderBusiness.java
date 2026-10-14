package ar.edu.iua.iw3.business;

import java.util.List;

import ar.edu.iua.iw3.business.exception.BusinessException;
import ar.edu.iua.iw3.business.exception.NotFoundException;
import ar.edu.iua.iw3.model.Order;
import ar.edu.iua.iw3.model.OrderStatusLog;

public interface IOrderBusiness {
	public List<Order> list() throws BusinessException;

	public Order load(Integer orderNumber) throws NotFoundException, BusinessException;

	public List<OrderStatusLog> listStatusLog(Integer orderNumber) throws NotFoundException, BusinessException;
}
