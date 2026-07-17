package erp_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String name;
    private String faculty;
    private int credits;
    private double attendancePercent;
    private int classesHeld;
    private int classesAttended;

    // Default Constructor
    public Subject() {
    }

    // Parameterized Constructor
    public Subject(Long id, String code, String name, String faculty,
                   int credits, double attendancePercent,
                   int classesHeld, int classesAttended) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.faculty = faculty;
        this.credits = credits;
        this.attendancePercent = attendancePercent;
        this.classesHeld = classesHeld;
        this.classesAttended = classesAttended;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public double getAttendancePercent() {
        return attendancePercent;
    }

    public void setAttendancePercent(double attendancePercent) {
        this.attendancePercent = attendancePercent;
    }

    public int getClassesHeld() {
        return classesHeld;
    }

    public void setClassesHeld(int classesHeld) {
        this.classesHeld = classesHeld;
    }

    public int getClassesAttended() {
        return classesAttended;
    }

    public void setClassesAttended(int classesAttended) {
        this.classesAttended = classesAttended;
    }
}