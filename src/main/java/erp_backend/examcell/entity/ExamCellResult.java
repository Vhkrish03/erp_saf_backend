package erp_backend.examcell.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Official semester examination result managed exclusively by the Exam Cell.
 *
 * This is SEPARATE from teacher-entered internal marks
 * (Assessment/AssessmentMark).
 * Teachers cannot create or directly modify this record.
 *
 * Lifecycle:
 * DRAFT → VERIFIED → APPROVED → PUBLISHED
 *
 * Only PUBLISHED results are visible to students on the Progress Card.
 */
@Entity
@Table(name = "exam_cell_results", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "student_id", "semester_name", "academic_year", "exam_session" })
})
public class ExamCellResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Student Reference ─────────────────────────────────────────────────────
    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "register_number", length = 30)
    private String registerNumber;

    @Column(name = "student_name", length = 150)
    private String studentName;

    @Column(nullable = false)
    private String department;

    // ── Semester/Academic Context ─────────────────────────────────────────────
    @Column(name = "semester_name", nullable = false, length = 20)
    private String semesterName; // e.g. "S5"

    @Column(name = "academic_year", nullable = false, length = 20)
    private String academicYear; // e.g. "2025-26"

    @Column(name = "exam_session", nullable = false, length = 50)
    private String examSession; // e.g. "Nov/Dec 2025"

    @Column(length = 100)
    private String examination; // e.g. "End Semester Examination"

    // ── Computed Academic Scores ──────────────────────────────────────────────
    private double sgpa;

    // ── Workflow Status ───────────────────────────────────────────────────────
    /**
     * DRAFT → Exam Cell entered / importing
     * VERIFIED → Exam Cell internally verified
     * APPROVED → Approved by authorized academic authority
     * PUBLISHED → Visible to students and all roles
     */
    @Column(nullable = false, length = 30)
    private String status = "DRAFT";

    // ── Audit Timestamps ─────────────────────────────────────────────────────
    @Column(name = "entered_by", length = 50)
    private String enteredBy;

    @Column(name = "verified_by", length = 50)
    private String verifiedBy;

    @Column(name = "approved_by", length = 50)
    private String approvedBy;

    @Column(name = "published_by", length = 50)
    private String publishedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Subject Results ───────────────────────────────────────────────────────
    @OneToMany(mappedBy = "examCellResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamCellResultSubject> subjects;

    // ── Audit Trail ──────────────────────────────────────────────────────────
    @OneToMany(mappedBy = "examCellResultId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExamCellResultAudit> auditLogs;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public ExamCellResult() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getExamSession() {
        return examSession;
    }

    public void setExamSession(String examSession) {
        this.examSession = examSession;
    }

    public String getExamination() {
        return examination;
    }

    public void setExamination(String examination) {
        this.examination = examination;
    }

    public double getSgpa() {
        return sgpa;
    }

    public void setSgpa(double sgpa) {
        this.sgpa = sgpa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEnteredBy() {
        return enteredBy;
    }

    public void setEnteredBy(String enteredBy) {
        this.enteredBy = enteredBy;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getPublishedBy() {
        return publishedBy;
    }

    public void setPublishedBy(String publishedBy) {
        this.publishedBy = publishedBy;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ExamCellResultSubject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<ExamCellResultSubject> subjects) {
        this.subjects = subjects;
    }

    public List<ExamCellResultAudit> getAuditLogs() {
        return auditLogs;
    }

    public void setAuditLogs(List<ExamCellResultAudit> auditLogs) {
        this.auditLogs = auditLogs;
    }
}
