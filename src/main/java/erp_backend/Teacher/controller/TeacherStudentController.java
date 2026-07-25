package erp_backend.Teacher.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import erp_backend.Teacher.service.TeacherStudentService;
import erp_backend.entity.Student;

@RestController
@RequestMapping("/api/teacher/students")
@CrossOrigin("*")
public class TeacherStudentController {

    private final TeacherStudentService service;

    public TeacherStudentController(TeacherStudentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Student> getStudents(

            @RequestParam(defaultValue = "") String department,

            @RequestParam(defaultValue = "") String semester,

            @RequestParam(defaultValue = "") String section) {

        return service.getStudents(
                department,
                semester,
                section);
    }
}