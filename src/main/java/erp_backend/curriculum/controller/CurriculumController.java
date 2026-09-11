package erp_backend.curriculum.controller;

import erp_backend.curriculum.entity.Curriculum;
import erp_backend.curriculum.service.CurriculumService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/curriculums")
@CrossOrigin("*")
public class CurriculumController {

    private final CurriculumService curriculumService;

    public CurriculumController(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @GetMapping
    public ResponseEntity<List<Curriculum>> getAllCurriculums() {
        return ResponseEntity.ok(curriculumService.getAllCurriculums());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Curriculum> getCurriculumById(@PathVariable Long id) {
        return curriculumService.getCurriculumById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Curriculum> createCurriculum(@RequestBody Curriculum curriculum) {
        try {
            return ResponseEntity.ok(curriculumService.createCurriculum(curriculum));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Curriculum> updateCurriculum(@PathVariable Long id, @RequestBody Curriculum curriculum) {
        try {
            return ResponseEntity.ok(curriculumService.updateCurriculum(id, curriculum));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCurriculum(@PathVariable Long id) {
        try {
            curriculumService.deleteCurriculum(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<Curriculum> updateStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            return ResponseEntity.ok(curriculumService.updateStatus(id, status));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/{id}/subjects")
    public ResponseEntity<Curriculum> addSubject(@PathVariable Long id, @RequestParam Long subjectId,
            @RequestParam(required = false) Integer sortOrder) {
        try {
            return ResponseEntity.ok(curriculumService.addSubject(id, subjectId, sortOrder));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}/subjects/{subjectId}")
    public ResponseEntity<Curriculum> removeSubject(@PathVariable Long id, @PathVariable Long subjectId) {
        try {
            return ResponseEntity.ok(curriculumService.removeSubject(id, subjectId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
