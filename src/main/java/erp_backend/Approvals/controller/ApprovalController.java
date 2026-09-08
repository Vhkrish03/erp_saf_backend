package erp_backend.Approvals.controller;

import erp_backend.Approvals.model.FacultyApprovalRequest;
import erp_backend.Approvals.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/approvals")
@CrossOrigin(origins = "*")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    @PostMapping("/request")
    public ResponseEntity<?> createRequest(@RequestBody FacultyApprovalRequest request) {
        try {
            FacultyApprovalRequest saved = approvalService.createRequest(request);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRequests() {
        return ResponseEntity.ok(approvalService.getPendingRequests());
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllRequests() {
        return ResponseEntity.ok(approvalService.getAllRequests());
    }

    @GetMapping("/hod/{hodId}")
    public ResponseEntity<?> getRequestsByHod(@PathVariable String hodId) {
        return ResponseEntity.ok(approvalService.getRequestsByHod(hodId));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String adminFeedback = body.getOrDefault("feedback", "Approved by Admin");
            FacultyApprovalRequest approved = approvalService.approveRequest(id, adminFeedback);
            return ResponseEntity.ok(approved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String adminFeedback = body.getOrDefault("feedback", "Rejected by Admin");
            FacultyApprovalRequest rejected = approvalService.rejectRequest(id, adminFeedback);
            return ResponseEntity.ok(rejected);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
