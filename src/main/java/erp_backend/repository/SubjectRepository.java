package erp_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import erp_backend.entity.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

}
