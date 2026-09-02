package erp_backend.attendance.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import erp_backend.Teacher.entity.Teacher;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "attendance_delegation")
public class AttendanceDelegation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assigned_by_id")
    private Teacher assignedBy;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private Teacher assignedTo;

    private String department;
    private String year;
    private String section;
    private LocalDate date;
    private String period;
    private String subject;

    private String reason;
    private String status; // PENDING, ACCEPTED, EXPIRED

    private LocalDateTime createdAt = LocalDateTime.now();

    public AttendanceDelegation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Teacher getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Teacher assignedBy) {
        this.assignedBy = assignedBy;
    }

    public Teacher getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Teacher assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
