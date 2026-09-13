package erp_backend.management.dto;

import java.util.Map;

public class AcademicOverviewDTO {
    private long totalAssessments;
    private long completedAssessments;
    private long pendingAssessments;
    private double overallPassPercentage;
    private Map<String, Double> departmentWisePassPercentage;

    public AcademicOverviewDTO() {
    }

    public long getTotalAssessments() {
        return totalAssessments;
    }

    public void setTotalAssessments(long totalAssessments) {
        this.totalAssessments = totalAssessments;
    }

    public long getCompletedAssessments() {
        return completedAssessments;
    }

    public void setCompletedAssessments(long completedAssessments) {
        this.completedAssessments = completedAssessments;
    }

    public long getPendingAssessments() {
        return pendingAssessments;
    }

    public void setPendingAssessments(long pendingAssessments) {
        this.pendingAssessments = pendingAssessments;
    }

    public double getOverallPassPercentage() {
        return overallPassPercentage;
    }

    public void setOverallPassPercentage(double overallPassPercentage) {
        this.overallPassPercentage = overallPassPercentage;
    }

    public Map<String, Double> getDepartmentWisePassPercentage() {
        return departmentWisePassPercentage;
    }

    public void setDepartmentWisePassPercentage(Map<String, Double> departmentWisePassPercentage) {
        this.departmentWisePassPercentage = departmentWisePassPercentage;
    }
}
