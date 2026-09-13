package erp_backend.finance_department.controller;

import erp_backend.finance_department.dto.FinanceDashboardDto;
import erp_backend.finance_department.service.FinanceDepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/finance_department")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FinanceDepartmentController {

    private final FinanceDepartmentService financeDepartmentService;

    public FinanceDepartmentController(FinanceDepartmentService financeDepartmentService) {
        this.financeDepartmentService = financeDepartmentService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<FinanceDashboardDto> getDashboardOverview() {
        return ResponseEntity.ok(financeDepartmentService.getDashboardData());
    }

    @GetMapping("/fee-structures")
    public ResponseEntity<?> getAllFeeStructures() {
        return ResponseEntity.ok(financeDepartmentService.getAllFeeStructures());
    }

    @PostMapping("/fee-structures/{id}/approve")
    public ResponseEntity<?> approveFeeStructure(@PathVariable Long id, @RequestParam String approvedBy) {
        try {
            return ResponseEntity.ok(financeDepartmentService.approveFeeStructure(id, approvedBy));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/fee-structures/{id}/reject")
    public ResponseEntity<?> rejectFeeStructure(@PathVariable Long id, @RequestParam String rejectedBy, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(financeDepartmentService.rejectFeeStructure(id, rejectedBy, body.get("reason")));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/fee-structures/{id}/publish")
    public ResponseEntity<?> publishFeeStructure(@PathVariable Long id, @RequestParam String publishedBy) {
        try {
            return ResponseEntity.ok(financeDepartmentService.publishFeeStructure(id, publishedBy));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
