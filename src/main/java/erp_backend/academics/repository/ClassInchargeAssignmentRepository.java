package erp_backend.academics.repository;

import erp_backend.academics.entity.ClassInchargeAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassInchargeAssignmentRepository extends JpaRepository<ClassInchargeAssignment, Long> {

    // Find active assignment for a class
    Optional<ClassInchargeAssignment> findByDepartmentAndYearAndSectionAndAcademicYearAndActiveTrue(
            String department, String year, String section, String academicYear);

    // Find active assignment for a teacher
    Optional<ClassInchargeAssignment> findByTeacher_EmployeeIdAndActiveTrue(String employeeId);

    // Get all assignments for a teacher (history)
    List<ClassInchargeAssignment> findByTeacher_EmployeeIdOrderByCreatedAtDesc(String employeeId);
}
