package erp_backend.finance_department.dto;

import java.math.BigDecimal;
import java.util.List;

public class FinanceDashboardDto {
    private BigDecimal totalDemand;
    private BigDecimal totalCollected;
    private BigDecimal totalPending;
    private BigDecimal totalOverdue;
    private double collectionRate;
    private BigDecimal todaysCollection;

    private List<CollectionTrendDto> collectionTrend;
    private List<FeeCategoryBreakdownDto> feeBreakdown;
    private List<DepartmentCollectionDto> departmentCollections;

    // Approvals overview
    private long pendingFeeStructures;
    private long pendingRefunds;
    private long unverifiedPayments;

    // Getters and Setters
    public BigDecimal getTotalDemand() { return totalDemand; }
    public void setTotalDemand(BigDecimal totalDemand) { this.totalDemand = totalDemand; }

    public BigDecimal getTotalCollected() { return totalCollected; }
    public void setTotalCollected(BigDecimal totalCollected) { this.totalCollected = totalCollected; }

    public BigDecimal getTotalPending() { return totalPending; }
    public void setTotalPending(BigDecimal totalPending) { this.totalPending = totalPending; }

    public BigDecimal getTotalOverdue() { return totalOverdue; }
    public void setTotalOverdue(BigDecimal totalOverdue) { this.totalOverdue = totalOverdue; }

    public double getCollectionRate() { return collectionRate; }
    public void setCollectionRate(double collectionRate) { this.collectionRate = collectionRate; }

    public BigDecimal getTodaysCollection() { return todaysCollection; }
    public void setTodaysCollection(BigDecimal todaysCollection) { this.todaysCollection = todaysCollection; }

    public List<CollectionTrendDto> getCollectionTrend() { return collectionTrend; }
    public void setCollectionTrend(List<CollectionTrendDto> collectionTrend) { this.collectionTrend = collectionTrend; }

    public List<FeeCategoryBreakdownDto> getFeeBreakdown() { return feeBreakdown; }
    public void setFeeBreakdown(List<FeeCategoryBreakdownDto> feeBreakdown) { this.feeBreakdown = feeBreakdown; }

    public List<DepartmentCollectionDto> getDepartmentCollections() { return departmentCollections; }
    public void setDepartmentCollections(List<DepartmentCollectionDto> departmentCollections) { this.departmentCollections = departmentCollections; }

    public long getPendingFeeStructures() { return pendingFeeStructures; }
    public void setPendingFeeStructures(long pendingFeeStructures) { this.pendingFeeStructures = pendingFeeStructures; }

    public long getPendingRefunds() { return pendingRefunds; }
    public void setPendingRefunds(long pendingRefunds) { this.pendingRefunds = pendingRefunds; }

    public long getUnverifiedPayments() { return unverifiedPayments; }
    public void setUnverifiedPayments(long unverifiedPayments) { this.unverifiedPayments = unverifiedPayments; }
}
