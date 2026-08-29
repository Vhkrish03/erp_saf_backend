package erp_backend.progress.repository;

import erp_backend.progress.entity.ProgressCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressCardRepository extends JpaRepository<ProgressCard, Long> {

    Optional<ProgressCard> findByStudentIdAndSemesterAndAcademicYear(
            String studentId, String semester, String academicYear);

    List<ProgressCard> findByStudentId(String studentId);

    List<ProgressCard> findByDepartmentAndSectionAndSemesterAndAcademicYear(
            String department, String section, String semester, String academicYear);

    List<ProgressCard> findByStatus(String status);

    List<ProgressCard> findByDepartmentAndStatus(String department, String status);
}
