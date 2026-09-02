package erp_backend.academics.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignment_audit_logs")
@Getter
@Setter
public class AssignmentAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String actionType; // e.g. "ASSIGN_CLASS_INCHARGE", "REMOVE_CLASS_INCHARGE", "ASSIGN_SUBJECT",
                               // "REMOVE_SUBJECT"

    @Column(nullable = false, length = 50)
    private String employeeId;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(nullable = false, length = 100)
    private String changedBy; // Default to "ADMIN" if not authenticated context

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    public AssignmentAuditLog() {
    }

    public AssignmentAuditLog(String actionType, String employeeId, String details, String changedBy) {
        this.actionType = actionType;
        this.employeeId = employeeId;
        this.details = details;
        this.changedBy = changedBy;
        this.timestamp = LocalDateTime.now();
    }
}
