package erp_backend.attendance.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import erp_backend.attendance.entity.AttendanceReport;
import erp_backend.attendance.repository.AttendanceReportRepository;
import erp_backend.entity.Student;
import erp_backend.repository.StudentRepository;

@Service
public class AttendanceReportService {

    private final AttendanceReportRepository repository;
    private final StudentRepository studentRepository;
    private final ObjectMapper objectMapper;

    public AttendanceReportService(AttendanceReportRepository repository,
            StudentRepository studentRepository,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.studentRepository = studentRepository;
        this.objectMapper = objectMapper;
    }

    public AttendanceReport saveReport(
            String department,
            String studentYear,
            String section,
            String subject,
            java.time.LocalDate date,
            String submittedBy,
            List<Map<String, Object>> records) {
        AttendanceReport report = new AttendanceReport();
        report.setDepartment(department);
        report.setStudentYear(studentYear);
        report.setSection(section);
        report.setSubject(subject);
        report.setDate(date);
        report.setSubmittedBy(submittedBy);
        report.setTotalStudents(records.size());

        int present = 0;
        int absent = 0;

        List<Map<String, Object>> enrichedRecords = new ArrayList<>();

        for (Map<String, Object> record : records) {
            String studentId = (String) record.get("studentId");
            Boolean isPresent = (Boolean) record.get("isPresent");
            if (isPresent == null) {
                isPresent = false;
            }

            if (isPresent) {
                present++;
            } else {
                absent++;
            }

            Map<String, Object> enriched = new HashMap<>();
            enriched.put("studentId", studentId);
            enriched.put("isPresent", isPresent);

            // Fetch name & rollNumber from repository
            Student student = studentRepository.findById(studentId).orElse(null);
            if (student != null) {
                enriched.put("studentName", student.getName());
                enriched.put("rollNumber", student.getRollNumber());
            } else {
                enriched.put("studentName", "Unknown Student");
                enriched.put("rollNumber", studentId);
            }

            enrichedRecords.add(enriched);
        }

        report.setPresentCount(present);
        report.setAbsentCount(absent);

        try {
            String json = objectMapper.writeValueAsString(enrichedRecords);
            report.setStudentRecordsJson(json);
        } catch (Exception e) {
            report.setStudentRecordsJson("[]");
        }

        return repository.save(report);
    }

    public List<AttendanceReport> getReportsForDepartment(String department) {
        return repository.findByDepartmentOrderByDateDesc(department);
    }
}
