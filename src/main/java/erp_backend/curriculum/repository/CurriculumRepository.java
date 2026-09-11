package erp_backend.curriculum.repository;

import erp_backend.curriculum.entity.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {

        Optional<Curriculum> findByDepartmentAndRegulationAndAcademicYearAndYearAndSemester(
                        String department, String regulation, String academicYear, String year, int semester);

        List<Curriculum> findByDepartment(String department);

        // Custom query to fetch published curriculum for a specific batch/sem
        Optional<Curriculum> findByDepartmentAndRegulationAndAcademicYearAndYearAndSemesterAndStatus(
                        String department, String regulation, String academicYear, String year, int semester,
                        String status);

        Optional<Curriculum> findFirstByDepartmentAndYearAndSemesterAndAcademicYearAndStatus(
                        String department, String year, int semester, String academicYear, String status);
}
