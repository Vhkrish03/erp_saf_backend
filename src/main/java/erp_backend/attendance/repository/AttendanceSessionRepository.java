package erp_backend.attendance.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import erp_backend.attendance.entity.AttendanceSession;

@Repository
public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {
    List<AttendanceSession> findByDepartment(String department);

    List<AttendanceSession> findByTeacherId(Long teacherId);

    List<AttendanceSession> findByDepartmentAndDate(String department, LocalDate date);

    AttendanceSession findByDepartmentAndYearAndSectionAndSubjectAndDateAndPeriod(
            String department, String year, String section, String subject, LocalDate date, String period);
}
