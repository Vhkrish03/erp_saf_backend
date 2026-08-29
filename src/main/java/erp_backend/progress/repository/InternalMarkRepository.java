package erp_backend.progress.repository;

import erp_backend.progress.entity.InternalMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InternalMarkRepository extends JpaRepository<InternalMark, Long> {

    Optional<InternalMark> findByStudentIdAndSubjectIdAndSemesterAndAcademicYear(
            String studentId, Long subjectId, String semester, String academicYear);

    List<InternalMark> findByStudentIdAndSemesterAndAcademicYear(
            String studentId, String semester, String academicYear);

    List<InternalMark> findByDepartmentAndSectionAndSemesterAndAcademicYear(
            String department, String section, String semester, String academicYear);

    List<InternalMark> findByStudentIdAndStatus(String studentId, String status);
}
