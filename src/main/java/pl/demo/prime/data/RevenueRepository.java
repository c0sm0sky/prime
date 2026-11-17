package pl.demo.prime.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface RevenueRepository extends JpaRepository<RevenueRecord, Long> {
    @EntityGraph(attributePaths = {"order", "order.customer"})
    List<RevenueRecord> findAllByOrderByIdDesc();
    @Query("select coalesce(sum(revenue.amount), 0) from RevenueRecord revenue")
    BigDecimal sumAmounts();
}
