package erp_backend.progress.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "internal_mark_configs", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "department", "semester", "academic_year" })
})
public class InternalMarkConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String semester;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(name = "weekly_test_weightage")
    private double weeklyTestWeightage; // e.g. 0.20 (20%)

    @Column(name = "iat1_weightage")
    private double iat1Weightage; // e.g. 0.40 (40%)

    @Column(name = "iat2_weightage")
    private double iat2Weightage; // e.g. 0.40 (40%)

    @Column(name = "total_internal_max")
    private double totalInternalMax; // e.g. 50.0

    @Column(name = "weekly_test_max_each")
    private double weeklyTestMaxEach; // e.g. 10.0

    @Column(name = "iat_max_marks")
    private double iatMaxMarks; // e.g. 50.0

    @Column(name = "num_weekly_tests")
    private int numWeeklyTests; // e.g. 6

    @Column(name = "best_of_weekly_tests")
    private int bestOfWeeklyTests; // e.g. 4 (best 4 out of 6)

    @Column(name = "is_active")
    private boolean isActive;

    public InternalMarkConfig() {
    }

    // Getters and Setters

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

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public double getWeeklyTestWeightage() {
        return weeklyTestWeightage;
    }

    public void setWeeklyTestWeightage(double weeklyTestWeightage) {
        this.weeklyTestWeightage = weeklyTestWeightage;
    }

    public double getIat1Weightage() {
        return iat1Weightage;
    }

    public void setIat1Weightage(double iat1Weightage) {
        this.iat1Weightage = iat1Weightage;
    }

    public double getIat2Weightage() {
        return iat2Weightage;
    }

    public void setIat2Weightage(double iat2Weightage) {
        this.iat2Weightage = iat2Weightage;
    }

    public double getTotalInternalMax() {
        return totalInternalMax;
    }

    public void setTotalInternalMax(double totalInternalMax) {
        this.totalInternalMax = totalInternalMax;
    }

    public double getWeeklyTestMaxEach() {
        return weeklyTestMaxEach;
    }

    public void setWeeklyTestMaxEach(double weeklyTestMaxEach) {
        this.weeklyTestMaxEach = weeklyTestMaxEach;
    }

    public double getIatMaxMarks() {
        return iatMaxMarks;
    }

    public void setIatMaxMarks(double iatMaxMarks) {
        this.iatMaxMarks = iatMaxMarks;
    }

    public int getNumWeeklyTests() {
        return numWeeklyTests;
    }

    public void setNumWeeklyTests(int numWeeklyTests) {
        this.numWeeklyTests = numWeeklyTests;
    }

    public int getBestOfWeeklyTests() {
        return bestOfWeeklyTests;
    }

    public void setBestOfWeeklyTests(int bestOfWeeklyTests) {
        this.bestOfWeeklyTests = bestOfWeeklyTests;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
