package erp_backend.buspass.repository;

import erp_backend.buspass.entity.BusPass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BusPassRepository extends JpaRepository<BusPass, Long> {
    Optional<BusPass> findByPersonIdAndPersonType(String personId, String personType);
}
