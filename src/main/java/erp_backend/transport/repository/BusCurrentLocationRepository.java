package erp_backend.transport.repository;

import erp_backend.transport.entity.BusCurrentLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BusCurrentLocationRepository extends JpaRepository<BusCurrentLocation, Long> {
    Optional<BusCurrentLocation> findByBusId(Long busId);
}
