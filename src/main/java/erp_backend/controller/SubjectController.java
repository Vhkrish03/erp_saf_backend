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
