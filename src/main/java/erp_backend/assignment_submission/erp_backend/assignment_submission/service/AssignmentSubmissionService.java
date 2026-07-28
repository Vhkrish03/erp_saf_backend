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

    public AssignmentSubmissionEntity saveSubmission(AssignmentSubmissionEntity submission) {

        if (submission.getCreatedAt() == null) {
            submission.setCreatedAt(LocalDateTime.now());
        }

        submission.setUpdatedAt(LocalDateTime.now());

        if ("SUBMITTED".equalsIgnoreCase(submission.getStatus())) {
            submission.setSubmittedAt(LocalDateTime.now());
        }


        return repository.save(submission);
    }

    public void deleteSubmission(Long id) {
        repository.deleteById(id);
    }
}