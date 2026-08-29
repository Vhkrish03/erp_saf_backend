package erp_backend.progress.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "internal_marks", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "student_id", "subject_id", "semester", "academic_year" })
})
public class InternalMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "subject_code")
    private String subjectCode;

    @Column(name = "subject_name")
    private String subjectName;

    @Column(name = "academic_year")
    private String academicYear;

    private String semester;
    private String department;
    private String section;

    @Column(name = "weekly_test_average")
    private Double weeklyTestAverage;

    @Column(name = "weekly_test_best_average")
    private Double weeklyTestBestAverage; // best N out of M

    @Column(name = "iat1_total")
    private Double iat1Total;

    @Column(name = "iat2_total")
    private Double iat2Total;

    @Column(name = "calculated_internal")
    private Double calculatedInternal;

    @Column(name = "final_internal")
    private Double finalInternal; // rounded

    @Column(name = "status")
    private String status; // DRAFT, CALCULATED, FINALIZED

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;

    @Column(name = "finalized_at")
    private LocalDateTime finalizedAt;

    public InternalMark() {
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

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Double getWeeklyTestAverage() {
        return weeklyTestAverage;
    }

    public void setWeeklyTestAverage(Double weeklyTestAverage) {
        this.weeklyTestAverage = weeklyTestAverage;
    }

    public Double getWeeklyTestBestAverage() {
        return weeklyTestBestAverage;
    }

    public void setWeeklyTestBestAverage(Double weeklyTestBestAverage) {
        this.weeklyTestBestAverage = weeklyTestBestAverage;
    }

    public Double getIat1Total() {
        return iat1Total;
    }

    public void setIat1Total(Double iat1Total) {
        this.iat1Total = iat1Total;
    }

    public Double getIat2Total() {
        return iat2Total;
    }

    public void setIat2Total(Double iat2Total) {
        this.iat2Total = iat2Total;
    }

    public Double getCalculatedInternal() {
        return calculatedInternal;
    }

    public void setCalculatedInternal(Double calculatedInternal) {
        this.calculatedInternal = calculatedInternal;
    }

    public Double getFinalInternal() {
        return finalInternal;
    }

    public void setFinalInternal(Double finalInternal) {
        this.finalInternal = finalInternal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public LocalDateTime getFinalizedAt() {
        return finalizedAt;
    }

    public void setFinalizedAt(LocalDateTime finalizedAt) {
        this.finalizedAt = finalizedAt;
    }
}
