package erp_backend.repository;

import erp_backend.entity.AssessmentWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssessmentWorkflowRepository extends JpaRepository<AssessmentWorkflow, Long> {
    Optional<AssessmentWorkflow> findByAssessmentId(Long assessmentId);
}
