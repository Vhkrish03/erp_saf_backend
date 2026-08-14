package erp_backend.academics.controller;

import erp_backend.academics.entity.AcademicYear;
import erp_backend.academics.service.AcademicYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/academic-years")
@CrossOrigin("*")
public class AcademicYearController {

    @Autowired
    private AcademicYearService service;

    @GetMapping
    public List<AcademicYear> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcademicYear> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public ResponseEntity<AcademicYear> getActive() {
        return service.getActiveYear()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public AcademicYear create(@RequestBody AcademicYear academicYear) {
        return service.create(academicYear);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AcademicYear> update(@PathVariable Long id, @RequestBody AcademicYear details) {
        try {
            return ResponseEntity.ok(service.update(id, details));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
