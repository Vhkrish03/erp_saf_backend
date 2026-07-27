package erp_backend.assignment.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import erp_backend.assignment.entity.AssignmentEntity;

public interface AssignmentRepository extends JpaRepository<AssignmentEntity, Long> {

}
