package erp_backend.service;
import java.util.List;

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


   



/**
 * Returns all students belonging to a particular section.
 *
 * Purpose:
 * This method is mainly used by the Teacher module.
 *
 * Example:
 * If the teacher opens an assignment created for "III CSE A",
 * the application needs to display every student in that section
 * so the teacher can mark whether the report was submitted,
 * assign marks, and add remarks.
 *
 * Future Uses:
 * - Attendance
 * - Internal Marks
 * - Assignment Report Submission
 * - Lab Record Verification
 * - Class-wise Student List
 */
public List<Student> getStudentsBySection(String section) {
    return repository.findBySection(section);
}


}
