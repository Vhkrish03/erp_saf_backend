package erp_backend.idcard.controller;

import erp_backend.idcard.dto.IdCardResponse;
import erp_backend.idcard.service.IdCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/id-card")
@CrossOrigin("*")
public class IdCardController {

    @Autowired
    private IdCardService idCardService;

    @GetMapping("/student/{studentId}")
    public IdCardResponse getStudentIdCard(@PathVariable String studentId) {
        return idCardService.getStudentIdCard(studentId);
    }

    @GetMapping("/teacher/{employeeId}")
    public IdCardResponse getTeacherIdCard(@PathVariable String employeeId) {
        return idCardService.getTeacherIdCard(employeeId);
    }
}
