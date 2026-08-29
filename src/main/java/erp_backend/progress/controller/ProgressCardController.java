package erp_backend.progress.controller;

import erp_backend.progress.entity.PerformanceRemark;
import erp_backend.progress.service.ProgressCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Progress Card REST Controller.
 *
 * Routes:
 * /api/progress/student/{studentId} → Student sees own card (FINALIZED +
 * PUBLISHED only)
 * /api/progress/staff/student/{studentId} → Staff sees full card (all statuses
 * + workflow context)
 * /api/progress/class → Class Incharge / HOD class-level summary
 * /api/progress/remarks/** → Remark management
 * /api/progress/internal-marks/calculate → Trigger internal mark calculation
 */
@RestController
@RequestMapping("/api/progress")
@CrossOrigin("*")
public class ProgressCardController {

    private final ProgressCardService progressCardService;

    public ProgressCardController(ProgressCardService progressCardService) {
        this.progressCardService = progressCardService;
    }

    // ── Student Access (FINALIZED internal + PUBLISHED exam only) ─────────────

    /**
     * GET /api/progress/student/{studentId}?semester=S5
     *
     * Returns the official verified Progress Card for a student.
     * - Internal marks: only DEAN_SUBMITTED (finalized) assessments
     * - Semester result: only PUBLISHED Exam Cell results
     * - Performance overview: calculated from DB thresholds
     *
     * IMPORTANT: Backend enforces studentId scoping — a student can ONLY access
     * their own card (enforce via JWT/session in production auth layer).
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentProgressCard(
            @PathVariable String studentId,
            @RequestParam String semester) {
        try {
            return ResponseEntity.ok(progressCardService.getProgressCard(studentId, semester));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to build progress card."));
        }
    }

    // ── Staff Access (all statuses + workflow context) ─────────────────────────

    /**
     * GET /api/progress/staff/student/{studentId}?semester=S5
     *
     * Returns full progress data including pending/draft/submitted statuses.
     * For use by: Teacher, Class Incharge, HOD, Dean.
     */
    @GetMapping("/staff/student/{studentId}")
    public ResponseEntity<?> getStaffProgressCard(
            @PathVariable String studentId,
            @RequestParam String semester) {
        try {
            return ResponseEntity.ok(progressCardService.getProgressCardForStaff(studentId, semester));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to build staff progress card."));
        }
    }

    // ── Class-Level View ─────────────────────────────────────────────────────

    /**
     * GET
     * /api/progress/class?department=CSE&semester=S5&section=A&academicYear=2025-26
     *
     * Returns class progress summary for Class Incharge / HOD dashboard.
     * Shows one row per student with internal mark summary and assessment status.
     */
    @GetMapping("/class")
    public ResponseEntity<?> getClassProgress(
            @RequestParam String department,
            @RequestParam String semester,
            @RequestParam String section,
            @RequestParam String academicYear) {
        try {
            return ResponseEntity.ok(
                    progressCardService.getClassProgress(department, semester, section, academicYear));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Internal Mark Calculation ─────────────────────────────────────────────

    /**
     * POST /api/progress/internal-marks/calculate
     * Body: { "studentId": "STU001", "subjectId": 5, "semester": "S5",
     * "academicYear": "2025-26" }
     *
     * Triggers backend calculation of internal marks using configured weightage.
     * For use by: Admin / HOD / Dean (as a batch job trigger or manual).
     */
    @PostMapping("/internal-marks/calculate")
    public ResponseEntity<?> calculateInternalMark(@RequestBody Map<String, Object> req) {
        try {
            String studentId = (String) req.get("studentId");
            Long subjectId = Long.valueOf(req.get("subjectId").toString());
            String semester = (String) req.get("semester");
            String academicYear = (String) req.get("academicYear");

            return ResponseEntity.ok(
                    progressCardService.calculateAndStoreInternalMark(studentId, subjectId, semester, academicYear));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Remarks ───────────────────────────────────────────────────────────────

    /**
     * POST /api/progress/remarks
     * Body: { studentId, semester, academicYear, remarkBy, remarkByRole,
     * remarkByName, remarkText }
     *
     * Adds a new performance remark. Does NOT delete previous remarks
     * (append-only).
     */
    @PostMapping("/remarks")
    public ResponseEntity<?> addRemark(@RequestBody Map<String, Object> req) {
        try {
            PerformanceRemark remark = progressCardService.addRemark(
                    (String) req.get("studentId"),
                    (String) req.get("semester"),
                    (String) req.get("academicYear"),
                    (String) req.get("remarkBy"),
                    (String) req.get("remarkByRole"),
                    (String) req.get("remarkByName"),
                    (String) req.get("remarkText"));
            return ResponseEntity.ok(remark);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
