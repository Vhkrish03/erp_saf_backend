package erp_backend.assignment_submission.erp_backend.assignment_submission.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import erp_backend.assignment_submission.erp_backend.assignment_submission.entity.AssignmentSubmissionEntity;
import erp_backend.assignment_submission.erp_backend.assignment_submission.repository.AssignmentSubmissionRepository;

@Service
public class AssignmentSubmissionService {

    private final AssignmentSubmissionRepository repository;

    public AssignmentSubmissionService(AssignmentSubmissionRepository repository) {
        this.repository = repository;
    }

    public List<AssignmentSubmissionEntity> getAllSubmissions() {
        return repository.findAll();
    }

    public List<AssignmentSubmissionEntity> getSubmissionsByAssignment(Long assignmentId) {
        return repository.findByAssignmentId(assignmentId);
    }

    /*
    This SaveSubmission Method used to 
    First time → creates a new record.
    Next time → updates the existing record instead of inserting another row.
     */

    
    public AssignmentSubmissionEntity saveSubmission(
        AssignmentSubmissionEntity submission) {

    AssignmentSubmissionEntity entity =
            repository.findByAssignmentIdAndStudentId(
                    submission.getAssignmentId(),
                    submission.getStudentId()
            ).orElse(new AssignmentSubmissionEntity());

    if (entity.getId() == null) {
        entity.setCreatedAt(LocalDateTime.now());
    }

    entity.setAssignmentId(submission.getAssignmentId());
    entity.setStudentId(submission.getStudentId());
    entity.setStatus(submission.getStatus());
    entity.setMarks(submission.getMarks());
    entity.setRemarks(submission.getRemarks());

    if ("SUBMITTED".equalsIgnoreCase(submission.getStatus())) {
        entity.setSubmittedAt(LocalDateTime.now());
    } else {
        entity.setSubmittedAt(null);
    }

    entity.setUpdatedAt(LocalDateTime.now());

    return repository.save(entity);
}

    public void deleteSubmission(Long id) {
        repository.deleteById(id);
    }
}