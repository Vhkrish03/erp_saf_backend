package erp_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

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
}