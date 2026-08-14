package erp_backend.academics.service;

import erp_backend.academics.entity.AcademicYear;
import erp_backend.academics.repository.AcademicYearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class AcademicYearService {

    @Autowired
    private AcademicYearRepository repository;

    public List<AcademicYear> getAll() {
        return repository.findAll();
    }

    public Optional<AcademicYear> getById(Long id) {
        return repository.findById(id);
    }

    public Optional<AcademicYear> getActiveYear() {
        return repository.findByIsActiveTrue();
    }

    @Transactional
    public AcademicYear create(AcademicYear academicYear) {
        if (academicYear.isActive()) {
            deactivateAll();
        }
        return repository.save(academicYear);
    }

    @Transactional
    public AcademicYear update(Long id, AcademicYear details) {
        return repository.findById(id).map(existing -> {
            existing.setYearName(details.getYearName());
            existing.setStartDate(details.getStartDate());
            existing.setEndDate(details.getEndDate());
            if (details.isActive() && !existing.isActive()) {
                deactivateAll();
            }
            existing.setActive(details.isActive());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Academic year not found with id: " + id));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private void deactivateAll() {
        List<AcademicYear> all = repository.findAll();
        for (AcademicYear ay : all) {
            ay.setActive(false);
        }
        repository.saveAll(all);
    }
}
