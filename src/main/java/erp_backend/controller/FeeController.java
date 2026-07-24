package erp_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import erp_backend.entity.Fee;
import erp_backend.service.FeeService;

@RestController
@RequestMapping("/api/fees")
@CrossOrigin("*")
public class FeeController {

    private final FeeService service;

    public FeeController(FeeService service) {
        this.service = service;
    }

    @GetMapping("/{studentId}")
    public List<Fee> getFees(@PathVariable String studentId) {
        return service.getFees(studentId);
    }
}
