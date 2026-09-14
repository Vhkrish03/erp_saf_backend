package erp_backend.management.repository;

import erp_backend.management.entity.FeeDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeDecisionRepository extends JpaRepository<FeeDecision, Long> {
    List<FeeDecision> findByAcademicYearAndDepartment(String academicYear, String department);

    List<FeeDecision> findByStatus(String status);
}
