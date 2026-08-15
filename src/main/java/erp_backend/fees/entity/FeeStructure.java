package erp_backend.fees.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(name = "fee_category", nullable = false)
    private String feeCategory; // e.g. "Tuition", "Exam", "Library", "Transport"

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

    public String getFeeCategory() {
        return feeCategory;
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

    public void setFeeCategory(String v) {
        this.feeCategory = v;
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
}
