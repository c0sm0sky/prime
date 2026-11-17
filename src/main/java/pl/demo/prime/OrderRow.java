package pl.demo.prime;

import pl.demo.prime.data.OrderStatus;
import lombok.Value;

import java.io.Serializable;

@Value
public class OrderRow implements Serializable {
    Long id;
    String name;
    Long customerId;
    String customerName;
    OrderStatus status;
}
