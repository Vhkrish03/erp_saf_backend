package erp_backend.assignment.controller;


import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import erp_backend.assignment.entity.AssignmentEntity;
import erp_backend.assignment.service.AssignmentService;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin("*")
public class AssignmentController {

    private final AssignmentService service;

    public AssignmentController(AssignmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<AssignmentEntity> getAssignments() {
        return service.getAllAssignments();
    }

    @GetMapping("/{id}")
    public AssignmentEntity getAssignment(@PathVariable Long id) {
        return service.getAssignment(id);
    }

    @PostMapping
    public AssignmentEntity createAssignment(@RequestBody AssignmentEntity assignment) {
        return service.createAssignment(assignment);
    }

    @DeleteMapping("/{id}")
    public void deleteAssignment(@PathVariable Long id) {
        service.deleteAssignment(id);
    }
}
