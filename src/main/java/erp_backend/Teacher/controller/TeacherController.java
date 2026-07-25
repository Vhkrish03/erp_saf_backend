package erp_backend.Teacher.controller;



import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import erp_backend.Teacher.entity.Teacher;
import erp_backend.Teacher.service.TeacherService;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin("*")
public class TeacherController {

    private final TeacherService service;

    public TeacherController(TeacherService service) {
        this.service = service;
    }

    @GetMapping
    public List<Teacher> getAllTeachers() {
        return service.getAllTeachers();
    }

    @GetMapping("/{employeeId}")
    public Teacher getTeacher(@PathVariable String employeeId) {
        return service.getTeacherByEmployeeId(employeeId);
    }
}
