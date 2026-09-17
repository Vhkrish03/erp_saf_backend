package erp_backend.coe.dto;

public class CoeBatchSummaryDto {
    private String department;
    private String semesterName;
    private String academicYear;
    private String examSession;
    private String examination;
    private String status;
    private long studentCount;
    private String submissionDate;
    private String submittedBy;
    private String approvalDate;
    private String approvedBy;
    private String rejectionDate;
    private String rejectedBy;
    private String rejectionReason;

    // Constructors, Getters & Setters
    public CoeBatchSummaryDto() {
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String v) {
        this.department = v;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String v) {
        this.semesterName = v;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String v) {
        this.academicYear = v;
    }

    public String getExamSession() {
        return examSession;
    }

    public void setExamSession(String v) {
        this.examSession = v;
    }

    public String getExamination() {
        return examination;
    }

    public void setExamination(String v) {
        this.examination = v;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String v) {
        this.status = v;
    }

    public long getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(long v) {
        this.studentCount = v;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String v) {
        this.submissionDate = v;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String v) {
        this.submittedBy = v;
    }

    public String getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(String v) {
        this.approvalDate = v;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String v) {
        this.approvedBy = v;
    }

    public String getRejectionDate() {
        return rejectionDate;
    }

    public void setRejectionDate(String v) {
        this.rejectionDate = v;
    }

    public String getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(String v) {
        this.rejectedBy = v;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String v) {
        this.rejectionReason = v;
    }
}
