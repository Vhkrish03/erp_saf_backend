package erp_backend.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import erp_backend.dto.LoginRequest;
import erp_backend.dto.LoginResponse;
import erp_backend.entity.User;
import erp_backend.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;

    /** Roles that log in with email instead of their staff/student ID */
    private static final Set<String> EMAIL_LOGIN_ROLES = Set.of("ADMIN", "SUPER_ADMIN");

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

        Optional<User> userOpt;

        if (EMAIL_LOGIN_ROLES.contains(role)) {
            // Admin/Super-admin still log in with email
            userOpt = userRepository.findByEmail(loginId);
        } else {
            // Student / Faculty / HOD log in with their reference ID + role
            userOpt = userRepository.findByReferenceIdAndRole(loginId, role);
        }

        if (userOpt.isEmpty()) {
            return fail("No account found for the provided ID.");
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

    private LoginResponse fail(String message) {
        return new LoginResponse(false, message, null, null, null, null, null);
    }
}