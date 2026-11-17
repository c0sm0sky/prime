package pl.demo.prime.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.demo.prime.data.Customer;
import pl.demo.prime.data.CustomerRepository;
import pl.demo.prime.data.CustomerStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository customers;

    @InjectMocks
    private CustomerService service;

    @Test
    void createsCustomerWithTrimmedValuesAndInitialStatus() {
        when(customers.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer customer = service.create("  Jan Kowalski  ", "  jan@example.com  ");

        assertEquals("Jan Kowalski", customer.getName());
        assertEquals("jan@example.com", customer.getEmail());
        assertEquals(CustomerStatus.NEW, customer.getStatus());
    }

    @Test
    void rejectsBlankCustomerName() {
        assertThrows(BusinessRuleException.class, () -> service.create("  ", "jan@example.com"));

        verify(customers, never()).save(any(Customer.class));
    }

    @Test
    void rejectsInvalidEmail() {
        assertThrows(BusinessRuleException.class, () -> service.create("Jan Kowalski", "to-nie-email"));

        verify(customers, never()).save(any(Customer.class));
    }

    @Test
    void rejectsEmailAlreadyUsedByAnotherCustomer() {
        when(customers.existsByEmailIgnoreCase("jan@example.com")).thenReturn(true);

        assertThrows(BusinessRuleException.class,
                () -> service.create("Jan Kowalski", "JAN@example.com"));

        verify(customers, never()).save(any(Customer.class));
    }

    @Test
    void delegatesDeleteSoDatabaseCanCascadeDependents() {
        service.delete(7L);

        verify(customers).deleteById(7L);
    }

    @Test
    void updatesExistingCustomer() {
        Customer customer = new Customer("Old Name", "old@example.com", CustomerStatus.NEW);
        when(customers.findById(7L)).thenReturn(Optional.of(customer));

        service.update(7L, "New Name", "new@example.com", CustomerStatus.ACTIVE);

        assertEquals("New Name", customer.getName());
        assertEquals("new@example.com", customer.getEmail());
        assertEquals(CustomerStatus.ACTIVE, customer.getStatus());
    }
}
