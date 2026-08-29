package erp_backend.repository;

import erp_backend.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
        List<Assessment> findByType(String type);

        List<Assessment> findByFacultyId(String facultyId);

        List<Assessment> findByDepartmentAndYearAndSemesterAndSection(String department, int year, String semester,
                        String section);

        List<Assessment> findByDepartmentAndSemesterAndSectionAndType(String department, String semester,
                        String section,
                        String type);

        List<Assessment> findByStatus(String status);

        List<Assessment> findByFacultyIdAndStatus(String facultyId, String status);

        List<Assessment> findByDepartmentAndStatus(String department, String status);

        @Query("SELECT a FROM Assessment a WHERE a.department = :dept AND a.semester = :semester AND a.section = :section AND a.status IN :statuses")
        List<Assessment> findByDepartmentSemesterSectionAndStatuses(
                        @Param("dept") String dept,
                        @Param("semester") String semester,
                        @Param("section") String section,
                        @Param("statuses") List<String> statuses);

        /** All assessments pending for class incharge for a specific class */
        @Query("SELECT a FROM Assessment a WHERE a.department = :dept AND a.semester = :semester AND a.section = :section AND a.status = 'SUBMITTED'")
        List<Assessment> findSubmittedForClass(
                        @Param("dept") String dept,
                        @Param("semester") String semester,
                        @Param("section") String section);

        /** HOD pending: INCHARGE_VERIFIED */
        @Query("SELECT a FROM Assessment a WHERE a.department = :dept AND a.status = 'INCHARGE_VERIFIED'")
        List<Assessment> findVerifiedByInchargeForDepartment(@Param("dept") String dept);

        /** Dean pending: HOD_APPROVED */
        @Query("SELECT a FROM Assessment a WHERE a.status = 'HOD_APPROVED'")
        List<Assessment> findHodApprovedForDean();

        List<Assessment> findByDepartmentAndSemesterAndSectionAndTypeAndAcademicYear(
                        String department, String semester, String section, String type, String academicYear);
}
