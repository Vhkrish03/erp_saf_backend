package erp_backend.management.dto;

public class ExaminationOverviewDTO {
    private long totalPublishedResults;
    private long totalStudentsAppeared;
    private long totalPassed;
    private long totalFailed;
    private double overallPassPercentage;

    public ExaminationOverviewDTO() {
    }

    public long getTotalPublishedResults() {
        return totalPublishedResults;
    }

    public void setTotalPublishedResults(long totalPublishedResults) {
        this.totalPublishedResults = totalPublishedResults;
    }

    public long getTotalStudentsAppeared() {
        return totalStudentsAppeared;
    }

    public void setTotalStudentsAppeared(long totalStudentsAppeared) {
        this.totalStudentsAppeared = totalStudentsAppeared;
    }

    public long getTotalPassed() {
        return totalPassed;
    }

    public void setTotalPassed(long totalPassed) {
        this.totalPassed = totalPassed;
    }

    public long getTotalFailed() {
        return totalFailed;
    }

    public void setTotalFailed(long totalFailed) {
        this.totalFailed = totalFailed;
    }

    public double getOverallPassPercentage() {
        return overallPassPercentage;
    }

    public void setOverallPassPercentage(double overallPassPercentage) {
        this.overallPassPercentage = overallPassPercentage;
    }
}
