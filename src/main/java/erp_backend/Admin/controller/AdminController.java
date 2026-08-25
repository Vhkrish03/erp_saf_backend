package erp_backend.Admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import erp_backend.entity.Student;
import erp_backend.Teacher.entity.Teacher;
import erp_backend.Hod.entity.Hod;
import erp_backend.entity.User;
import erp_backend.Admin.service.AdminService;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ==========================================
    // STUDENTS CRUD
    // ==========================================

    @PostMapping("/students")
    public ResponseEntity<?> createStudent(@RequestBody Map<String, Object> payload) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> studentMap = (Map<String, Object>) payload.get("student");
            String password = (String) payload.get("password");

            if (studentMap == null || password == null || password.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Missing student data or password"));
            }

            Student student = new Student();
            student.setId((String) studentMap.get("id"));
            student.setName((String) studentMap.get("name"));
            student.setRollNumber((String) studentMap.get("rollNumber"));
            student.setDepartment((String) studentMap.get("department"));
            student.setSection((String) studentMap.get("section"));
            student.setYear(studentMap.get("year") != null ? studentMap.get("year").toString() : null);
            student.setSemester(studentMap.get("semester") != null ? studentMap.get("semester").toString() : null);
            student.setEmail((String) studentMap.get("email"));
            student.setPhone((String) studentMap.get("phone"));
            student.setBloodGroup((String) studentMap.get("bloodGroup"));
            student.setDob((String) studentMap.get("dob"));
            student.setEmergencyContactName((String) studentMap.get("emergencyContactName"));
            student.setEmergencyContactPhone((String) studentMap.get("emergencyContactPhone"));
            student.setAddress((String) studentMap.get("address"));
            student.setAdvisor((String) studentMap.get("advisor"));
            student.setCgpa(
                    studentMap.get("cgpa") != null ? Double.parseDouble(studentMap.get("cgpa").toString()) : 0.0);

            student.setResidencyType(
                    studentMap.get("residencyType") != null ? (String) studentMap.get("residencyType") : "DAY_SCHOLAR");
            student.setTransportRequired(
                    studentMap.get("transportRequired") != null ? (Boolean) studentMap.get("transportRequired")
                            : false);
            student.setTransportStatus(
                    studentMap.get("transportStatus") != null ? (String) studentMap.get("transportStatus")
                            : "NOT_ASSIGNED");

            if (student.getId() == null || student.getId().trim().isEmpty() ||
                    student.getEmail() == null || student.getEmail().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Student ID and Email are required"));
            }

            Student created = adminService.createStudent(student, password);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Server error: " + e.getMessage()));
        }
    }

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(adminService.getAllStudents());
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable String id) {
        Student student = adminService.getStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable String id, @RequestBody Student studentDetails) {
        try {
            Student updated = adminService.updateStudent(id, studentDetails);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Server error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable String id) {
        try {
            adminService.deleteStudent(id);
            return ResponseEntity.ok(Map.of("message", "Student deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    // ==========================================
    // TEACHERS CRUD
    // ==========================================

    @PostMapping("/teachers")
    public ResponseEntity<?> createTeacher(@RequestBody Map<String, Object> payload) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> teacherMap = (Map<String, Object>) payload.get("teacher");
            String password = (String) payload.get("password");

            if (teacherMap == null || password == null || password.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Missing teacher data or password"));
            }

            Teacher teacher = new Teacher();
            teacher.setEmployeeId((String) teacherMap.get("employeeId"));
            teacher.setName((String) teacherMap.get("name"));
            teacher.setGender((String) teacherMap.get("gender"));

            if (teacherMap.get("dob") != null) {
                teacher.setDob(java.time.LocalDate.parse(teacherMap.get("dob").toString()));
            }
            teacher.setDepartment((String) teacherMap.get("department"));
            teacher.setDesignation((String) teacherMap.get("designation"));
            teacher.setQualification((String) teacherMap.get("qualification"));

            if (teacherMap.get("experienceYears") != null) {
                teacher.setExperienceYears(Integer.parseInt(teacherMap.get("experienceYears").toString()));
            }
            teacher.setPhone((String) teacherMap.get("phone"));
            teacher.setEmail((String) teacherMap.get("email"));
            teacher.setAddress((String) teacherMap.get("address"));

            if (teacherMap.get("joiningDate") != null) {
                teacher.setJoiningDate(java.time.LocalDate.parse(teacherMap.get("joiningDate").toString()));
            }
            teacher.setStatus((String) teacherMap.get("status"));
            teacher.setPhotoUrl((String) teacherMap.get("photoUrl"));
            teacher.setEmergencyContactName((String) teacherMap.get("emergencyContactName"));
            teacher.setEmergencyContactNumber((String) teacherMap.get("emergencyContactNumber"));

            if (teacher.getEmployeeId() == null || teacher.getEmployeeId().trim().isEmpty() ||
                    teacher.getEmail() == null || teacher.getEmail().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Employee ID and Email are required"));
            }

            Teacher created = adminService.createTeacher(teacher, password);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Server error: " + e.getMessage()));
        }
    }

    @GetMapping("/teachers")
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        return ResponseEntity.ok(adminService.getAllTeachers());
    }

    @GetMapping("/teachers/{id}")
    public ResponseEntity<Teacher> getTeacher(@PathVariable Long id) {
        Teacher teacher = adminService.getTeacher(id);
        if (teacher == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(teacher);
    }

    @PutMapping("/teachers/{id}")
    public ResponseEntity<?> updateTeacher(@PathVariable Long id, @RequestBody Teacher teacherDetails) {
        try {
            Teacher updated = adminService.updateTeacher(id, teacherDetails);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Server error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/teachers/{id}")
    public ResponseEntity<?> deleteTeacher(@PathVariable Long id) {
        try {
            adminService.deleteTeacher(id);
            return ResponseEntity.ok(Map.of("message", "Teacher deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    // ==========================================
    // HODS CRUD
    // ==========================================

    @PostMapping("/hods")
    public ResponseEntity<?> createHod(@RequestBody Map<String, Object> payload) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> hodMap = (Map<String, Object>) payload.get("hod");
            String password = (String) payload.get("password");

            if (hodMap == null || password == null || password.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Missing HOD data or password"));
            }

            Hod hod = new Hod();
            hod.setEmployeeId((String) hodMap.get("employeeId"));
            hod.setName((String) hodMap.get("name"));
            hod.setGender((String) hodMap.get("gender"));

            if (hodMap.get("dob") != null) {
                hod.setDob(java.time.LocalDate.parse(hodMap.get("dob").toString()));
            }
            hod.setDepartment((String) hodMap.get("department"));
            hod.setDesignation((String) hodMap.get("designation"));
            hod.setPhone((String) hodMap.get("phone"));
            hod.setEmail((String) hodMap.get("email"));
            hod.setAddress((String) hodMap.get("address"));

            if (hod.getEmployeeId() == null || hod.getEmployeeId().trim().isEmpty() ||
                    hod.getEmail() == null || hod.getEmail().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Employee ID and Email are required"));
            }

            Hod created = adminService.createHod(hod, password);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Server error: " + e.getMessage()));
        }
    }

    @GetMapping("/hods")
    public ResponseEntity<List<Hod>> getAllHods() {
        return ResponseEntity.ok(adminService.getAllHods());
    }

    @GetMapping("/hods/{id}")
    public ResponseEntity<Hod> getHod(@PathVariable Long id) {
        Hod hod = adminService.getHod(id);
        if (hod == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(hod);
    }

    @PutMapping("/hods/{id}")
    public ResponseEntity<?> updateHod(@PathVariable Long id, @RequestBody Hod hodDetails) {
        try {
            Hod updated = adminService.updateHod(id, hodDetails);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Server error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/hods/{id}")
    public ResponseEntity<?> deleteHod(@PathVariable Long id) {
        try {
            adminService.deleteHod(id);
            return ResponseEntity.ok(Map.of("message", "HOD deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    // ==========================================
    // SUPER ADMIN ADMINS & USERS CONTROL
    // ==========================================

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody Map<String, Object> payload) {
        try {
            String fullName = (String) payload.get("fullName");
            String email = (String) payload.get("email");
            String password = (String) payload.get("password");

            if (fullName == null || email == null || password == null ||
                    fullName.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "All fields are required"));
            }

            User adminUser = adminService.createAdmin(fullName, email, password);
            return ResponseEntity.status(HttpStatus.CREATED).body(adminUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Server error: " + e.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            adminService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }
}
