package erp_backend.coe.service;

import erp_backend.examcell.entity.ExamCellResult;
import erp_backend.examcell.entity.ExamCellResultAudit;
import erp_backend.examcell.entity.Examination;
import erp_backend.examcell.repository.ExamCellResultRepository;
import erp_backend.examcell.repository.ExamCellResultAuditRepository;
import erp_backend.examcell.repository.ExaminationRepository;
import erp_backend.coe.dto.CoeBatchSummaryDto;
import erp_backend.coe.dto.CoeDashboardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CoeService {

    @Autowired
    private ExamCellResultRepository resultRepo;

    @Autowired
    private ExaminationRepository examinationRepo;

    @Autowired
    private ExamCellResultAuditRepository auditRepo;

    public CoeDashboardResponse getDashboardSummary() {
        CoeDashboardResponse res = new CoeDashboardResponse();

        List<Examination> exams = examinationRepo.findAll();
        res.setTotalExaminations(exams.size());

        long upcoming = exams.stream().filter(e -> "UPCOMING".equalsIgnoreCase(e.getStatus())).count();
        long ongoing = exams.stream().filter(e -> "ONGOING".equalsIgnoreCase(e.getStatus())).count();
        long completed = exams.stream().filter(e -> "COMPLETED".equalsIgnoreCase(e.getStatus())).count();

        res.setUpcomingExaminations(upcoming);
        res.setOngoingExaminations(ongoing);
        res.setCompletedExaminations(completed);

        List<ExamCellResult> allResults = resultRepo.findAll();
        // A batch is defined by (dept, sem, year, session, status)
        Map<String, List<ExamCellResult>> batches = allResults.stream()
                .collect(Collectors.groupingBy(r -> r.getDepartment() + "|" +
                        r.getSemesterName() + "|" +
                        r.getAcademicYear() + "|" +
                        r.getExamSession() + "|" +
                        r.getStatus()));

        long pending = 0;
        long approved = 0;
        long rejected = 0;
        long published = 0;
        long pendingAuth = 0;

        for (Map.Entry<String, List<ExamCellResult>> entry : batches.entrySet()) {
            String key = entry.getKey();
            if (key.endsWith("|SUBMITTED"))
                pending++;
            else if (key.endsWith("|APPROVED"))
                approved++;
            else if (key.endsWith("|REJECTED"))
                rejected++;
            else if (key.endsWith("|PUBLISHED"))
                published++;
            else if (key.endsWith("|AUTHORIZED_FOR_PUBLICATION"))
                pendingAuth++;
        }

        res.setPendingApproval(pending);
        res.setApprovedBatches(approved);
        res.setRejectedBatches(rejected);
        res.setPublishedBatches(published);
        res.setPendingPublicationAuth(pendingAuth);

        return res;
    }

    public List<CoeBatchSummaryDto> getBatchesByStatus(String status) {
        List<ExamCellResult> results = "ALL".equalsIgnoreCase(status) ? resultRepo.findAll()
                : resultRepo.findByStatus(status);

        Map<String, List<ExamCellResult>> grouped = results.stream()
                .collect(Collectors.groupingBy(r -> r.getDepartment() + "|" +
                        r.getSemesterName() + "|" +
                        r.getAcademicYear() + "|" +
                        r.getExamSession() + "|" +
                        r.getExamination() + "|" +
                        r.getStatus()));

        List<CoeBatchSummaryDto> dtos = new ArrayList<>();
        for (List<ExamCellResult> batch : grouped.values()) {
            if (batch.isEmpty())
                continue;
            ExamCellResult first = batch.get(0);
            CoeBatchSummaryDto dto = new CoeBatchSummaryDto();
            dto.setDepartment(first.getDepartment());
            dto.setSemesterName(first.getSemesterName());
            dto.setAcademicYear(first.getAcademicYear());
            dto.setExamSession(first.getExamSession());
            dto.setExamination(first.getExamination());
            dto.setStatus(first.getStatus());
            dto.setStudentCount(batch.size());

            dto.setSubmissionDate(first.getVerifiedAt() != null ? first.getVerifiedAt().toString() : "");
            dto.setSubmittedBy(first.getVerifiedBy() != null ? first.getVerifiedBy() : "");
            dto.setApprovalDate(first.getApprovedAt() != null ? first.getApprovedAt().toString() : "");
            dto.setApprovedBy(first.getApprovedBy() != null ? first.getApprovedBy() : "");
            dto.setRejectionDate(first.getRejectedAt() != null ? first.getRejectedAt().toString() : "");
            dto.setRejectedBy(first.getRejectedBy() != null ? first.getRejectedBy() : "");
            dto.setRejectionReason(first.getRejectionReason() != null ? first.getRejectionReason() : "");

            dtos.add(dto);
        }
        return dtos;
    }

    public List<ExamCellResult> getBatchDetails(String department, String semester, String year, String session,
            String status) {
        return resultRepo.findByDepartmentAndSemesterNameAndAcademicYearAndStatus(
                department, semester, year, status);
    }

    @Transactional
    public Map<String, Object> approveBatch(Map<String, String> request) {
        String department = request.get("department");
        String semester = request.get("semesterName");
        String year = request.get("academicYear");
        String status = request.get("status"); // Expected SUBMITTED
        String performedBy = request.get("performedBy");

        List<ExamCellResult> batch = resultRepo.findByDepartmentAndSemesterNameAndAcademicYearAndStatus(
                department, semester, year, status);

        if (batch.isEmpty())
            throw new RuntimeException("No batch found for approval.");
        if (!"SUBMITTED".equals(status))
            throw new RuntimeException("Only SUBMITTED batches can be approved.");

        LocalDateTime now = LocalDateTime.now();
        for (ExamCellResult r : batch) {
            r.setStatus("APPROVED");
            r.setApprovedBy(performedBy);
            r.setApprovedAt(now);
            resultRepo.save(r);

            ExamCellResultAudit audit = new ExamCellResultAudit();
            audit.setExamCellResultId(r.getId());
            audit.setAction("APPROVE");
            audit.setPerformedBy(performedBy);
            audit.setPerformedByRole("COE");
            audit.setComments("Batch Approved by COE");
            audit.setPerformedAt(now);
            auditRepo.save(audit);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Batch approved successfully");
        return response;
    }

    @Transactional
    public Map<String, Object> rejectBatch(Map<String, String> request) {
        String department = request.get("department");
        String semester = request.get("semesterName");
        String year = request.get("academicYear");
        String status = request.get("status");
        String performedBy = request.get("performedBy");
        String reason = request.get("rejectionReason");

        List<ExamCellResult> batch = resultRepo.findByDepartmentAndSemesterNameAndAcademicYearAndStatus(
                department, semester, year, status);

        if (batch.isEmpty())
            throw new RuntimeException("No batch found for rejection.");

        LocalDateTime now = LocalDateTime.now();
        for (ExamCellResult r : batch) {
            r.setStatus("REJECTED");
            r.setRejectedBy(performedBy);
            r.setRejectedAt(now);
            r.setRejectionReason(reason);
            resultRepo.save(r);

            ExamCellResultAudit audit = new ExamCellResultAudit();
            audit.setExamCellResultId(r.getId());
            audit.setAction("REJECT");
            audit.setPerformedBy(performedBy);
            audit.setPerformedByRole("COE");
            audit.setComments("Rejected: " + reason);
            audit.setPerformedAt(now);
            auditRepo.save(audit);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Batch rejected successfully");
        return response;
    }

    @Transactional
    public Map<String, Object> authorizePublication(Map<String, String> request) {
        String department = request.get("department");
        String semester = request.get("semesterName");
        String year = request.get("academicYear");
        String status = request.get("status"); // Expected APPROVED
        String performedBy = request.get("performedBy");

        List<ExamCellResult> batch = resultRepo.findByDepartmentAndSemesterNameAndAcademicYearAndStatus(
                department, semester, year, status);

        if (batch.isEmpty())
            throw new RuntimeException("No batch found for publication authorization.");
        if (!"APPROVED".equals(status))
            throw new RuntimeException("Only APPROVED batches can be authorized for publication.");

        LocalDateTime now = LocalDateTime.now();
        for (ExamCellResult r : batch) {
            r.setStatus("AUTHORIZED_FOR_PUBLICATION");
            r.setPublicationAuthorizedBy(performedBy);
            r.setPublicationAuthorizedAt(now);
            resultRepo.save(r);

            ExamCellResultAudit audit = new ExamCellResultAudit();
            audit.setExamCellResultId(r.getId());
            audit.setAction("AUTHORIZE_PUBLICATION");
            audit.setPerformedBy(performedBy);
            audit.setPerformedByRole("COE");
            audit.setComments("Publication Authorized by COE");
            audit.setPerformedAt(now);
            auditRepo.save(audit);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Publication Authorized successfully");
        return response;
    }
}
