package erp_backend.management.dto;

public class DepartmentPerformanceDTO {
    private String departmentName;
    private long totalStudents;
    private double attendancePercentage;
    private double passPercentage;
    private double feeCollectionPercentage;

    public DepartmentPerformanceDTO() {
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public double getPassPercentage() {
        return passPercentage;
    }

    public void setPassPercentage(double passPercentage) {
        this.passPercentage = passPercentage;
    }

    public double getFeeCollectionPercentage() {
        return feeCollectionPercentage;
    }

    public void setFeeCollectionPercentage(double feeCollectionPercentage) {
        this.feeCollectionPercentage = feeCollectionPercentage;
    }
}
