package erp_backend.attendance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import erp_backend.attendance.entity.AttendanceDelegation;

@Repository
public interface AttendanceDelegationRepository extends JpaRepository<AttendanceDelegation, Long> {
    List<AttendanceDelegation> findByAssignedToIdAndStatus(Long assignedToId, String status);

    List<AttendanceDelegation> findByAssignedById(Long assignedById);
}
