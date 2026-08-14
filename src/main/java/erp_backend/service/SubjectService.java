package erp_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import erp_backend.entity.Subject;
import erp_backend.repository.SubjectRepository;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository repository;

    public List<Subject> getAllSubjects() {
        return repository.findAll();
    }

    public List<Subject> getSubjectsByDepartment(String dept) {
        return repository.findByDepartment(dept);
    }

    public List<Subject> getSubjectsByDeptAndSemester(String dept, int semester) {
        return repository.findByDepartmentAndSemester(dept, semester);
    }

    public Subject createSubject(Subject subject) {
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
            existing.setSubjectType(details.getSubjectType());
            existing.setAcademicYear(details.getAcademicYear());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));
    }

    public void deleteSubject(Long id) {
        repository.deleteById(id);
    }
}