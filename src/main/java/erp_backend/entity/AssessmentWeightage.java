package erp_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "assessment_weightages")
public class AssessmentWeightage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String department;
    private String semester;

    @Column(name = "component_type")
    private String componentType; // WRITTEN, ASSIGNMENT, SEMINAR, QUIZ

    private double weightage; // e.g. 0.50, 0.20, 0.15, 0.15

    public AssessmentWeightage() {
    }

    public AssessmentWeightage(String department, String semester, String componentType, double weightage) {
        this.department = department;
        this.semester = semester;
        this.componentType = componentType;
        this.weightage = weightage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public double getWeightage() {
        return weightage;
    }

    public void setWeightage(double weightage) {
        this.weightage = weightage;
    }
}
