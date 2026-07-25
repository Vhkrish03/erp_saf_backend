package erp_backend.Teacher.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import erp_backend.Teacher.entity.Teacher;
import erp_backend.Teacher.repository.TeacherRepository;

@Service
public class TeacherService {

    private final TeacherRepository repository;

    public TeacherService(TeacherRepository repository) {
        this.repository = repository;
    }

    public Teacher getTeacherByEmployeeId(String employeeId) {

        Optional<Teacher> teacher = repository.findByEmployeeId(employeeId);

        return teacher.orElse(null);
    }
}