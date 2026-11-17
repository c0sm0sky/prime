package pl.demo.prime.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.demo.prime.data.Customer;
import pl.demo.prime.data.CustomerStatus;
import pl.demo.prime.data.CustomerRepository;
import pl.demo.prime.data.OrderEntity;
import pl.demo.prime.data.OrderRepository;
import pl.demo.prime.data.OrderStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orders;

    @Mock
    private CustomerRepository customers;

    @InjectMocks
    private OrderService service;

    @Test
    void createsOrderLinkedToCustomer() {
        Customer customer = new Customer("Jan Kowalski", "jan@example.com", CustomerStatus.NEW);
        when(customers.findById(3L)).thenReturn(Optional.of(customer));
        when(orders.save(any(OrderEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderEntity order = service.create("  Wdrożenie  ", 3L);

        assertEquals("Wdrożenie", order.getName());
        assertEquals(customer, order.getCustomer());
        assertEquals(OrderStatus.NEW, order.getStatus());
    }

    @Test
    void delegatesDeleteSoDatabaseCanCascadeRevenues() {
        service.delete(5L);

        verify(orders).deleteById(5L);
    }

    @Test
    void rejectsOrderWithoutExistingCustomer() {
        assertThrows(BusinessRuleException.class, () -> service.create("Usługa", 404L));

        verify(orders, never()).save(any(OrderEntity.class));
    }
}
