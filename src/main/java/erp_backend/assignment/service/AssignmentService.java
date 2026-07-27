package erp_backend.assignment.service;



import java.util.List;
import org.springframework.stereotype.Service;
import erp_backend.assignment.entity.AssignmentEntity;
import erp_backend.assignment.repository.AssignmentRepository;

@Service
public class AssignmentService {

    private final AssignmentRepository repository;

    public AssignmentService(AssignmentRepository repository) {
        this.repository = repository;
    }

    public List<AssignmentEntity> getAllAssignments() {
        return repository.findAll();
    }

    public AssignmentEntity createAssignment(AssignmentEntity assignment) {
        return repository.save(assignment);
    }

    public AssignmentEntity getAssignment(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteAssignment(Long id) {
        repository.deleteById(id);
    }
}
