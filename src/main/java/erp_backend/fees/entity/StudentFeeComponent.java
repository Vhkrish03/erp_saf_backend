package erp_backend.fees.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "student_fee_components")
public class StudentFeeComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_fee_id", nullable = false)
    @JsonIgnore
    private StudentFee studentFee;

    @Column(nullable = false)
    private String name; // Name of the applied fee component

    @Column(nullable = false)
    private double amount;

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StudentFee getStudentFee() {
        return studentFee;
    }

    public void setStudentFee(StudentFee studentFee) {
        this.studentFee = studentFee;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
