package erp_backend.exam.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "semester_result_audits")
public class SemesterResultAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "semester_result_id")
    private Long semesterResultId;

    @Column(name = "student_id")
    private String studentId;

    private String action; // e.g. STATUS_CHANGE, CORRECTION

    @Column(name = "previous_state")
    private String previousState;

    @Column(name = "new_state")
    private String newState;

    @Column(name = "performed_by")
    private String performedBy;

    @Column(name = "performed_at")
    private LocalDateTime performedAt;

    private String comments;

    public SemesterResultAudit() {
    }

    public SemesterResultAudit(Long semesterResultId, String studentId, String action, String previousState,
            String newState, String performedBy, String comments) {
        this.semesterResultId = semesterResultId;
        this.studentId = studentId;
        this.action = action;
        this.previousState = previousState;
        this.newState = newState;
        this.performedBy = performedBy;
        this.performedAt = LocalDateTime.now();
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSemesterResultId() {
        return semesterResultId;
    }

    public void setSemesterResultId(Long semesterResultId) {
        this.semesterResultId = semesterResultId;
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

    public String getPreviousState() {
        return previousState;
    }

    public void setPreviousState(String previousState) {
        this.previousState = previousState;
    }

    public String getNewState() {
        return newState;
    }

    public void setNewState(String newState) {
        this.newState = newState;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
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
