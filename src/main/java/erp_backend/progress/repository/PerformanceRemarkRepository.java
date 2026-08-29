package erp_backend.progress.repository;

import erp_backend.progress.entity.PerformanceRemark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceRemarkRepository extends JpaRepository<PerformanceRemark, Long> {

    List<PerformanceRemark> findByStudentIdAndSemesterAndAcademicYear(
            String studentId, String semester, String academicYear);

    List<PerformanceRemark> findByStudentIdOrderByCreatedAtDesc(String studentId);

    List<PerformanceRemark> findByRemarkByAndRemarkByRole(String remarkBy, String remarkByRole);
}
