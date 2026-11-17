package pl.demo.prime;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Value
public class RevenueRow implements Serializable {
    Long id;
    String description;
    BigDecimal amount;
    LocalDate receivedOn;
    Long orderId;
    String orderName;
    String customerName;
}
