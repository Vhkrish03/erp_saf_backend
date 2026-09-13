package erp_backend.management.dto;

import java.math.BigDecimal;

public class FinancialOverviewDTO {
    private BigDecimal totalDemand;
    private BigDecimal totalCollected;
    private BigDecimal totalPending;
    private double collectionPercentage;

    public FinancialOverviewDTO() {
        this.totalDemand = BigDecimal.ZERO;
        this.totalCollected = BigDecimal.ZERO;
        this.totalPending = BigDecimal.ZERO;
        this.collectionPercentage = 0.0;
    }

    public BigDecimal getTotalDemand() {
        return totalDemand;
    }

    public void setTotalDemand(BigDecimal totalDemand) {
        this.totalDemand = totalDemand;
    }

    public BigDecimal getTotalCollected() {
        return totalCollected;
    }

    public void setTotalCollected(BigDecimal totalCollected) {
        this.totalCollected = totalCollected;
    }

    public BigDecimal getTotalPending() {
        return totalPending;
    }

    public void setTotalPending(BigDecimal totalPending) {
        this.totalPending = totalPending;
    }

    public double getCollectionPercentage() {
        return collectionPercentage;
    }

    public void setCollectionPercentage(double collectionPercentage) {
        this.collectionPercentage = collectionPercentage;
    }
}
