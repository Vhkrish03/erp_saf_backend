package erp_backend.curriculum.service;

import erp_backend.curriculum.entity.Curriculum;
import erp_backend.curriculum.entity.CurriculumSubject;
import erp_backend.curriculum.repository.CurriculumRepository;
import erp_backend.curriculum.repository.CurriculumSubjectRepository;
import erp_backend.entity.Subject;
import erp_backend.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final SubjectRepository subjectRepository;

    public CurriculumService(CurriculumRepository curriculumRepository,
            SubjectRepository subjectRepository) {
        this.curriculumRepository = curriculumRepository;
        this.subjectRepository = subjectRepository;
    }

    public List<Curriculum> getAllCurriculums() {
        return curriculumRepository.findAll();
    }

    public Optional<Curriculum> getCurriculumById(Long id) {
        return curriculumRepository.findById(id);
    }

    @Transactional
    public Curriculum createCurriculum(Curriculum curriculum) {
        // Validation for duplicates
        Optional<Curriculum> existing = curriculumRepository
                .findByDepartmentAndRegulationAndAcademicYearAndYearAndSemester(
                        curriculum.getDepartment(), curriculum.getRegulation(), curriculum.getAcademicYear(),
                        curriculum.getYear(), curriculum.getSemester());
        if (existing.isPresent()) {
            throw new RuntimeException("Curriculum already exists for this combination.");
        }
        curriculum.setStatus("DRAFT");
        return curriculumRepository.save(curriculum);
    }

    @Transactional
    public Curriculum updateCurriculum(Long id, Curriculum details) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum not found"));

        if (!"DRAFT".equals(curriculum.getStatus())) {
            throw new RuntimeException("Only DRAFT curriculum can be updated");
        }

        curriculum.setDepartment(details.getDepartment());
        curriculum.setRegulation(details.getRegulation());
        curriculum.setAcademicYear(details.getAcademicYear());
        curriculum.setYear(details.getYear());
        curriculum.setSemester(details.getSemester());

        return curriculumRepository.save(curriculum);
    }

    @Transactional
    public Curriculum updateStatus(Long id, String status) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum not found"));

        // Basic workflow validation
        switch (status) {
            case "UNDER_REVIEW":
                if (!"DRAFT".equals(curriculum.getStatus()))
                    throw new RuntimeException("Invalid status transition.");
                break;
            case "APPROVED":
            case "REJECTED":
                if (!"UNDER_REVIEW".equals(curriculum.getStatus()))
                    throw new RuntimeException("Invalid status transition.");
                break;
            case "PUBLISHED":
                if (!"APPROVED".equals(curriculum.getStatus()))
                    throw new RuntimeException("Invalid status transition.");
                break;
        }

        curriculum.setStatus(status);
        return curriculumRepository.save(curriculum);
    }

    @Transactional
    public Curriculum addSubject(Long curriculumId, Long subjectId, Integer sortOrder) {
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new RuntimeException("Curriculum not found"));

        if (!"DRAFT".equals(curriculum.getStatus())) {
            throw new RuntimeException("Cannot add subjects to non-DRAFT curriculum");
        }

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        // Check if already added
        boolean exists = curriculum.getSubjects().stream()
                .anyMatch(cs -> cs.getSubject().getId().equals(subjectId));

        if (exists) {
            throw new RuntimeException("Subject is already part of this curriculum");
        }

        CurriculumSubject cs = new CurriculumSubject();
        cs.setSubject(subject);
        cs.setSortOrder(sortOrder == null ? curriculum.getSubjects().size() + 1 : sortOrder);

        curriculum.addSubject(cs);
        return curriculumRepository.save(curriculum);
    }

    @Transactional
    public Curriculum removeSubject(Long curriculumId, Long subjectId) {
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new RuntimeException("Curriculum not found"));

        if (!"DRAFT".equals(curriculum.getStatus())) {
            throw new RuntimeException("Cannot remove subjects from non-DRAFT curriculum");
        }

        CurriculumSubject toRemove = curriculum.getSubjects().stream()
                .filter(cs -> cs.getSubject().getId().equals(subjectId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Subject not found in curriculum"));

        curriculum.removeSubject(toRemove);
        return curriculumRepository.save(curriculum);
    }

    @Transactional
    public void deleteCurriculum(Long id) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum not found"));

        if ("PUBLISHED".equals(curriculum.getStatus())) {
            throw new RuntimeException("Cannot delete a PUBLISHED curriculum");
        }

        curriculumRepository.delete(curriculum);
    }
}
