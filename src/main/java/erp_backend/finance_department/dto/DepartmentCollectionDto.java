package erp_backend.finance_department.dto;

import java.math.BigDecimal;

public class DepartmentCollectionDto {
    private String department;
    private BigDecimal expected;
    private BigDecimal collected;
    private BigDecimal pending;
    private double collectionPercentage;

    public DepartmentCollectionDto(String department, BigDecimal expected, BigDecimal collected, BigDecimal pending, double collectionPercentage) {
        this.department = department;
        this.expected = expected;
        this.collected = collected;
        this.pending = pending;
        this.collectionPercentage = collectionPercentage;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public BigDecimal getExpected() { return expected; }
    public void setExpected(BigDecimal expected) { this.expected = expected; }

    public BigDecimal getCollected() { return collected; }
    public void setCollected(BigDecimal collected) { this.collected = collected; }

    public BigDecimal getPending() { return pending; }
    public void setPending(BigDecimal pending) { this.pending = pending; }

    public double getCollectionPercentage() { return collectionPercentage; }
    public void setCollectionPercentage(double collectionPercentage) { this.collectionPercentage = collectionPercentage; }
}
