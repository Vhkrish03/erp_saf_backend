package erp_backend.repository;

import erp_backend.entity.AssessmentWeightage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentWeightageRepository extends JpaRepository<AssessmentWeightage, Long> {
    List<AssessmentWeightage> findByDepartmentAndSemester(String department, String semester);

    Optional<AssessmentWeightage> findByDepartmentAndSemesterAndComponentType(String department, String semester,
            String componentType);
}
