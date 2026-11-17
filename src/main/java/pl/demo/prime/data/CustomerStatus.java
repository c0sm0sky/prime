package pl.demo.prime.data;

public enum CustomerStatus {
    NEW("Nowy"),
    ACTIVE("Aktywny"),
    INACTIVE("Nieaktywny");

    private final String label;

    CustomerStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
