package pl.demo.prime;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.Profile;
import pl.demo.prime.service.BusinessRuleException;
import pl.demo.prime.service.CustomerService;
import pl.demo.prime.service.OrderService;
import pl.demo.prime.service.RevenueService;
import pl.demo.prime.data.CustomerStatus;
import pl.demo.prime.data.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component("dashboardView")
@Scope("view")
@RequiredArgsConstructor
@Profile("wildfly | docker | springboot")
public class DashboardView {
    private final CustomerService customerService;
    private final OrderService orderService;
    private final RevenueService revenueService;
    private final DisplayFormatters displayFormatters;

    @Getter @Setter private String customerName;
    @Getter @Setter private String customerEmail;
    @Getter @Setter private String orderName;
    @Getter @Setter private Long orderCustomerId;
    @Getter @Setter private BigDecimal revenueAmount;
    @Getter @Setter private Long revenueOrderId;
    @Getter @Setter private BigDecimal editRevenueAmount;
    @Getter private String editType;
    private Long editId;
    @Getter @Setter private Long editOrderId;
    @Getter @Setter private Long editCustomerId;
    @Getter @Setter private String editName;
    @Getter @Setter private String editDetail;
    @Getter @Setter private CustomerStatus editCustomerStatus;
    @Getter @Setter private OrderStatus editOrderStatus;
    private List<CustomerRow> customerRows;
    private List<OrderRow> orderRows;
    private List<RevenueRow> revenueRows;
    private Long customerCount;
    private Long orderCount;
    private String revenueTotal;

    public void addCustomer() {
        if (perform(() -> customerService.create(customerName, customerEmail))) {
            customerName = "";
            customerEmail = "";
        }
    }

    public void addOrder() {
        if (perform(() -> orderService.create(orderName, orderCustomerId))) {
            orderName = "";
            orderCustomerId = null;
        }
    }

    public void addRevenue() {
        if (perform(() -> revenueService.create(revenueOrderId, revenueAmount))) {
            revenueOrderId = null;
            revenueAmount = null;
        }
    }

    public List<CustomerRow> getCustomers() {
        if (customerRows == null) customerRows = customerService.findAll().stream()
                .map(row -> new CustomerRow(row.getId(), row.getName(), row.getEmail(), row.getStatus())).toList();
        return customerRows;
    }

    public List<OrderRow> getOrders() {
        if (orderRows == null) orderRows = orderService.findAll().stream()
                .map(row -> new OrderRow(row.getId(), row.getName(), row.getCustomer().getId(),
                        row.getCustomer().getName(), row.getStatus())).toList();
        return orderRows;
    }

    public List<RevenueRow> getRevenues() {
        if (revenueRows == null) revenueRows = revenueService.findAll().stream()
                .map(row -> new RevenueRow(row.getId(), row.getDescription(), row.getAmount(), row.getReceivedOn(),
                        row.getOrder() == null ? null : row.getOrder().getId(),
                        row.getOrder() == null ? null : row.getOrder().getName(),
                        row.getOrder() == null ? null : row.getOrder().getCustomer().getName())).toList();
        return revenueRows;
    }

    public long getCustomerCount() {
        if (customerCount == null) customerCount = customerService.count();
        return customerCount;
    }

    public long getOrderCount() {
        if (orderCount == null) orderCount = orderService.count();
        return orderCount;
    }

    public String getRevenueTotal() {
        if (revenueTotal == null) revenueTotal = displayFormatters.currency(revenueService.total());
        return revenueTotal;
    }
    public String formatCurrency(BigDecimal amount) { return displayFormatters.currency(amount); }
    public String formatDate(LocalDate date) { return displayFormatters.date(date); }
    public List<CustomerStatus> getCustomerStatuses() { return List.of(CustomerStatus.values()); }
    public List<OrderStatus> getOrderStatuses() { return List.of(OrderStatus.values()); }

    public String beginEditCustomer(CustomerRow customer) {
        editType = "customer";
        editId = customer.getId();
        editName = customer.getName();
        editDetail = customer.getEmail();
        editCustomerStatus = customer.getStatus();
        return null;
    }

    public String beginEditOrder(OrderRow order) {
        editType = "order";
        editId = order.getId();
        editName = order.getName();
        editCustomerId = order.getCustomerId();
        editOrderStatus = order.getStatus();
        return null;
    }

    public String beginEditRevenue(RevenueRow revenue) {
        editType = "revenue";
        editId = revenue.getId();
        editRevenueAmount = revenue.getAmount();
        editOrderId = revenue.getOrderId();
        return null;
    }

    public void saveEdit() {
        switch (editType == null ? "" : editType) {
            case "customer" -> perform(() -> customerService.update(editId, editName, editDetail, editCustomerStatus));
            case "order" -> perform(() -> orderService.update(editId, editName, editCustomerId, editOrderStatus));
            case "revenue" -> saveRevenueEdit();
            default -> showError("Nie można rozpoznać edytowanego rekordu.");
        }
        if (!FacesContext.getCurrentInstance().isValidationFailed()) editType = null;
    }

    private void saveRevenueEdit() {
        perform(() -> revenueService.update(editId, editOrderId, editRevenueAmount));
    }

    public String deleteCustomer(Long id) { perform(() -> customerService.delete(id)); return null; }
    public String deleteOrder(Long id) { perform(() -> orderService.delete(id)); return null; }
    public String deleteRevenue(Long id) { perform(() -> revenueService.delete(id)); return null; }

    private boolean perform(Runnable action) {
        try {
            action.run();
            invalidateDashboardCache();
            return true;
        } catch (BusinessRuleException exception) {
            showError(exception.getMessage());
            return false;
        }
    }

    private void invalidateDashboardCache() {
        customerRows = null;
        orderRows = null;
        revenueRows = null;
        customerCount = null;
        orderCount = null;
        revenueTotal = null;
    }

    private void showError(String message) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.validationFailed();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
    }
}
