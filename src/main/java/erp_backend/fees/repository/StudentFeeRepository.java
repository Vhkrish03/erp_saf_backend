package erp_backend.fees.repository;

import erp_backend.fees.entity.StudentFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentFeeRepository extends JpaRepository<StudentFee, Long> {
    List<StudentFee> findByStudentId(String studentId);

    Optional<StudentFee> findByStudentIdAndFeeStructureId(String studentId, Long feeStructureId);

    List<StudentFee> findByAcademicYearAndSemester(String academicYear, String semester);

    @Query("SELECT sf FROM StudentFee sf WHERE sf.student.department = :dept " +
            "AND sf.student.semester = :sem AND sf.student.section = :sec " +
            "AND sf.academicYear = :ay")
    List<StudentFee> findByClassSection(
            @Param("dept") String department,
            @Param("sem") String semester,
            @Param("sec") String section,
            @Param("ay") String academicYear);

    @Query("SELECT sf FROM StudentFee sf WHERE sf.student.department = :dept " +
            "AND sf.student.semester = :sem AND sf.student.section = :sec " +
            "AND sf.academicYear = :ay AND sf.paymentStatus IN ('PENDING', 'OVERDUE', 'PARTIALLY_PAID')")
    List<StudentFee> findPendingByClassSection(
            @Param("dept") String department,
            @Param("sem") String semester,
            @Param("sec") String section,
            @Param("ay") String academicYear);
}
