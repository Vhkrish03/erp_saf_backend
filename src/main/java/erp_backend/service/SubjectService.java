package erp_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import erp_backend.entity.Subject;
import erp_backend.repository.SubjectRepository;
import erp_backend.curriculum.repository.CurriculumRepository;
import erp_backend.curriculum.entity.Curriculum;
import erp_backend.curriculum.entity.CurriculumSubject;
import java.util.Optional;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository repository;

    @Autowired
    private CurriculumRepository curriculumRepository;

    public List<Subject> getAllSubjects() {
        return repository.findAll();
    }

    public List<Subject> getSubjectsByDepartment(String dept) {
        return repository.findByDepartment(dept);
    }

    public List<Subject> getSubjectsByDeptAndSemester(String dept, int semester) {
        return repository.findByDepartmentAndSemester(dept, semester);
    }

    public List<Subject> getSubjectsByFilter(String dept, String year, int semester) {
        return repository.findByDepartmentAndYearAndSemester(dept, year, semester);
    }

    public List<Subject> getSubjectsByFilterExtended(String dept, String year, int semester, String academicYear) {
        if (academicYear != null && !academicYear.trim().isEmpty()) {
            Optional<Curriculum> curriculumOpt = curriculumRepository
                    .findFirstByDepartmentAndYearAndSemesterAndAcademicYearAndStatus(
                            dept, year, semester, academicYear, "PUBLISHED");

            if (curriculumOpt.isPresent()) {
                return curriculumOpt.get().getSubjects().stream()
                        .map(CurriculumSubject::getSubject)
                        .toList();
            }
        }
        return getSubjectsByFilter(dept, year, semester);
    }

    public Subject createSubject(Subject subject) {
        if (subject.getYear() == null || subject.getYear().trim().isEmpty()) {
            subject.setYear(calculateYearFromSemester(subject.getSemester()));
        }
        return repository.save(subject);
    }

    public Subject updateSubject(Long id, Subject details) {
        return repository.findById(id).map(existing -> {
            existing.setCode(details.getCode());
            existing.setName(details.getName());
            existing.setFaculty(details.getFaculty());
            existing.setCredits(details.getCredits());
            existing.setAttendancePercent(details.getAttendancePercent());
            existing.setClassesHeld(details.getClassesHeld());
            existing.setClassesAttended(details.getClassesAttended());
            existing.setEmployeeId(details.getEmployeeId());
            existing.setDepartment(details.getDepartment());
            existing.setSemester(details.getSemester());
            if (details.getYear() != null && !details.getYear().trim().isEmpty()) {
                existing.setYear(details.getYear());
            } else {
                existing.setYear(calculateYearFromSemester(details.getSemester()));
            }
            existing.setSubjectType(details.getSubjectType());
            existing.setAcademicYear(details.getAcademicYear());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));
    }

    public void deleteSubject(Long id) {
        repository.deleteById(id);
    }

    private String calculateYearFromSemester(int semester) {
        if (semester == 1 || semester == 2)
            return "1st year";
        if (semester == 3 || semester == 4)
            return "2nd year";
        if (semester == 5 || semester == 6)
            return "3rd year";
        if (semester == 7 || semester == 8)
            return "4th year";
        return "";
    }
}