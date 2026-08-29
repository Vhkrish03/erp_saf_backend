package erp_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import erp_backend.entity.Subject;
import erp_backend.service.SubjectService;

@RestController
@RequestMapping("/api/subjects")
@CrossOrigin("*")
public class SubjectController {

    @Autowired
    private SubjectService service;

    @GetMapping
    public List<Subject> getSubjects() {
        return service.getAllSubjects();
    }

    @GetMapping("/department/{dept}")
    public List<Subject> getSubjectsByDepartment(@PathVariable String dept) {
        return service.getSubjectsByDepartment(dept);
    }

    @GetMapping("/department/{dept}/semester/{sem}")
    public List<Subject> getSubjectsByDeptAndSemester(@PathVariable String dept, @PathVariable int sem) {
        return service.getSubjectsByDeptAndSemester(dept, sem);
    }

    @GetMapping("/filter")
    public List<Subject> getSubjectsByFilter(
            @RequestParam String department,
            @RequestParam String year,
            @RequestParam int semester) {
        String normalizedYear = normalizeYear(year);
        return service.getSubjectsByFilter(department, normalizedYear, semester);
    }

    private String normalizeYear(String year) {
        if (year == null)
            return "";
        String trimmed = year.trim();
        if (trimmed.equals("1") || trimmed.equalsIgnoreCase("1st year") || trimmed.equalsIgnoreCase("I"))
            return "1st year";
        if (trimmed.equals("2") || trimmed.equalsIgnoreCase("2nd year") || trimmed.equalsIgnoreCase("II"))
            return "2nd year";
        if (trimmed.equals("3") || trimmed.equalsIgnoreCase("3rd year") || trimmed.equalsIgnoreCase("III"))
            return "3rd year";
        if (trimmed.equals("4") || trimmed.equalsIgnoreCase("4th year") || trimmed.equalsIgnoreCase("IV"))
            return "4th year";
        return trimmed;
    }

    @PostMapping
    public ResponseEntity<Subject> createSubject(@RequestBody Subject subject) {
        return ResponseEntity.ok(service.createSubject(subject));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subject> updateSubject(@PathVariable Long id, @RequestBody Subject details) {
        try {
            return ResponseEntity.ok(service.updateSubject(id, details));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        service.deleteSubject(id);
        return ResponseEntity.ok().build();
    }
}
