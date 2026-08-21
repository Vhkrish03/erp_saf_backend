package erp_backend.buspass.controller;

import erp_backend.buspass.dto.BusPassResponse;
import erp_backend.buspass.service.BusPassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bus-pass")
@CrossOrigin("*")
public class BusPassController {

    @Autowired
    private BusPassService busPassService;

    @GetMapping("/student/{studentId}")
    public BusPassResponse getStudentBusPass(@PathVariable String studentId) {
        return busPassService.getStudentBusPass(studentId);
    }

    @GetMapping("/teacher/{employeeId}")
    public BusPassResponse getTeacherBusPass(@PathVariable String employeeId) {
        return busPassService.getTeacherBusPass(employeeId);
    }
}
