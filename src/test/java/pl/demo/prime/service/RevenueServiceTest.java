package pl.demo.prime.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.demo.prime.data.Customer;
import pl.demo.prime.data.CustomerStatus;
import pl.demo.prime.data.OrderEntity;
import pl.demo.prime.data.OrderStatus;
import pl.demo.prime.data.OrderRepository;
import pl.demo.prime.data.RevenueRecord;
import pl.demo.prime.data.RevenueRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RevenueServiceTest {
    @Mock
    private RevenueRepository revenues;

    @Mock
    private OrderRepository orders;

    @InjectMocks
    private RevenueService service;

    @Test
    void createsRevenueLinkedToOrder() {
        OrderEntity order = order("Wdrożenie");
        when(orders.findById(4L)).thenReturn(Optional.of(order));
        when(revenues.save(any(RevenueRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.create(4L, new BigDecimal("125.50"));

        verify(revenues).save(org.mockito.ArgumentMatchers.argThat(revenue ->
                revenue.getOrder() == order
                        && revenue.getDescription().equals("Wdrożenie")
                        && revenue.getAmount().compareTo(new BigDecimal("125.50")) == 0));
    }

    @Test
    void rejectsNonPositiveRevenue() {
        when(orders.findById(4L)).thenReturn(Optional.of(order("Usługa")));

        assertThrows(BusinessRuleException.class, () -> service.create(4L, BigDecimal.ZERO));

        verify(revenues, never()).save(any(RevenueRecord.class));
    }

    @Test
    void updatesRevenueAndKeepsItLinkedToOrder() {
        OrderEntity order = order("Nowa usługa");
        RevenueRecord revenue = new RevenueRecord(order, new BigDecimal("10.00"), LocalDate.now());
        when(revenues.findById(8L)).thenReturn(Optional.of(revenue));
        when(orders.findById(4L)).thenReturn(Optional.of(order));

        service.update(8L, 4L, new BigDecimal("88.25"));

        assertEquals("Nowa usługa", revenue.getDescription());
        assertEquals(0, revenue.getAmount().compareTo(new BigDecimal("88.25")));
        assertEquals(order, revenue.getOrder());
    }

    private OrderEntity order(String name) {
        return new OrderEntity(name, new Customer("Jan Kowalski", "jan@example.com", CustomerStatus.NEW), OrderStatus.NEW);
    }
}
