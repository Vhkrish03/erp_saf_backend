package erp_backend.fees.dto;

/** Summary statistics for a class section's fee compliance. */
public class FeeDashboardDto {
    private String department;
    private String semester;
    private String section;
    private String academicYear;

    private int totalStudents;
    private int paidCount;
    private int partiallyPaidCount;
    private int pendingCount;
    private int overdueCount;

    private double totalFeeAmount;
    private double collectedAmount;
    private double outstandingAmount;

    public FeeDashboardDto() {
    }

    public FeeDashboardDto(String department, String semester, String section, String academicYear) {
        this.department = department;
        this.semester = semester;
        this.section = section;
        this.academicYear = academicYear;
    }

    // Getters
    public String getDepartment() {
        return department;
    }

    public String getSemester() {
        return semester;
    }

    public String getSection() {
        return section;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public int getPaidCount() {
        return paidCount;
    }

    public int getPartiallyPaidCount() {
        return partiallyPaidCount;
    }

    public int getPendingCount() {
        return pendingCount;
    }

    public int getOverdueCount() {
        return overdueCount;
    }

    public double getTotalFeeAmount() {
        return totalFeeAmount;
    }

    public double getCollectedAmount() {
        return collectedAmount;
    }

    public double getOutstandingAmount() {
        return outstandingAmount;
    }

    // Setters
    public void setDepartment(String v) {
        this.department = v;
    }

    public void setSemester(String v) {
        this.semester = v;
    }

    public void setSection(String v) {
        this.section = v;
    }

    public void setAcademicYear(String v) {
        this.academicYear = v;
    }

    public void setTotalStudents(int v) {
        this.totalStudents = v;
    }

    public void setPaidCount(int v) {
        this.paidCount = v;
    }

    public void setPartiallyPaidCount(int v) {
        this.partiallyPaidCount = v;
    }

    public void setPendingCount(int v) {
        this.pendingCount = v;
    }

    public void setOverdueCount(int v) {
        this.overdueCount = v;
    }

    public void setTotalFeeAmount(double v) {
        this.totalFeeAmount = v;
    }

    public void setCollectedAmount(double v) {
        this.collectedAmount = v;
    }

    public void setOutstandingAmount(double v) {
        this.outstandingAmount = v;
    }
}
