package erp_backend.academics.repository;

import erp_backend.academics.entity.AssignmentAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentAuditLogRepository extends JpaRepository<AssignmentAuditLog, Long> {
}
