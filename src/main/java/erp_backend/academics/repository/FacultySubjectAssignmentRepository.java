package erp_backend.academics.repository;

import erp_backend.academics.entity.FacultySubjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacultySubjectAssignmentRepository extends JpaRepository<FacultySubjectAssignment, Long> {
    List<FacultySubjectAssignment> findByTeacherEmployeeId(String employeeId);

    List<FacultySubjectAssignment> findByDepartmentAndYearAndSemesterAndSection(String department, String year,
            String semester, String section);

    List<FacultySubjectAssignment> findBySection(String section);

    Optional<FacultySubjectAssignment> findBySubjectIdAndTeacherEmployeeIdAndSectionAndAcademicYear(Long subjectId,
            String employeeId, String section, String academicYear);
}
