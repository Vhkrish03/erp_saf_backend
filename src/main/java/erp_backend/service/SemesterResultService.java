package erp_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import erp_backend.entity.SemesterResult;
import erp_backend.repository.SemesterResultRepository;

@Service
public class SemesterResultService {

    private final SemesterResultRepository repository;

    public SemesterResultService(SemesterResultRepository repository) {
        this.repository = repository;
    }

    public List<SemesterResult> getResults(String studentId) {
        return repository.findByStudentId(studentId);
    }
}