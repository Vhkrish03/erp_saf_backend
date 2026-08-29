package erp_backend.examcell.repository;

import erp_backend.examcell.entity.ExamCellResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamCellResultRepository extends JpaRepository<ExamCellResult, Long> {

        Optional<ExamCellResult> findByStudentIdAndSemesterNameAndAcademicYear(
                        String studentId, String semesterName, String academicYear);

        List<ExamCellResult> findByStudentId(String studentId);

        Optional<ExamCellResult> findByStudentIdAndSemesterName(String studentId, String semesterName);

        List<ExamCellResult> findByStudentIdAndStatus(String studentId, String status);

        List<ExamCellResult> findByDepartmentAndSemesterNameAndAcademicYear(
                        String department, String semesterName, String academicYear);

        List<ExamCellResult> findByDepartmentAndSemesterNameAndAcademicYearAndStatus(
                        String department, String semesterName, String academicYear, String status);

        /** Only PUBLISHED results visible to students */
        @Query("SELECT r FROM ExamCellResult r WHERE r.studentId = :studentId AND r.status = 'PUBLISHED'")
        List<ExamCellResult> findPublishedByStudentId(@Param("studentId") String studentId);

        @Query("SELECT r FROM ExamCellResult r WHERE r.studentId = :studentId AND r.semesterName = :semester AND r.status = 'PUBLISHED'")
        Optional<ExamCellResult> findPublishedByStudentIdAndSemester(
                        @Param("studentId") String studentId,
                        @Param("semester") String semester);

        List<ExamCellResult> findByStatus(String status);

        List<ExamCellResult> findByDepartmentAndStatus(String department, String status);
}
