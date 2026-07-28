package erp_backend.assignment_submission.controller;


import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import erp_backend.assignment_submission.dto.StudentAssignmentReportDTO;
import erp_backend.assignment_submission.entity.AssignmentSubmissionEntity;
import erp_backend.assignment_submission.service.AssignmentSubmissionService;




@RestController
@RequestMapping("/api/assignment-submissions")
@CrossOrigin("*")
public class AssignmentSubmissionController {

    private final AssignmentSubmissionService service;

    public AssignmentSubmissionController(AssignmentSubmissionService service) {
        this.service = service;
    }

    @GetMapping
    public List<AssignmentSubmissionEntity> getAllSubmissions() {
        return service.getAllSubmissions();
    }

    @GetMapping("/{assignmentId}")
    public List<AssignmentSubmissionEntity> getByAssignment(
            @PathVariable Long assignmentId) {

        return service.getSubmissionsByAssignment(assignmentId);
    }

    /**
 * Returns all students in a section together with their
 * assignment submission status.
 *
 * API:
 * GET /api/assignment-submissions/report/{assignmentId}/{section}
 *
 * Example:
 * /api/assignment-submissions/report/1/IV CSE A
 */
    @GetMapping("/report/{assignmentId}/{section}")
        public List<StudentAssignmentReportDTO> getAssignmentReport(
            @PathVariable Long assignmentId,
            @PathVariable String section) {

        return service.getAssignmentReport(assignmentId, section);
    }

    @PostMapping
    public AssignmentSubmissionEntity saveSubmission(
            @RequestBody AssignmentSubmissionEntity submission) {

        return service.saveSubmission(submission);
    }

    @DeleteMapping("/{id}")
    public void deleteSubmission(@PathVariable Long id) {
        service.deleteSubmission(id);
    }
}