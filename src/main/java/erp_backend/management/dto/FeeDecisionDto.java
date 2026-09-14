package erp_backend.management.dto;

import erp_backend.management.entity.FeeDecision;
import erp_backend.management.entity.FeeDecisionComponent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class FeeDecisionDto {
    private Long id;
    private String academicYear;
    private String department;
    private String program;
    private String studentYear;
    private String semester;
    private String status;
    private String createdBy;
    private LocalDateTime creationDate;
    private List<FeeDecisionComponentDto> components;
    private double totalAmount;

    public FeeDecisionDto() {
    }

    public FeeDecisionDto(FeeDecision entity) {
        this.id = entity.getId();
        this.academicYear = entity.getAcademicYear();
        this.department = entity.getDepartment();
        this.program = entity.getProgram();
        this.studentYear = entity.getStudentYear();
        this.semester = entity.getSemester();
        this.status = entity.getStatus();
        this.createdBy = entity.getCreatedBy();
        this.creationDate = entity.getCreationDate();

        if (entity.getComponents() != null) {
            this.components = entity.getComponents().stream()
                    .map(FeeDecisionComponentDto::new)
                    .collect(Collectors.toList());
            this.totalAmount = this.components.stream().mapToDouble(FeeDecisionComponentDto::getAmount).sum();
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public String getDepartment() {
        return department;
    }

    public String getProgram() {
        return program;
    }

    public String getStudentYear() {
        return studentYear;
    }

    public String getSemester() {
        return semester;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public List<FeeDecisionComponentDto> getComponents() {
        return components;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public static class FeeDecisionComponentDto {
        private String name;
        private double amount;
        private String applicableCondition;

        public FeeDecisionComponentDto() {
        }

        public FeeDecisionComponentDto(FeeDecisionComponent entity) {
            this.name = entity.getName();
            this.amount = entity.getAmount();
            this.applicableCondition = entity.getApplicableCondition();
        }

        public String getName() {
            return name;
        }

        public double getAmount() {
            return amount;
        }

        public String getApplicableCondition() {
            return applicableCondition;
        }
    }
}
