package erp_backend.coe.controller;

import erp_backend.coe.dto.CoeBatchSummaryDto;
import erp_backend.coe.dto.CoeDashboardResponse;
import erp_backend.coe.service.CoeService;
import erp_backend.examcell.entity.ExamCellResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coe")
@CrossOrigin(origins = "*")
public class CoeController {

    @Autowired
    private CoeService coeService;

    @GetMapping("/dashboard")
    public ResponseEntity<CoeDashboardResponse> getDashboardSummary() {
        return ResponseEntity.ok(coeService.getDashboardSummary());
    }

    @GetMapping("/batches")
    public ResponseEntity<List<CoeBatchSummaryDto>> getBatchesByStatus(@RequestParam String status) {
        return ResponseEntity.ok(coeService.getBatchesByStatus(status));
    }

    @GetMapping("/batches/details")
    public ResponseEntity<List<ExamCellResult>> getBatchDetails(
            @RequestParam String department,
            @RequestParam String semesterName,
            @RequestParam String academicYear,
            @RequestParam String examSession,
            @RequestParam String status) {
        return ResponseEntity
                .ok(coeService.getBatchDetails(department, semesterName, academicYear, examSession, status));
    }

    @PostMapping("/batches/approve")
    public ResponseEntity<Map<String, Object>> approveBatch(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(coeService.approveBatch(request));
    }

    @PostMapping("/batches/reject")
    public ResponseEntity<Map<String, Object>> rejectBatch(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(coeService.rejectBatch(request));
    }

    @PostMapping("/batches/authorize-publication")
    public ResponseEntity<Map<String, Object>> authorizePublication(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(coeService.authorizePublication(request));
    }
}
