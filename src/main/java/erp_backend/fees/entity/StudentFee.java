package erp_backend.fees.entity;

import erp_backend.entity.Student;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Links a Student to a FeeStructure for a given academic year + semester.
 * Backend auto-computes balanceAmount and paymentStatus on every save.
 */
@Entity
@Table(name = "student_fees", uniqueConstraints = @UniqueConstraint(columnNames = { "student_id", "fee_structure_id" }))
public class StudentFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(nullable = false)
    private String semester;

    @Column(name = "total_fee", nullable = false)
    private double totalFee;

    @Column(name = "amount_paid")
    private double amountPaid = 0.0;

    /** Auto-computed: totalFee - amountPaid */
    @Column(name = "balance_amount")
    private double balanceAmount;

    /**
     * Auto-computed by backend before every persist/update.
     * Values: PAID | PARTIALLY_PAID | PENDING | OVERDUE | WAIVED
     */
    @Column(name = "payment_status", nullable = false)
    private String paymentStatus = "PENDING";

    @Column(name = "due_date")
    private LocalDate dueDate;

    private String remarks;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        recomputeStatus();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        recomputeStatus();
    }

    /** Call this method whenever amountPaid or totalFee changes. */
    public void recomputeStatus() {
        balanceAmount = totalFee - amountPaid;
        if (balanceAmount <= 0) {
            paymentStatus = "PAID";
        } else if (amountPaid > 0) {
            paymentStatus = "PARTIALLY_PAID";
        } else if (dueDate != null && LocalDate.now().isAfter(dueDate)) {
            paymentStatus = "OVERDUE";
        } else {
            paymentStatus = "PENDING";
        }
    }

    // --- Getters ---
    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public FeeStructure getFeeStructure() {
        return feeStructure;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public String getSemester() {
        return semester;
    }

    public double getTotalFee() {
        return totalFee;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public double getBalanceAmount() {
        return balanceAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // --- Setters ---
    public void setId(Long id) {
        this.id = id;
    }

    public void setStudent(Student s) {
        this.student = s;
    }

    public void setFeeStructure(FeeStructure f) {
        this.feeStructure = f;
    }

    public void setAcademicYear(String v) {
        this.academicYear = v;
    }

    public void setSemester(String v) {
        this.semester = v;
    }

    public void setTotalFee(double v) {
        this.totalFee = v;
    }

    public void setAmountPaid(double v) {
        this.amountPaid = v;
    }

    public void setBalanceAmount(double v) {
        this.balanceAmount = v;
    }

    public void setPaymentStatus(String v) {
        this.paymentStatus = v;
    }

    public void setDueDate(LocalDate v) {
        this.dueDate = v;
    }

    public void setRemarks(String v) {
        this.remarks = v;
    }

    public void setCreatedAt(LocalDateTime v) {
        this.createdAt = v;
    }

    public void setUpdatedAt(LocalDateTime v) {
        this.updatedAt = v;
    }
}
