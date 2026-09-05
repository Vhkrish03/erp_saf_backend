package erp_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notices")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    private String date;

    private String category;

    @Column(name = "is_important")
    private boolean isImportant;

    private String uploaderRole; // e.g., ADMIN, HOD, TEACHER
    private String department;   // e.g., ALL, CSE, ECE
    private String purpose;
    private String fileUrl;

    public Notice() {
    }

    // Getters

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public String getUploaderRole() {
        return uploaderRole;
    }

    public String getDepartment() {
        return department;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    // Setters

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImportant(boolean isImportant) {
        this.isImportant = isImportant;
    }

    public void setUploaderRole(String uploaderRole) {
        this.uploaderRole = uploaderRole;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}