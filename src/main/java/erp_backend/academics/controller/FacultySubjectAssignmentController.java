package erp_backend.academics.controller;

import erp_backend.academics.entity.FacultySubjectAssignment;
import erp_backend.academics.service.FacultySubjectAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/faculty-subjects")
@CrossOrigin("*")
public class FacultySubjectAssignmentController {

    @Autowired
    private FacultySubjectAssignmentService service;

    @GetMapping
    public List<FacultySubjectAssignment> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacultySubjectAssignment> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/teacher/{employeeId}")
    public List<Map<String, Object>> getTeacherAssignments(@PathVariable String employeeId) {
        return service.getAssignmentsByTeacher(employeeId).stream().map(a -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", a.getId());
            m.put("subjectId", a.getSubject().getId());
            m.put("subjectCode", a.getSubject().getCode());
            m.put("subjectName", a.getSubject().getName());
            m.put("semester", a.getSemester());
            m.put("department", a.getDepartment());
            m.put("year", a.getYear());
            m.put("section", a.getSection());
            m.put("academicYear", a.getAcademicYear());
            return m;
        }).collect(Collectors.toList());
    }

    @GetMapping("/section")
    public List<FacultySubjectAssignment> getSectionAssignments(
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String semester) {

        if (department != null && year != null && semester != null && section != null) {
            return service.getAssignmentsForClass(department, year, semester, section);
        } else if (section != null) {
            return service.getAssignmentsBySection(section);
        }
        return service.getAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            Long subjectId = ((Number) body.get("subjectId")).longValue();
            String employeeId = (String) body.get("employeeId");
            String department = (String) body.get("department");
            String year = String.valueOf(body.get("year"));
            String semester = String.valueOf(body.get("semester"));
            String section = (String) body.get("section");
            String academicYear = (String) body.get("academicYear");

            FacultySubjectAssignment assignment = service.assign(subjectId, employeeId, department, year, semester,
                    section, academicYear);
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            Map<String, String> res = new HashMap<>();
            res.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Long subjectId = ((Number) body.get("subjectId")).longValue();
            String employeeId = (String) body.get("employeeId");
            String department = (String) body.get("department");
            String year = String.valueOf(body.get("year"));
            String semester = String.valueOf(body.get("semester"));
            String section = (String) body.get("section");
            String academicYear = (String) body.get("academicYear");

            FacultySubjectAssignment assignment = service.update(id, subjectId, employeeId, department, year, semester,
                    section, academicYear);
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            Map<String, String> res = new HashMap<>();
            res.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
