package erp_backend.management.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

import erp_backend.management.dto.InstitutionOverviewDTO;
import erp_backend.management.dto.StudentOverviewDTO;
import erp_backend.management.dto.StaffOverviewDTO;
import erp_backend.repository.StudentRepository;
import erp_backend.Teacher.repository.TeacherRepository;
import erp_backend.academics.repository.AcademicYearRepository;
import erp_backend.repository.UserRepository;
import erp_backend.entity.Student;
import erp_backend.Teacher.entity.Teacher;
import erp_backend.academics.entity.AcademicYear;
import erp_backend.entity.User;

@Service
public class ManagementService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AcademicYearRepository academicYearRepository;
    private final UserRepository userRepository;

    public ManagementService(StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            AcademicYearRepository academicYearRepository,
            UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.academicYearRepository = academicYearRepository;
        this.userRepository = userRepository;
    }

    public InstitutionOverviewDTO getInstitutionOverview() {
        InstitutionOverviewDTO dto = new InstitutionOverviewDTO();

        dto.setTotalStudents(studentRepository.count());
        dto.setTotalTeachers(teacherRepository.count());

        // Count users with roles that might be considered non-teaching staff (e.g.
        // ACCOUNTANT, LIBRARIAN, etc.)
        List<User> users = userRepository.findAll();
        long nonTeachingStaff = users.stream()
                .filter(u -> List.of("ACCOUNTANT", "LIBRARIAN", "MESS_ADMIN", "ADMIN").contains(u.getRole()))
                .count();
        dto.setTotalNonTeachingStaff(nonTeachingStaff);

        dto.setTotalDepartments(9); // Fixed official departments: CSE, AIDS, BIOTECH, ECE, EEE, BME, CIVIL, MECH,
                                    // S&H

        Optional<AcademicYear> activeYear = academicYearRepository.findByIsActiveTrue();
        if (activeYear.isPresent()) {
            dto.setCurrentAcademicYear(activeYear.get().getYearName());
        } else {
            dto.setCurrentAcademicYear("N/A");
        }

        dto.setCurrentSemester("Current");

        return dto;
    }

    public StudentOverviewDTO getStudentOverview(String department, String year, String semester) {
        List<Student> students = studentRepository.findAll();

        // Apply filters if provided
        if (department != null && !department.isEmpty()) {
            students = students.stream().filter(s -> department.equals(s.getDepartment())).collect(Collectors.toList());
        }
        if (year != null && !year.isEmpty()) {
            students = students.stream().filter(s -> year.equals(s.getYear())).collect(Collectors.toList());
        }
        if (semester != null && !semester.isEmpty()) {
            students = students.stream().filter(s -> semester.equals(s.getSemester())).collect(Collectors.toList());
        }

        StudentOverviewDTO dto = new StudentOverviewDTO();
        dto.setTotalStudents(students.size());
        dto.setActiveStudents(students.size()); // Assuming all are active for now

        Map<String, Long> deptCount = students.stream()
                .filter(s -> s.getDepartment() != null)
                .collect(Collectors.groupingBy(Student::getDepartment, Collectors.counting()));
        dto.setDepartmentWiseStrength(deptCount);

        Map<String, Long> yearCount = students.stream()
                .filter(s -> s.getYear() != null)
                .collect(Collectors.groupingBy(Student::getYear, Collectors.counting()));
        dto.setYearWiseStrength(yearCount);

        Map<String, Long> semCount = students.stream()
                .filter(s -> s.getSemester() != null)
                .collect(Collectors.groupingBy(Student::getSemester, Collectors.counting()));
        dto.setSemesterWiseStrength(semCount);

        return dto;
    }

    public StaffOverviewDTO getStaffOverview(String department) {
        List<Teacher> teachers = teacherRepository.findAll();

        if (department != null && !department.isEmpty()) {
            teachers = teachers.stream().filter(t -> department.equals(t.getDepartment())).collect(Collectors.toList());
        }

        StaffOverviewDTO dto = new StaffOverviewDTO();
        dto.setTotalTeachingStaff(teachers.size());

        // Count non-teaching staff from users again
        List<User> users = userRepository.findAll();
        long nonTeachingStaff = users.stream()
                .filter(u -> List.of("ACCOUNTANT", "LIBRARIAN", "MESS_ADMIN", "ADMIN").contains(u.getRole()))
                .count();
        dto.setTotalNonTeachingStaff(nonTeachingStaff);

        Map<String, Long> deptCount = teachers.stream()
                .filter(t -> t.getDepartment() != null)
                .collect(Collectors.groupingBy(Teacher::getDepartment, Collectors.counting()));
        dto.setDepartmentWiseTeachingStaff(deptCount);

        return dto;
    }
}
