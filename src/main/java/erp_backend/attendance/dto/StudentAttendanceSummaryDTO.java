package erp_backend.attendance.dto;

import java.util.List;

public class StudentAttendanceSummaryDTO {
    private int totalClassesHeld;
    private int totalClassesAttended;
    private double overallPercentage;
    private List<StudentSubjectAttendanceDTO> subjects;

    public StudentAttendanceSummaryDTO() {
    }

    public int getTotalClassesHeld() {
        return totalClassesHeld;
    }

    public void setTotalClassesHeld(int totalClassesHeld) {
        this.totalClassesHeld = totalClassesHeld;
    }

    public int getTotalClassesAttended() {
        return totalClassesAttended;
    }

    public void setTotalClassesAttended(int totalClassesAttended) {
        this.totalClassesAttended = totalClassesAttended;
    }

    public double getOverallPercentage() {
        return overallPercentage;
    }

    public void setOverallPercentage(double overallPercentage) {
        this.overallPercentage = overallPercentage;
    }

    public List<StudentSubjectAttendanceDTO> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<StudentSubjectAttendanceDTO> subjects) {
        this.subjects = subjects;
    }
}
