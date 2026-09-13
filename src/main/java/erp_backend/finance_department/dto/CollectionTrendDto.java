package erp_backend.finance_department.dto;

import java.math.BigDecimal;

public class CollectionTrendDto {
    private String label; // e.g., "April", "May", or "Week 1"
    private BigDecimal amount;

    public CollectionTrendDto(String label, BigDecimal amount) {
        this.label = label;
        this.amount = amount;
    }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
