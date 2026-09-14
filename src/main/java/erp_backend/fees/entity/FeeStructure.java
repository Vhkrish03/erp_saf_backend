package erp_backend.fees.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fee_structures")
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "academic_year", nullable = false)
    private String academicYear; // e.g. "2025-26"

    @Column(nullable = false)
    private String semester; // e.g. "VII"

    @Column(nullable = false)
    private String department; // e.g. "CSE"

    @Column(name = "year_of_study")
    private String yearOfStudy; // e.g. "4"

    private String section; // null = applies to all sections

    @OneToMany(mappedBy = "feeStructure", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FeeComponent> feeComponents = new ArrayList<>();

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(name = "due_date")
    private LocalDate dueDate;

    private String description;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "status")
    private String status = "DRAFT"; // DRAFT, SUBMITTED, UNDER_REVIEW, APPROVED, PUBLISHED, REJECTED
    
    @Column(name = "version")
    private Integer version = 1;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "submitted_by")
    private String submittedBy;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "published_by")
    private String publishedBy;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // --- Getters ---
    public Long getId() {
        return id;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public String getSemester() {
        return semester;
    }

    public String getDepartment() {
        return department;
    }

    public String getYearOfStudy() {
        return yearOfStudy;
    }

    public String getSection() {
        return section;
    }

    public List<FeeComponent> getFeeComponents() {
        return feeComponents;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // --- Setters ---
    public void setId(Long id) {
        this.id = id;
    }

    public void setAcademicYear(String v) {
        this.academicYear = v;
    }

    public void setSemester(String v) {
        this.semester = v;
    }

    public void setDepartment(String v) {
        this.department = v;
    }

    public void setYearOfStudy(String v) {
        this.yearOfStudy = v;
    }

    public void setSection(String v) {
        this.section = v;
    }

    public void setFeeComponents(List<FeeComponent> feeComponents) {
        this.feeComponents = feeComponents;
        if (feeComponents != null) {
            for (FeeComponent c : feeComponents) {
                c.setFeeStructure(this);
            }
        }
    }

    public void setTotalAmount(double v) {
        this.totalAmount = v;
    }

    public void setDueDate(LocalDate v) {
        this.dueDate = v;
    }

    public void setDescription(String v) {
        this.description = v;
    }

    public void setActive(boolean v) {
        this.isActive = v;
    }

    public void setCreatedBy(String v) {
        this.createdBy = v;
    }

    public void setCreatedAt(LocalDateTime v) {
        this.createdAt = v;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(String submittedBy) { this.submittedBy = submittedBy; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public String getPublishedBy() { return publishedBy; }
    public void setPublishedBy(String publishedBy) { this.publishedBy = publishedBy; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
