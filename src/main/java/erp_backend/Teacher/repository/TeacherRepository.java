package erp_backend.Teacher.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import erp_backend.Teacher.entity.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByEmployeeId(String employeeId);

    boolean existsByEmployeeId(String employeeId);
}