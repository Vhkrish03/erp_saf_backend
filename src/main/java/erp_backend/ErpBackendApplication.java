package erp_backend;

import erp_backend.repository.SubjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ErpBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErpBackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase(SubjectRepository subjectRepository) {
        return args -> {
            // Force Subject ID 3 to be held by EMP006
            subjectRepository.findById(3L).ifPresent(subject -> {
                subject.setEmployeeId("EMP006");
                subjectRepository.save(subject);
                System.out.println("====== STARTUP DB SYNC: Updated Subject ID 3 with employeeId = EMP006 ======");
            });

            // Also map CS8701 (Computer Networks) to EMP006
            subjectRepository.findByCode("CS8701").ifPresent(subject -> {
                subject.setEmployeeId("EMP006");
                subjectRepository.save(subject);
                System.out.println("====== STARTUP DB SYNC: Updated CS8701 Subject with employeeId = EMP006 ======");
            });

            // Populate year field for existing subjects based on semester
            subjectRepository.findAll().forEach(subject -> {
                if (subject.getYear() == null || subject.getYear().trim().isEmpty()) {
                    int sem = subject.getSemester();
                    String yearVal = "";
                    if (sem == 1 || sem == 2)
                        yearVal = "1st year";
                    else if (sem == 3 || sem == 4)
                        yearVal = "2nd year";
                    else if (sem == 5 || sem == 6)
                        yearVal = "3rd year";
                    else if (sem == 7 || sem == 8)
                        yearVal = "4th year";

                    if (!yearVal.isEmpty()) {
                        subject.setYear(yearVal);
                        subjectRepository.save(subject);
                        System.out.println("====== STARTUP DB SYNC: Migrated Subject " + subject.getCode() + " ["
                                + subject.getName() + "] to Year: " + yearVal + " ======");
                    }
                }
            });
        };
    }
}