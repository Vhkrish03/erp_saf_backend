package erp_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import erp_backend.entity.Student;

public interface StudentRepository extends JpaRepository<Student, String> {

        List<Student> findByDepartment(String department);

        List<Student> findBySemester(String semester);

        List<Student> findBySection(String section);

        List<Student> findByDepartmentAndSemester(
                        String department,
                        String semester);

        List<Student> findByDepartmentAndSection(
                        String department,
                        String section);

        List<Student> findBySemesterAndSection(
                        String semester,
                        String section);

        List<Student> findByDepartmentAndSemesterAndSection(
                        String department,
                        String semester,
                        String section);

        List<Student> findByDepartmentAndYear(
                        String department,
                        String year);

        List<Student> findByDepartmentAndYearAndSection(
                        String department,
                        String year,
                        String section);

        @Query("SELECT s FROM Student s WHERE s.department = :department AND s.year IN :years")
        List<Student> findByDepartmentAndYearIn(
                        @Param("department") String department,
                        @Param("years") List<String> years);

        @Query("SELECT s FROM Student s WHERE s.department = :department AND s.year IN :years AND s.section = :section")
        List<Student> findByDepartmentAndYearInAndSection(
                        @Param("department") String department,
                        @Param("years") List<String> years,
                        @Param("section") String section);
}