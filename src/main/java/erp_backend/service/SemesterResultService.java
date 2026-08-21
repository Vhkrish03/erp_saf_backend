package erp_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import erp_backend.entity.SemesterResult;
import erp_backend.repository.SemesterResultRepository;

import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;
import erp_backend.entity.ExamResult;
import erp_backend.entity.Student;
import erp_backend.exam.entity.SemesterResultAudit;
import erp_backend.repository.StudentRepository;
import erp_backend.exam.repository.SemesterResultAuditRepository;

@Service
public class SemesterResultService {

    private final SemesterResultRepository repository;
    private final StudentRepository studentRepository;
    private final SemesterResultAuditRepository auditRepository;

    public SemesterResultService(SemesterResultRepository repository,
            StudentRepository studentRepository,
            SemesterResultAuditRepository auditRepository) {
        this.repository = repository;
        this.studentRepository = studentRepository;
        this.auditRepository = auditRepository;
    }

    public List<SemesterResult> getResults(String studentId) {
        return repository.findByStudent_Id(studentId);
    }

    public Optional<SemesterResult> getResultByStudentAndSemester(String studentId, String semesterName) {
        return repository.findByStudent_IdAndSemesterName(studentId, semesterName);
    }

    public List<SemesterResult> getFilteredResults(String department, String semesterName, String section,
            String academicYear) {
        return repository.findByStudent_DepartmentAndSemesterNameAndStudent_SectionAndAcademicYear(
                department, semesterName, section, academicYear);
    }

    @Transactional
    public SemesterResult saveSemesterResult(SemesterResult payload, String performedBy, String comments) {
        if (payload.getStudent() == null || payload.getStudent().getId() == null) {
            throw new IllegalArgumentException("Student information is required.");
        }

        String studentId = payload.getStudent().getId();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found."));

        Optional<SemesterResult> existingOpt = repository.findByStudent_IdAndSemesterName(studentId,
                payload.getSemesterName());
        SemesterResult result;
        String actionType;
        String prevStatus = "NEW";

        if (existingOpt.isPresent()) {
            result = existingOpt.get();
            if ("LOCKED".equalsIgnoreCase(result.getStatus())) {
                throw new IllegalStateException(
                        "Result is locked. Modifications must go through authorized correction.");
            }
            prevStatus = result.getStatus();
            actionType = "CORRECTED";

            // clear old results to let orphanRemoval do the clean up
            result.getResults().clear();
        } else {
            result = new SemesterResult();
            result.setStudent(student);
            result.setSemesterName(payload.getSemesterName());
            result.setStatus("DRAFT");
            actionType = "ENTERED";
        }

        result.setAcademicYear(payload.getAcademicYear());
        result.setExamination(payload.getExamination());
        result.setExamSession(payload.getExamSession());
        result.setUpdatedAt(LocalDateTime.now());

        // Process children
        List<ExamResult> childResults = new java.util.ArrayList<>();
        if (payload.getResults() != null) {
            for (ExamResult rPayload : payload.getResults()) {
                ExamResult r = new ExamResult();
                r.setSubjectCode(rPayload.getSubjectCode());
                r.setSubjectName(rPayload.getSubjectName());
                r.setGrade(rPayload.getGrade());
                r.setMarksObtained(rPayload.getMarksObtained());
                r.setMaxMarks(rPayload.getMaxMarks() > 0 ? rPayload.getMaxMarks() : 100);
                r.setCredits(rPayload.getCredits() > 0 ? rPayload.getCredits() : 3);
                r.setSemesterResult(result);
                childResults.add(r);
            }
        }

        // Calculate SGPA
        double sgpa = calculateSgpa(childResults);
        result.setSgpa(sgpa);
        result.setResults(childResults);

        SemesterResult saved = repository.save(result);

        // Audit Trail
        SemesterResultAudit audit = new SemesterResultAudit(
                saved.getId(),
                studentId,
                actionType,
                prevStatus,
                saved.getStatus(),
                performedBy,
                comments != null ? comments : "Semester result " + actionType.toLowerCase());
        auditRepository.save(audit);

        // Update Student dynamic CGPA
        updateStudentCgpa(studentId);

        return saved;
    }

    @Transactional
    public SemesterResult updateStatus(Long resultId, String newStatus, String performedBy, String comments) {
        SemesterResult result = repository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Semester result not found."));

        String oldStatus = result.getStatus();
        result.setStatus(newStatus.toUpperCase());
        if ("PUBLISHED".equalsIgnoreCase(newStatus)) {
            result.setPublishedAt(LocalDateTime.now());
        }
        result.setUpdatedAt(LocalDateTime.now());

        SemesterResult saved = repository.save(result);

        // Audit Trail
        SemesterResultAudit audit = new SemesterResultAudit(
                saved.getId(),
                saved.getStudent().getId(),
                "STATUS_CHANGE",
                oldStatus,
                saved.getStatus(),
                performedBy,
                comments != null ? comments : "Status transition to " + newStatus);
        auditRepository.save(audit);

        // Update CGPA
        updateStudentCgpa(saved.getStudent().getId());

        return saved;
    }

    @Transactional
    public void updateStudentCgpa(String studentId) {
        double cgpa = calculateCgpa(studentId);
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student != null) {
            student.setCgpa(cgpa);
            studentRepository.save(student);
        }
    }

    public double calculateSgpa(List<ExamResult> examResults) {
        double totalWeightedGradePoints = 0.0;
        int totalCredits = 0;
        for (ExamResult r : examResults) {
            String grade = r.getGrade() != null ? r.getGrade().toUpperCase().trim() : "";
            double gp = 0.0;
            switch (grade) {
                case "O":
                    gp = 10.0;
                    break;
                case "A+":
                    gp = 9.0;
                    break;
                case "A":
                    gp = 8.0;
                    break;
                case "B+":
                    gp = 7.0;
                    break;
                case "B":
                    gp = 6.0;
                    break;
                case "C":
                    gp = 5.0;
                    break;
                default:
                    gp = 0.0;
                    break;
            }
            r.setGradePoint(gp);
            if ("U".equals(grade) || "RA".equals(grade) || "UA".equals(grade) || "W".equals(grade)
                    || "".equals(grade)) {
                r.setResultStatus("FAIL");
            } else {
                r.setResultStatus("PASS");
            }
            if (r.getCredits() > 0) {
                totalWeightedGradePoints += r.getCredits() * gp;
                totalCredits += r.getCredits();
            }
        }
        if (totalCredits == 0)
            return 0.0;
        double calculatedSgpa = totalWeightedGradePoints / totalCredits;
        return Math.round(calculatedSgpa * 100.0) / 100.0;
    }

    public double calculateCgpa(String studentId) {
        List<SemesterResult> semesterResults = repository.findByStudent_Id(studentId);
        double totalWeightedGradePoints = 0.0;
        int totalCredits = 0;
        for (SemesterResult sr : semesterResults) {
            if ("PUBLISHED".equalsIgnoreCase(sr.getStatus()) || "LOCKED".equalsIgnoreCase(sr.getStatus())) {
                if (sr.getResults() != null) {
                    for (ExamResult r : sr.getResults()) {
                        double gp = r.getGradePoint();
                        if (r.getCredits() > 0) {
                            totalWeightedGradePoints += r.getCredits() * gp;
                            totalCredits += r.getCredits();
                        }
                    }
                }
            }
        }
        if (totalCredits == 0)
            return 0.0;
        double calculatedCgpa = totalWeightedGradePoints / totalCredits;
        return Math.round(calculatedCgpa * 100.0) / 100.0;
    }

    public List<SemesterResultAudit> getAudits(Long resultId) {
        return auditRepository.findBySemesterResultIdOrderByPerformedAtDesc(resultId);
    }
}