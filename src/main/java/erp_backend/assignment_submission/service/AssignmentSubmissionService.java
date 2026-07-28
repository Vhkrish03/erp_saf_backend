package erp_backend.assignment_submission.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import erp_backend.assignment_submission.dto.StudentAssignmentReportDTO;
import erp_backend.assignment_submission.entity.AssignmentSubmissionEntity;
import erp_backend.assignment_submission.repository.AssignmentSubmissionRepository;
import erp_backend.entity.Student;
import erp_backend.repository.StudentRepository;



@Service
public class AssignmentSubmissionService {

    private final AssignmentSubmissionRepository repository;

    private final StudentRepository studentRepository;

    public AssignmentSubmissionService(
        AssignmentSubmissionRepository repository,
        StudentRepository studentRepository) {

    this.repository = repository;
    this.studentRepository = studentRepository;
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

/**
 * Returns every student in the given section together with
 * their assignment submission status.
 *
 * Purpose:
 * Used by the Teacher Report Submission screen.
 *
 * Workflow:
 * 1. Get all students in the section.
 * 2. Check whether each student has a submission record.
 * 3. If yes, return status, marks and remarks.
 * 4. If no, mark as NOT_SUBMITTED.
 */
public List<StudentAssignmentReportDTO> getAssignmentReport(
        Long assignmentId,
        String section) {

    List<Student> students = studentRepository.findBySection(section);

    List<StudentAssignmentReportDTO> report = new ArrayList<>();

    for (Student student : students) {

        StudentAssignmentReportDTO dto = new StudentAssignmentReportDTO();

        dto.setStudentId(student.getId());
        dto.setStudentName(student.getName());
        dto.setRollNumber(student.getRollNumber());

        Optional<AssignmentSubmissionEntity> submission =
                repository.findByAssignmentIdAndStudentId(
                        assignmentId,
                        student.getId());

        if (submission.isPresent()) {

            dto.setStatus(submission.get().getStatus());
            dto.setMarks(submission.get().getMarks());
            dto.setRemarks(submission.get().getRemarks());

        } else {

            dto.setStatus("NOT_SUBMITTED");
            dto.setMarks(null);
            dto.setRemarks(null);

        }

        report.add(dto);
    }

    return report;
}


}