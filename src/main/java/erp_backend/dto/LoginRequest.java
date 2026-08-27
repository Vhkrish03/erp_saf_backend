package erp_backend.dto;

public class LoginRequest {

    /**
     * For STUDENT/FACULTY/HOD: their ID (STU2309, EMP008, HOD001).
     * For ADMIN/SUPER_ADMIN : their email address.
     */
    private String loginId;

    private String password;

    /**
     * The role the user is selecting on the login screen.
     * Accepted values: STUDENT, FACULTY, HOD, ADMIN, SUPER_ADMIN
     */
    private String role;

    public LoginRequest() {
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}