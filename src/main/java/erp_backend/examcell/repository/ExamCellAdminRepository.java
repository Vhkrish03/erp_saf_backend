package erp_backend.examcell.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import erp_backend.examcell.entity.ExamCellAdmin;

public interface ExamCellAdminRepository extends JpaRepository<ExamCellAdmin, Long> {
    Optional<ExamCellAdmin> findByEmployeeId(String employeeId);

    boolean existsByEmployeeId(String employeeId);
}
