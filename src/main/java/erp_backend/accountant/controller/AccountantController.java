package erp_backend.accountant.controller;

import erp_backend.accountant.service.AccountantService;
import erp_backend.fees.dto.RecordPaymentRequest;
import erp_backend.fees.dto.StudentFeeDto;
import erp_backend.fees.entity.FeePayment;
import erp_backend.finance_department.dto.FinanceDashboardDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller strictly for day-to-day Accountant operations.
 * Mounts on /api/accountant/*
 */
@RestController
@RequestMapping("/api/accountant")
@CrossOrigin("*")
public class AccountantController {

    private final AccountantService accountantService;

    public AccountantController(AccountantService accountantService) {
        this.accountantService = accountantService;
    }

    /**
     * Dashboard Overview Endpoint for Accountants.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<FinanceDashboardDto> getDashboardData() {
        return ResponseEntity.ok(accountantService.getAccountantDashboard());
    }

    /**
     * Retrieve Fees for a Specific Student.
     */
    @GetMapping("/student-fees/{studentId}")
    public ResponseEntity<List<StudentFeeDto>> getStudentFees(@PathVariable String studentId) {
        return ResponseEntity.ok(accountantService.getStudentFees(studentId));
    }

    /**
     * Retrieve pending/all fees based on filters
     */
    @GetMapping("/fees/filter")
    public ResponseEntity<List<StudentFeeDto>> getFeesByFilter(
            @RequestParam String department,
            @RequestParam String academicYear,
            @RequestParam String semester,
            @RequestParam(required = false, defaultValue = "false") boolean pendingOnly) {
        return ResponseEntity.ok(accountantService.getFeesByFilter(department, academicYear, semester, pendingOnly));
    }

    /**
     * Record a Payment externally received by the accountant.
     */
    @PostMapping("/payments")
    public ResponseEntity<FeePayment> recordPayment(@RequestBody RecordPaymentRequest request) {
        try {
            return ResponseEntity.ok(accountantService.recordPayment(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
