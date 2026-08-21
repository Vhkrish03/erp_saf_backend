package erp_backend.idcard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "id_cards")
public class IdCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "person_id", nullable = false)
    private String personId; // student id or employee id

    @Column(name = "person_type", nullable = false)
    private String personType; // "STUDENT" or "TEACHER"

    @Column(name = "validity")
    private String validity; // e.g. "2023-27" or "LIFETIME"

    @Column(name = "dayscholar_status")
    private String dayscholarStatus; // e.g. "DAYSCHOLAR" or "HOSTELLER"

    @Column(name = "barcode_data")
    private String barcodeData;

    @Column(name = "status")
    private String status; // "ACTIVE" or "EXPIRED"

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public IdCard() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
