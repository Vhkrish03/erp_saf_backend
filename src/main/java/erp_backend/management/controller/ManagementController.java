package erp_backend.management.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import erp_backend.management.dto.*;
import erp_backend.management.service.ManagementService;

@RestController
@RequestMapping("/api/management")
@CrossOrigin(origins = "*")
public class ManagementController {

    private final ManagementService managementService;

    public ManagementController(ManagementService managementService) {
        this.managementService = managementService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ManagementDashboardDataDTO> getDashboardData(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String academicYear,
            @RequestParam(required = false) String semester) {
        return ResponseEntity.ok(managementService.getDashboardData(department, academicYear, semester));
    }

    @GetMapping("/search")
    public ResponseEntity<java.util.List<SearchResultDTO>> globalSearch(@RequestParam String q) {
        return ResponseEntity.ok(managementService.globalSearch(q));
    }

    @GetMapping("/institution-overview")
    public ResponseEntity<InstitutionOverviewDTO> getInstitutionOverview() {
        return ResponseEntity.ok(managementService.getInstitutionOverview());
    }

    @GetMapping("/students/overview")
    public ResponseEntity<StudentOverviewDTO> getStudentOverview(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String semester) {
        return ResponseEntity.ok(managementService.getStudentOverview(department, year, semester));
    }

    @GetMapping("/staff/overview")
    public ResponseEntity<StaffOverviewDTO> getStaffOverview(
            @RequestParam(required = false) String department) {
        return ResponseEntity.ok(managementService.getStaffOverview(department));
    }

    @GetMapping("/academic/overview")
    public ResponseEntity<AcademicOverviewDTO> getAcademicOverview(
            @RequestParam(required = false) String department) {
        return ResponseEntity.ok(managementService.getAcademicOverview(department));
    }

    @GetMapping("/attendance/overview")
    public ResponseEntity<AttendanceOverviewDTO> getAttendanceOverview(
            @RequestParam(required = false) String department) {
        return ResponseEntity.ok(managementService.getAttendanceOverview(department));
    }

    @GetMapping("/examinations/overview")
    public ResponseEntity<ExaminationOverviewDTO> getExaminationOverview(
            @RequestParam(required = false) String department) {
        return ResponseEntity.ok(managementService.getExaminationOverview(department));
    }

    @GetMapping("/finance/overview")
    public ResponseEntity<FinancialOverviewDTO> getFinancialOverview(
            @RequestParam(required = false) String department) {
        return ResponseEntity.ok(managementService.getFinancialOverview(department));
    }

    @GetMapping("/administration/overview")
    public ResponseEntity<AdministrationOverviewDTO> getAdministrationOverview(
            @RequestParam(required = false) String department) {
        return ResponseEntity.ok(managementService.getAdministrationOverview(department));
    }

    @GetMapping("/transport/overview")
    public ResponseEntity<TransportOverviewDTO> getTransportOverview() {
        return ResponseEntity.ok(managementService.getTransportOverview());
    }

    @GetMapping("/hostel/overview")
    public ResponseEntity<HostelOverviewDTO> getHostelOverview() {
        return ResponseEntity.ok(managementService.getHostelOverview());
    }

    @GetMapping("/department-performance/all")
    public ResponseEntity<java.util.List<DepartmentPerformanceDTO>> getAllDepartmentPerformances() {
        return ResponseEntity.ok(managementService.getAllDepartmentPerformances());
    }

    @GetMapping("/fee-decisions")
    public ResponseEntity<java.util.List<FeeDecisionDto>> getAllFeeDecisions() {
        return ResponseEntity.ok(managementService.getAllFeeDecisions());
    }

    @PostMapping("/fee-decisions")
    public ResponseEntity<FeeDecisionDto> createFeeDecision(@RequestBody FeeDecisionDto dto) {
        return ResponseEntity.ok(managementService.createFeeDecision(dto));
    }

    @PutMapping("/fee-decisions/{id}/status")
    public ResponseEntity<FeeDecisionDto> updateFeeDecisionStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(managementService.updateFeeDecisionStatus(id, status));
    }
}
