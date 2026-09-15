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
import erp_backend.management.repository.FeeDecisionRepository;
import erp_backend.management.entity.FeeDecision;
import erp_backend.management.entity.FeeDecisionComponent;

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
    private final FeeDecisionRepository feeDecisionRepository;

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
            HostelRepository hostelRepository,
            FeeDecisionRepository feeDecisionRepository) {
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
        this.feeDecisionRepository = feeDecisionRepository;
    }

    public ManagementDashboardDataDTO getDashboardData(String department, String academicYear, String semester) {
        ManagementDashboardDataDTO dto = new ManagementDashboardDataDTO();

        dto.setInstitutionOverview(getInstitutionOverview());
        dto.setStudentOverview(getStudentOverview(department, null, semester)); // For students, semester filter applies
        dto.setStaffOverview(getStaffOverview(department));
        dto.setAcademicPerformance(getAcademicOverview(department, academicYear, semester));
        dto.setAttendanceOverview(getAttendanceOverview(department, academicYear, semester));
        dto.setExaminationOverview(getExaminationOverview(department, academicYear, semester));
        dto.setFeeOverview(getFinancialOverview(department, academicYear, semester));
        dto.setActionCenter(getAdministrationOverview(department));
        dto.setDepartmentAttendance(getAllDepartmentPerformances(academicYear, semester));

        return dto;
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

    public AcademicOverviewDTO getAcademicOverview(String department, String academicYear, String semester) {
        List<Assessment> assessments = assessmentRepository.findAll();
        assessments = assessments.stream().filter(a -> {
            boolean matches = true;
            if (department != null && !department.isEmpty()) {
                matches = matches && department.equals(a.getDepartment());
            }
            if (academicYear != null && !academicYear.isEmpty()) {
                matches = matches && academicYear.equals(a.getAcademicYear());
            }
            if (semester != null && !semester.isEmpty()) {
                matches = matches && semester.equals(a.getSemester());
            }
            return matches;
        }).collect(Collectors.toList());

        AcademicOverviewDTO dto = new AcademicOverviewDTO();
        dto.setTotalAssessments(assessments.size());

        long completed = assessments.stream()
                .filter(a -> "COMPLETED".equals(a.getStatus()) || "PUBLISHED".equals(a.getStatus())).count();
        dto.setCompletedAssessments(completed);
        dto.setPendingAssessments(assessments.size() - completed);
        dto.setOverallPassPercentage(0.0); // Would require deeper mark analysis
        return dto;
    }

    public AttendanceOverviewDTO getAttendanceOverview(String department, String academicYear, String semester) {
        List<AttendanceRecord> records = attendanceRecordRepository.findAll();
        records = records.stream().filter(r -> {
            boolean matches = true;
            if (department != null && !department.isEmpty()) {
                matches = matches && r.getStudent() != null && department.equals(r.getStudent().getDepartment());
            }
            if (academicYear != null && !academicYear.isEmpty()) {
                matches = matches && r.getAttendanceSession() != null
                        && academicYear.equals(r.getAttendanceSession().getAcademicYear());
            }
            if (semester != null && !semester.isEmpty()) {
                matches = matches && r.getAttendanceSession() != null
                        && (semester.equals(r.getAttendanceSession().getSemester())
                                || semester.equals(r.getStudent().getSemester()));
            }
            return matches;
        }).collect(Collectors.toList());

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

    public ExaminationOverviewDTO getExaminationOverview(String department, String academicYear, String semester) {
        List<SemesterResult> results = semesterResultRepository.findAll();
        results = results.stream().filter(r -> {
            boolean matches = true;
            if (department != null && !department.isEmpty()) {
                matches = matches && r.getStudent() != null && department.equals(r.getStudent().getDepartment());
            }
            if (academicYear != null && !academicYear.isEmpty()) {
                matches = matches && academicYear.equals(r.getAcademicYear());
            }
            if (semester != null && !semester.isEmpty()) {
                matches = matches && r.getSemesterName() != null && r.getSemesterName().equals(semester);
            }
            return matches;
        }).collect(Collectors.toList());

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

    public FinancialOverviewDTO getFinancialOverview(String department, String academicYear, String semester) {
        List<StudentFee> fees = studentFeeRepository.findAll();
        fees = fees.stream().filter(f -> {
            boolean matches = true;
            if (department != null && !department.isEmpty()) {
                matches = matches && f.getStudent() != null && department.equals(f.getStudent().getDepartment());
            }
            if (academicYear != null && !academicYear.isEmpty()) {
                matches = matches && academicYear.equals(f.getAcademicYear());
            }
            if (semester != null && !semester.isEmpty()) {
                matches = matches && semester.equals(f.getSemester());
            }
            return matches;
        }).collect(Collectors.toList());

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

    public List<DepartmentPerformanceDTO> getAllDepartmentPerformances(String academicYear, String semester) {
        List<String> departments = Arrays.asList("CSE", "AIDS", "BIOTECH", "ECE", "EEE", "BME", "CIVIL", "MECH", "S&H");
        List<DepartmentPerformanceDTO> list = new ArrayList<>();

        // PERFORMANCE EFFICIENCY: Retrieve DB dump ONCE to avoid N+1 / 36 repeated
        List<Student> allStudents = studentRepository.findAll();
        List<AttendanceRecord> allRecords = attendanceRecordRepository.findAll();
        List<SemesterResult> allResults = semesterResultRepository.findAll();
        List<StudentFee> allFees = studentFeeRepository.findAll();

        for (String dept : departments) {
            DepartmentPerformanceDTO dto = new DepartmentPerformanceDTO();
            dto.setDepartmentName(dept);

            // Total Students
            long students = allStudents.stream().filter(s -> dept.equals(s.getDepartment())).count();
            dto.setTotalStudents(students);

            // Attendance
            List<AttendanceRecord> records = allRecords.stream().filter(r -> {
                boolean matches = r.getStudent() != null && dept.equals(r.getStudent().getDepartment());
                if (academicYear != null && !academicYear.isEmpty()) {
                    matches = matches && r.getAttendanceSession() != null
                            && academicYear.equals(r.getAttendanceSession().getAcademicYear());
                }
                if (semester != null && !semester.isEmpty()) {
                    matches = matches && r.getStudent() != null && semester.equals(r.getStudent().getSemester());
                }
                return matches;
            }).collect(Collectors.toList());
            if (!records.isEmpty()) {
                long present = records.stream().filter(r -> "PRESENT".equalsIgnoreCase(r.getStatus())).count();
                dto.setAttendancePercentage((double) present / records.size() * 100);
            } else {
                dto.setAttendancePercentage(0);
            }

            // Pass Rate
            List<SemesterResult> results = allResults.stream().filter(r -> {
                boolean matches = r.getStudent() != null && dept.equals(r.getStudent().getDepartment());
                if (academicYear != null && !academicYear.isEmpty()) {
                    matches = matches && academicYear.equals(r.getAcademicYear());
                }
                if (semester != null && !semester.isEmpty()) {
                    matches = matches && r.getSemesterName() != null && r.getSemesterName().equals(semester);
                }
                return matches;
            }).collect(Collectors.toList());
            if (!results.isEmpty()) {
                long passed = results.stream().filter(r -> r.getSgpa() >= 5.0).count();
                dto.setPassPercentage((double) passed / results.size() * 100);
            } else {
                dto.setPassPercentage(0);
            }

            // Fee Collection
            List<StudentFee> fees = allFees.stream().filter(f -> {
                boolean matches = f.getStudent() != null && dept.equals(f.getStudent().getDepartment());
                if (academicYear != null && !academicYear.isEmpty()) {
                    matches = matches && academicYear.equals(f.getAcademicYear());
                }
                if (semester != null && !semester.isEmpty()) {
                    matches = matches && semester.equals(f.getSemester());
                }
                return matches;
            }).collect(Collectors.toList());
            if (!fees.isEmpty()) {
                BigDecimal demand = BigDecimal.ZERO;
                BigDecimal collected = BigDecimal.ZERO;
                for (StudentFee f : fees) {
                    demand = demand.add(BigDecimal.valueOf(f.getTotalFee()));
                    collected = collected.add(BigDecimal.valueOf(f.getAmountPaid()));
                }
                if (demand.compareTo(BigDecimal.ZERO) > 0) {
                    dto.setFeeCollectionPercentage(collected.doubleValue() / demand.doubleValue() * 100);
                } else {
                    dto.setFeeCollectionPercentage(0);
                }
            } else {
                dto.setFeeCollectionPercentage(0);
            }

            list.add(dto);
        }
        return list;
    }

    // kept for legacy controller endpoints compatibility if other modules use them
    public List<DepartmentPerformanceDTO> getAllDepartmentPerformances() {
        return getAllDepartmentPerformances(null, null);
    }

    public AcademicOverviewDTO getAcademicOverview(String department) {
        return getAcademicOverview(department, null, null);
    }

    public AttendanceOverviewDTO getAttendanceOverview(String department) {
        return getAttendanceOverview(department, null, null);
    }

    public ExaminationOverviewDTO getExaminationOverview(String department) {
        return getExaminationOverview(department, null, null);
    }

    public FinancialOverviewDTO getFinancialOverview(String department) {
        return getFinancialOverview(department, null, null);
    }

    public List<FeeDecisionDto> getAllFeeDecisions() {
        return feeDecisionRepository.findAll().stream()
                .map(FeeDecisionDto::new)
                .collect(Collectors.toList());
    }

    public FeeDecisionDto createFeeDecision(FeeDecisionDto dto) {
        FeeDecision decision = new FeeDecision();
        decision.setAcademicYear(dto.getAcademicYear());
        decision.setDepartment(dto.getDepartment());
        decision.setProgram(dto.getProgram());
        decision.setStudentYear(dto.getStudentYear());
        decision.setSemester(dto.getSemester());
        decision.setCreatedBy(dto.getCreatedBy());

        if (dto.getComponents() != null) {
            List<FeeDecisionComponent> components = dto.getComponents().stream().map(cDto -> {
                FeeDecisionComponent component = new FeeDecisionComponent();
                component.setName(cDto.getName());
                component.setAmount(cDto.getAmount());
                component.setApplicableCondition(cDto.getApplicableCondition());
                return component;
            }).collect(Collectors.toList());
            decision.setComponents(components);
        }

        FeeDecision saved = feeDecisionRepository.save(decision);
        return new FeeDecisionDto(saved);
    }

    public List<SearchResultDTO> globalSearch(String query) {
        List<SearchResultDTO> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty())
            return results;
        String q = query.toLowerCase();

        for (Student s : studentRepository.findAll()) {
            if ((s.getName() != null && s.getName().toLowerCase().contains(q)) ||
                    (s.getRollNumber() != null && s.getRollNumber().toLowerCase().contains(q))) {
                results.add(new SearchResultDTO("Student", s.getName(), s.getRollNumber()));
            }
        }
        for (Teacher t : teacherRepository.findAll()) {
            if ((t.getName() != null && t.getName().toLowerCase().contains(q)) ||
                    (t.getEmployeeId() != null && t.getEmployeeId().toLowerCase().contains(q))) {
                results.add(new SearchResultDTO("Teacher", t.getName(), t.getEmployeeId()));
            }
        }
        for (StudentFee f : studentFeeRepository.findAll()) {
            if (f.getId() != null && f.getId().toString().contains(q)) {
                results.add(new SearchResultDTO("Fee", "Fee ID: " + f.getId(),
                        "Student: " + (f.getStudent() != null ? f.getStudent().getName() : "")));
            }
        }
        return results.size() > 20 ? results.subList(0, 20) : results;
    }

    public FeeDecisionDto updateFeeDecisionStatus(Long id, String status) {
        Optional<FeeDecision> opt = feeDecisionRepository.findById(id);
        if (opt.isPresent()) {
            FeeDecision decision = opt.get();
            decision.setStatus(status);
            return new FeeDecisionDto(feeDecisionRepository.save(decision));
        }
        throw new RuntimeException("Fee Decision not found");
    }
}
