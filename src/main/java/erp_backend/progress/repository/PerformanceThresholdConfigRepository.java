package erp_backend.progress.repository;

import erp_backend.progress.entity.PerformanceThresholdConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceThresholdConfigRepository extends JpaRepository<PerformanceThresholdConfig, Long> {

    List<PerformanceThresholdConfig> findByIsActiveOrderBySortOrderAsc(boolean isActive);
}
