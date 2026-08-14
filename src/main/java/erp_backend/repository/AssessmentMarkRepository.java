package erp_backend.repository;

import erp_backend.entity.AssessmentMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentMarkRepository extends JpaRepository<AssessmentMark, Long> {
    List<AssessmentMark> findByAssessmentId(Long assessmentId);

    List<AssessmentMark> findByAssessmentIdAndStudentId(Long assessmentId, String studentId);

    List<AssessmentMark> findByStudentId(String studentId);

    Optional<AssessmentMark> findByAssessmentIdAndComponentIdAndStudentId(Long assessmentId, Long componentId,
            String studentId);

    List<AssessmentMark> findByComponentId(Long componentId);
}
