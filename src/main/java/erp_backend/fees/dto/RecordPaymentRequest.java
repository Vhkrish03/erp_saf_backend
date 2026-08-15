package erp_backend.fees.dto;

/** Request body for recording a fee payment. */
public class RecordPaymentRequest {
    private String studentId;
    private Long studentFeeId;
    private double amountPaid;
    private String paymentDate; // ISO date: yyyy-MM-dd
    private String paymentMode; // CASH | ONLINE | DD | CHEQUE | NEFT | UPI
    private String transactionId;
    private String paymentReference;
    private String remarks;
    private String recordedBy; // employee ID
    private String academicYear;
    private String semester;
    private String feeCategory;

    // Getters
    public String getStudentId() {
        return studentId;
    }

    public Long getStudentFeeId() {
        return studentFeeId;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getRecordedBy() {
        return recordedBy;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public String getSemester() {
        return semester;
    }

    public String getFeeCategory() {
        return feeCategory;
    }

    // Setters
    public void setStudentId(String v) {
        this.studentId = v;
    }

    public void setStudentFeeId(Long v) {
        this.studentFeeId = v;
    }

    public void setAmountPaid(double v) {
        this.amountPaid = v;
    }

    public void setPaymentDate(String v) {
        this.paymentDate = v;
    }

    public void setPaymentMode(String v) {
        this.paymentMode = v;
    }

    public void setTransactionId(String v) {
        this.transactionId = v;
    }

    public void setPaymentReference(String v) {
        this.paymentReference = v;
    }

    public void setRemarks(String v) {
        this.remarks = v;
    }

    public void setRecordedBy(String v) {
        this.recordedBy = v;
    }

    public void setAcademicYear(String v) {
        this.academicYear = v;
    }

    public void setSemester(String v) {
        this.semester = v;
    }

    public void setFeeCategory(String v) {
        this.feeCategory = v;
    }
}
