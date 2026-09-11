package erp_backend.curriculum.repository;

import erp_backend.curriculum.entity.CurriculumSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurriculumSubjectRepository extends JpaRepository<CurriculumSubject, Long> {
    List<CurriculumSubject> findByCurriculumIdOrderBySortOrderAsc(Long curriculumId);
}
