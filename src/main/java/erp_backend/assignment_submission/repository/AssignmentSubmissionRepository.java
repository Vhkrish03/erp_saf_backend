package erp_backend.assignment_submission.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import erp_backend.assignment_submission.entity.AssignmentSubmissionEntity;



public interface AssignmentSubmissionRepository
        extends JpaRepository<AssignmentSubmissionEntity, Long> {

    List<AssignmentSubmissionEntity> findByAssignmentId(Long assignmentId);


    /**
 * Finds a submission record for a particular assignment
 * and a particular student.
 *
 * Purpose:
 * Used to check whether a student has already been marked
 * as Submitted or Not Submitted by the teacher.
 *
 * Example:
 * Assignment ID = 5
 * Student ID = STU001
 *
 * Returns:
 * Existing submission record if available,
 * otherwise returns an empty Optional.
 */


    Optional<AssignmentSubmissionEntity> findByAssignmentIdAndStudentId(
        Long assignmentId,
        String studentId);

}


/*

findByAssignmentId(Long assignmentId)

- because when the teacher opens Assignment 1, we need to load all submission records for Assignment 1.

 */