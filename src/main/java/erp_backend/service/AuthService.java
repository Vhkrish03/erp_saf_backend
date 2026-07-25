package erp_backend.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import erp_backend.dto.LoginRequest;
import erp_backend.dto.LoginResponse;
import erp_backend.entity.User;
import erp_backend.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request) {

        Optional<User> user = userRepository.findByEmail(request.getEmail());
    
        if (user.isEmpty()) {
            return new LoginResponse(false, "User not found", null, null, null, null, null);
        }
    
        if (!user.get().getPassword().equals(request.getPassword())) {
            return new LoginResponse(false, "Invalid password", null, null, null, null, null);
        }
    
        if (!user.get().getIsActive()) {
            return new LoginResponse(false, "Account is inactive", null, null, null, null, null);
        }
    
        user.get().setLastLogin(LocalDateTime.now());
        userRepository.save(user.get());
    
        return new LoginResponse(
            true,
            "Login Successful",
            user.get().getRole(),
            user.get().getId(),
            user.get().getFullName(),
            user.get().getEmail(),
            user.get().getReferenceId()
    );
    }
}