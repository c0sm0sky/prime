package pl.demo.prime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.demo.prime.data.Customer;
import pl.demo.prime.data.CustomerRepository;
import pl.demo.prime.data.CustomerStatus;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final CustomerRepository customers;

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customers.findAllByOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public long count() {
        return customers.count();
    }

    @Transactional
    public Customer create(String name, String email) {
        String validatedEmail = email(email);
        ensureEmailAvailable(validatedEmail, null);
        return customers.save(new Customer(customerName(name), validatedEmail, CustomerStatus.NEW));
    }

    @Transactional
    public void update(Long id, String name, String email, CustomerStatus status) {
        Customer customer = customers.findById(id).orElseThrow(() -> new BusinessRuleException("Klient już nie istnieje."));
        String validatedName = customerName(name);
        String validatedEmail = email(email);
        if (status == null) throw new BusinessRuleException("Wybierz prawidłowy status klienta.");
        ensureEmailAvailable(validatedEmail, id);
        customer.setName(validatedName);
        customer.setEmail(validatedEmail);
        customer.setStatus(status);
    }

    @Transactional
    public void delete(Long id) {
        customers.deleteById(id);
    }

    private String required(String value, String message) {
        if (value == null || value.isBlank()) throw new BusinessRuleException(message);
        String trimmed = value.trim();
        if (trimmed.length() > 255) throw new BusinessRuleException("Wartość może mieć maksymalnie 255 znaków.");
        return trimmed;
    }

    private String customerName(String value) {
        return required(value, "Podaj nazwę klienta.");
    }

    private String email(String value) {
        String normalized = required(value, "Podaj e-mail.");
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new BusinessRuleException("Podaj prawidłowy adres e-mail.");
        }
        return normalized.toLowerCase(java.util.Locale.ROOT);
    }

    private void ensureEmailAvailable(String email, Long currentId) {
        boolean alreadyUsed = currentId == null
                ? customers.existsByEmailIgnoreCase(email)
                : customers.existsByEmailIgnoreCaseAndIdNot(email, currentId);
        if (alreadyUsed) throw new BusinessRuleException("Ten adres e-mail jest już przypisany do klienta.");
    }
}
