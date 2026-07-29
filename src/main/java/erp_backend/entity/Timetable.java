package erp_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "timetable")
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String day;
    private String time;
    private String subject;
    private String room;
    private String faculty;
    private String employeeId; 
    private String year;
    private String section;
    private String department;

    public Timetable() {
    }

    public Timetable(Long id,String day,String time,String subject,String room,String employeeId,String faculty,String year,String section,String department) {

    this.id = id;
    this.day = day;
    this.time = time;
    this.subject = subject;
    this.room = room;
    this.employeeId = employeeId;
    this.faculty = faculty;
    this.year = year;
    this.section = section;
    this.department = department;
    }


    // Getter and Setter for id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter and Setter for day
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    // Getter and Setter for time
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    // Getter and Setter for subject
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    // Getter and Setter for room
    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    // Getter and Setter for faculty
    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    // Getter and Setter for year
public String getYear() {
    return year;
}

public void setYear(String year) {
    this.year = year;
}

// Getter and Setter for section
public String getSection() {
    return section;
}

public void setSection(String section) {
    this.section = section;
}

// Getter and Setter for department
public String getDepartment() {
    return department;
}

public void setDepartment(String department) {
    this.department = department;
}

}