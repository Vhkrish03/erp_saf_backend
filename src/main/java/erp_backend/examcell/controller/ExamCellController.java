package erp_backend.examcell.controller;

import erp_backend.examcell.entity.ExamCellResult;
import erp_backend.examcell.entity.ExamCellResultAudit;
import erp_backend.examcell.service.ExamCellService;
import erp_backend.entity.Student;
import erp_backend.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST endpoints for the Exam Cell module.
 *
 * Role separation:
 * /api/exam-cell/** → Exam Cell officers (enter, verify, approve, publish)
 * /api/exam-cell/student/** → Student read-only (published only)
 * /api/exam-cell/class/** → Teacher/HOD class-level views
 */
@RestController
@RequestMapping("/api/exam-cell")
@CrossOrigin("*")
public class ExamCellController {

    private final ExamCellService examCellService;
    private final StudentRepository studentRepository;

    public ExamCellController(ExamCellService examCellService,
            StudentRepository studentRepository) {
        this.examCellService = examCellService;
        this.studentRepository = studentRepository;
    }

    // ── Exam Cell: Save / Update Result (DRAFT) ───────────────────────────────

    /**
     * POST /api/exam-cell/results?performedBy=EMP001&role=EXAM_CELL
     * Create or update an official exam result (only EXAM_CELL / ADMIN).
     */
    @PostMapping("/results")
    public ResponseEntity<?> saveResult(
            @RequestBody ExamCellResult result,
            @RequestParam String performedBy,
            @RequestParam(defaultValue = "EXAM_CELL") String role) {
        try {
            return ResponseEntity.ok(examCellService.saveOrUpdateResult(result, performedBy, role));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }

    /**
     * POST /api/exam-cell/results/bulk?performedBy=EMP001&role=EXAM_CELL
     * Bulk save student exam results.
     */
    @PostMapping("/results/bulk")
    public ResponseEntity<?> saveResultBulk(
            @RequestBody List<ExamCellResult> results,
            @RequestParam String performedBy,
            @RequestParam(defaultValue = "EXAM_CELL") String role) {
        try {
            return ResponseEntity.ok(examCellService.saveOrUpdateResultBulk(results, performedBy, role));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }

    // ── Workflow Transitions ──────────────────────────────────────────────────

    /**
     * POST /api/exam-cell/results/{id}/verify
     * Exam Cell internally verifies the entered data. DRAFT → VERIFIED.
     */
    @PostMapping("/results/{id}/verify")
    public ResponseEntity<?> verifyResult(
            @PathVariable Long id,
            @RequestParam String performedBy,
            @RequestParam(defaultValue = "EXAM_CELL") String role,
            @RequestParam(required = false) String comments) {
        try {
            return ResponseEntity.ok(examCellService.verifyResult(id, performedBy, role, comments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/exam-cell/results/{id}/approve
     * Dean / authorized authority approves. VERIFIED → APPROVED.
     */
    @PostMapping("/results/{id}/approve")
    public ResponseEntity<?> approveResult(
            @PathVariable Long id,
            @RequestParam String performedBy,
            @RequestParam(defaultValue = "DEAN") String role,
            @RequestParam(required = false) String comments) {
        try {
            return ResponseEntity.ok(examCellService.approveResult(id, performedBy, role, comments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/exam-cell/results/{id}/publish
     * Exam Cell / Admin publishes. APPROVED → PUBLISHED.
     * After this, students can view their results.
     */
    @PostMapping("/results/{id}/publish")
    public ResponseEntity<?> publishResult(
            @PathVariable Long id,
            @RequestParam String performedBy,
            @RequestParam(defaultValue = "EXAM_CELL") String role) {
        try {
            return ResponseEntity.ok(examCellService.publishResult(id, performedBy, role));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/exam-cell/results/{id}/return
     * Return a result back to DRAFT for correction.
     */
    @PostMapping("/results/{id}/return")
    public ResponseEntity<?> returnForCorrection(
            @PathVariable Long id,
            @RequestParam String performedBy,
            @RequestParam(defaultValue = "EXAM_CELL") String role,
            @RequestParam(required = false) String reason) {
        try {
            return ResponseEntity.ok(examCellService.returnForCorrection(id, performedBy, role, reason));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Exam Cell: Query Endpoints ────────────────────────────────────────────

    /** GET all results for a student (any status) — Exam Cell / Admin view */
    @GetMapping("/results/student/{studentId}")
    public ResponseEntity<List<ExamCellResult>> getAllResultsForStudent(
            @PathVariable String studentId) {
        return ResponseEntity.ok(examCellService.getAllResultsForStudent(studentId));
    }

    /** GET results by status — for Exam Cell dashboard */
    @GetMapping("/results/status/{status}")
    public ResponseEntity<List<ExamCellResult>> getResultsByStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(examCellService.getResultsByStatus(status));
    }

    /** GET single result by ID */
    @GetMapping("/results/{id}")
    public ResponseEntity<?> getResultById(@PathVariable Long id) {
        return examCellService.getResultById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET audit trail for a result */
    @GetMapping("/results/{id}/audit")
    public ResponseEntity<List<ExamCellResultAudit>> getAuditTrail(@PathVariable Long id) {
        return ResponseEntity.ok(examCellService.getAuditTrail(id));
    }

    // ── Class-Level View (Exam Cell / Teacher / HOD read) ────────────────────

    /**
     * GET class-wise result summary for dashboard.
     * Returns one row per student with result status.
     */
    @GetMapping("/results/class")
    public ResponseEntity<?> getClassResultSummary(
            @RequestParam String department,
            @RequestParam String semester,
            @RequestParam String academicYear,
            @RequestParam(required = false) String section) {
        try {
            List<Student> students;
            if (section != null && !section.isBlank()) {
                students = studentRepository.findByDepartmentAndSemesterAndSection(department, semester, section);
            } else {
                students = studentRepository.findByDepartmentAndSemester(department, semester);
            }
            List<Map<String, Object>> summary = examCellService.getClassResultSummary(department, semester,
                    academicYear, students);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Student View (Published Only) ─────────────────────────────────────────

    /**
     * GET /api/exam-cell/student/{studentId}/results
     * Student sees ONLY their own PUBLISHED results.
     */
    @GetMapping("/student/{studentId}/results")
    public ResponseEntity<List<ExamCellResult>> getPublishedResultsForStudent(
            @PathVariable String studentId) {
        return ResponseEntity.ok(examCellService.getPublishedResultsForStudent(studentId));
    }

    /**
     * GET /api/exam-cell/student/{studentId}/results/{semester}
     * Student sees PUBLISHED result for a specific semester.
     */
    @GetMapping("/student/{studentId}/results/{semester}")
    public ResponseEntity<?> getPublishedResultForSemester(
            @PathVariable String studentId,
            @PathVariable String semester) {
        return examCellService.getPublishedResultForStudentAndSemester(studentId, semester)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
