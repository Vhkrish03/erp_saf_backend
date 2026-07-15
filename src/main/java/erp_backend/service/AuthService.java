package erp_backend.service;

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
            return new LoginResponse(false, "User not found");
        }

        if (!user.get().getPassword().equals(request.getPassword())) {
            return new LoginResponse(false, "Invalid password");
        }

        return new LoginResponse(true, "Login Successful");
    }
}