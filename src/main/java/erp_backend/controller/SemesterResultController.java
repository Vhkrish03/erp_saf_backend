package erp_backend.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import erp_backend.entity.SemesterResult;
import erp_backend.exam.entity.SemesterResultAudit;
import erp_backend.entity.Student;
import erp_backend.service.SemesterResultService;
import erp_backend.repository.StudentRepository;

@RestController
@RequestMapping("/api/results")
@CrossOrigin("*")
public class SemesterResultController {

    private final SemesterResultService service;
    private final StudentRepository studentRepository;

    public SemesterResultController(SemesterResultService service, StudentRepository studentRepository) {
        this.service = service;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/{studentId}")
    public List<SemesterResult> getResults(
            @PathVariable String studentId) {

        return service.getResults(studentId);
    }

    @GetMapping("/student/{studentId}/semester/{semester}")
    public ResponseEntity<SemesterResult> getResultByStudentAndSemester(
            @PathVariable String studentId,
            @PathVariable String semester) {
        return service.getResultByStudentAndSemester(studentId, semester)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/admin/list")
    public ResponseEntity<List<Map<String, Object>>> listResultsAdmin(
            @RequestParam String department,
            @RequestParam String semester,
            @RequestParam String section,
            @RequestParam String academicYear) {

        List<Student> students = studentRepository.findByDepartmentAndSemesterAndSection(department, semester, section);
        List<Map<String, Object>> response = new ArrayList<>();

        for (Student student : students) {
            Map<String, Object> map = new HashMap<>();
            map.put("studentId", student.getId());
            map.put("rollNumber", student.getRollNumber());
            map.put("studentName", student.getName());

            Optional<SemesterResult> rOpt = service.getResultByStudentAndSemester(student.getId(), semester);
            if (rOpt.isPresent()) {
                SemesterResult r = rOpt.get();
                map.put("resultId", r.getId());
                map.put("status", r.getStatus());
                map.put("sgpa", r.getSgpa());
                map.put("academicYear", r.getAcademicYear());
                map.put("examination", r.getExamination());
                map.put("results", r.getResults());
            } else {
                map.put("resultId", null);
                map.put("status", "PENDING_ENTRY");
                map.put("sgpa", 0.0);
                map.put("academicYear", academicYear);
                map.put("examination", null);
                map.put("results", new ArrayList<>());
            }
            response.add(map);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<Map<String, Object>> getStats(
            @RequestParam String department,
            @RequestParam String semester,
            @RequestParam String section,
            @RequestParam String academicYear) {

        List<Student> students = studentRepository.findByDepartmentAndSemesterAndSection(department, semester, section);

        int totalStudents = students.size();
        int awaitingEntry = 0;
        int awaitingVerification = 0;
        int published = 0;
        int correctionRequired = 0;

        for (Student student : students) {
            Optional<SemesterResult> rOpt = service.getResultByStudentAndSemester(student.getId(), semester);
            if (rOpt.isPresent()) {
                String status = rOpt.get().getStatus();
                if ("DRAFT".equalsIgnoreCase(status)) {
                    awaitingEntry++; // Draft means entry in progress, but we can call it awaiting verification once
                                     // SUBMITTED/ENTERED
                } else if ("ENTERED".equalsIgnoreCase(status)) {
                    awaitingVerification++;
                } else if ("VERIFIED".equalsIgnoreCase(status)) {
                    awaitingVerification++;
                } else if ("PUBLISHED".equalsIgnoreCase(status) || "LOCKED".equalsIgnoreCase(status)) {
                    published++;
                } else if ("CORRECTION_REQUEST".equalsIgnoreCase(status) || "CORRECTION".equalsIgnoreCase(status)) {
                    correctionRequired++;
                }
            } else {
                awaitingEntry++;
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", totalStudents);
        stats.put("awaitingEntry", awaitingEntry);
        stats.put("awaitingVerification", awaitingVerification);
        stats.put("published", published);
        stats.put("correctionRequired", correctionRequired);

        return ResponseEntity.ok(stats);
    }

    @PostMapping("/admin/save")
    public ResponseEntity<SemesterResult> saveResult(
            @RequestBody SemesterResult result,
            @RequestParam String performedBy,
            @RequestParam(required = false) String comments) {
        try {
            return ResponseEntity.ok(service.saveSemesterResult(result, performedBy, comments));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/admin/{id}/status")
    public ResponseEntity<SemesterResult> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam String performedBy,
            @RequestParam(required = false) String comments) {
        try {
            return ResponseEntity.ok(service.updateStatus(id, status, performedBy, comments));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/admin/{id}/audits")
    public ResponseEntity<List<SemesterResultAudit>> getAudits(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAudits(id));
    }
}