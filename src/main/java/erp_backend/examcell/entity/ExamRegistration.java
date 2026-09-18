package erp_backend.examcell.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "examcell_registrations")
public class ExamRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examination_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Examination examination;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private String registerNumber;

    @Column(nullable = false)
    private String status = "ELIGIBLE"; // ELIGIBLE, NOT_ELIGIBLE, PENDING_VERIFICATION, REGISTERED

    private String eligibilityReason;

    private boolean feePaid;

    @ElementCollection
    @CollectionTable(name = "examcell_registration_subjects", joinColumns = @JoinColumn(name = "registration_id"))
    @Column(name = "subject_code")
    private java.util.List<String> registeredSubjects = new java.util.ArrayList<>();

    @Column(name = "total_fee")
    private Double totalFee = 0.0;

    @Column(name = "payment_status")
    private String paymentStatus = "PENDING"; // PENDING, SUCCESS, FAILED, VERIFIED

    @Column(name = "verification_status")
    private String verificationStatus; // VERIFIED, NOT_VERIFIED, PENDING_VERIFICATION

    @Column(name = "verified_by")
    private String verifiedBy;

    @Column(name = "verification_date")
    private LocalDateTime verificationDate;

    private LocalDateTime registeredAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Examination getExamination() {
        return examination;
    }

    public void setExamination(Examination examination) {
        this.examination = examination;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEligibilityReason() {
        return eligibilityReason;
    }

    public void setEligibilityReason(String eligibilityReason) {
        this.eligibilityReason = eligibilityReason;
    }

    public boolean isFeePaid() {
        return feePaid;
    }

    public void setFeePaid(boolean feePaid) {
        this.feePaid = feePaid;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public LocalDateTime getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(LocalDateTime verificationDate) {
        this.verificationDate = verificationDate;
    }

    public java.util.List<String> getRegisteredSubjects() {
        return registeredSubjects;
    }

    public void setRegisteredSubjects(java.util.List<String> registeredSubjects) {
        this.registeredSubjects = registeredSubjects;
    }

    public Double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Double totalFee) {
        this.totalFee = totalFee;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
