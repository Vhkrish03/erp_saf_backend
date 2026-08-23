package erp_backend.event.dto;

import erp_backend.event.entity.Event;
import erp_backend.event.enums.EventStatus;
import erp_backend.event.enums.EventType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private EventType eventType;
    private String organizerName;
    private String organizerDepartment;
    private String venue;
    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate registrationStartDate;
    private LocalDate registrationEndDate;
    private Boolean registrationRequired;
    private String registrationLink;
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
    private String eligibility;
    private String targetAudience;
    private String department;
    private String year;
    private String section;
    private String imageUrl;
    private String attachmentUrl;
    private EventStatus status;
    private String rejectionReason;
    private Long createdById;
    private String createdByName;
    private String createdByRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;

    public EventResponse() {
    }

    public static EventResponse fromEntity(Event event) {
        if (event == null)
            return null;
        EventResponse res = new EventResponse();
        res.setId(event.getId());
        res.setTitle(event.getTitle());
        res.setDescription(event.getDescription());
        res.setEventType(event.getEventType());
        res.setOrganizerName(event.getOrganizerName());
        res.setOrganizerDepartment(event.getOrganizerDepartment());
        res.setVenue(event.getVenue());
        res.setEventDate(event.getEventDate());
        res.setStartTime(event.getStartTime());
        res.setEndTime(event.getEndTime());
        res.setRegistrationStartDate(event.getRegistrationStartDate());
        res.setRegistrationEndDate(event.getRegistrationEndDate());
        res.setRegistrationRequired(event.getRegistrationRequired());
        res.setRegistrationLink(event.getRegistrationLink());
        res.setContactPerson(event.getContactPerson());
        res.setContactEmail(event.getContactEmail());
        res.setContactPhone(event.getContactPhone());
        res.setEligibility(event.getEligibility());
        res.setTargetAudience(event.getTargetAudience());
        res.setDepartment(event.getDepartment());
        res.setYear(event.getYear());
        res.setSection(event.getSection());
        res.setImageUrl(event.getImageUrl());
        res.setAttachmentUrl(event.getAttachmentUrl());
        res.setStatus(event.getStatus());
        res.setRejectionReason(event.getRejectionReason());
        if (event.getCreatedBy() != null) {
            res.setCreatedById(event.getCreatedBy().getId());
            res.setCreatedByName(event.getCreatedBy().getFullName());
        }
        res.setCreatedByRole(event.getCreatedByRole());
        res.setCreatedAt(event.getCreatedAt());
        res.setUpdatedAt(event.getUpdatedAt());
        res.setPublishedAt(event.getPublishedAt());
        return res;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getOrganizerDepartment() {
        return organizerDepartment;
    }

    public void setOrganizerDepartment(String organizerDepartment) {
        this.organizerDepartment = organizerDepartment;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalDate getRegistrationStartDate() {
        return registrationStartDate;
    }

    public void setRegistrationStartDate(LocalDate registrationStartDate) {
        this.registrationStartDate = registrationStartDate;
    }

    public LocalDate getRegistrationEndDate() {
        return registrationEndDate;
    }

    public void setRegistrationEndDate(LocalDate registrationEndDate) {
        this.registrationEndDate = registrationEndDate;
    }

    public Boolean getRegistrationRequired() {
        return registrationRequired;
    }

    public void setRegistrationRequired(Boolean registrationRequired) {
        this.registrationRequired = registrationRequired;
    }

    public String getRegistrationLink() {
        return registrationLink;
    }

    public void setRegistrationLink(String registrationLink) {
        this.registrationLink = registrationLink;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getEligibility() {
        return eligibility;
    }

    public void setEligibility(String eligibility) {
        this.eligibility = eligibility;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getCreatedByRole() {
        return createdByRole;
    }

    public void setCreatedByRole(String createdByRole) {
        this.createdByRole = createdByRole;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
}
