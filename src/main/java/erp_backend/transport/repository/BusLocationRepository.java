package erp_backend.transport.repository;

import erp_backend.transport.entity.BusLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BusLocationRepository extends JpaRepository<BusLocation, Long> {
    List<BusLocation> findByBusIdOrderByRecordedAtDesc(Long busId);
}
