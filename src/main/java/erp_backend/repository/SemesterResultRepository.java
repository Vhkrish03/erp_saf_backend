package erp_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import erp_backend.entity.SemesterResult;

public interface SemesterResultRepository
                extends JpaRepository<SemesterResult, Long> {

        List<SemesterResult> findByStudent_Id(String studentId);

        Optional<SemesterResult> findByStudent_IdAndSemesterName(String studentId, String semesterName);

        List<SemesterResult> findByStudent_DepartmentAndSemesterNameAndStudent_SectionAndAcademicYear(
                        String department, String semesterName, String section, String academicYear);

        List<SemesterResult> findByStatus(String status);
}