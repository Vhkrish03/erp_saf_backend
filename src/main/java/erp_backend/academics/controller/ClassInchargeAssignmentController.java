package erp_backend.academics.controller;

import erp_backend.academics.entity.ClassInchargeAssignment;
import erp_backend.academics.service.ClassInchargeAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/class-incharge-assignments")
@CrossOrigin("*")
public class ClassInchargeAssignmentController {

    @Autowired
    private ClassInchargeAssignmentService service;

    @GetMapping("/teacher/{employeeId}")
    public ResponseEntity<?> getActiveAssignmentForTeacher(@PathVariable String employeeId) {
        return service.getActiveAssignmentByTeacher(employeeId)
                .map(assignment -> {
                    Map<String, Object> res = new HashMap<>();
                    res.put("id", assignment.getId());
                    res.put("department", assignment.getDepartment());
                    res.put("year", assignment.getYear());
                    res.put("section", assignment.getSection());
                    res.put("academicYear", assignment.getAcademicYear());
                    return ResponseEntity.ok((Object) res);
                })
                .orElse(ResponseEntity.ok().build());
    }

    @GetMapping("/class")
    public ResponseEntity<?> getActiveAssignmentForClass(
            @RequestParam String department,
            @RequestParam String year,
            @RequestParam String section,
            @RequestParam String academicYear) {

        return service.getActiveAssignmentByClass(department, year, section, academicYear)
                .map(assignment -> {
                    Map<String, Object> res = new HashMap<>();
                    res.put("teacherName", assignment.getTeacher().getName());
                    res.put("employeeId", assignment.getTeacher().getEmployeeId());
                    return ResponseEntity.ok((Object) res);
                })
                .orElse(ResponseEntity.ok().build());
    }

    @PostMapping
    public ResponseEntity<?> assignClassIncharge(@RequestBody Map<String, Object> body) {
        try {
            String employeeId = (String) body.get("employeeId");
            String department = (String) body.get("department");
            String year = String.valueOf(body.get("year"));
            String section = (String) body.get("section");
            String academicYear = (String) body.get("academicYear");

            ClassInchargeAssignment assignment = service.assignOrReplaceClassIncharge(
                    employeeId, department, year, section, academicYear);
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            Map<String, String> res = new HashMap<>();
            res.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(res);
        }
    }

    @DeleteMapping("/teacher/{employeeId}")
    public ResponseEntity<?> unassignClassIncharge(@PathVariable String employeeId) {
        service.unassignClassIncharge(employeeId);
        return ResponseEntity.ok().build();
    }
}
