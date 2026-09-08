package erp_backend.Approvals.service;

import erp_backend.Admin.service.AdminService;
import erp_backend.Approvals.model.FacultyApprovalRequest;
import erp_backend.Approvals.repository.FacultyApprovalRequestRepository;
import erp_backend.Teacher.entity.Teacher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class ApprovalService {

    @Autowired
    private FacultyApprovalRequestRepository approvalRepo;

    @Autowired
    private AdminService adminService;

    @Transactional
    public FacultyApprovalRequest createRequest(FacultyApprovalRequest request) {
        request.setStatus("PENDING");
        return approvalRepo.save(request);
    }

    public List<FacultyApprovalRequest> getPendingRequests() {
        return approvalRepo.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    public List<FacultyApprovalRequest> getRequestsByHod(String hodId) {
        return approvalRepo.findByHodIdOrderByCreatedAtDesc(hodId);
    }

    public List<FacultyApprovalRequest> getAllRequests() {
        return approvalRepo.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public FacultyApprovalRequest approveRequest(Long id, String adminFeedback) throws Exception {
        FacultyApprovalRequest req = approvalRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (!"PENDING".equals(req.getStatus())) {
            throw new IllegalStateException("Request is already " + req.getStatus());
        }

        ObjectMapper mapper = new ObjectMapper();

        switch (req.getActionType()) {
            case "ADD":
                // Parse payload to Teacher object
                Teacher newTeacher = mapper.readValue(req.getPayload(), Teacher.class);
                String password = newTeacher.getPassword() != null ? newTeacher.getPassword() : "pass@123";
                adminService.createTeacher(newTeacher, password);
                break;
            case "EDIT":
                Teacher editDetails = mapper.readValue(req.getPayload(), Teacher.class);
                adminService.updateTeacher(req.getTeacherId(), editDetails);
                break;
            case "DELETE":
                adminService.deleteTeacher(req.getTeacherId());
                break;
            default:
                throw new IllegalArgumentException("Unknown action type: " + req.getActionType());
        }

        req.setStatus("APPROVED");
        req.setAdminFeedback(adminFeedback);
        return approvalRepo.save(req);
    }

    @Transactional
    public FacultyApprovalRequest rejectRequest(Long id, String adminFeedback) {
        FacultyApprovalRequest req = approvalRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (!"PENDING".equals(req.getStatus())) {
            throw new IllegalStateException("Request is already " + req.getStatus());
        }

        req.setStatus("REJECTED");
        req.setAdminFeedback(adminFeedback);
        return approvalRepo.save(req);
    }
}
