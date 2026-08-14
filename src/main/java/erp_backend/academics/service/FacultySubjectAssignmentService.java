package erp_backend.academics.service;

import erp_backend.Teacher.entity.Teacher;
import erp_backend.Teacher.repository.TeacherRepository;
import erp_backend.entity.Subject;
import erp_backend.repository.SubjectRepository;
import erp_backend.academics.entity.FacultySubjectAssignment;
import erp_backend.academics.repository.FacultySubjectAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class FacultySubjectAssignmentService {

    @Autowired
    private FacultySubjectAssignmentRepository repository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    public List<FacultySubjectAssignment> getAll() {
        return repository.findAll();
    }

    public Optional<FacultySubjectAssignment> getById(Long id) {
        return repository.findById(id);
    }

    public List<FacultySubjectAssignment> getAssignmentsByTeacher(String employeeId) {
        return repository.findByTeacherEmployeeId(employeeId);
    }

    public List<FacultySubjectAssignment> getAssignmentsBySection(String section) {
        return repository.findBySection(section);
    }

    public List<FacultySubjectAssignment> getAssignmentsForClass(String department, String year, String semester,
            String section) {
        return repository.findByDepartmentAndYearAndSemesterAndSection(department, year, semester, section);
    }

    @Transactional
    public FacultySubjectAssignment assign(Long subjectId, String employeeId, String department,
            String year, String semester, String section, String academicYear) {

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found with ID: " + subjectId));

        Teacher teacher = teacherRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with Employee ID: " + employeeId));

        // Prevent duplicates
        Optional<FacultySubjectAssignment> existing = repository
                .findBySubjectIdAndTeacherEmployeeIdAndSectionAndAcademicYear(subjectId, employeeId, section,
                        academicYear);
        if (existing.isPresent()) {
            throw new IllegalStateException("Assignment already exists for Subject: " + subject.getName()
                    + ", Teacher: " + teacher.getName() + " in Section: " + section + " [" + academicYear + "]");
        }

        FacultySubjectAssignment assignment = new FacultySubjectAssignment();
        assignment.setSubject(subject);
        assignment.setTeacher(teacher);
        assignment.setDepartment(department);
        assignment.setYear(year);
        assignment.setSemester(semester);
        assignment.setSection(section);
        assignment.setAcademicYear(academicYear);

        return repository.save(assignment);
    }

    @Transactional
    public FacultySubjectAssignment update(Long id, Long subjectId, String employeeId, String department,
            String year, String semester, String section, String academicYear) {
        FacultySubjectAssignment assignment = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found with ID: " + id));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found with ID: " + subjectId));

        Teacher teacher = teacherRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with Employee ID: " + employeeId));

        assignment.setSubject(subject);
        assignment.setTeacher(teacher);
        assignment.setDepartment(department);
        assignment.setYear(year);
        assignment.setSemester(semester);
        assignment.setSection(section);
        assignment.setAcademicYear(academicYear);

        return repository.save(assignment);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
