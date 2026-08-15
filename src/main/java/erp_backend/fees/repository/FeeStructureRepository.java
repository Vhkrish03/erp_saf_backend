package erp_backend.fees.repository;

import erp_backend.fees.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    List<FeeStructure> findByIsActiveTrue();

    List<FeeStructure> findByDepartmentAndSemesterAndAcademicYear(
            String department, String semester, String academicYear);

    List<FeeStructure> findByAcademicYearAndSemester(String academicYear, String semester);
}
