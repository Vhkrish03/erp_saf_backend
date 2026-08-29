package erp_backend.progress.repository;

import erp_backend.progress.entity.InternalMarkConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface InternalMarkConfigRepository extends JpaRepository<InternalMarkConfig, Long> {

        /**
         * isActive is a primitive boolean; JPA derives the query as "active" =
         * true/false.
         * Use findByDepartmentAndSemesterAndAcademicYearAndIsActive.
         */
        Optional<InternalMarkConfig> findByDepartmentAndSemesterAndAcademicYearAndIsActive(
                        String department, String semester, String academicYear, boolean isActive);

        List<InternalMarkConfig> findByDepartmentAndAcademicYear(String department, String academicYear);

        Optional<InternalMarkConfig> findByDepartmentAndSemesterAndAcademicYear(
                        String department, String semester, String academicYear);
}
