package erp_backend.examcell.repository;

import erp_backend.examcell.entity.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExaminationRepository extends JpaRepository<Examination, Long> {
    List<Examination> findByDepartmentAndAcademicYearAndSemesterName(String department, String academicYear,
            String semesterName);

    List<Examination> findByStatus(String status);
}
