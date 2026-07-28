package erp_backend.assignment_submission.erp_backend.assignment_submission.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import erp_backend.assignment_submission.erp_backend.assignment_submission.entity.AssignmentSubmissionEntity;



public interface AssignmentSubmissionRepository
        extends JpaRepository<AssignmentSubmissionEntity, Long> {

    List<AssignmentSubmissionEntity> findByAssignmentId(Long assignmentId);

    Optional<AssignmentSubmissionEntity> findByAssignmentIdAndStudentId(
        Long assignmentId,
        String studentId);

}


/*

findByAssignmentId(Long assignmentId)

- because when the teacher opens Assignment 1, we need to load all submission records for Assignment 1.

 */