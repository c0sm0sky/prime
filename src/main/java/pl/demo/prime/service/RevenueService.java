package pl.demo.prime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.demo.prime.data.OrderEntity;
import pl.demo.prime.data.OrderRepository;
import pl.demo.prime.data.RevenueRecord;
import pl.demo.prime.data.RevenueRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueService {
    private final RevenueRepository revenues;
    private final OrderRepository orders;

    @Transactional(readOnly = true)
    public List<RevenueRecord> findAll() {
        return revenues.findAllByOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public BigDecimal total() {
        return revenues.sumAmounts();
    }

    @Transactional
    public void create(Long orderId, BigDecimal amount) {
        OrderEntity order = order(orderId);
        validateAmount(amount);
        revenues.save(new RevenueRecord(order, amount, LocalDate.now()));
    }

    @Transactional
    public void update(Long id, Long orderId, BigDecimal amount) {
        RevenueRecord revenue = revenues.findById(id).orElseThrow(() -> new BusinessRuleException("Przychód już nie istnieje."));
        OrderEntity order = order(orderId);
        validateAmount(amount);
        revenue.setDescription(order.getName());
        revenue.setAmount(amount);
        revenue.setOrder(order);
    }

    @Transactional
    public void delete(Long id) {
        revenues.deleteById(id);
    }

    private OrderEntity order(Long id) {
        if (id == null) throw new BusinessRuleException("Wybierz zamówienie.");
        return orders.findById(id).orElseThrow(() -> new BusinessRuleException("Wybrane zamówienie już nie istnieje."));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) throw new BusinessRuleException("Kwota musi być większa od zera.");
    }
}
