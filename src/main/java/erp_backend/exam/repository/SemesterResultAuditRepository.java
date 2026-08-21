package erp_backend.exam.repository;

import erp_backend.exam.entity.SemesterResultAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SemesterResultAuditRepository extends JpaRepository<SemesterResultAudit, Long> {
    List<SemesterResultAudit> findBySemesterResultIdOrderByPerformedAtDesc(Long semesterResultId);
}
