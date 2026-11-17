package pl.demo.prime.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "revenues")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class RevenueRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    @NonNull
    @Setter
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    @NotNull
    @DecimalMin("0.01")
    @NonNull
    @Setter
    private BigDecimal amount;

    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.DATE)
    @NotNull
    @NonNull
    private LocalDate receivedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @Setter
    private OrderEntity order;

    public RevenueRecord(OrderEntity order, BigDecimal amount, LocalDate receivedOn) {
        this.description = order.getName();
        this.amount = amount;
        this.receivedOn = receivedOn;
        this.order = order;
    }

}
