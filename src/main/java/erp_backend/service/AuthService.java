package erp_backend.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import erp_backend.dto.LoginRequest;
import erp_backend.dto.LoginResponse;
import erp_backend.entity.User;
import erp_backend.repository.UserRepository;
import erp_backend.repository.StudentRepository;
import erp_backend.Teacher.repository.TeacherRepository;
import erp_backend.Hod.repository.HodRepository;
import erp_backend.examcell.repository.ExamCellAdminRepository;

import erp_backend.entity.Student;
import erp_backend.Teacher.entity.Teacher;
import erp_backend.Hod.entity.Hod;
import erp_backend.examcell.entity.ExamCellAdmin;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final HodRepository hodRepository;
    private final ExamCellAdminRepository examCellAdminRepository;

    /** Roles that log in with email instead of their staff/student ID */
    private static final Set<String> EMAIL_LOGIN_ROLES = Set.of("ADMIN", "SUPER_ADMIN");

    public AuthService(UserRepository userRepository,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            HodRepository hodRepository,
            ExamCellAdminRepository examCellAdminRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.hodRepository = hodRepository;
        this.examCellAdminRepository = examCellAdminRepository;
    }

    public LoginResponse login(LoginRequest request) {

        String loginId = request.getLoginId() == null ? "" : request.getLoginId().trim();
        String password = request.getPassword() == null ? "" : request.getPassword();
        String role = request.getRole() == null ? "" : request.getRole().trim().toUpperCase();

        if (loginId.isEmpty()) {
            return fail("Please enter your ID / Email.");
        }
        if (password.isEmpty()) {
            return fail("Please enter your password.");
        }

        if (EMAIL_LOGIN_ROLES.contains(role)) {
            // Admin/Super-admin still log in with email
            Optional<User> userOpt = userRepository.findByEmail(loginId);
            if (userOpt.isEmpty()) {
                return fail("No account found for the provided Email.");
            }
            User user = userOpt.get();
            if (!user.getPassword().equals(password)) {
                return fail("Incorrect password. Please try again.");
            }
            if (Boolean.FALSE.equals(user.getIsActive())) {
                return fail("Your account is inactive. Contact the administrator.");
            }
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            return new LoginResponse(
                    true,
                    "Login Successful",
                    user.getRole(),
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getReferenceId());
        }

        if (role.equals("STUDENT")) {
            Optional<Student> opt = studentRepository.findById(loginId);
            if (opt.isEmpty())
                return fail("No account found for the provided Student ID.");
            Student s = opt.get();
            if (s.getPassword() == null || !s.getPassword().equals(password))
                return fail("Incorrect password. Please try again.");
            return new LoginResponse(true, "Login Successful", "STUDENT", null, s.getName(), s.getEmail(), s.getId());
        } else if (role.equals("FACULTY")) {
            Optional<Teacher> opt = teacherRepository.findByEmployeeId(loginId);
            if (opt.isEmpty())
                return fail("No account found for the provided Employee ID.");
            Teacher t = opt.get();
            if (t.getPassword() == null || !t.getPassword().equals(password))
                return fail("Incorrect password. Please try again.");
            return new LoginResponse(true, "Login Successful", "FACULTY", t.getId(), t.getName(), t.getEmail(),
                    t.getEmployeeId());
        } else if (role.equals("HOD")) {
            Optional<Hod> opt = hodRepository.findByEmployeeId(loginId);
            if (opt.isEmpty())
                return fail("No account found for the provided Employee ID.");
            Hod h = opt.get();
            if (h.getPassword() == null || !h.getPassword().equals(password))
                return fail("Incorrect password. Please try again.");
            return new LoginResponse(true, "Login Successful", "HOD", h.getId(), h.getName(), h.getEmail(),
                    h.getEmployeeId());
        } else if (role.equals("EXAM_CELL")) {
            Optional<ExamCellAdmin> opt = examCellAdminRepository.findByEmployeeId(loginId);
            if (opt.isEmpty())
                return fail("No account found for the provided Employee ID.");
            ExamCellAdmin e = opt.get();
            if (e.getPassword() == null || !e.getPassword().equals(password))
                return fail("Incorrect password. Please try again.");
            return new LoginResponse(true, "Login Successful", "EXAM_CELL", e.getId(), e.getName(), e.getEmail(),
                    e.getEmployeeId());
        }

        return fail("Invalid Role specified.");
    }

    private LoginResponse fail(String message) {
        return new LoginResponse(false, message, null, null, null, null, null);
    }
}