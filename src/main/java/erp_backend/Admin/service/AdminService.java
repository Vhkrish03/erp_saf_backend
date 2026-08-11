package erp_backend.Admin.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import erp_backend.entity.User;
import erp_backend.entity.Student;
import erp_backend.Teacher.entity.Teacher;
import erp_backend.Hod.entity.Hod;
import erp_backend.repository.UserRepository;
import erp_backend.repository.StudentRepository;
import erp_backend.Teacher.repository.TeacherRepository;
import erp_backend.Hod.repository.HodRepository;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final HodRepository hodRepository;

    public AdminService(UserRepository userRepository,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            HodRepository hodRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.hodRepository = hodRepository;
    }

    // ==========================================
    // STUDENT MANAGEMENT
    // ==========================================

    @Transactional
    public Student createStudent(Student student, String password) {
        if (studentRepository.existsById(student.getId())) {
            throw new IllegalArgumentException("Student ID " + student.getId() + " already exists.");
        }
        if (userRepository.existsByEmail(student.getEmail())) {
            throw new IllegalArgumentException("Email " + student.getEmail() + " is already registered.");
        }

        // Save Student
        Student savedStudent = studentRepository.save(student);

        // Save corresponding User account
        User user = new User();
        user.setFullName(student.getName());
        user.setEmail(student.getEmail());
        user.setPassword(password); // Plain text
        user.setRole("STUDENT");
        user.setReferenceId(student.getId());
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        return savedStudent;
    }

    @Transactional
    public Student updateStudent(String id, Student details) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found."));

        // If email changed, verify no duplicate
        if (!student.getEmail().equalsIgnoreCase(details.getEmail())) {
            if (userRepository.existsByEmail(details.getEmail())) {
                throw new IllegalArgumentException("Email " + details.getEmail() + " is already registered.");
            }
            // Update User email
            Optional<User> optUser = userRepository.findByReferenceIdAndRole(id, "STUDENT");
            if (optUser.isPresent()) {
                User user = optUser.get();
                user.setEmail(details.getEmail());
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
            }
        }

        // Update User name
        if (!student.getName().equalsIgnoreCase(details.getName())) {
            Optional<User> optUser = userRepository.findByReferenceIdAndRole(id, "STUDENT");
            if (optUser.isPresent()) {
                User user = optUser.get();
                user.setFullName(details.getName());
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
            }
        }

        student.setName(details.getName());
        student.setRollNumber(details.getRollNumber());
        student.setDepartment(details.getDepartment());
        student.setSection(details.getSection());
        student.setYear(details.getYear());
        student.setSemester(details.getSemester());
        student.setEmail(details.getEmail());
        student.setPhone(details.getPhone());
        student.setBloodGroup(details.getBloodGroup());
        student.setDob(details.getDob());
        student.setEmergencyContactName(details.getEmergencyContactName());
        student.setEmergencyContactPhone(details.getEmergencyContactPhone());
        student.setAddress(details.getAddress());
        student.setAdvisor(details.getAdvisor());
        student.setCgpa(details.getCgpa());

        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(String id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found."));
        userRepository.deleteByReferenceIdAndRole(id, "STUDENT");
        studentRepository.delete(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudent(String id) {
        return studentRepository.findById(id).orElse(null);
    }

    // ==========================================
    // TEACHER MANAGEMENT
    // ==========================================

    @Transactional
    public Teacher createTeacher(Teacher teacher, String password) {
        if (teacherRepository.existsByEmployeeId(teacher.getEmployeeId())) {
            throw new IllegalArgumentException("Employee ID " + teacher.getEmployeeId() + " already exists.");
        }
        if (userRepository.existsByEmail(teacher.getEmail())) {
            throw new IllegalArgumentException("Email " + teacher.getEmail() + " is already registered.");
        }

        // Save Teacher
        Teacher savedTeacher = teacherRepository.save(teacher);

        // Save corresponding User
        User user = new User();
        user.setFullName(teacher.getName());
        user.setEmail(teacher.getEmail());
        user.setPassword(password);
        user.setRole("FACULTY"); // Uses FACULTY for roles check in login
        user.setReferenceId(teacher.getEmployeeId());
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        return savedTeacher;
    }

    @Transactional
    public Teacher updateTeacher(Long id, Teacher details) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found."));

        // If email changed, check duplicates
        if (!teacher.getEmail().equalsIgnoreCase(details.getEmail())) {
            if (userRepository.existsByEmail(details.getEmail())) {
                throw new IllegalArgumentException("Email " + details.getEmail() + " is already registered.");
            }
            Optional<User> optUser = userRepository.findByReferenceIdAndRole(teacher.getEmployeeId(), "FACULTY");
            if (optUser.isPresent()) {
                User user = optUser.get();
                user.setEmail(details.getEmail());
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
            }
        }

        // Update User name
        if (!teacher.getName().equalsIgnoreCase(details.getName())) {
            Optional<User> optUser = userRepository.findByReferenceIdAndRole(teacher.getEmployeeId(), "FACULTY");
            if (optUser.isPresent()) {
                User user = optUser.get();
                user.setFullName(details.getName());
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
            }
        }

        teacher.setName(details.getName());
        teacher.setGender(details.getGender());
        teacher.setDob(details.getDob());
        teacher.setDepartment(details.getDepartment());
        teacher.setDesignation(details.getDesignation());
        teacher.setQualification(details.getQualification());
        teacher.setExperienceYears(details.getExperienceYears());
        teacher.setPhone(details.getPhone());
        teacher.setEmail(details.getEmail());
        teacher.setAddress(details.getAddress());
        teacher.setJoiningDate(details.getJoiningDate());
        teacher.setStatus(details.getStatus());
        teacher.setPhotoUrl(details.getPhotoUrl());
        teacher.setEmergencyContactName(details.getEmergencyContactName());
        teacher.setEmergencyContactNumber(details.getEmergencyContactNumber());

        return teacherRepository.save(teacher);
    }

    @Transactional
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found."));
        userRepository.deleteByReferenceIdAndRole(teacher.getEmployeeId(), "FACULTY");
        teacherRepository.delete(teacher);
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Teacher getTeacher(Long id) {
        return teacherRepository.findById(id).orElse(null);
    }

    // ==========================================
    // HOD MANAGEMENT
    // ==========================================

    @Transactional
    public Hod createHod(Hod hod, String password) {
        if (hodRepository.existsByEmployeeId(hod.getEmployeeId())) {
            throw new IllegalArgumentException("Employee ID " + hod.getEmployeeId() + " already exists.");
        }
        if (userRepository.existsByEmail(hod.getEmail())) {
            throw new IllegalArgumentException("Email " + hod.getEmail() + " is already registered.");
        }

        // Save Hod
        Hod savedHod = hodRepository.save(hod);

        // Save corresponding User account
        User user = new User();
        user.setFullName(hod.getName());
        user.setEmail(hod.getEmail());
        user.setPassword(password);
        user.setRole("HOD");
        user.setReferenceId(hod.getEmployeeId());
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        return savedHod;
    }

    @Transactional
    public Hod updateHod(Long id, Hod details) {
        Hod hod = hodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("HOD not found."));

        // If email changed, check duplicates
        if (!hod.getEmail().equalsIgnoreCase(details.getEmail())) {
            if (userRepository.existsByEmail(details.getEmail())) {
                throw new IllegalArgumentException("Email " + details.getEmail() + " is already registered.");
            }
            Optional<User> optUser = userRepository.findByReferenceIdAndRole(hod.getEmployeeId(), "HOD");
            if (optUser.isPresent()) {
                User user = optUser.get();
                user.setEmail(details.getEmail());
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
            }
        }

        // Update User name
        if (!hod.getName().equalsIgnoreCase(details.getName())) {
            Optional<User> optUser = userRepository.findByReferenceIdAndRole(hod.getEmployeeId(), "HOD");
            if (optUser.isPresent()) {
                User user = optUser.get();
                user.setFullName(details.getName());
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
            }
        }

        hod.setName(details.getName());
        hod.setGender(details.getGender());
        hod.setDob(details.getDob());
        hod.setDepartment(details.getDepartment());
        hod.setDesignation(details.getDesignation());
        hod.setPhone(details.getPhone());
        hod.setEmail(details.getEmail());
        hod.setAddress(details.getAddress());

        return hodRepository.save(hod);
    }

    @Transactional
    public void deleteHod(Long id) {
        Hod hod = hodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("HOD not found."));
        userRepository.deleteByReferenceIdAndRole(hod.getEmployeeId(), "HOD");
        hodRepository.delete(hod);
    }

    public List<Hod> getAllHods() {
        return hodRepository.findAll();
    }

    public Hod getHod(Long id) {
        return hodRepository.findById(id).orElse(null);
    }

    // ==========================================
    // ADMIN ACCOUNT MANAGEMENT (Super Admin Actions)
    // ==========================================

    @Transactional
    public User createAdmin(String fullName, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email " + email + " is already registered.");
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("ADMIN");
        user.setIsActive(true);
        user.setReferenceId(null);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        // If they are STUDENT, FACULTY, or HOD, delete corresponding record
        if ("STUDENT".equalsIgnoreCase(user.getRole())) {
            studentRepository.findById(user.getReferenceId()).ifPresent(studentRepository::delete);
        } else if ("FACULTY".equalsIgnoreCase(user.getRole())) {
            teacherRepository.findByEmployeeId(user.getReferenceId()).ifPresent(teacherRepository::delete);
        } else if ("HOD".equalsIgnoreCase(user.getRole())) {
            hodRepository.findByEmployeeId(user.getReferenceId()).ifPresent(hodRepository::delete);
        }
        userRepository.delete(user);
    }
}
