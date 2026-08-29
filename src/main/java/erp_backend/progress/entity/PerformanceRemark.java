package erp_backend.progress.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "performance_remarks")
public class PerformanceRemark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(nullable = false)
    private String semester;

    @Column(name = "academic_year")
    private String academicYear;

    @Column(name = "remark_by", nullable = false)
    private String remarkBy; // employeeId or userId

    @Column(name = "remark_by_role", nullable = false)
    private String remarkByRole; // FACULTY, CLASS_INCHARGE, HOD

    @Column(name = "remark_by_name")
    private String remarkByName;

    @Column(name = "remark_text", columnDefinition = "TEXT", nullable = false)
    private String remarkText;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public PerformanceRemark() {
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

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getRemarkBy() {
        return remarkBy;
    }

    public void setRemarkBy(String remarkBy) {
        this.remarkBy = remarkBy;
    }

    public String getRemarkByRole() {
        return remarkByRole;
    }

    public void setRemarkByRole(String remarkByRole) {
        this.remarkByRole = remarkByRole;
    }

    public String getRemarkByName() {
        return remarkByName;
    }

    public void setRemarkByName(String remarkByName) {
        this.remarkByName = remarkByName;
    }

    public String getRemarkText() {
        return remarkText;
    }

    public void setRemarkText(String remarkText) {
        this.remarkText = remarkText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
