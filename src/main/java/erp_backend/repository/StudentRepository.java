package erp_backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import erp_backend.entity.Student;

public interface StudentRepository
        extends JpaRepository<Student, String> {
}
