package erp_backend.attendance.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import erp_backend.attendance.entity.AttendanceReport;

public interface AttendanceReportRepository extends JpaRepository<AttendanceReport, Long> {

    List<AttendanceReport> findByDepartmentOrderByDateDesc(String department);

    java.util.Optional<AttendanceReport> findByDepartmentAndStudentYearAndSectionAndSubjectAndDate(
            String department,
            String studentYear,
            String section,
            String subject,
            java.time.LocalDate date);
}
