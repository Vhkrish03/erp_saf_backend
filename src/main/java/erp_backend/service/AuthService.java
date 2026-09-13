package erp_backend.service;

import java.time.LocalDateTime;
import java.util.Optional;

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

        if (loginId.isEmpty()) {
            return fail("Please enter your ID / Email.");
        }
        if (password.isEmpty()) {
            return fail("Please enter your password.");
        }

        // 1. Check in User table (Email-based roles)
        Optional<User> userOpt = userRepository.findByEmail(loginId);
        if (userOpt.isPresent()) {
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

        // 2. Check Student
        Optional<Student> studentOpt = studentRepository.findById(loginId);
        if (studentOpt.isPresent()) {
            Student s = studentOpt.get();
            if (s.getPassword() == null || !s.getPassword().equals(password))
                return fail("Incorrect password. Please try again.");
            return new LoginResponse(true, "Login Successful", "STUDENT", null, s.getName(), s.getEmail(), s.getId());
        }

        // 3. Check Teacher (Faculty)
        Optional<Teacher> teacherOpt = teacherRepository.findByEmployeeId(loginId);
        if (teacherOpt.isPresent()) {
            Teacher t = teacherOpt.get();
            if (t.getPassword() == null || !t.getPassword().equals(password))
                return fail("Incorrect password. Please try again.");
            return new LoginResponse(true, "Login Successful", "FACULTY", t.getId(), t.getName(), t.getEmail(),
                    t.getEmployeeId());
        }

        // 4. Check HOD
        Optional<Hod> hodOpt = hodRepository.findByEmployeeId(loginId);
        if (hodOpt.isPresent()) {
            Hod h = hodOpt.get();
            if (h.getPassword() == null || !h.getPassword().equals(password))
                return fail("Incorrect password. Please try again.");
            return new LoginResponse(true, "Login Successful", "HOD", h.getId(), h.getName(), h.getEmail(),
                    h.getEmployeeId());
        }

        // 5. Check Exam Cell
        Optional<ExamCellAdmin> examOpt = examCellAdminRepository.findByEmployeeId(loginId);
        if (examOpt.isPresent()) {
            ExamCellAdmin e = examOpt.get();
            if (e.getPassword() == null || !e.getPassword().equals(password))
                return fail("Incorrect password. Please try again.");
            return new LoginResponse(true, "Login Successful", "EXAM_CELL", e.getId(), e.getName(), e.getEmail(),
                    e.getEmployeeId());
        }

        return fail("No account found for the provided ID / Email.");
    }

    private LoginResponse fail(String message) {
        return new LoginResponse(false, message, null, null, null, null, null);
    }
}