package erp_backend.Teacher.service;

import java.util.List;

import org.springframework.stereotype.Service;

import erp_backend.entity.Student;
import erp_backend.repository.StudentRepository;

@Service
public class TeacherStudentService {

    private final StudentRepository studentRepository;

    public TeacherStudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Get all students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}