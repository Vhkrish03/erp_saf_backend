package erp_backend.progress.controller;

import erp_backend.progress.service.ProgressCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@CrossOrigin("*")
public class ProgressCardController {

    private final ProgressCardService progressCardService;

    public ProgressCardController(ProgressCardService progressCardService) {
        this.progressCardService = progressCardService;
    }

    @GetMapping("/{studentId}/progress-card")
    public ResponseEntity<Map<String, Object>> getProgressCard(
            @PathVariable String studentId,
            @RequestParam String semester) {
        try {
            return ResponseEntity.ok(progressCardService.getProgressCard(studentId, semester));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
