package pl.demo.prime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.demo.prime.data.Customer;
import pl.demo.prime.data.CustomerRepository;
import pl.demo.prime.data.OrderEntity;
import pl.demo.prime.data.OrderRepository;
import pl.demo.prime.data.OrderStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orders;
    private final CustomerRepository customers;

    @Transactional(readOnly = true)
    public List<OrderEntity> findAll() {
        return orders.findAllByOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public long count() {
        return orders.count();
    }

    @Transactional
    public OrderEntity create(String name, Long customerId) {
        return orders.save(new OrderEntity(required(name), customer(customerId), OrderStatus.NEW));
    }

    @Transactional
    public void update(Long id, String name, Long customerId, OrderStatus status) {
        OrderEntity order = orders.findById(id).orElseThrow(() -> new BusinessRuleException("Zamówienie już nie istnieje."));
        String validatedName = required(name);
        Customer validatedCustomer = customer(customerId);
        if (status == null) throw new BusinessRuleException("Wybierz prawidłowy status zamówienia.");
        order.setName(validatedName);
        order.setCustomer(validatedCustomer);
        order.setStatus(status);
    }

    @Transactional
    public void delete(Long id) {
        orders.deleteById(id);
    }

    private Customer customer(Long id) {
        return customers.findById(id).orElseThrow(() -> new BusinessRuleException("Wybierz istniejącego klienta."));
    }

    private String required(String value) {
        return required(value, "Podaj nazwę zamówienia.");
    }

    private String required(String value, String message) {
        if (value == null || value.isBlank()) throw new BusinessRuleException(message);
        String trimmed = value.trim();
        if (trimmed.length() > 255) throw new BusinessRuleException("Wartość może mieć maksymalnie 255 znaków.");
        return trimmed;
    }
}
