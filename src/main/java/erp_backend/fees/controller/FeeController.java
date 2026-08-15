package erp_backend.fees.controller;

import erp_backend.fees.dto.FeeDashboardDto;
import erp_backend.fees.dto.RecordPaymentRequest;
import erp_backend.fees.dto.StudentFeeDto;
import erp_backend.fees.entity.FeePayment;
import erp_backend.fees.entity.FeeStructure;
import erp_backend.fees.entity.StudentFee;
import erp_backend.fees.service.FeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for the Fees Management module.
 *
 * Base path: /api/fees
 *
 * Fee Structure (Admin):
 * POST /api/fees/structure - Create fee structure
 * GET /api/fees/structure - List all active structures
 * GET /api/fees/structure/class - Get structures for a dept/sem/ay
 *
 * Student Fees (Class Incharge):
 * POST /api/fees/student-fees/assign - Assign fee structure to student
 * GET /api/fees/student-fees/student/{studentId} - Student's fee records
 * GET /api/fees/student-fees/class - All fees for a class section
 * GET /api/fees/student-fees/pending - Pending fees for a class section
 *
 * Payments:
 * POST /api/fees/payments - Record a payment
 * GET /api/fees/payments/student/{studentId} - Payment history
 *
 * Dashboard / Compliance:
 * GET /api/fees/dashboard - Aggregated stats for a class
 */
@RestController
@RequestMapping("/api/fees")
@CrossOrigin("*")
public class FeeController {

    private final FeeService feeService;

    public FeeController(FeeService feeService) {
        this.feeService = feeService;
    }

    // ────────────────────── Fee Structure ──────────────────────

    @PostMapping("/structure")
    public ResponseEntity<FeeStructure> createFeeStructure(@RequestBody FeeStructure feeStructure) {
        try {
            return ResponseEntity.ok(feeService.createFeeStructure(feeStructure));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/structure")
    public ResponseEntity<List<FeeStructure>> getAllFeeStructures() {
        return ResponseEntity.ok(feeService.getAllFeeStructures());
    }

    @GetMapping("/structure/class")
    public ResponseEntity<List<FeeStructure>> getFeeStructuresForClass(
            @RequestParam String department,
            @RequestParam String semester,
            @RequestParam String academicYear) {
        return ResponseEntity.ok(feeService.getFeeStructuresForClass(department, semester, academicYear));
    }

    // ───────────────────── Student Fees ─────────────────────────

    @PostMapping("/student-fees/assign")
    public ResponseEntity<StudentFee> assignFee(
            @RequestParam String studentId,
            @RequestParam Long feeStructureId) {
        try {
            return ResponseEntity.ok(feeService.assignFeeToStudent(studentId, feeStructureId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/student-fees/student/{studentId}")
    public ResponseEntity<List<StudentFeeDto>> getFeesForStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(feeService.getFeesForStudent(studentId));
    }

    @GetMapping("/student-fees/class")
    public ResponseEntity<List<StudentFeeDto>> getFeesForClass(
            @RequestParam String department,
            @RequestParam String semester,
            @RequestParam String section,
            @RequestParam String academicYear) {
        return ResponseEntity.ok(feeService.getFeesForClass(department, semester, section, academicYear));
    }

    @GetMapping("/student-fees/pending")
    public ResponseEntity<List<StudentFeeDto>> getPendingFees(
            @RequestParam String department,
            @RequestParam String semester,
            @RequestParam String section,
            @RequestParam String academicYear) {
        return ResponseEntity.ok(feeService.getPendingFeesForClass(department, semester, section, academicYear));
    }

    // ─────────────────────── Payments ───────────────────────────

    @PostMapping("/payments")
    public ResponseEntity<FeePayment> recordPayment(@RequestBody RecordPaymentRequest request) {
        try {
            return ResponseEntity.ok(feeService.recordPayment(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/payments/student/{studentId}")
    public ResponseEntity<List<FeePayment>> getPaymentHistory(@PathVariable String studentId) {
        return ResponseEntity.ok(feeService.getPaymentHistoryForStudent(studentId));
    }

    // ───────────────────── Dashboard ────────────────────────────

    @GetMapping("/dashboard")
    public ResponseEntity<FeeDashboardDto> getFeeDashboard(
            @RequestParam String department,
            @RequestParam String semester,
            @RequestParam String section,
            @RequestParam String academicYear) {
        try {
            return ResponseEntity.ok(feeService.getDashboard(department, semester, section, academicYear));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
