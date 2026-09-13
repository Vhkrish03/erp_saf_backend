package erp_backend.management.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import java.math.BigDecimal;

import erp_backend.management.dto.*;
import erp_backend.repository.StudentRepository;
import erp_backend.Teacher.repository.TeacherRepository;
import erp_backend.academics.repository.AcademicYearRepository;
import erp_backend.repository.UserRepository;
import erp_backend.repository.AssessmentRepository;
import erp_backend.repository.SemesterResultRepository;
import erp_backend.attendance.repository.AttendanceRecordRepository;
import erp_backend.fees.repository.StudentFeeRepository;
import erp_backend.fees.repository.FeePaymentRepository;

import erp_backend.entity.Student;
import erp_backend.Teacher.entity.Teacher;
import erp_backend.academics.entity.AcademicYear;
import erp_backend.entity.User;
import erp_backend.entity.Assessment;
import erp_backend.entity.SemesterResult;
import erp_backend.attendance.entity.AttendanceRecord;
import erp_backend.fees.entity.StudentFee;
import erp_backend.transport.repository.BusRepository;
import erp_backend.notice.repository.NoticeRepository;
import erp_backend.Approvals.repository.FacultyApprovalRequestRepository;
import erp_backend.Approvals.model.FacultyApprovalRequest;
import erp_backend.notice.entity.Notice;
import erp_backend.hostel.repository.HostelRepository;
import erp_backend.hostel.entity.Hostel;

@Service
public class ManagementService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AcademicYearRepository academicYearRepository;
    private final UserRepository userRepository;
    private final AssessmentRepository assessmentRepository;
    private final SemesterResultRepository semesterResultRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final StudentFeeRepository studentFeeRepository;
    private final BusRepository busRepository;
    private final NoticeRepository noticeRepository;
    private final FacultyApprovalRequestRepository facultyApprovalRequestRepository;
    private final HostelRepository hostelRepository;

    public ManagementService(StudentRepository studentRepository,
            TeacherRepository teacherRepository,
            AcademicYearRepository academicYearRepository,
            UserRepository userRepository,
            AssessmentRepository assessmentRepository,
            SemesterResultRepository semesterResultRepository,
            AttendanceRecordRepository attendanceRecordRepository,
            StudentFeeRepository studentFeeRepository,
            BusRepository busRepository,
            NoticeRepository noticeRepository,
            FacultyApprovalRequestRepository facultyApprovalRequestRepository,
            HostelRepository hostelRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.academicYearRepository = academicYearRepository;
        this.userRepository = userRepository;
        this.assessmentRepository = assessmentRepository;
        this.semesterResultRepository = semesterResultRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.studentFeeRepository = studentFeeRepository;
        this.busRepository = busRepository;
        this.noticeRepository = noticeRepository;
        this.facultyApprovalRequestRepository = facultyApprovalRequestRepository;
        this.hostelRepository = hostelRepository;
    }

    public InstitutionOverviewDTO getInstitutionOverview() {
        InstitutionOverviewDTO dto = new InstitutionOverviewDTO();
        dto.setTotalStudents(studentRepository.count());
        dto.setTotalTeachers(teacherRepository.count());
        List<User> users = userRepository.findAll();
        long nonTeachingStaff = users.stream()
                .filter(u -> List.of("ACCOUNTANT", "LIBRARIAN", "MESS_ADMIN", "ADMIN").contains(u.getRole()))
                .count();
        dto.setTotalNonTeachingStaff(nonTeachingStaff);
        dto.setTotalDepartments(9);
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
        dto.setActiveStudents(students.size());
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

    public AcademicOverviewDTO getAcademicOverview(String department) {
        List<Assessment> assessments = assessmentRepository.findAll();
        if (department != null && !department.isEmpty()) {
            assessments = assessments.stream().filter(a -> department.equals(a.getDepartment()))
                    .collect(Collectors.toList());
        }
        AcademicOverviewDTO dto = new AcademicOverviewDTO();
        dto.setTotalAssessments(assessments.size());

        long completed = assessments.stream()
                .filter(a -> "COMPLETED".equals(a.getStatus()) || "PUBLISHED".equals(a.getStatus())).count();
        dto.setCompletedAssessments(completed);
        dto.setPendingAssessments(assessments.size() - completed);
        dto.setOverallPassPercentage(0.0); // Would require deeper mark analysis
        return dto;
    }

    public AttendanceOverviewDTO getAttendanceOverview(String department) {
        List<AttendanceRecord> records = attendanceRecordRepository.findAll();
        if (department != null && !department.isEmpty()) {
            records = records.stream()
                    .filter(r -> r.getStudent() != null && department.equals(r.getStudent().getDepartment()))
                    .collect(Collectors.toList());
        }
        AttendanceOverviewDTO dto = new AttendanceOverviewDTO();
        dto.setTotalRecords(records.size());
        long present = records.stream().filter(r -> "PRESENT".equalsIgnoreCase(r.getStatus())).count();
        dto.setPresentRecords(present);

        if (records.size() > 0) {
            dto.setOverallPercentage((double) present / records.size() * 100);
        } else {
            dto.setOverallPercentage(0.0);
        }
        return dto;
    }

    public ExaminationOverviewDTO getExaminationOverview(String department) {
        List<SemesterResult> results = semesterResultRepository.findAll();
        if (department != null && !department.isEmpty()) {
            results = results.stream()
                    .filter(r -> r.getStudent() != null && department.equals(r.getStudent().getDepartment()))
                    .collect(Collectors.toList());
        }
        ExaminationOverviewDTO dto = new ExaminationOverviewDTO();
        long published = results.stream().filter(r -> "PUBLISHED".equals(r.getStatus())).count();
        dto.setTotalPublishedResults(published);
        dto.setTotalStudentsAppeared(results.size());

        long passed = results.stream().filter(r -> r.getSgpa() >= 5.0).count();
        long failed = results.size() - passed;
        dto.setTotalPassed(passed);
        dto.setTotalFailed(failed);

        if (results.size() > 0) {
            dto.setOverallPassPercentage((double) passed / results.size() * 100);
        } else {
            dto.setOverallPassPercentage(0.0);
        }
        return dto;
    }

    public FinancialOverviewDTO getFinancialOverview(String department) {
        List<StudentFee> fees = studentFeeRepository.findAll();
        if (department != null && !department.isEmpty()) {
            fees = fees.stream()
                    .filter(f -> f.getStudent() != null && department.equals(f.getStudent().getDepartment()))
                    .collect(Collectors.toList());
        }

        FinancialOverviewDTO dto = new FinancialOverviewDTO();
        BigDecimal demand = BigDecimal.ZERO;
        BigDecimal collected = BigDecimal.ZERO;
        BigDecimal pending = BigDecimal.ZERO;

        for (StudentFee fee : fees) {
            demand = demand.add(BigDecimal.valueOf(fee.getTotalFee()));
            collected = collected.add(BigDecimal.valueOf(fee.getAmountPaid()));
            pending = pending.add(BigDecimal.valueOf(fee.getBalanceAmount()));
        }

        dto.setTotalDemand(demand);
        dto.setTotalCollected(collected);
        dto.setTotalPending(pending);
        if (demand.compareTo(BigDecimal.ZERO) > 0) {
            dto.setCollectionPercentage(collected.doubleValue() / demand.doubleValue() * 100);
        }
        return dto;
    }

    public AdministrationOverviewDTO getAdministrationOverview(String department) {
        AdministrationOverviewDTO dto = new AdministrationOverviewDTO();
        List<Notice> notices = noticeRepository.findAll();
        List<FacultyApprovalRequest> approvals = facultyApprovalRequestRepository.findAll();

        if (department != null && !department.isEmpty()) {
            // Notice entity handles department filtering differently or might not have
            // simple getDepartment()
            // Kept simple for high-level management view
            approvals = approvals.stream().filter(a -> department.equals(a.getDepartment()))
                    .collect(Collectors.toList());
        }

        dto.setActiveNotices(notices.size());

        long pending = approvals.stream().filter(a -> "PENDING_HOD".equals(a.getStatus())
                || "PENDING_ADMIN".equals(a.getStatus()) || "PENDING".equals(a.getStatus())).count();
        long completed = approvals.stream()
                .filter(a -> "APPROVED".equals(a.getStatus()) || "REJECTED".equals(a.getStatus())).count();

        dto.setPendingApprovals(pending);
        dto.setCompletedApprovals(completed);

        return dto;
    }

    public TransportOverviewDTO getTransportOverview() {
        TransportOverviewDTO dto = new TransportOverviewDTO();
        dto.setTotalBuses(busRepository.count());
        dto.setActiveRoutes(12); // Hardcoded until RouteRepository is fully resolved
        return dto;
    }

    public HostelOverviewDTO getHostelOverview() {
        HostelOverviewDTO dto = new HostelOverviewDTO();
        List<Hostel> hostels = hostelRepository.findAll();
        dto.setTotalHostels(hostels.size());
        dto.setTotalCapacity(hostels.stream().mapToLong(Hostel::getTotalBeds).sum());
        dto.setOccupiedBeds(hostels.stream().mapToLong(Hostel::getOccupiedBeds).sum());
        return dto;
    }
}
