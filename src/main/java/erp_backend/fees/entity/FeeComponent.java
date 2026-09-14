package erp_backend.fees.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "fee_components")
public class FeeComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_structure_id", nullable = false)
    @JsonIgnore
    private FeeStructure feeStructure;

    @Column(nullable = false)
    private String name; // e.g., Tuition, Hostel, Mess, Bus, Exam, Library

    @Column(nullable = false)
    private double amount;

    // e.g., "ALL", "HOSTELLER", "DAY_SCHOLAR", "TRANSPORT_REQUIRED"
    @Column(name = "applicable_condition")
    private String applicableCondition = "ALL";

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FeeStructure getFeeStructure() {
        return feeStructure;
    }

    public void setFeeStructure(FeeStructure feeStructure) {
        this.feeStructure = feeStructure;
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

    public String getApplicableCondition() {
        return applicableCondition;
    }

    public void setApplicableCondition(String applicableCondition) {
        this.applicableCondition = applicableCondition;
    }
}
