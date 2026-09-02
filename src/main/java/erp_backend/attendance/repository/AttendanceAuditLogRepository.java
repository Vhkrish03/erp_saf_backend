package erp_backend.attendance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import erp_backend.attendance.entity.AttendanceAuditLog;

@Repository
public interface AttendanceAuditLogRepository extends JpaRepository<AttendanceAuditLog, Long> {
    List<AttendanceAuditLog> findByAttendanceRecordId(Long recordId);
}
