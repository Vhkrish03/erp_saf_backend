package erp_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import erp_backend.entity.SemesterResult;

public interface SemesterResultRepository
        extends JpaRepository<SemesterResult, Long> {

        List<SemesterResult> findByStudent_Id(String studentId);

}