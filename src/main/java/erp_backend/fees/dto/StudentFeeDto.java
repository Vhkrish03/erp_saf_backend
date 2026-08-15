package erp_backend.fees.dto;

/** Lightweight DTO returned per student in the class fee list. */
public class StudentFeeDto {
    private Long studentFeeId;
    private String studentId;
    private String studentName;
    private String rollNumber;
    private String department;
    private String semester;
    private String section;
    private String academicYear;
    private String feeCategory;
    private double totalFee;
    private double amountPaid;
    private double balanceAmount;
    private String paymentStatus; // PAID | PARTIALLY_PAID | PENDING | OVERDUE | WAIVED
    private String dueDate;
    private String remarks;
    private String updatedAt;

    // Getters
    public Long getStudentFeeId() {
        return studentFeeId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

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

    public String getFeeCategory() {
        return feeCategory;
    }

    public double getTotalFee() {
        return totalFee;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public double getBalanceAmount() {
        return balanceAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setStudentFeeId(Long v) {
        this.studentFeeId = v;
    }

    public void setStudentId(String v) {
        this.studentId = v;
    }

    public void setStudentName(String v) {
        this.studentName = v;
    }

    public void setRollNumber(String v) {
        this.rollNumber = v;
    }

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

    public void setFeeCategory(String v) {
        this.feeCategory = v;
    }

    public void setTotalFee(double v) {
        this.totalFee = v;
    }

    public void setAmountPaid(double v) {
        this.amountPaid = v;
    }

    public void setBalanceAmount(double v) {
        this.balanceAmount = v;
    }

    public void setPaymentStatus(String v) {
        this.paymentStatus = v;
    }

    public void setDueDate(String v) {
        this.dueDate = v;
    }

    public void setRemarks(String v) {
        this.remarks = v;
    }

    public void setUpdatedAt(String v) {
        this.updatedAt = v;
    }
}
