package erp_backend.examcell.controller;

import erp_backend.examcell.entity.ExamCellResult;
import erp_backend.examcell.entity.ExamCellResultAudit;
import erp_backend.examcell.entity.Examination;
import erp_backend.examcell.entity.ExamRegistration;
import erp_backend.examcell.service.ExamCellService;
import erp_backend.examcell.service.ExaminationService;
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
    private final ExaminationService examinationService;
    private final StudentRepository studentRepository;

    public ExamCellController(ExamCellService examCellService,
            ExaminationService examinationService,
            StudentRepository studentRepository) {
        this.examCellService = examCellService;
        this.examinationService = examinationService;
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
     * POST /api/exam-cell/results/{id}/submit
     * Exam Cell finishes entry and submits for CoE Approval. DRAFT/REJECTED →
     * SUBMITTED.
     */
    @PostMapping("/results/{id}/submit")
    public ResponseEntity<?> submitResult(
            @PathVariable Long id,
            @RequestParam String performedBy,
            @RequestParam(defaultValue = "EXAM_CELL") String role,
            @RequestParam(required = false) String comments) {
        try {
            return ResponseEntity.ok(examCellService.submitResult(id, performedBy, role, comments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/exam-cell/results/{id}/approve
     * CoE approves the result. SUBMITTED → APPROVED.
     */
    @PostMapping("/results/{id}/approve")
    public ResponseEntity<?> approveResult(
            @PathVariable Long id,
            @RequestParam String performedBy,
            @RequestParam(defaultValue = "COE") String role,
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
     * POST /api/exam-cell/results/{id}/reject
     * CoE rejects the result. SUBMITTED → REJECTED.
     */
    @PostMapping("/results/{id}/reject")
    public ResponseEntity<?> rejectResult(
            @PathVariable Long id,
            @RequestParam String performedBy,
            @RequestParam(defaultValue = "COE") String role,
            @RequestParam(required = false) String reason) {
        try {
            return ResponseEntity.ok(examCellService.rejectResult(id, performedBy, role, reason));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/exam-cell/results/{id}
     * Delete a semester result.
     */
    @DeleteMapping("/results/{id}")
    public ResponseEntity<?> deleteResult(
            @PathVariable Long id,
            @RequestParam String performedBy,
            @RequestParam(defaultValue = "EXAM_CELL") String role) {
        try {
            examCellService.deleteResult(id, performedBy, role);
            return ResponseEntity.ok(Map.of("message", "Result deleted successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/exam-cell/results/batch
     * Delete an entire batch of results.
     */
    @DeleteMapping("/results/batch")
    public ResponseEntity<?> deleteBatch(
            @RequestParam String department,
            @RequestParam String semesterName,
            @RequestParam String academicYear,
            @RequestParam String examSession,
            @RequestParam String performedBy,
            @RequestParam(defaultValue = "EXAM_CELL") String role) {
        try {
            examCellService.deleteBatch(department, semesterName, academicYear, examSession, performedBy, role);
            return ResponseEntity.ok(Map.of("message", "Batch deleted successfully"));
        } catch (Exception e) {
            e.printStackTrace();
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
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String section) {
        try {
            List<Student> students;
            if (year != null && !year.isBlank()) {
                List<String> yearVariants = getYearVariants(year);
                if (section != null && !section.isBlank()) {
                    students = studentRepository.findByDepartmentAndYearInAndSection(department, yearVariants, section);
                } else {
                    students = studentRepository.findByDepartmentAndYearIn(department, yearVariants);
                }
            } else {
                // Map S1-S8 to Roman numerals since students are stored with Roman numeral
                // semesters in DB
                String romanSemester = semester;
                if (semester != null) {
                    switch (semester.trim().toUpperCase()) {
                        case "S1":
                        case "1":
                            romanSemester = "I";
                            break;
                        case "S2":
                        case "2":
                            romanSemester = "II";
                            break;
                        case "S3":
                        case "3":
                            romanSemester = "III";
                            break;
                        case "S4":
                        case "4":
                            romanSemester = "IV";
                            break;
                        case "S5":
                        case "5":
                            romanSemester = "V";
                            break;
                        case "S6":
                        case "6":
                            romanSemester = "VI";
                            break;
                        case "S7":
                        case "7":
                            romanSemester = "VII";
                            break;
                        case "S8":
                        case "8":
                            romanSemester = "VIII";
                            break;
                    }
                }
                if (section != null && !section.isBlank()) {
                    students = studentRepository.findByDepartmentAndSemesterAndSection(department, romanSemester,
                            section);
                } else {
                    students = studentRepository.findByDepartmentAndSemester(department, romanSemester);
                }
            }
            List<Map<String, Object>> summary = examCellService.getClassResultSummary(department, semester,
                    academicYear, students);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private List<String> getYearVariants(String year) {
        if (year == null || year.isBlank()) {
            return java.util.Collections.emptyList();
        }
        String clean = year.trim().toUpperCase();
        switch (clean) {
            case "I":
            case "1":
            case "1ST":
            case "1ST YEAR":
                return java.util.Arrays.asList("I", "1", "1st year", "1st");
            case "II":
            case "2":
            case "2ND":
            case "2ND YEAR":
                return java.util.Arrays.asList("II", "2", "2nd year", "2nd");
            case "III":
            case "3":
            case "3RD":
            case "3RD YEAR":
                return java.util.Arrays.asList("III", "3", "3rd year", "3rd");
            case "IV":
            case "4":
            case "4TH":
            case "4TH YEAR":
                return java.util.Arrays.asList("IV", "4", "4th year", "4th");
            default:
                return java.util.Arrays.asList(year, year.toLowerCase(), year.toUpperCase());
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

    // ── Examination Planning & Management ─────────────────────────────────────

    @PostMapping("/examinations")
    public ResponseEntity<?> createExamination(
            @RequestBody Examination examination,
            @RequestParam String performedBy,
            @RequestParam(defaultValue = "EXAM_CELL") String role) {
        try {
            return ResponseEntity.ok(examinationService.createExamination(examination, performedBy));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/examinations/{id}")
    public ResponseEntity<?> updateExamination(
            @PathVariable Long id,
            @RequestBody Examination examination,
            @RequestParam String performedBy) {
        try {
            return ResponseEntity.ok(examinationService.updateExamination(id, examination, performedBy));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/examinations")
    public ResponseEntity<List<Examination>> getAllExaminations() {
        return ResponseEntity.ok(examinationService.getAllExaminations());
    }

    @GetMapping("/departments")
    public ResponseEntity<List<String>> getDepartments() {
        return ResponseEntity.ok(examinationService.getDistinctDepartments());
    }

    @GetMapping("/examinations/{id}")
    public ResponseEntity<?> getExamination(@PathVariable Long id) {
        Examination exam = examinationService.getExamination(id);
        if (exam != null)
            return ResponseEntity.ok(exam);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/examinations/{id}")
    public ResponseEntity<?> deleteExamination(@PathVariable Long id, @RequestParam String performedBy) {
        try {
            examinationService.deleteExamination(id);
            return ResponseEntity.ok(Map.of("message", "Examination deleted."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Examination Registration & Eligibility ───────────────────────────────

    @GetMapping("/examinations/{id}/registrations")
    public ResponseEntity<List<ExamRegistration>> getRegistrations(@PathVariable Long id) {
        return ResponseEntity.ok(examinationService.getRegistrationsForExam(id));
    }

    @PostMapping("/examinations/{id}/generate-eligibility")
    public ResponseEntity<?> generateEligibilityList(@PathVariable Long id) {
        try {
            examinationService.generateEligibilityList(id);
            return ResponseEntity.ok(Map.of("message", "Eligibility list generated."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/registrations/{id}/status")
    public ResponseEntity<?> updateRegistrationStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam boolean feePaid) {
        try {
            examinationService.updateRegistrationStatus(id, status, feePaid);
            return ResponseEntity.ok(Map.of("message", "Registration updated."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/registrations/{id}/verify-payment")
    public ResponseEntity<?> verifyStudentPayment(
            @PathVariable Long id,
            @RequestParam String verifiedBy,
            @RequestParam String status) {
        try {
            return ResponseEntity.ok(examinationService.verifyStudentPayment(id, verifiedBy, status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Student pays exam fee. Records the payment reference and sets status
     * PAYMENT_PENDING.
     * Exam Cell must separately verify via /verify-payment.
     * POST
     * /api/exam-cell/registrations/{id}/pay-fee?paymentReference=TXN123&amountPaid=650
     */
    @PostMapping("/registrations/{id}/pay-fee")
    public ResponseEntity<?> recordStudentPayment(
            @PathVariable Long id,
            @RequestParam String paymentReference,
            @RequestParam double amountPaid) {
        try {
            return ResponseEntity.ok(examinationService.recordStudentPayment(id, paymentReference, amountPaid));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all COE_APPROVED examinations visible to a specific student.
     * Filters by student's department & semester.
     * GET /api/exam-cell/student/{studentId}/approved-exams
     */
    @GetMapping("/student/{studentId}/approved-exams")
    public ResponseEntity<?> getApprovedExamsForStudent(@PathVariable String studentId) {
        try {
            erp_backend.entity.Student student = studentRepository.findById(studentId).orElse(null);
            if (student == null)
                return ResponseEntity.notFound().build();

            List<erp_backend.examcell.entity.Examination> all = examinationService.getAllExaminations();
            List<erp_backend.examcell.entity.Examination> visible = all.stream()
                    .filter(e -> "COE_APPROVED".equals(e.getApprovalStatus()))
                    .filter(e -> normalizeDept(student.getDepartment()).equals(normalizeDept(e.getDepartment())))
                    .filter(e -> e.getSemesterName() == null || e.getSemesterName().isBlank() ||
                            normalizeSem(e.getSemesterName()).equals(normalizeSem(student.getSemester())))
                    .toList();
            return ResponseEntity.ok(visible);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get a student's registration record for a specific exam.
     * GET /api/exam-cell/student/{studentId}/exam/{examId}/registration
     */
    @GetMapping("/student/{studentId}/exam/{examId}/registration")
    public ResponseEntity<?> getStudentRegistration(
            @PathVariable String studentId,
            @PathVariable Long examId) {
        try {
            List<erp_backend.examcell.entity.ExamRegistration> regs = examinationService
                    .getRegistrationsForExam(examId);
            return regs.stream()
                    .filter(r -> r.getStudentId().equals(studentId))
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .findFirst()
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/examinations/{id}/fee-deadline")
    public ResponseEntity<?> setFeeDeadline(
            @PathVariable Long id,
            @RequestParam String deadline) {
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(deadline);
            return ResponseEntity.ok(examinationService.setFeeDeadline(id, date));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── CoE Workflow & Fees ──────────────────────────────────────────────────

    @PostMapping("/examinations/{id}/submit")
    public ResponseEntity<?> submitExamination(
            @PathVariable Long id,
            @RequestParam String performedBy) {
        try {
            return ResponseEntity.ok(examinationService.submitToCoe(id, performedBy));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/examinations/{id}/approve")
    public ResponseEntity<?> approveExamination(
            @PathVariable Long id,
            @RequestParam String performedBy) {
        try {
            return ResponseEntity.ok(examinationService.approveByCoe(id, performedBy));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/examinations/{id}/reject")
    public ResponseEntity<?> rejectExamination(
            @PathVariable Long id,
            @RequestParam String reason,
            @RequestParam String performedBy) {
        try {
            return ResponseEntity.ok(examinationService.rejectByCoe(id, reason, performedBy));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/examinations/{id}/sync-fees")
    public ResponseEntity<?> syncExamFees(@PathVariable Long id) {
        try {
            examinationService.syncExamFeesWithAccountant(id);
            return ResponseEntity.ok(Map.of("message", "Exam fees synced with Accountant module successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }



    // ── Examination Timetable ────────────────────────────────────────────────

    @GetMapping("/examinations/{id}/timetable")
    public ResponseEntity<List<erp_backend.examcell.entity.ExamTimetable>> getTimetable(@PathVariable Long id) {
        return ResponseEntity.ok(examinationService.getTimetableForExam(id));
    }

    @PutMapping("/papers/{id}/fee")
    public ResponseEntity<?> updatePaperFee(
            @PathVariable Long id,
            @RequestParam Double fee) {
        try {
            return ResponseEntity.ok(examinationService.updatePaperFee(id, fee));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/examinations/{id}/timetable")
    public ResponseEntity<?> addTimetable(@PathVariable Long id,
            @RequestBody erp_backend.examcell.entity.ExamTimetable timetable) {
        try {
            return ResponseEntity.ok(examinationService.addTimetable(id, timetable));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Hall Ticket ──────────────────────────────────────────────────────────

    @GetMapping("/registrations/{id}/hall-ticket")
    public ResponseEntity<?> getHallTicket(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(examinationService.generateHallTicket(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Examination Attendance ───────────────────────────────────────────────

    @GetMapping("/timetable/{id}/attendance")
    public ResponseEntity<?> getTimetableAttendance(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(examinationService.getAttendanceForTimetableMapped(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/attendance/{id}/mark")
    public ResponseEntity<?> markAttendance(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String remarks,
            @RequestParam String performedBy) {
        try {
            examinationService.markAttendance(id, status, remarks, performedBy);
            return ResponseEntity.ok(Map.of("message", "Attendance marked seamlessly."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private String normalizeDept(String d) {
        if (d == null) return "";
        String low = d.toLowerCase().trim();
        if (low.contains("computer") || low.equals("cs") || low.equals("cse")) return "CSE";
        if (low.contains("electronics") || low.contains("communication") || low.equals("ece")) return "ECE";
        if (low.contains("electrical") || low.equals("eee")) return "EEE";
        if (low.contains("mechanical") || low.equals("mech") || low.equals("me")) return "MECH";
        if (low.contains("civil") || low.equals("ce")) return "CIVIL";
        if (low.contains("information") || low.equals("it")) return "IT";
        if (low.contains("artificial") || low.contains("data") || low.contains("ai")) return "AIDS";
        return low.replaceAll("[^a-z0-9]", "");
    }

    private String normalizeSem(String s) {
        if (s == null) return "";
        String low = s.toLowerCase().trim();
        if (low.equals("1") || low.equals("i") || low.equals("first")) return "1";
        if (low.equals("2") || low.equals("ii") || low.equals("second")) return "2";
        if (low.equals("3") || low.equals("iii") || low.equals("third")) return "3";
        if (low.equals("4") || low.equals("iv") || low.equals("fourth")) return "4";
        if (low.equals("5") || low.equals("v") || low.equals("fifth")) return "5";
        if (low.equals("6") || low.equals("vi") || low.equals("sixth")) return "6";
        if (low.equals("7") || low.equals("vii") || low.equals("seventh")) return "7";
        if (low.equals("8") || low.equals("viii") || low.equals("eighth")) return "8";
        return low.replaceAll("[^a-z0-9]", "");
    }
}
