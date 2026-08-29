package erp_backend.progress.entity;

import jakarta.persistence.*;

/**
 * Configurable performance category thresholds stored in DB.
 * Avoids hard-coding performance labels in Flutter or Java.
 *
 * Example rows:
 * label=EXCELLENT, minAverage=85.0, maxAverage=100.0
 * label=VERY_GOOD, minAverage=70.0, maxAverage=84.99
 * label=GOOD, minAverage=60.0, maxAverage=69.99
 * label=AVERAGE, minAverage=50.0, maxAverage=59.99
 * label=AT_RISK, minAverage=0.0, maxAverage=49.99
 */
@Entity
@Table(name = "performance_threshold_configs")
public class PerformanceThresholdConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Display label for this category, e.g. EXCELLENT, VERY_GOOD, AT_RISK */
    @Column(nullable = false, length = 50)
    private String label;

    /** Human-readable short description, e.g. "Excellent Performance" */
    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    /** Minimum average percentage (inclusive) for this category */
    @Column(name = "min_average", nullable = false)
    private double minAverage;

    /** Maximum average percentage (inclusive) for this category */
    @Column(name = "max_average", nullable = false)
    private double maxAverage;

    /** Priority order (lower = higher priority) to resolve boundary ties */
    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public PerformanceThresholdConfig() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public double getMinAverage() {
        return minAverage;
    }

    public void setMinAverage(double minAverage) {
        this.minAverage = minAverage;
    }

    public double getMaxAverage() {
        return maxAverage;
    }

    public void setMaxAverage(double maxAverage) {
        this.maxAverage = maxAverage;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
