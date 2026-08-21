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
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

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

    @GetMapping("/{studentId}")
    public ResponseEntity<List<Map<String, Object>>> getOldFeesCompatibility(@PathVariable String studentId) {
        List<StudentFeeDto> studentFees = feeService.getFeesForStudent(studentId);
        List<Map<String, Object>> response = studentFees.stream().map(sf -> {
            Map<String, Object> map = new HashMap<>();
            map.put("particular", sf.getFeeCategory());
            map.put("amount", sf.getTotalFee());
            map.put("isPaid", "PAID".equalsIgnoreCase(sf.getPaymentStatus()));
            map.put("paid", "PAID".equalsIgnoreCase(sf.getPaymentStatus()));
            map.put("dueDate", sf.getDueDate());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
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

    @PostMapping("/student-fees/configure-custom")
    public ResponseEntity<?> configureCustomFees(@RequestBody Map<String, Object> req) {
        try {
            String studentId = (String) req.get("studentId");
            String academicYear = (String) req.get("academicYear");
            String semester = (String) req.get("semester");

            double tuitionFee = Double.parseDouble(req.get("tuitionFee").toString());
            double messFee = Double.parseDouble(req.get("messFee").toString());
            double trainingFee = Double.parseDouble(req.get("trainingFee").toString());
            double otherFee = Double.parseDouble(req.get("otherFee").toString());
            double transportFee = Double.parseDouble(req.get("transportFee").toString());
            double hostelFee = Double.parseDouble(req.get("hostelFee").toString());

            feeService.configureCustomFeesForStudent(
                    studentId, academicYear, semester,
                    tuitionFee, messFee, trainingFee, otherFee, transportFee, hostelFee);
            return ResponseEntity.ok(Map.of("message", "Custom fees configured successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
