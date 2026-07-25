package erp_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import erp_backend.dto.ContactUpdateRequest;
import erp_backend.entity.Student;
import erp_backend.repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository repository;

    public Student getStudent(String id) {
        return repository.findById(id).orElse(null);
    }

    public Student updateContact(String studentId, ContactUpdateRequest request) {

        Student student = repository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress());
        student.setEmergencyContactName(request.getEmergencyContactName());
        student.setEmergencyContactPhone(request.getEmergencyContactPhone());

        return repository.save(student);
    }
}
