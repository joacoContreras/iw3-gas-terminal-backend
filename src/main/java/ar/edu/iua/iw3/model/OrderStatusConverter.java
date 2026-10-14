package ar.edu.iua.iw3.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Guarda el estado como el numero 1..4 de la consigna (y no como ordinal 0..3
 * ni como texto). Se aplica solo a todo atributo de tipo OrderStatus.
 */
@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderStatus, Integer> {

	@Override
	public Integer convertToDatabaseColumn(OrderStatus status) {
		return status == null ? null : status.getCode();
	}

	@Override
	public OrderStatus convertToEntityAttribute(Integer code) {
		return code == null ? null : OrderStatus.fromCode(code);
	}
}
