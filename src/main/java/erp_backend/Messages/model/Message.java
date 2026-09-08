package erp_backend.Messages.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "confidential_messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @Column(name = "sender_name", nullable = false)
    private String senderName;

    @Column(name = "sender_role", nullable = false)
    private String senderRole; // STUDENT, TEACHER, HOD

    @Column(name = "department", nullable = false)
    private String department;

    @Column(name = "receiver_id", nullable = false)
    private String receiverId;

    @Column(name = "receiver_name", nullable = false)
    private String receiverName;

    @Column(name = "receiver_role", nullable = false)
    private String receiverRole; // STUDENT, TEACHER, HOD

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "is_read_by_receiver")
    private boolean isReadByReceiver = false;

    @Column(name = "is_read_by_admin")
    private boolean isReadByAdmin = false;

    @Column(name = "timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverRole() {
        return receiverRole;
    }

    public void setReceiverRole(String receiverRole) {
        this.receiverRole = receiverRole;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isReadByReceiver() {
        return isReadByReceiver;
    }

    public void setReadByReceiver(boolean readByReceiver) {
        isReadByReceiver = readByReceiver;
    }

    public boolean isReadByAdmin() {
        return isReadByAdmin;
    }

    public void setReadByAdmin(boolean readByAdmin) {
        isReadByAdmin = readByAdmin;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
