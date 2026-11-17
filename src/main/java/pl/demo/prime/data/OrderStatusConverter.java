package pl.demo.prime.data;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {
    @Override
    public String convertToDatabaseColumn(OrderStatus status) {
        return status == null ? null : status.getLabel();
    }

    @Override
    public OrderStatus convertToEntityAttribute(String value) {
        return value == null ? null : Arrays.stream(OrderStatus.values())
                .filter(status -> status.getLabel().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown order status: " + value));
    }
}
