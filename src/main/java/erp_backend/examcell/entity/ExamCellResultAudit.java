package erp_backend.examcell.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Immutable audit log for every status change / action done on an
 * ExamCellResult.
 * Records are never overwritten. One INSERT per action.
 */
@Entity
@Table(name = "exam_cell_result_audits")
public class ExamCellResultAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Links to ExamCellResult.id (plain Long, not FK, to allow audit even after
     * deletion)
     */
    @Column(name = "exam_cell_result_id", nullable = false)
    private Long examCellResultId;

    @Column(name = "student_id", length = 50)
    private String studentId;

    /**
     * Action codes: CREATED, UPDATED, VERIFIED, APPROVED, PUBLISHED,
     * RETURNED, CORRECTION_REQUESTED
     */
    @Column(nullable = false, length = 50)
    private String action;

    @Column(name = "previous_status", length = 30)
    private String previousStatus;

    @Column(name = "new_status", length = 30)
    private String newStatus;

    /** Employee ID or User ID of the person performing the action */
    @Column(name = "performed_by", nullable = false, length = 50)
    private String performedBy;

    /** Role of the person: EXAM_CELL, ADMIN, DEAN */
    @Column(name = "performed_by_role", length = 30)
    private String performedByRole;

    @Column(name = "performed_at", nullable = false)
    private LocalDateTime performedAt;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @PrePersist
    protected void onCreate() {
        if (performedAt == null) {
            performedAt = LocalDateTime.now();
        }
    }

    public ExamCellResultAudit() {
    }

    public ExamCellResultAudit(Long examCellResultId, String studentId, String action,
            String previousStatus, String newStatus,
            String performedBy, String performedByRole, String comments) {
        this.examCellResultId = examCellResultId;
        this.studentId = studentId;
        this.action = action;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.performedBy = performedBy;
        this.performedByRole = performedByRole;
        this.comments = comments;
        this.performedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExamCellResultId() {
        return examCellResultId;
    }

    public void setExamCellResultId(Long examCellResultId) {
        this.examCellResultId = examCellResultId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getPerformedByRole() {
        return performedByRole;
    }

    public void setPerformedByRole(String performedByRole) {
        this.performedByRole = performedByRole;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
