package erp_backend.controller;

import erp_backend.Hod.entity.Hod;
import erp_backend.Hod.repository.HodRepository;
import erp_backend.Teacher.entity.Teacher;
import erp_backend.Teacher.repository.TeacherRepository;
import erp_backend.academics.entity.FacultySubjectAssignment;
import erp_backend.academics.repository.FacultySubjectAssignmentRepository;
import erp_backend.entity.Student;
import erp_backend.examcell.entity.ExamCellResult;
import erp_backend.examcell.entity.ExamCellResultSubject;
import erp_backend.examcell.repository.ExamCellResultRepository;
import erp_backend.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/semester-results")
@CrossOrigin("*")
public class SemesterResultsController {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final HodRepository hodRepository;
    private final ExamCellResultRepository examCellResultRepository;
    private final FacultySubjectAssignmentRepository facultySubjectAssignmentRepository;

    public SemesterResultsController(
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            HodRepository hodRepository,
            ExamCellResultRepository examCellResultRepository,
            FacultySubjectAssignmentRepository facultySubjectAssignmentRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.hodRepository = hodRepository;
        this.examCellResultRepository = examCellResultRepository;
        this.facultySubjectAssignmentRepository = facultySubjectAssignmentRepository;
    }

    private String mapSemesterToRoman(String semester) {
        if (semester == null)
            return "";
        String clean = semester.trim().toUpperCase();
        switch (clean) {
            case "S1":
            case "1":
                return "I";
            case "S2":
            case "2":
                return "II";
            case "S3":
            case "3":
                return "III";
            case "S4":
            case "4":
                return "IV";
            case "S5":
            case "5":
                return "V";
            case "S6":
            case "6":
                return "VI";
            case "S7":
            case "7":
                return "VII";
            case "S8":
            case "8":
                return "VIII";
            default:
                return clean;
        }
    }

    // Helper to check if a subject grade represents a fail
    private boolean isFailedGrade(String grade) {
        if (grade == null)
            return true;
        String g = grade.trim().toUpperCase();
        return g.equals("U") || g.equals("RA") || g.equals("UA") || g.equals("W") || g.isEmpty();
    }

    // Helper to map grade to grade point (out of 10)
    private double gradeToPoint(String grade) {
        if (grade == null)
            return 0.0;
        switch (grade.trim().toUpperCase()) {
            case "O":
                return 10.0;
            case "A+":
                return 9.0;
            case "A":
                return 8.0;
            case "B+":
                return 7.0;
            case "B":
                return 6.0;
            case "C":
                return 5.0;
            default:
                return 0.0;
        }
    }

    // ── Student: Own Result VIEW ONLY ─────────────────────────────────────────

    @GetMapping("/my-result")
    public ResponseEntity<?> getMyResult(
            @RequestParam String studentId,
            @RequestParam String semester) {
        Optional<ExamCellResult> opt = examCellResultRepository
                .findByStudentIdAndSemesterNameAndAcademicYear(studentId, semester,
                        resolveAcademicYearForSem(semester));
        if (opt.isEmpty()) {
            opt = examCellResultRepository.findByStudentId(studentId).stream()
                    .filter(r -> r.getSemesterName().equalsIgnoreCase(semester))
                    .findFirst();
        }

        if (opt.isPresent()) {
            ExamCellResult r = opt.get();
            if (!"PUBLISHED".equalsIgnoreCase(r.getStatus())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Semester result has not been published yet."));
            }
            return ResponseEntity.ok(r);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Semester result has not been published yet."));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentResult(
            @PathVariable String studentId,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String teacherId,
            @RequestParam(required = false) String hodId) {

        Student student = studentRepository.findById(studentId)
                .orElse(null);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Student not found."));
        }

        // Authorization checks
        if (hodId != null && !hodId.isBlank()) {
            Hod hod = hodRepository.findByEmployeeId(hodId).orElse(null);
            if (hod == null || !hod.getDepartment().equalsIgnoreCase(student.getDepartment())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Unauthorized access. HOD department mismatch."));
            }
        } else if (teacherId != null && !teacherId.isBlank()) {
            Teacher t = teacherRepository.findByEmployeeId(teacherId).orElse(null);
            if (t == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Teacher not found."));
            }
            List<FacultySubjectAssignment> assignments = facultySubjectAssignmentRepository
                    .findByTeacherEmployeeId(teacherId);
            boolean authorized = assignments.stream()
                    .anyMatch(a -> a.getDepartment().equalsIgnoreCase(student.getDepartment()) &&
                            a.getSection().equalsIgnoreCase(student.getSection()));
            if (!authorized) {
                boolean deptAuthorized = assignments.stream()
                        .anyMatch(a -> a.getDepartment().equalsIgnoreCase(student.getDepartment()));
                if (!deptAuthorized) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("message", "Unauthorized access. Class assignment mismatch."));
                }
            }
        }

        List<ExamCellResult> results = examCellResultRepository.findByStudentId(studentId);
        if (semester != null && !semester.isBlank()) {
            String targetSem = mapSemesterToRoman(semester);
            Optional<ExamCellResult> opt = results.stream()
                    .filter(r -> r.getSemesterName().equalsIgnoreCase(semester) ||
                            mapSemesterToRoman(r.getSemesterName()).equalsIgnoreCase(targetSem))
                    .findFirst();

            if (opt.isPresent()) {
                ExamCellResult r = opt.get();
                // Student request (both hodId and teacherId are null) requires PUBLISHED status
                if (hodId == null && teacherId == null && !"PUBLISHED".equalsIgnoreCase(r.getStatus())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("message", "Semester result has not been published yet."));
                }
                return ResponseEntity.ok(r);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Result not found."));
        }

        // Filter out non-published for students
        if (hodId == null && teacherId == null) {
            results = results.stream()
                    .filter(r -> "PUBLISHED".equalsIgnoreCase(r.getStatus()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(results);
    }

    // ── Teacher: View Authorized Students Results ─────────────────────────────

    @GetMapping("/teacher/students")
    public ResponseEntity<?> getTeacherStudentsResults(
            @RequestParam String teacherId,
            @RequestParam String academicYear,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String year,
            @RequestParam String department,
            @RequestParam String section) {

        // Validate teacher assignment
        List<FacultySubjectAssignment> assignments = facultySubjectAssignmentRepository
                .findByTeacherEmployeeId(teacherId);

        boolean authorized = false;
        if (year != null && !year.isBlank()) {
            List<String> yearVariants = getYearVariants(year);
            authorized = assignments.stream().anyMatch(a -> a.getDepartment().equalsIgnoreCase(department) &&
                    yearVariants.stream().anyMatch(y -> y.equalsIgnoreCase(a.getYear())) &&
                    a.getSection().equalsIgnoreCase(section) &&
                    a.getAcademicYear().equalsIgnoreCase(academicYear));
        } else if (semester != null && !semester.isBlank()) {
            String romanSem = mapSemesterToRoman(semester);
            authorized = assignments.stream().anyMatch(a -> a.getDepartment().equalsIgnoreCase(department) &&
                    mapSemesterToRoman(a.getSemester()).equalsIgnoreCase(romanSem) &&
                    a.getSection().equalsIgnoreCase(section) &&
                    a.getAcademicYear().equalsIgnoreCase(academicYear));
        }

        if (!authorized) {
            Teacher t = teacherRepository.findByEmployeeId(teacherId).orElse(null);
            if (t == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Teacher not found."));
            }
        }

        // Fetch students in this class
        List<Student> students;
        if (year != null && !year.isBlank()) {
            students = studentRepository.findByDepartmentAndYearInAndSection(department, getYearVariants(year),
                    section);
        } else {
            String romanSem = mapSemesterToRoman(semester);
            students = studentRepository.findByDepartmentAndSemesterAndSection(department, romanSem, section);
        }

        List<Map<String, Object>> response = new ArrayList<>();

        for (Student s : students) {
            String studentSem = null;
            if (semester != null && !semester.isBlank()) {
                studentSem = mapSemesterToRoman(semester);
            }
            if (studentSem == null || studentSem.isBlank()) {
                studentSem = s.getSemester();
            }
            if (studentSem == null || studentSem.isBlank()) {
                if (year != null && !year.isBlank()) {
                    studentSem = guessSemesterFromYear(year);
                }
            }

            final String semName = studentSem;
            Optional<ExamCellResult> rOpt = examCellResultRepository
                    .findByStudentIdAndSemesterNameAndAcademicYear(s.getId(), semName, academicYear);
            if (rOpt.isEmpty()) {
                rOpt = examCellResultRepository.findByStudentId(s.getId()).stream()
                        .filter(r -> r.getSemesterName().equalsIgnoreCase(semName))
                        .findFirst();
            }

            Map<String, Object> map = new HashMap<>();
            map.put("studentId", s.getId());
            map.put("studentName", s.getName());
            map.put("rollNumber", s.getRollNumber());
            map.put("section", s.getSection());
            map.put("department", s.getDepartment());
            map.put("semester", semName);

            if (rOpt.isPresent()) {
                ExamCellResult r = rOpt.get();
                map.put("resultId", r.getId());
                map.put("sgpa", r.getSgpa());
                map.put("cgpa", s.getCgpa());
                map.put("status", r.getStatus());
                map.put("resultStatus", hasFailedSubjects(r) ? "FAIL" : "PASS");
                map.put("backlogs", countBacklogs(r));
            } else {
                map.put("resultId", null);
                map.put("sgpa", 0.0);
                map.put("cgpa", s.getCgpa());
                map.put("status", "NOT_PUBLISHED");
                map.put("resultStatus", "PENDING");
                map.put("backlogs", 0);
            }
            response.add(map);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/teacher/student/{studentId}")
    public ResponseEntity<?> getTeacherStudentResult(
            @PathVariable String studentId,
            @RequestParam String teacherId,
            @RequestParam String semester) {
        return getStudentResult(studentId, semester, teacherId, null);
    }

    // ── HOD: View Department Students Results ─────────────────────────────────

    @GetMapping("/hod/students")
    public ResponseEntity<?> getHodStudentsResults(
            @RequestParam String hodId,
            @RequestParam String academicYear,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String year,
            @RequestParam String department,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String resultStatus) {

        // Validate HOD identity and department
        Hod hod = hodRepository.findByEmployeeId(hodId).orElse(null);
        if (hod == null || !hod.getDepartment().equalsIgnoreCase(department)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Unauthorized HOD access."));
        }

        List<Student> students;
        if (year != null && !year.isBlank()) {
            if (section != null && !section.isBlank()) {
                students = studentRepository.findByDepartmentAndYearInAndSection(department, getYearVariants(year),
                        section);
            } else {
                students = studentRepository.findByDepartmentAndYearIn(department, getYearVariants(year));
            }
        } else {
            String romanSem = mapSemesterToRoman(semester);
            if (section != null && !section.isBlank()) {
                students = studentRepository.findByDepartmentAndSemesterAndSection(department, romanSem, section);
            } else {
                students = studentRepository.findByDepartmentAndSemester(department, romanSem);
            }
        }

        List<Map<String, Object>> response = new ArrayList<>();
        for (Student s : students) {
            String studentSem = null;
            if (semester != null && !semester.isBlank()) {
                studentSem = mapSemesterToRoman(semester);
            }
            if (studentSem == null || studentSem.isBlank()) {
                studentSem = s.getSemester();
            }
            if (studentSem == null || studentSem.isBlank()) {
                if (year != null && !year.isBlank()) {
                    studentSem = guessSemesterFromYear(year);
                }
            }

            final String semName = studentSem;
            Optional<ExamCellResult> rOpt = examCellResultRepository
                    .findByStudentIdAndSemesterNameAndAcademicYear(s.getId(), semName, academicYear);
            if (rOpt.isEmpty()) {
                rOpt = examCellResultRepository.findByStudentId(s.getId()).stream()
                        .filter(r -> r.getSemesterName().equalsIgnoreCase(semName))
                        .findFirst();
            }

            Map<String, Object> map = new HashMap<>();
            map.put("studentId", s.getId());
            map.put("studentName", s.getName());
            map.put("rollNumber", s.getRollNumber());
            map.put("section", s.getSection());
            map.put("department", s.getDepartment());
            map.put("semester", semName);
            map.put("cgpa", s.getCgpa());

            String resStatus = "PENDING";
            int arrears = 0;
            double sgpa = 0.0;
            String publicationStatus = "NOT_PUBLISHED";

            if (rOpt.isPresent()) {
                ExamCellResult r = rOpt.get();
                sgpa = r.getSgpa();
                publicationStatus = r.getStatus();
                arrears = countBacklogs(r);
                resStatus = arrears > 0 ? "FAIL" : "PASS";

                map.put("resultId", r.getId());
            }

            map.put("sgpa", sgpa);
            map.put("status", publicationStatus); // DRAFT, VERIFIED, APPROVED, PUBLISHED
            map.put("resultStatus", resStatus); // PASS, FAIL, PENDING
            map.put("backlogs", arrears);

            if (resultStatus == null || resultStatus.isBlank() || resultStatus.equalsIgnoreCase(resStatus)) {
                response.add(map);
            }
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/hod/student/{studentId}")
    public ResponseEntity<?> getHodStudentResult(
            @PathVariable String studentId,
            @RequestParam String hodId,
            @RequestParam String semester) {
        return getStudentResult(studentId, semester, null, hodId);
    }

    @GetMapping("/hod/statistics")
    public ResponseEntity<?> getHodStatistics(
            @RequestParam String hodId,
            @RequestParam String academicYear,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String year,
            @RequestParam String department,
            @RequestParam(required = false) String section) {

        // Validate HOD identity and department
        Hod hod = hodRepository.findByEmployeeId(hodId).orElse(null);
        if (hod == null || !hod.getDepartment().equalsIgnoreCase(department)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Unauthorized HOD access."));
        }

        List<Student> students;
        if (year != null && !year.isBlank()) {
            if (section != null && !section.isBlank()) {
                students = studentRepository.findByDepartmentAndYearInAndSection(department, getYearVariants(year),
                        section);
            } else {
                students = studentRepository.findByDepartmentAndYearIn(department, getYearVariants(year));
            }
        } else {
            String romanSem = mapSemesterToRoman(semester);
            if (section != null && !section.isBlank()) {
                students = studentRepository.findByDepartmentAndSemesterAndSection(department, romanSem, section);
            } else {
                students = studentRepository.findByDepartmentAndSemester(department, romanSem);
            }
        }

        int totalStudents = students.size();
        int passed = 0;
        int failed = 0;
        int backlogStudentsCount = 0;
        double sumSgpa = 0.0;
        double highestSgpa = 0.0;
        double lowestSgpa = 10.0;
        int appeared = 0;

        // Statistics helper structures
        Map<String, SubjectPerformance> subjectPerfMap = new LinkedHashMap<>();

        for (Student s : students) {
            String studentSem = null;
            if (semester != null && !semester.isBlank()) {
                studentSem = mapSemesterToRoman(semester);
            }
            if (studentSem == null || studentSem.isBlank()) {
                studentSem = s.getSemester();
            }
            if (studentSem == null || studentSem.isBlank()) {
                if (year != null && !year.isBlank()) {
                    studentSem = guessSemesterFromYear(year);
                }
            }

            final String semName = studentSem;
            Optional<ExamCellResult> rOpt = examCellResultRepository
                    .findByStudentIdAndSemesterNameAndAcademicYear(s.getId(), semName, academicYear);
            if (rOpt.isEmpty()) {
                rOpt = examCellResultRepository.findByStudentId(s.getId()).stream()
                        .filter(r -> r.getSemesterName().equalsIgnoreCase(semName))
                        .findFirst();
            }

            if (rOpt.isPresent()) {
                ExamCellResult r = rOpt.get();
                appeared++;
                double sgpa = r.getSgpa();
                sumSgpa += sgpa;
                if (sgpa > highestSgpa)
                    highestSgpa = sgpa;
                if (sgpa < lowestSgpa)
                    lowestSgpa = sgpa;

                int arrears = countBacklogs(r);
                if (arrears > 0) {
                    failed++;
                    backlogStudentsCount++;
                } else {
                    passed++;
                }

                // Process subject-wise statistics
                if (r.getSubjects() != null) {
                    for (ExamCellResultSubject sub : r.getSubjects()) {
                        String code = sub.getSubjectCode();
                        SubjectPerformance sp = subjectPerfMap.computeIfAbsent(code,
                                k -> new SubjectPerformance(code, sub.getSubjectName()));
                        sp.appeared++;
                        boolean subFailed = isFailedGrade(sub.getGrade())
                                || "FAIL".equalsIgnoreCase(sub.getResultStatus());
                        if (subFailed) {
                            sp.failed++;
                        } else {
                            sp.passed++;
                        }
                        sp.totalPoints += gradeToPoint(sub.getGrade());
                    }
                }
            }
        }

        if (appeared == 0)
            lowestSgpa = 0.0;

        double passPercentage = appeared > 0 ? ((double) passed / appeared) * 100.0 : 0.0;
        double averageSgpa = appeared > 0 ? sumSgpa / appeared : 0.0;

        List<Map<String, Object>> subjectPerfList = new ArrayList<>();
        for (SubjectPerformance sp : subjectPerfMap.values()) {
            Map<String, Object> subMap = new LinkedHashMap<>();
            subMap.put("subjectCode", sp.subjectCode);
            subMap.put("subjectName", sp.subjectName);
            subMap.put("studentsAppeared", sp.appeared);
            subMap.put("studentsPassed", sp.passed);
            subMap.put("studentsFailed", sp.failed);
            double subPassPct = sp.appeared > 0 ? ((double) sp.passed / sp.appeared) * 100.0 : 0.0;
            subMap.put("passPercentage", Math.round(subPassPct * 100.0) / 100.0);
            double avgGp = sp.appeared > 0 ? sp.totalPoints / sp.appeared : 0.0;
            subMap.put("averageGradePoint", Math.round(avgGp * 100.0) / 100.0);
            subjectPerfList.add(subMap);
        }

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalStudents", totalStudents);
        stats.put("appearedCount", appeared);
        stats.put("passedCount", passed);
        stats.put("failedCount", failed);
        stats.put("backlogStudents", backlogStudentsCount);
        stats.put("passPercentage", Math.round(passPercentage * 100.0) / 100.0);
        stats.put("averageSGPA", Math.round(averageSgpa * 100.0) / 100.0);
        stats.put("highestSGPA", Math.round(highestSgpa * 100.0) / 100.0);
        stats.put("lowestSGPA", Math.round(lowestSgpa * 100.0) / 100.0);
        stats.put("subjectPerformance", subjectPerfList);

        return ResponseEntity.ok(stats);
    }

    private String guessSemesterFromYear(String year) {
        if (year == null)
            return "I";
        String clean = year.trim().toUpperCase();
        if (clean.contains("2") || clean.startsWith("II"))
            return "III";
        if (clean.contains("3") || clean.startsWith("III"))
            return "V";
        if (clean.contains("4") || clean.startsWith("IV"))
            return "VII";
        return "I";
    }

    private List<String> getYearVariants(String year) {
        if (year == null || year.isBlank()) {
            return java.util.Collections.emptyList();
        }
        String clean = year.trim().toUpperCase();
        switch (clean) {
            case "I":
            case "1":
            case "1ST":
            case "1ST YEAR":
                return java.util.Arrays.asList("I", "1", "1st year", "1st");
            case "II":
            case "2":
            case "2ND":
            case "2ND YEAR":
                return java.util.Arrays.asList("II", "2", "2nd year", "2nd");
            case "III":
            case "3":
            case "3RD":
            case "3RD YEAR":
                return java.util.Arrays.asList("III", "3", "3rd year", "3rd");
            case "IV":
            case "4":
            case "4TH":
            case "4TH YEAR":
                return java.util.Arrays.asList("IV", "4", "4th year", "4th");
            default:
                return java.util.Arrays.asList(year, clean);
        }
    }

    // ── Internal Helpers ──────────────────────────────────────────────────────

    private String resolveAcademicYearForSem(String semester) {
        // Fallback or dynamic resolve academic year mapping
        java.time.LocalDate today = java.time.LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        if (month >= 6) {
            return year + "-" + String.valueOf(year + 1).substring(2);
        } else {
            return (year - 1) + "-" + String.valueOf(year).substring(2);
        }
    }

    private boolean hasFailedSubjects(ExamCellResult r) {
        if (r.getSubjects() == null)
            return false;
        return r.getSubjects().stream()
                .anyMatch(sub -> isFailedGrade(sub.getGrade()) || "FAIL".equalsIgnoreCase(sub.getResultStatus()));
    }

    private int countBacklogs(ExamCellResult r) {
        if (r.getSubjects() == null)
            return 0;
        return (int) r.getSubjects().stream()
                .filter(sub -> isFailedGrade(sub.getGrade()) || "FAIL".equalsIgnoreCase(sub.getResultStatus())).count();
    }

    private static class SubjectPerformance {
        String subjectCode;
        String subjectName;
        int appeared = 0;
        int passed = 0;
        int failed = 0;
        double totalPoints = 0.0;

        SubjectPerformance(String code, String name) {
            this.subjectCode = code;
            this.subjectName = name;
        }
    }
}
