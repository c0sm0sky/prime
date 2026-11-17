package pl.demo.prime;

import pl.demo.prime.data.CustomerStatus;
import lombok.Value;

import java.io.Serializable;

@Value
public class CustomerRow implements Serializable {
    Long id;
    String name;
    String email;
    CustomerStatus status;
}
