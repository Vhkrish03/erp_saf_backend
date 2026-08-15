package erp_backend.fees.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import erp_backend.entity.Student;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Records each individual payment transaction against a StudentFee.
 * Multiple FeePayments can exist per StudentFee (partial payments).
 */
@Entity
@Table(name = "fee_payments")
public class FeePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_fee_id", nullable = false)
    @JsonIgnore
    private StudentFee studentFee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "amount_paid", nullable = false)
    private double amountPaid;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    /** Enum-like: CASH | ONLINE | DD | CHEQUE | NEFT | UPI */
    @Column(name = "payment_mode")
    private String paymentMode;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "payment_reference")
    private String paymentReference;

    private String remarks;

    /** Employee ID of the staff who recorded this payment */
    @Column(name = "recorded_by")
    private String recordedBy;

    @Column(name = "academic_year")
    private String academicYear;

    @Column(name = "semester")
    private String semester;

    @Column(name = "fee_category")
    private String feeCategory;

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

    public StudentFee getStudentFee() {
        return studentFee;
    }

    public Student getStudent() {
        return student;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getRecordedBy() {
        return recordedBy;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public String getSemester() {
        return semester;
    }

    public String getFeeCategory() {
        return feeCategory;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // --- Setters ---
    public void setId(Long id) {
        this.id = id;
    }

    public void setStudentFee(StudentFee v) {
        this.studentFee = v;
    }

    public void setStudent(Student v) {
        this.student = v;
    }

    public void setAmountPaid(double v) {
        this.amountPaid = v;
    }

    public void setPaymentDate(LocalDate v) {
        this.paymentDate = v;
    }

    public void setPaymentMode(String v) {
        this.paymentMode = v;
    }

    public void setTransactionId(String v) {
        this.transactionId = v;
    }

    public void setPaymentReference(String v) {
        this.paymentReference = v;
    }

    public void setRemarks(String v) {
        this.remarks = v;
    }

    public void setRecordedBy(String v) {
        this.recordedBy = v;
    }

    public void setAcademicYear(String v) {
        this.academicYear = v;
    }

    public void setSemester(String v) {
        this.semester = v;
    }

    public void setFeeCategory(String v) {
        this.feeCategory = v;
    }

    public void setCreatedAt(LocalDateTime v) {
        this.createdAt = v;
    }
}
