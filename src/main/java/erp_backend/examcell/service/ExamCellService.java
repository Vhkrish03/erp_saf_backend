package erp_backend.examcell.service;

import erp_backend.examcell.entity.ExamCellResult;
import erp_backend.examcell.entity.ExamCellResultAudit;
import erp_backend.examcell.entity.ExamCellResultSubject;
import erp_backend.examcell.repository.ExamCellResultAuditRepository;
import erp_backend.examcell.repository.ExamCellResultRepository;
import erp_backend.entity.Student;
import erp_backend.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Exam Cell Service.
 *
 * Enforces the workflow: DRAFT → VERIFIED → APPROVED → PUBLISHED
 *
 * Only PUBLISHED records are accessible to students (enforced at query level).
 * Teachers and HODs can see APPROVED + PUBLISHED records.
 * Students see only their own PUBLISHED records.
 */
@Service
public class ExamCellService {

    private final ExamCellResultRepository resultRepository;
    private final ExamCellResultAuditRepository auditRepository;
    private final StudentRepository studentRepository;

    public ExamCellService(ExamCellResultRepository resultRepository,
            ExamCellResultAuditRepository auditRepository,
            StudentRepository studentRepository) {
        this.resultRepository = resultRepository;
        this.auditRepository = auditRepository;
        this.studentRepository = studentRepository;
    }

    // ── Exam Cell: Create / Save Result (DRAFT) ───────────────────────────────

    @Transactional
    public ExamCellResult saveOrUpdateResult(ExamCellResult incoming, String performedBy, String role) {
        // Reject if trying to overwrite a PUBLISHED result directly, unless incoming is
        // also PUBLISHED
        Optional<ExamCellResult> existing = resultRepository
                .findByStudentIdAndSemesterNameAndAcademicYear(
                        incoming.getStudentId(), incoming.getSemesterName(), incoming.getAcademicYear());

        ExamCellResult result;
        String previousStatus;

        if (existing.isPresent()) {
            result = existing.get();
            if ("PUBLISHED".equalsIgnoreCase(result.getStatus())
                    && !"PUBLISHED".equalsIgnoreCase(incoming.getStatus())) {
                throw new IllegalStateException(
                        "Result is already PUBLISHED. Cannot overwrite published official results.");
            }
            previousStatus = result.getStatus();
            // Update mutable fields
            result.setRegisterNumber(incoming.getRegisterNumber());
            result.setStudentName(incoming.getStudentName());
            result.setExamSession(incoming.getExamSession());
            result.setExamination(incoming.getExamination());
            result.setSgpa(incoming.getSgpa());
            result.setDepartment(incoming.getDepartment());
            if (incoming.getStatus() != null && !incoming.getStatus().isBlank()) {
                result.setStatus(incoming.getStatus());
            }

            // Replace subjects
            if (result.getSubjects() != null)
                result.getSubjects().clear();
            if (incoming.getSubjects() != null) {
                for (ExamCellResultSubject sub : incoming.getSubjects()) {
                    sub.setExamCellResult(result);
                    if (result.getSubjects() != null)
                        result.getSubjects().add(sub);
                }
            }
        } else {
            // Validate student exists
            studentRepository.findById(incoming.getStudentId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Student not found: " + incoming.getStudentId()));

            result = incoming;
            if (incoming.getStatus() == null || incoming.getStatus().isBlank()) {
                result.setStatus("DRAFT");
            }
            previousStatus = null;

            // Populate student name if not provided
            if (result.getStudentName() == null || result.getStudentName().isBlank()) {
                Student student = studentRepository.findById(result.getStudentId()).orElse(null);
                if (student != null)
                    result.setStudentName(student.getName());
            }
        }
        result.setEnteredBy(performedBy);

        if ("PUBLISHED".equalsIgnoreCase(result.getStatus())) {
            result.setPublishedBy(performedBy);
            result.setPublishedAt(LocalDateTime.now());
        }

        // Link subjects back BEFORE saving to prevent transient / null property
        // exceptions
        if (result.getSubjects() != null) {
            for (ExamCellResultSubject sub : result.getSubjects()) {
                sub.setExamCellResult(result);
            }
        }

        ExamCellResult saved = resultRepository.save(result);

        // Audit
        auditRepository.save(new ExamCellResultAudit(
                saved.getId(), saved.getStudentId(),
                previousStatus == null ? "CREATED" : "UPDATED",
                previousStatus, saved.getStatus(),
                performedBy, role, null));

        return saved;
    }

    @Transactional
    public List<ExamCellResult> saveOrUpdateResultBulk(List<ExamCellResult> incomingList, String performedBy,
            String role) {
        List<ExamCellResult> savedList = new java.util.ArrayList<>();
        for (ExamCellResult incoming : incomingList) {
            savedList.add(saveOrUpdateResult(incoming, performedBy, role));
        }
        return savedList;
    }

    // ── Workflow State Transitions ────────────────────────────────────────────

    /**
     * Exam Cell: mark result as VERIFIED.
     * Allowed from: DRAFT
     */
    @Transactional
    public ExamCellResult verifyResult(Long resultId, String performedBy, String role, String comments) {
        ExamCellResult result = getResultOrThrow(resultId);
        validateTransition(result.getStatus(), "DRAFT", "VERIFIED");

        String oldStatus = result.getStatus();
        result.setStatus("VERIFIED");
        result.setVerifiedBy(performedBy);
        result.setVerifiedAt(LocalDateTime.now());
        resultRepository.save(result);

        auditRepository.save(new ExamCellResultAudit(
                resultId, result.getStudentId(), "VERIFIED",
                oldStatus, "VERIFIED", performedBy, role, comments));

        return result;
    }

    /**
     * Authorized authority (Dean/Admin): APPROVE the result.
     * Allowed from: VERIFIED
     */
    @Transactional
    public ExamCellResult approveResult(Long resultId, String performedBy, String role, String comments) {
        ExamCellResult result = getResultOrThrow(resultId);
        validateTransition(result.getStatus(), "VERIFIED", "APPROVED");

        String oldStatus = result.getStatus();
        result.setStatus("APPROVED");
        result.setApprovedBy(performedBy);
        result.setApprovedAt(LocalDateTime.now());
        resultRepository.save(result);

        auditRepository.save(new ExamCellResultAudit(
                resultId, result.getStudentId(), "APPROVED",
                oldStatus, "APPROVED", performedBy, role, comments));

        return result;
    }

    /**
     * Exam Cell / Admin: PUBLISH the result.
     * Allowed from: APPROVED
     * After this, students can see the result in their Progress Card.
     */
    @Transactional
    public ExamCellResult publishResult(Long resultId, String performedBy, String role) {
        ExamCellResult result = getResultOrThrow(resultId);
        validateTransition(result.getStatus(), "APPROVED", "PUBLISHED");

        String oldStatus = result.getStatus();
        result.setStatus("PUBLISHED");
        result.setPublishedBy(performedBy);
        result.setPublishedAt(LocalDateTime.now());
        resultRepository.save(result);

        auditRepository.save(new ExamCellResultAudit(
                resultId, result.getStudentId(), "PUBLISHED",
                oldStatus, "PUBLISHED", performedBy, role, null));

        return result;
    }

    /**
     * Return result back to DRAFT for correction.
     * Allowed from: VERIFIED or APPROVED (not PUBLISHED).
     */
    @Transactional
    public ExamCellResult returnForCorrection(Long resultId, String performedBy, String role, String reason) {
        ExamCellResult result = getResultOrThrow(resultId);
        if ("PUBLISHED".equalsIgnoreCase(result.getStatus())) {
            throw new IllegalStateException("Cannot return a PUBLISHED result. Use a correction workflow.");
        }
        String oldStatus = result.getStatus();
        result.setStatus("DRAFT");
        resultRepository.save(result);

        auditRepository.save(new ExamCellResultAudit(
                resultId, result.getStudentId(), "RETURNED_FOR_CORRECTION",
                oldStatus, "DRAFT", performedBy, role, reason));

        return result;
    }

    // ── Query Methods ─────────────────────────────────────────────────────────

    /** Student access: only PUBLISHED results */
    public List<ExamCellResult> getPublishedResultsForStudent(String studentId) {
        return resultRepository.findPublishedByStudentId(studentId);
    }

    public Optional<ExamCellResult> getPublishedResultForStudentAndSemester(String studentId, String semester) {
        return resultRepository.findPublishedByStudentIdAndSemester(studentId, semester);
    }

    /** All results (any status) — for Exam Cell / Admin */
    public List<ExamCellResult> getAllResultsForStudent(String studentId) {
        return resultRepository.findByStudentId(studentId);
    }

    public List<ExamCellResult> getResultsByDepartmentAndSemester(String department, String semester,
            String academicYear) {
        return resultRepository.findByDepartmentAndSemesterNameAndAcademicYear(department, semester, academicYear);
    }

    public List<ExamCellResult> getResultsByStatus(String status) {
        return resultRepository.findByStatus(status);
    }

    public Optional<ExamCellResult> getResultById(Long id) {
        return resultRepository.findById(id);
    }

    public List<ExamCellResultAudit> getAuditTrail(Long resultId) {
        return auditRepository.findByExamCellResultIdOrderByPerformedAtAsc(resultId);
    }

    /** Class-wise result list for Exam Cell dashboard */
    public List<Map<String, Object>> getClassResultSummary(
            String department, String semester, String academicYear,
            List<Student> students) {

        return students.stream().map(student -> {
            Optional<ExamCellResult> resOpt = resultRepository
                    .findByStudentIdAndSemesterNameAndAcademicYear(
                            student.getId(), semester, academicYear);
            java.util.Map<String, Object> row = new java.util.HashMap<>();
            row.put("studentId", student.getId());
            row.put("studentName", student.getName());
            row.put("rollNumber", student.getRollNumber());
            if (resOpt.isPresent()) {
                ExamCellResult r = resOpt.get();
                row.put("resultId", r.getId());
                row.put("status", r.getStatus());
                row.put("sgpa", r.getSgpa());
                row.put("examSession", r.getExamSession());
                row.put("publishedAt", r.getPublishedAt());
                row.put("subjectCount", r.getSubjects() != null ? r.getSubjects().size() : 0);
            } else {
                row.put("resultId", null);
                row.put("status", "PENDING_ENTRY");
                row.put("sgpa", 0.0);
                row.put("examSession", null);
                row.put("publishedAt", null);
                row.put("subjectCount", 0);
            }
            return row;
        }).collect(java.util.stream.Collectors.toList());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private ExamCellResult getResultOrThrow(Long resultId) {
        return resultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Exam Cell result not found: " + resultId));
    }

    /**
     * Validates that the current status is one of the required previous states.
     * Only one previous state needed here for simplicity; extend if needed.
     */
    private void validateTransition(String currentStatus, String requiredPrevious, String targetStatus) {
        if (!requiredPrevious.equalsIgnoreCase(currentStatus)) {
            throw new IllegalStateException(
                    String.format("Cannot transition to %s from %s. Expected current status: %s.",
                            targetStatus, currentStatus, requiredPrevious));
        }
    }
}
