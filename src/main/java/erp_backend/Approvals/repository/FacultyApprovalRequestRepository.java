package erp_backend.Approvals.repository;

import erp_backend.Approvals.model.FacultyApprovalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacultyApprovalRequestRepository extends JpaRepository<FacultyApprovalRequest, Long> {
    List<FacultyApprovalRequest> findByHodIdOrderByCreatedAtDesc(String hodId);

    List<FacultyApprovalRequest> findByDepartmentOrderByCreatedAtDesc(String department);

    List<FacultyApprovalRequest> findByStatusOrderByCreatedAtDesc(String status);

    List<FacultyApprovalRequest> findAllByOrderByCreatedAtDesc();
}
