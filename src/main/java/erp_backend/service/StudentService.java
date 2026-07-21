package erp_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import erp_backend.entity.Student;
import erp_backend.repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    StudentRepository repository;

    public Student getStudent(String id) {
        return repository.findById(id).orElse(null);
    }
}