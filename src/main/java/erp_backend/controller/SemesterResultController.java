package erp_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import erp_backend.entity.SemesterResult;
import erp_backend.service.SemesterResultService;

@RestController
@RequestMapping("/api/results")
@CrossOrigin("*")
public class SemesterResultController {

    private final SemesterResultService service;

    public SemesterResultController(SemesterResultService service) {
        this.service = service;
    }

    @GetMapping("/{studentId}")
    public List<SemesterResult> getResults(
            @PathVariable String studentId) {

        return service.getResults(studentId);
    }
}