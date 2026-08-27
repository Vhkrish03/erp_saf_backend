package erp_backend.attendance.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import erp_backend.attendance.entity.AttendanceReport;
import erp_backend.attendance.service.AttendanceReportService;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AttendanceReportController {

    private final AttendanceReportService service;

    public AttendanceReportController(AttendanceReportService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> submitAttendance(@RequestBody Map<String, Object> payload) {
        try {
            String department = (String) payload.get("department");
            String studentYear = (String) payload.get("studentYear");
            String section = (String) payload.get("section");
            String subject = (String) payload.get("subject");
            String submittedBy = (String) payload.get("submittedBy");

            String dateStr = (String) payload.get("date");
            LocalDate date = LocalDate.now();
            if (dateStr != null && !dateStr.trim().isEmpty()) {
                date = LocalDate.parse(dateStr);
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> records = (List<Map<String, Object>>) payload.get("records");

            if (department == null || studentYear == null || section == null || subject == null || records == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Missing required fields"));
            }

            AttendanceReport saved = service.saveReport(
                    department,
                    studentYear,
                    section,
                    subject,
                    date,
                    submittedBy,
                    records);

            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Failed to save attendance: " + e.getMessage()));
        }
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<AttendanceReport>> getDepartmentReports(@PathVariable String department) {
        try {
            List<AttendanceReport> reports = service.getReportsForDepartment(department.trim());
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
