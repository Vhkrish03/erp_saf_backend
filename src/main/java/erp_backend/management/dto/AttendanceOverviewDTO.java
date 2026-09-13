package erp_backend.management.dto;

import java.util.Map;

public class AttendanceOverviewDTO {
    private double overallPercentage;
    private long totalRecords;
    private long presentRecords;
    private Map<String, Double> departmentWiseAttendance;

    public AttendanceOverviewDTO() {
    }

    public double getOverallPercentage() {
        return overallPercentage;
    }

    public void setOverallPercentage(double overallPercentage) {
        this.overallPercentage = overallPercentage;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public long getPresentRecords() {
        return presentRecords;
    }

    public void setPresentRecords(long presentRecords) {
        this.presentRecords = presentRecords;
    }

    public Map<String, Double> getDepartmentWiseAttendance() {
        return departmentWiseAttendance;
    }

    public void setDepartmentWiseAttendance(Map<String, Double> departmentWiseAttendance) {
        this.departmentWiseAttendance = departmentWiseAttendance;
    }
}
