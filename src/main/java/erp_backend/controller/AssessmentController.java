package erp_backend.controller;

import erp_backend.entity.Assessment;
import erp_backend.entity.AssessmentMark;
import erp_backend.entity.Student;
import erp_backend.service.AssessmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assessments")
@CrossOrigin("*")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping
    public ResponseEntity<Assessment> createAssessment(@RequestBody Assessment assessment) {
        return ResponseEntity.ok(assessmentService.createAssessment(assessment));
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<Assessment>> getWeeklyAssessments(
            @RequestParam String department,
            @RequestParam String semester,
            @RequestParam String section) {
        return ResponseEntity.ok(assessmentService.getWeeklyAssessments(department, semester, section));
    }

    @GetMapping("/iat")
    public ResponseEntity<List<Assessment>> getIatAssessments(
            @RequestParam String department,
            @RequestParam String semester,
            @RequestParam String section) {
        return ResponseEntity.ok(assessmentService.getIatAssessments(department, semester, section));
    }

    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<List<Assessment>> getAssessmentsByFaculty(@PathVariable String facultyId) {
        return ResponseEntity.ok(assessmentService.getAssessmentsByFaculty(facultyId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assessment> getAssessmentById(@PathVariable Long id) {
        return assessmentService.getAssessmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{assessmentId}/students")
    public ResponseEntity<List<Student>> getStudentsForAssessment(@PathVariable Long assessmentId) {
        try {
            return ResponseEntity.ok(assessmentService.getStudentsForAssessment(assessmentId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{assessmentId}/marks")
    public ResponseEntity<List<AssessmentMark>> saveMarks(
            @PathVariable Long assessmentId,
            @RequestParam Long componentId,
            @RequestBody List<AssessmentMark> marks,
            @RequestParam String facultyId) {
        try {
            return ResponseEntity.ok(assessmentService.saveMarks(assessmentId, componentId, marks, facultyId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{assessmentId}/submit")
    public ResponseEntity<Assessment> submitAssessment(
            @PathVariable Long assessmentId,
            @RequestParam String facultyId) {
        try {
            return ResponseEntity.ok(assessmentService.submitAssessment(assessmentId, facultyId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{assessmentId}/marks")
    public ResponseEntity<List<AssessmentMark>> getMarksForAssessment(@PathVariable Long assessmentId) {
        return ResponseEntity.ok(assessmentService.getMarksForAssessment(assessmentId));
    }

    @PostMapping("/{assessmentId}/verify/class-incharge")
    public ResponseEntity<Assessment> verifyClassIncharge(
            @PathVariable Long assessmentId,
            @RequestParam boolean accept) {
        try {
            return ResponseEntity.ok(assessmentService.verifyClassIncharge(assessmentId, accept));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{assessmentId}/verify/hod")
    public ResponseEntity<Assessment> verifyHod(
            @PathVariable Long assessmentId,
            @RequestParam boolean approve,
            @RequestParam(required = false) String comment) {
        try {
            return ResponseEntity.ok(assessmentService.verifyHod(assessmentId, approve, comment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{assessmentId}/submit/dean")
    public ResponseEntity<Assessment> submitToDean(@PathVariable Long assessmentId) {
        try {
            return ResponseEntity.ok(assessmentService.submitToDean(assessmentId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/consolidated")
    public ResponseEntity<Map<String, Object>> getConsolidatedMarksReport(
            @RequestParam String department,
            @RequestParam String semester,
            @RequestParam String section) {
        try {
            return ResponseEntity.ok(assessmentService.getConsolidatedMarksReport(department, semester, section));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearClassAssessments(
            @RequestParam String department,
            @RequestParam String semester,
            @RequestParam String section) {
        try {
            assessmentService.clearClassAssessments(department, semester, section);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
