package erp_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import erp_backend.entity.Fee;
import erp_backend.repository.FeeRepository;

@Service
public class FeeService {

    private final FeeRepository repository;

    public FeeService(FeeRepository repository) {
        this.repository = repository;
    }

    public List<Fee> getFees(String studentId) {
        return repository.findByStudentId(studentId);
    }
}
