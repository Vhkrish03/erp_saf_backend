package erp_backend.transport.repository;

import erp_backend.transport.entity.StudentTransportAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentTransportAssignmentRepository extends JpaRepository<StudentTransportAssignment, Long> {
    List<StudentTransportAssignment> findByStudentId(String studentId);

    Optional<StudentTransportAssignment> findByStudentIdAndStatus(String studentId, String status);

    @Query("SELECT COUNT(a) FROM StudentTransportAssignment a WHERE a.bus.id = :busId AND a.status = 'ACTIVE'")
    long countActiveAssignmentsByBusId(Long busId);

    List<StudentTransportAssignment> findByBusIdAndStatus(Long busId, String status);

    void deleteByRouteId(Long routeId);

    void deleteByBusId(Long busId);
}
