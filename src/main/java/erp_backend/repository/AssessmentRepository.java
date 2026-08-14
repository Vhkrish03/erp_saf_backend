package erp_backend.repository;

import erp_backend.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByType(String type);

    List<Assessment> findByFacultyId(String facultyId);

    List<Assessment> findByDepartmentAndYearAndSemesterAndSection(String department, int year, String semester,
            String section);

    List<Assessment> findByDepartmentAndSemesterAndSectionAndType(String department, String semester, String section,
            String type);
}
