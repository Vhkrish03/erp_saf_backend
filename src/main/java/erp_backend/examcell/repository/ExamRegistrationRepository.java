package erp_backend.examcell.repository;

import erp_backend.examcell.entity.ExamRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRegistrationRepository extends JpaRepository<ExamRegistration, Long> {
    List<ExamRegistration> findByExaminationId(Long examinationId);

    Optional<ExamRegistration> findByExaminationIdAndStudentId(Long examinationId, String studentId);
}
