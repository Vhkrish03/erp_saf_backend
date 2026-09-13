package erp_backend.management.dto;

import java.util.Map;

public class StudentOverviewDTO {
    private long totalStudents;
    private long activeStudents;
    private Map<String, Long> departmentWiseStrength;
    private Map<String, Long> yearWiseStrength;
    private Map<String, Long> semesterWiseStrength;
    private Map<String, Long> genderWiseStrength;

    public StudentOverviewDTO() {
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getActiveStudents() {
        return activeStudents;
    }

    public void setActiveStudents(long activeStudents) {
        this.activeStudents = activeStudents;
    }

    public Map<String, Long> getDepartmentWiseStrength() {
        return departmentWiseStrength;
    }

    public void setDepartmentWiseStrength(Map<String, Long> departmentWiseStrength) {
        this.departmentWiseStrength = departmentWiseStrength;
    }

    public Map<String, Long> getYearWiseStrength() {
        return yearWiseStrength;
    }

    public void setYearWiseStrength(Map<String, Long> yearWiseStrength) {
        this.yearWiseStrength = yearWiseStrength;
    }

    public Map<String, Long> getSemesterWiseStrength() {
        return semesterWiseStrength;
    }

    public void setSemesterWiseStrength(Map<String, Long> semesterWiseStrength) {
        this.semesterWiseStrength = semesterWiseStrength;
    }

    public Map<String, Long> getGenderWiseStrength() {
        return genderWiseStrength;
    }

    public void setGenderWiseStrength(Map<String, Long> genderWiseStrength) {
        this.genderWiseStrength = genderWiseStrength;
    }
}
