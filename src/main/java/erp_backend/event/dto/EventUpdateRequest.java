package erp_backend.event.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventUpdateRequest {
    private String title;
    private String description;
    private String eventType;
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

    public EventUpdateRequest() {
    }

    // Getters and Setters

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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
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
}
