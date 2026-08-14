package erp_backend.repository;

import erp_backend.entity.AssessmentComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentComponentRepository extends JpaRepository<AssessmentComponent, Long> {
    List<AssessmentComponent> findByAssessmentId(Long assessmentId);
}
