package erp_backend.controller;



import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import erp_backend.dto.LoginRequest;
import erp_backend.dto.LoginResponse;
import erp_backend.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
        System.out.println("AuthController Loaded...");
    }

    @GetMapping("/test")
    public String test() {
        return "API Working";
    }

    @GetMapping("/home")
    public String home() {
        return "hello world";
    }

    

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        return authService.login(request);

    }

    

}
