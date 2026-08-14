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
        };
    }
}