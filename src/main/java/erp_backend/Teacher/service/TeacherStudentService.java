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

        boolean hasDept = department != null && !department.trim().isEmpty();
        boolean hasSem = semester != null && !semester.trim().isEmpty();
        boolean hasSec = section != null && !section.trim().isEmpty();

        if (hasDept && hasSem && hasSec) {
            return repository.findByDepartmentAndSemesterAndSection(department.trim(), semester.trim(), section.trim());
        } else if (hasDept && hasSem) {
            return repository.findByDepartmentAndSemester(department.trim(), semester.trim());
        } else if (hasDept && hasSec) {
            return repository.findByDepartmentAndSection(department.trim(), section.trim());
        } else if (hasSem && hasSec) {
            return repository.findBySemesterAndSection(semester.trim(), section.trim());
        } else if (hasDept) {
            return repository.findByDepartment(department.trim());
        } else if (hasSem) {
            return repository.findBySemester(semester.trim());
        } else if (hasSec) {
            return repository.findBySection(section.trim());
        }

        return repository.findAll();
    }
}