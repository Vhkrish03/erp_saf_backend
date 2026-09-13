package erp_backend.finance_department.dto;

import java.math.BigDecimal;

public class FeeCategoryBreakdownDto {
    private String category;
    private BigDecimal expected;
    private BigDecimal collected;
    private BigDecimal pending;

    public FeeCategoryBreakdownDto(String category, BigDecimal expected, BigDecimal collected, BigDecimal pending) {
        this.category = category;
        this.expected = expected;
        this.collected = collected;
        this.pending = pending;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getExpected() { return expected; }
    public void setExpected(BigDecimal expected) { this.expected = expected; }

    public BigDecimal getCollected() { return collected; }
    public void setCollected(BigDecimal collected) { this.collected = collected; }

    public BigDecimal getPending() { return pending; }
    public void setPending(BigDecimal pending) { this.pending = pending; }
}
