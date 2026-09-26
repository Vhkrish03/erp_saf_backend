package erp_backend.attendance.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import erp_backend.academics.entity.FacultySubjectAssignment;
import erp_backend.attendance.entity.AttendanceDelegation;

import erp_backend.attendance.dto.AttendanceSaveRequest;
import erp_backend.attendance.entity.AttendanceRecord;
import erp_backend.attendance.entity.AttendanceSession;
import erp_backend.attendance.service.AttendanceCoreService;

@RestController
@RequestMapping("/api/attendance-core")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AttendanceCoreController {

    private final AttendanceCoreService coreService;

    public AttendanceCoreController(AttendanceCoreService coreService) {
        this.coreService = coreService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveAttendance(@RequestBody AttendanceSaveRequest request) {
        try {
            AttendanceSession session = coreService.saveOrUpdateAttendance(request);
            return ResponseEntity.ok(session);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/teacher/{employeeId}/assignments")
    public ResponseEntity<?> getTeacherAssignments(@PathVariable String employeeId) {
        try {
            List<FacultySubjectAssignment> assignments = coreService.getTeacherAssignments(employeeId);
            List<AttendanceDelegation> delegations = coreService.getTeacherDelegations(employeeId);
            List<erp_backend.academics.entity.ClassInchargeAssignment> incharges = coreService
                    .getTeacherInchargeAssignments(employeeId);

            Map<String, Object> response = new HashMap<>();
            response.put("assignments", assignments);
            response.put("delegations", delegations);
            response.put("incharges", incharges);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/session/{sessionId}/records")
    public ResponseEntity<?> getSessionRecords(@PathVariable Long sessionId) {
        List<AttendanceRecord> records = coreService.getRecordsForSession(sessionId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<?> getSessionsForDepartment(@PathVariable String department) {
        List<AttendanceSession> sessions = coreService.getDepartmentSessions(department);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/reports/department/{department}")
    public ResponseEntity<?> getReportsForDepartment(@PathVariable String department) {
        List<AttendanceSession> sessions = coreService.getDepartmentSessions(department);
        // Sort sessions from newest to oldest
        sessions.sort((a, b) -> b.getId().compareTo(a.getId()));
        
        List<Map<String, Object>> response = new java.util.ArrayList<>();
        for (AttendanceSession session : sessions) {
             List<AttendanceRecord> records = coreService.getRecordsForSession(session.getId());
             long presentCount = records.stream().filter(r -> "PRESENT".equalsIgnoreCase(r.getStatus())).count();
             long absentCount = records.size() - presentCount;

             List<Map<String, String>> studentData = new java.util.ArrayList<>();
             for (AttendanceRecord r : records) {
                 Map<String, String> sMap = new HashMap<>();
                 sMap.put("name", r.getStudent().getName());
                 sMap.put("rollNumber", r.getStudent().getRollNumber());
                 sMap.put("status", r.getStatus());
                 studentData.add(sMap);
             }

             Map<String, Object> map = new HashMap<>();
             map.put("id", session.getId());
             map.put("subject", session.getSubject());
             map.put("section", session.getSection());
             map.put("studentYear", session.getYear());
             map.put("date", session.getDate() != null ? session.getDate().toString() : "");
             map.put("period", session.getPeriod());
             map.put("presentCount", presentCount);
             map.put("absentCount", absentCount);
             map.put("totalStudents", records.size());
             map.put("submittedBy", session.getSubmittedBy());
             map.put("status", session.getStatus().name());
             map.put("studentRecords", studentData);
             response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllSessions() {
        List<AttendanceSession> sessions = coreService.getAllSessions();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkAttendance(
            @RequestParam String department,
            @RequestParam String year,
            @RequestParam String section,
            @RequestParam String subject,
            @RequestParam String period,
            @RequestParam String date) {
        try {
            java.time.LocalDate localDate = java.time.LocalDate.parse(date);
            AttendanceSession session = coreService.getSessionDetails(department, year, section, subject, localDate, period);
            if (session != null) {
                List<AttendanceRecord> records = coreService.getRecordsForSession(session.getId());
                List<Map<String, Object>> mappedRecords = new java.util.ArrayList<>();
                for (AttendanceRecord r : records) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("studentId", r.getStudent().getId());
                    map.put("status", r.getStatus());
                    mappedRecords.add(map);
                }
                
                Map<String, Object> sessionMap = new HashMap<>();
                sessionMap.put("status", session.getStatus().name());
                
                return ResponseEntity.ok(Map.of(
                        "exists", true,
                        "session", sessionMap,
                        "records", mappedRecords
                ));
            } else {
                return ResponseEntity.ok(Map.of("exists", false));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/admin/verify/{sessionId}")
    public ResponseEntity<?> verifySession(@PathVariable Long sessionId, @RequestBody Map<String, Object> payload) {
        try {
            boolean isApproved = (Boolean) payload.getOrDefault("isApproved", false);
            String reason = (String) payload.getOrDefault("reason", "");
            String verifiedBy = (String) payload.getOrDefault("verifiedBy", "Admin");

            AttendanceSession verified = coreService.verifySession(sessionId, isApproved, reason, verifiedBy);
            return ResponseEntity.ok(verified);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error verifying: " + e.getMessage()));
        }
    }

    @PutMapping("/admin/modify/{recordId}")
    public ResponseEntity<?> modifyRecord(@PathVariable Long recordId, @RequestBody Map<String, String> payload) {
        try {
            String newStatus = payload.get("newStatus");
            String modifiedBy = payload.get("modifiedBy");
            String modifierRole = payload.get("modifierRole");
            String reason = payload.get("reason");

            AttendanceRecord modified = coreService.modifyRecord(recordId, newStatus, modifiedBy, modifierRole, reason);
            return ResponseEntity.ok(modified);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error modifying: " + e.getMessage()));
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentAttendance(@PathVariable String studentId) {
        List<AttendanceRecord> records = coreService.getStudentAttendance(studentId);
        // Compute basic percentage map
        int presentCount = 0;
        for (AttendanceRecord r : records) {
            if ("PRESENT".equalsIgnoreCase(r.getStatus())) {
                presentCount++;
            }
        }
        Map<String, Object> response = new HashMap<>();
        response.put("records", records);
        response.put("total", records.size());
        response.put("present", presentCount);
        double percentage = records.isEmpty() ? 0 : ((double) presentCount / records.size()) * 100;
        response.put("percentage", percentage);
        return ResponseEntity.ok(response);
    }
}
