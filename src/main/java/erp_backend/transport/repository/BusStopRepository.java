package erp_backend.transport.repository;

import erp_backend.transport.entity.BusStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BusStopRepository extends JpaRepository<BusStop, Long> {
    List<BusStop> findByRouteIdOrderByStopOrderAsc(Long routeId);

    void deleteByRouteId(Long routeId);
}
