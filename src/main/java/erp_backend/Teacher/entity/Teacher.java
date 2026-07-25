package erp_backend.Teacher.entity;



import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "teachers")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id")
    private String employeeId;

    private String name;
    private String gender;
    private LocalDate dob;
    private String department;
    private String designation;
    private String qualification;

    @Column(name = "experience_years")
    private Integer experienceYears;

    private String phone;
    private String email;
    private String address;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    private String status;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "emergency_contact_name")
    private String emergencyContactName;

    @Column(name = "emergency_contact_number")
    private String emergencyContactNumber;

    // Generate Getters and Setters
}
