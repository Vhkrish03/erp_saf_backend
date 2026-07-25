package erp_backend.Teacher.service;

import java.util.List;

import org.springframework.stereotype.Service;

import erp_backend.entity.Student;
import erp_backend.repository.StudentRepository;

@Service
public class TeacherStudentService {

    private final StudentRepository repository;

    public TeacherStudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public List<Student> getStudents(
            String department,
            String semester,
            String section) {

        if (!department.isEmpty() &&
                !semester.isEmpty() &&
                !section.isEmpty()) {

            return repository.findByDepartmentAndSemesterAndSection(
                    department,
                    semester,
                    section);
        }

        if (!department.isEmpty() &&
                !semester.isEmpty()) {

            return repository.findByDepartmentAndSemester(
                    department,
                    semester);
        }

        if (!department.isEmpty()) {
            return repository.findByDepartment(department);
        }

        if (!semester.isEmpty()) {
            return repository.findBySemester(semester);
        }

        if (!section.isEmpty()) {
            return repository.findBySection(section);
        }

        return repository.findAll();
    }
}