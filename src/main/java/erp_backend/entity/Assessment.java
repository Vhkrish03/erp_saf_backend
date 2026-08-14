package erp_backend.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "assessments")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // "WEEKLY" or "IAT"
    private String name; // e.g. "Weekly Test 1", "IAT 1"

    @Column(name = "academic_year")
    private String academicYear;

    private String department;
    private int year;
    private String semester;
    private String section;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(name = "faculty_id")
    private String facultyId;

    private String date;

    @Column(name = "max_marks")
    private double maxMarks;

    private String status; // DRAFT, SUBMITTED, INCHARGE_VERIFIED, HOD_APPROVED, DEAN_SUBMITTED

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssessmentComponent> components;

    @OneToOne(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true)
    private AssessmentWorkflow workflow;

    public Assessment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(double maxMarks) {
        this.maxMarks = maxMarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<AssessmentComponent> getComponents() {
        return components;
    }

    public void setComponents(List<AssessmentComponent> components) {
        this.components = components;
    }

    public AssessmentWorkflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(AssessmentWorkflow workflow) {
        this.workflow = workflow;
    }
}
