package erp_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assessment_workflows")
public class AssessmentWorkflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id")
    @JsonIgnore
    private Assessment assessment;

    @Column(name = "faculty_status")
    private String facultyStatus; // DRAFT, SUBMITTED

    @Column(name = "class_incharge_status")
    private String classInchargeStatus; // PENDING, VERIFIED, REOPENED

    @Column(name = "hod_status")
    private String hodStatus; // PENDING, APPROVED, REJECTED

    @Column(name = "dean_status")
    private String deanStatus; // PENDING, LOCKED

    @Column(name = "hod_comments")
    private String hodComments;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    public AssessmentWorkflow() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public String getFacultyStatus() {
        return facultyStatus;
    }

    public void setFacultyStatus(String facultyStatus) {
        this.facultyStatus = facultyStatus;
    }

    public String getClassInchargeStatus() {
        return classInchargeStatus;
    }

    public void setClassInchargeStatus(String classInchargeStatus) {
        this.classInchargeStatus = classInchargeStatus;
    }

    public String getHodStatus() {
        return hodStatus;
    }

    public void setHodStatus(String hodStatus) {
        this.hodStatus = hodStatus;
    }

    public String getDeanStatus() {
        return deanStatus;
    }

    public void setDeanStatus(String deanStatus) {
        this.deanStatus = deanStatus;
    }

    public String getHodComments() {
        return hodComments;
    }

    public void setHodComments(String hodComments) {
        this.hodComments = hodComments;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
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

    public LocalDateTime getLockedAt() {
        return lockedAt;
    }

    public void setLockedAt(LocalDateTime lockedAt) {
        this.lockedAt = lockedAt;
    }
}
