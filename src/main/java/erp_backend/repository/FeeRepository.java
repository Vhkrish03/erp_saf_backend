package erp_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import erp_backend.entity.Fee;

public interface FeeRepository extends JpaRepository<Fee, Long> {

    List<Fee> findByStudentId(String studentId);

}
