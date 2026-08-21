package erp_backend.idcard.dto;

public class IdCardResponse {
    private String name;
    private String idOrRollNumber; // Register number or employee ID
    private String department;
    private String section;
    private String year;
    private String semester;
    private String designation; // For staff
    private String validity;
    private String dayscholarStatus; // e.g. "DAYSCHOLAR"
    private String barcodeData;
    private String bloodGroup;
    private String dob;
    private String emergencyContact;
    private String address;
    private String photoUrl;
    private String status;

    public IdCardResponse() {
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdOrRollNumber() {
        return idOrRollNumber;
    }

    public void setIdOrRollNumber(String idOrRollNumber) {
        this.idOrRollNumber = idOrRollNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getDayscholarStatus() {
        return dayscholarStatus;
    }

    public void setDayscholarStatus(String dayscholarStatus) {
        this.dayscholarStatus = dayscholarStatus;
    }

    public String getBarcodeData() {
        return barcodeData;
    }

    public void setBarcodeData(String barcodeData) {
        this.barcodeData = barcodeData;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
