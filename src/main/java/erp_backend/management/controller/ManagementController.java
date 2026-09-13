package erp_backend.management.controller;

import org.springframework.web.bind.annotation.*;

import erp_backend.management.dto.InstitutionOverviewDTO;
import erp_backend.management.dto.StudentOverviewDTO;
import erp_backend.management.dto.StaffOverviewDTO;
import erp_backend.management.service.ManagementService;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/management")
@CrossOrigin(origins = "*") // Follow existing CORS policy or use specific policy if defined
public class ManagementController {

    private final ManagementService managementService;

    public ManagementController(ManagementService managementService) {
        this.managementService = managementService;
    }

    @GetMapping("/institution-overview")
    public ResponseEntity<InstitutionOverviewDTO> getInstitutionOverview() {
        // Simple manual role check can be added here if no Spring Security interceptor
        // exists
        // However, standard Spring configuration should handle role checking.
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
}
