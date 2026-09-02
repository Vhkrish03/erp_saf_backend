package erp_backend.academics.service;

import erp_backend.Teacher.entity.Teacher;
import erp_backend.Teacher.repository.TeacherRepository;
import erp_backend.academics.entity.AssignmentAuditLog;
import erp_backend.academics.repository.AssignmentAuditLogRepository;
import erp_backend.academics.entity.ClassInchargeAssignment;
import erp_backend.academics.repository.ClassInchargeAssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ClassInchargeAssignmentService {

    @Autowired
    private ClassInchargeAssignmentRepository repository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private AssignmentAuditLogRepository auditLogRepository;

    public Optional<ClassInchargeAssignment> getActiveAssignmentByTeacher(String employeeId) {
        return repository.findByTeacher_EmployeeIdAndActiveTrue(employeeId);
    }

    public Optional<ClassInchargeAssignment> getActiveAssignmentByClass(String department, String year, String section,
            String academicYear) {
        return repository.findByDepartmentAndYearAndSectionAndAcademicYearAndActiveTrue(department, year, section,
                academicYear);
    }

    @Transactional
    public ClassInchargeAssignment assignOrReplaceClassIncharge(String employeeId, String department, String year,
            String section, String academicYear) {
        // Find existing for this class
        Optional<ClassInchargeAssignment> existingForClass = repository
                .findByDepartmentAndYearAndSectionAndAcademicYearAndActiveTrue(
                        department, year, section, academicYear);

        if (existingForClass.isPresent()) {
            if (existingForClass.get().getTeacher().getEmployeeId().equals(employeeId)) {
                // Already assigned to this teacher
                return existingForClass.get();
            } else {
                // Remove existing
                String removedId = existingForClass.get().getTeacher().getEmployeeId();
                repository.delete(existingForClass.get());
                repository.flush();

                auditLogRepository.save(new AssignmentAuditLog("REMOVED_CLASS_INCHARGE", removedId,
                        "Replaced. Class: " + department + " Year " + year + " Sec " + section + " Session "
                                + academicYear,
                        "ADMIN"));
            }
        }

        // Also ensure this teacher is not already incharge of another class. If so,
        // remove them from that one.
        Optional<ClassInchargeAssignment> existingForTeacher = repository
                .findByTeacher_EmployeeIdAndActiveTrue(employeeId);
        if (existingForTeacher.isPresent()) {
            ClassInchargeAssignment old = existingForTeacher.get();
            repository.delete(old);
            repository.flush();

            auditLogRepository.save(new AssignmentAuditLog("REMOVED_CLASS_INCHARGE", employeeId,
                    "Replaced via new assignment. Class: " + old.getDepartment() + " Year " + old.getYear() + " Sec "
                            + old.getSection() + " Session " + old.getAcademicYear(),
                    "ADMIN"));
        }

        Teacher teacher = teacherRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        ClassInchargeAssignment assignment = new ClassInchargeAssignment(teacher, department, year, section,
                academicYear);
        assignment = repository.save(assignment);

        auditLogRepository.save(new AssignmentAuditLog("ASSIGN_CLASS_INCHARGE", employeeId,
                "Assigned Class: " + department + " Year " + year + " Sec " + section + " Session " + academicYear,
                "ADMIN"));

        return assignment;
    }

    @Transactional
    public void unassignClassIncharge(String employeeId) {
        Optional<ClassInchargeAssignment> existing = repository.findByTeacher_EmployeeIdAndActiveTrue(employeeId);
        existing.ifPresent(assignment -> {
            repository.delete(assignment);
            auditLogRepository.save(new AssignmentAuditLog("REMOVE_CLASS_INCHARGE", employeeId,
                    "Removed Class: " + assignment.getDepartment() + " Year " + assignment.getYear() + " Sec "
                            + assignment.getSection() + " Session " + assignment.getAcademicYear(),
                    "ADMIN"));
        });
    }
}
