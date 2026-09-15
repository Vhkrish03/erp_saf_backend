package erp_backend.management.dto;

import java.util.List;

public class ManagementDashboardDataDTO {

    private InstitutionOverviewDTO institutionOverview;
    private StudentOverviewDTO studentOverview;
    private StaffOverviewDTO staffOverview;
    private AcademicOverviewDTO academicPerformance;
    private AttendanceOverviewDTO attendanceOverview;
    private ExaminationOverviewDTO examinationOverview;
    private FinancialOverviewDTO feeOverview;
    private AdministrationOverviewDTO actionCenter;
    private List<DepartmentPerformanceDTO> departmentAttendance;

    public ManagementDashboardDataDTO() {
    }

    public InstitutionOverviewDTO getInstitutionOverview() {
        return institutionOverview;
    }

    public void setInstitutionOverview(InstitutionOverviewDTO institutionOverview) {
        this.institutionOverview = institutionOverview;
    }

    public StudentOverviewDTO getStudentOverview() {
        return studentOverview;
    }

    public void setStudentOverview(StudentOverviewDTO studentOverview) {
        this.studentOverview = studentOverview;
    }

    public StaffOverviewDTO getStaffOverview() {
        return staffOverview;
    }

    public void setStaffOverview(StaffOverviewDTO staffOverview) {
        this.staffOverview = staffOverview;
    }

    public AcademicOverviewDTO getAcademicPerformance() {
        return academicPerformance;
    }

    public void setAcademicPerformance(AcademicOverviewDTO academicPerformance) {
        this.academicPerformance = academicPerformance;
    }

    public AttendanceOverviewDTO getAttendanceOverview() {
        return attendanceOverview;
    }

    public void setAttendanceOverview(AttendanceOverviewDTO attendanceOverview) {
        this.attendanceOverview = attendanceOverview;
    }

    public ExaminationOverviewDTO getExaminationOverview() {
        return examinationOverview;
    }

    public void setExaminationOverview(ExaminationOverviewDTO examinationOverview) {
        this.examinationOverview = examinationOverview;
    }

    public FinancialOverviewDTO getFeeOverview() {
        return feeOverview;
    }

    public void setFeeOverview(FinancialOverviewDTO feeOverview) {
        this.feeOverview = feeOverview;
    }

    public AdministrationOverviewDTO getActionCenter() {
        return actionCenter;
    }

    public void setActionCenter(AdministrationOverviewDTO actionCenter) {
        this.actionCenter = actionCenter;
    }

    public List<DepartmentPerformanceDTO> getDepartmentAttendance() {
        return departmentAttendance;
    }

    public void setDepartmentAttendance(List<DepartmentPerformanceDTO> departmentAttendance) {
        this.departmentAttendance = departmentAttendance;
    }
}
