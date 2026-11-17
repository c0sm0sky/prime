package pl.demo.prime.data;

public enum OrderStatus {
    NEW("Nowe"),
    IN_PROGRESS("W realizacji"),
    COMPLETED("Zakończone"),
    CANCELLED("Anulowane");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
