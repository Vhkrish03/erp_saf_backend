package erp_backend.dto;

public class LoginResponse {

    private boolean success;
    private String message;
    private String role;
    private Long id;
    private String fullName;
    private String email;
    private String referenceId;

    public LoginResponse() {
    }

    public LoginResponse(boolean success,
                         String message,
                         String role,
                         Long id,
                         String fullName,
                         String email,
                         String referenceId
                        ) {

        this.success = success;
        this.message = message;
        this.role = role;
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.referenceId = referenceId;
    }

    // Getters and Setters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReferenceId() {
        return referenceId;
    }
    
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

}