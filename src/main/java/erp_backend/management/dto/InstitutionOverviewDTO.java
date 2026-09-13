package erp_backend.management.dto;

public class InstitutionOverviewDTO {
    private long totalStudents;
    private long totalTeachers;
    private long totalNonTeachingStaff;
    private int totalDepartments;
    private String currentAcademicYear;
    private String currentSemester;

    public InstitutionOverviewDTO() {}

    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }

    public long getTotalTeachers() { return totalTeachers; }
    public void setTotalTeachers(long totalTeachers) { this.totalTeachers = totalTeachers; }

    public long getTotalNonTeachingStaff() { return totalNonTeachingStaff; }
    public void setTotalNonTeachingStaff(long totalNonTeachingStaff) { this.totalNonTeachingStaff = totalNonTeachingStaff; }

    public int getTotalDepartments() { return totalDepartments; }
    public void setTotalDepartments(int totalDepartments) { this.totalDepartments = totalDepartments; }

    public String getCurrentAcademicYear() { return currentAcademicYear; }
    public void setCurrentAcademicYear(String currentAcademicYear) { this.currentAcademicYear = currentAcademicYear; }

    public String getCurrentSemester() { return currentSemester; }
    public void setCurrentSemester(String currentSemester) { this.currentSemester = currentSemester; }
}
