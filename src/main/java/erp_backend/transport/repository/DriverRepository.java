package erp_backend.transport.repository;

import erp_backend.transport.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByEmployeeId(String employeeId);

    boolean existsByEmployeeId(String employeeId);
}
