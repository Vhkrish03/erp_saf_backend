package erp_backend.Teacher.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import erp_backend.Teacher.service.TeacherStudentService;
import erp_backend.entity.Student;

@RestController
@RequestMapping("/api/teacher/students")
@CrossOrigin("*")
public class TeacherStudentController {

    private final TeacherStudentService teacherStudentService;

    public TeacherStudentController(TeacherStudentService teacherStudentService) {
        this.teacherStudentService = teacherStudentService;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return teacherStudentService.getAllStudents();
    }
}