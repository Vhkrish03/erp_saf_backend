package erp_backend.examcell.repository;

import erp_backend.examcell.entity.ExamCellResultAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamCellResultAuditRepository extends JpaRepository<ExamCellResultAudit, Long> {

    List<ExamCellResultAudit> findByExamCellResultIdOrderByPerformedAtAsc(Long examCellResultId);

    List<ExamCellResultAudit> findByStudentIdOrderByPerformedAtAsc(String studentId);
}
