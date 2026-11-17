package pl.demo.prime.data;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter
public class CustomerStatusConverter implements AttributeConverter<CustomerStatus, String> {
    @Override
    public String convertToDatabaseColumn(CustomerStatus status) {
        return status == null ? null : status.getLabel();
    }

    @Override
    public CustomerStatus convertToEntityAttribute(String value) {
        return value == null ? null : Arrays.stream(CustomerStatus.values())
                .filter(status -> status.getLabel().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown customer status: " + value));
    }
}
