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

    public Timetable() {
    }

    public Timetable(Long id, String day, String time, String subject, String room, String faculty) {
        this.id = id;
        this.day = day;
        this.time = time;
        this.subject = subject;
        this.room = room;
        this.faculty = faculty;
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
}