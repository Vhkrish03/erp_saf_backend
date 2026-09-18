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

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(value = "DELETE FROM examcell_registrations WHERE examination_id = :examId", nativeQuery = true)
    void deleteByExaminationIdNative(@org.springframework.data.repository.query.Param("examId") Long examId);
}
