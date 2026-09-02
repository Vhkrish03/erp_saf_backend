package erp_backend.attendance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import erp_backend.attendance.entity.AttendanceRecord;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findByAttendanceSessionId(Long sessionId);

    List<AttendanceRecord> findByStudentId(String studentId);
}
