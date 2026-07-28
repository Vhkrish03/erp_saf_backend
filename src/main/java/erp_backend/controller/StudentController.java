package erp_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import erp_backend.dto.ContactUpdateRequest;
import erp_backend.entity.Student;
import erp_backend.service.StudentService;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    StudentService service;

    @GetMapping("/{id}")
    public Student getStudent(@PathVariable String id) {
        return service.getStudent(id);
    }

    @PutMapping("/contact/{id}")
    public Student updateContact(
        @PathVariable String id,
        @RequestBody ContactUpdateRequest request) {

    return service.updateContact(id, request);
}

    /**
 * Returns all students in a given section.
 *
 * API:
 * GET /api/student/section/{section}
 *
 * Example:
 * GET /api/student/section/III CSE A
 *
 * Used By:
 * Teacher Module
 *
 * Current Purpose:
 * Display all students when the teacher opens the
 * Assignment Report Submission screen.
 *
 * Future Purpose:
 * Can also be reused for Attendance, Marks Entry,
 * Viva, Lab, and other class-wise operations.
 */
@GetMapping("/section/{section}")
public List<Student> getStudentsBySection(
        @PathVariable String section) {

    return service.getStudentsBySection(section);
}
    
}
