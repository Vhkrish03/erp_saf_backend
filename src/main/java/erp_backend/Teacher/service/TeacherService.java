package erp_backend.Teacher.service;

import java.util.List;

import org.springframework.stereotype.Service;

import erp_backend.Teacher.entity.Teacher;
import erp_backend.Teacher.repository.TeacherRepository;



@Service
public class TeacherService {

    private final TeacherRepository repository;

    public TeacherService(TeacherRepository repository) {
        this.repository = repository;
    }

    public List<Teacher> getAllTeachers() {
        return repository.findAll();
    }

    public Teacher getTeacherByEmployeeId(String employeeId) {
        return repository.findByEmployeeId(employeeId).orElse(null);
    }
}
