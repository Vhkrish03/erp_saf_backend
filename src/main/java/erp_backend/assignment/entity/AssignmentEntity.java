package erp_backend.assignment.entity;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="assignments")
public class AssignmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 5000)
    private String description;

    @Column(name="due_date")
    private LocalDateTime dueDate;

    private String section;

    @Column(name="attachment_file")
    private String attachmentFile;

    @Column(name="created_by")
    private String createdBy;

    @Column(name="is_published")
    private Boolean isPublished;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    public AssignmentEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id=id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title=title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description=description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate=dueDate;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section=section;
    }

    public String getAttachmentFile() {
        return attachmentFile;
    }

    public void setAttachmentFile(String attachmentFile) {
        this.attachmentFile=attachmentFile;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy=createdBy;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished=isPublished;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt=createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt=updatedAt;
    }
}
