package erp_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import erp_backend.entity.Student;
import erp_backend.repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository repository;

    

    public Student getStudent(String id) {
        return repository.findById(id).orElse(null);
    }

    public Student updateContact(String studentId, Student updatedStudent) {

        Student student = repository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setEmail(updatedStudent.getEmail());
        student.setPhone(updatedStudent.getPhone());
        student.setAddress(updatedStudent.getAddress());

        student.setEmergencyContactName(
                updatedStudent.getEmergencyContactName());

        student.setEmergencyContactPhone(
                updatedStudent.getEmergencyContactPhone());

        return repository.save(student);
    }
}