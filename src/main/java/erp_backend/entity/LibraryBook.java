package erp_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "library_books")
public class LibraryBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private String title;

    private String author;

    private String issueDate;

    private String dueDate;

    private boolean isOverdue;

    public LibraryBook() {
    }

    // Getters

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public boolean isOverdue() {
        return isOverdue;
    }

    // Setters

    public void setId(Long id) {
        this.id = id;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setIsOverdue(boolean isOverdue) {
        this.isOverdue = isOverdue;
    }
}