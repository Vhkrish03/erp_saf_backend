package erp_backend.management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "management_fee_decision_components")
public class FeeDecisionComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_decision_id", nullable = false)
    @JsonBackReference
    private FeeDecision feeDecision;

    private String name; // e.g. Tuition Fee, Exam Fee, Transport
    private double amount;

    @Column(name = "applicable_condition")
    private String applicableCondition; // ALL, HOSTELLER, DAY_SCHOLAR, TRANSPORT_REQUIRED

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FeeDecision getFeeDecision() {
        return feeDecision;
    }

    public void setFeeDecision(FeeDecision feeDecision) {
        this.feeDecision = feeDecision;
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
