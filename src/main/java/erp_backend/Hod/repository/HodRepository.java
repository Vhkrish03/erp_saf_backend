package erp_backend.Hod.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import erp_backend.Hod.entity.Hod;

public interface HodRepository extends JpaRepository<Hod, Long> {
    Optional<Hod> findByEmployeeId(String employeeId);

    boolean existsByEmployeeId(String employeeId);
}
