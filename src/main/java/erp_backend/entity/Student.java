package erp_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student {

    @Id
    private String id;

    private String name;

    @Column(name = "roll_number")
    private String rollNumber;

    private String department;
    private String section;
    private String year;
    private String semester;
    private String email;
    private String phone;

    @Column(name = "blood_group")
    private String bloodGroup;

    private String dob;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;
    
    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;

    private String address;
    private String advisor;
    private double cgpa;

    public Student() {
    }

    // Getters

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public String getDepartment() {
        return department;
    }

    public String getSection() {
        return section;
    }

    public String getYear() {
        return year;
    }

    public String getSemester() {
        return semester;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public String getDob() {
        return dob;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public String getAddress() {
        return address;
    }

    public String getAdvisor() {
        return advisor;
    }

    public double getCgpa() {
        return cgpa;
    }

    // Setters

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAdvisor(String advisor) {
        this.advisor = advisor;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }
}



