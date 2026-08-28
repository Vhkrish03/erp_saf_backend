package erp_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import erp_backend.entity.Subject;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByCode(String code);

    java.util.List<Subject> findByDepartment(String department);

    java.util.List<Subject> findByDepartmentAndSemester(String department, int semester);

    java.util.List<Subject> findByDepartmentAndYearAndSemester(String department, String year, int semester);
}
