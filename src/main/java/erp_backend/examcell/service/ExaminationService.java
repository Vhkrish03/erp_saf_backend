package erp_backend.examcell.service;

import erp_backend.entity.Student;
import erp_backend.examcell.entity.ExamRegistration;
import erp_backend.examcell.entity.Examination;
import erp_backend.examcell.repository.ExamRegistrationRepository;
import erp_backend.examcell.repository.ExaminationRepository;
import erp_backend.repository.StudentRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExaminationService {

    private final ExaminationRepository examinationRepository;
    private final ExamRegistrationRepository registrationRepository;
    private final erp_backend.examcell.repository.ExamTimetableRepository timetableRepository;
    private final erp_backend.examcell.repository.ExamAttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final erp_backend.fees.repository.FeeStructureRepository feeStructureRepository;
    private final erp_backend.fees.repository.StudentFeeRepository studentFeeRepository;

    public ExaminationService(ExaminationRepository examinationRepository,
            ExamRegistrationRepository registrationRepository,
            erp_backend.examcell.repository.ExamTimetableRepository timetableRepository,
            erp_backend.examcell.repository.ExamAttendanceRepository attendanceRepository,
            StudentRepository studentRepository,
            erp_backend.fees.repository.FeeStructureRepository feeStructureRepository,
            erp_backend.fees.repository.StudentFeeRepository studentFeeRepository) {
        this.examinationRepository = examinationRepository;
        this.registrationRepository = registrationRepository;
        this.timetableRepository = timetableRepository;
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.feeStructureRepository = feeStructureRepository;
        this.studentFeeRepository = studentFeeRepository;
    }

    // ─── Examination Management ────────────────────────────────────────────────

    public Examination createExamination(Examination exam, String performedBy) {
        exam.setCreatedBy(performedBy);
        exam.setCreatedAt(LocalDateTime.now());
        if (exam.getStatus() == null)
            exam.setStatus("DRAFT");
        return examinationRepository.save(exam);
    }

    public Examination updateExamination(long id, Examination updated, String performedBy) {
        Examination existing = examinationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Examination not found"));

        existing.setAcademicYear(updated.getAcademicYear());
        existing.setSemesterName(updated.getSemesterName());
        existing.setYear(updated.getYear());
        existing.setDepartment(updated.getDepartment());
        existing.setExamType(updated.getExamType());
        existing.setExamName(updated.getExamName());
        existing.setStartDate(updated.getStartDate());
        existing.setEndDate(updated.getEndDate());
        existing.setExamSession(updated.getExamSession());
        existing.setStatus(updated.getStatus());

        return examinationRepository.save(existing);
    }

    public List<Examination> getAllExaminations() {
        return examinationRepository.findAll();
    }

    public Examination getExamination(Long id) {
        return examinationRepository.findById(id).orElse(null);
    }

    public List<String> getDistinctDepartments() {
        return studentRepository.findDistinctDepartments();
    }

    // ─── Exam Registration & Eligibility ────────────────────────────────────────

    public List<ExamRegistration> getRegistrationsForExam(Long examId) {
        return registrationRepository.findByExaminationId(examId);
    }

    /**
     * Finds eligible students for a specific exam based on its context constraints,
     * and seeds ExamRegistration records if they don't exist.
     */
    public void generateEligibilityList(Long examId) {
        Examination exam = examinationRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Examination not found"));

        List<Student> students = studentRepository.findByDepartment(exam.getDepartment());

        // Auto-populate subjects from timetable to ensure fee calculations work
        // downstream
        List<erp_backend.examcell.entity.ExamTimetable> tt = getTimetableForExam(examId);
        List<String> ttSubjects = tt.stream().map(t -> t.getSubjectCode()).toList();

        for (Student s : students) {
            Optional<ExamRegistration> existing = registrationRepository.findByExaminationIdAndStudentId(examId,
                    s.getId());
            if (existing.isEmpty()) {
                ExamRegistration reg = new ExamRegistration();
                reg.setExamination(exam);
                reg.setStudentId(s.getId());
                reg.setStudentName(s.getName());
                reg.setRegisterNumber(s.getRollNumber());

                // Eligibility Logic (Mocking ERP rules: e.g. Attendance > 75%, Fee paid)
                // We'll mark them ELIGIBLE by default for prototype, but in real ERP this
                // consumes master services.
                reg.setStatus("ELIGIBLE");
                reg.setEligibilityReason("Satisfies minimal requirements.");
                reg.setFeePaid(false); // Default
                reg.setRegisteredSubjects(ttSubjects);
                registrationRepository.save(reg);
            }
        }
    }

    public void updateRegistrationStatus(Long regId, String status, boolean isFeePaid) {
        ExamRegistration reg = registrationRepository.findById(regId)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found"));
        reg.setStatus(status);
        reg.setFeePaid(isFeePaid);
        if ("REGISTERED".equals(status)) {
            reg.setRegisteredAt(LocalDateTime.now());
        }
        registrationRepository.save(reg);
    }

    // ─── Exam Timetable Management ───────────────────────────────────────────

    public erp_backend.examcell.entity.ExamTimetable addTimetable(Long examId,
            erp_backend.examcell.entity.ExamTimetable timetable) {
        Examination exam = examinationRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Examination not found"));
        timetable.setExamination(exam);

        // Basic conflict validation can be done here by checking existing timings
        // Note: Not doing complex classroom overlap as per generic ERP instruction,
        // rely on Exam Cell to schedule properly.

        return timetableRepository.save(timetable);
    }

    public Examination submitToCoe(Long examId, String performedBy) {
        Examination exam = examinationRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Examination not found"));

        List<erp_backend.examcell.entity.ExamTimetable> tt = getTimetableForExam(examId);
        if (tt.isEmpty()) {
            throw new IllegalStateException("Cannot submit: Timetable/Papers are empty.");
        }
        exam.setApprovalStatus("SUBMITTED_TO_COE");
        return examinationRepository.save(exam);
    }

    public Examination approveByCoe(Long examId, String performedBy) {
        Examination exam = examinationRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Examination not found"));

        exam.setApprovalStatus("COE_APPROVED");
        exam.setApprovedBy(performedBy);
        exam.setApprovedAt(LocalDateTime.now());
        return examinationRepository.save(exam);
    }

    public Examination rejectByCoe(Long examId, String reason, String performedBy) {
        Examination exam = examinationRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Examination not found"));
        exam.setApprovalStatus("COE_REJECTED");
        exam.setRejectionReason(reason);
        return examinationRepository.save(exam);
    }

    public ExamRegistration verifyStudentPayment(Long regId, String verifiedBy, String verificationStatus) {
        ExamRegistration reg = registrationRepository.findById(regId)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found"));

        reg.setVerificationStatus(verificationStatus);
        reg.setVerifiedBy(verifiedBy);
        reg.setVerificationDate(LocalDateTime.now());
        if ("VERIFIED".equals(verificationStatus)) {
            reg.setFeePaid(true); // Inherited flag legacy support
            reg.setPaymentStatus("SUCCESS");
        }
        return registrationRepository.save(reg);
    }

    public Examination setFeeDeadline(Long examId, LocalDate deadline) {
        Examination exam = examinationRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Examination not found"));
        exam.setFeeDeadline(deadline);
        return examinationRepository.save(exam);
    }

    public erp_backend.examcell.entity.ExamTimetable updatePaperFee(Long paperId, Double fee) {
        erp_backend.examcell.entity.ExamTimetable tt = timetableRepository.findById(paperId)
                .orElseThrow(() -> new IllegalArgumentException("Paper not found"));
        tt.setExamFee(fee);
        return timetableRepository.save(tt);
    }

    public void syncExamFeesWithAccountant(Long examId) {
        Examination exam = examinationRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Examination not found"));

        List<erp_backend.examcell.entity.ExamTimetable> tt = getTimetableForExam(examId);
        List<ExamRegistration> registrations = registrationRepository.findByExaminationId(examId);

        // Calculate fee per student
        for (ExamRegistration reg : registrations) {
            double fee = 0;
            if (reg.getRegisteredSubjects() != null) {
                for (String subjectCode : reg.getRegisteredSubjects()) {
                    erp_backend.examcell.entity.ExamTimetable entry = tt.stream()
                            .filter(t -> t.getSubjectCode().equals(subjectCode)).findFirst().orElse(null);
                    if (entry != null && entry.getExamFee() != null) {
                        fee += entry.getExamFee();
                    }
                }
            }
            reg.setTotalFee(fee);

            // Check existing student fee
            erp_backend.fees.entity.StudentFee sf = null;
            String feeDesc = "Exam Fee - " + exam.getExamName();

            // Push to accountant fees
            if (fee > 0) {
                // Creating a FeeStructure for this exam ad-hoc if not exists
                erp_backend.fees.entity.FeeStructure fs = new erp_backend.fees.entity.FeeStructure();
                fs.setAcademicYear(exam.getAcademicYear());
                fs.setSemester(exam.getSemesterName());
                fs.setDepartment(exam.getDepartment());
                fs.setTotalAmount(fee);
                fs.setActive(true);
                fs.setCreatedBy("EXAM_CELL");
                fs.setStatus("PUBLISHED");
                fs.setDescription(feeDesc);

                java.util.List<erp_backend.fees.entity.FeeComponent> components = new java.util.ArrayList<>();
                erp_backend.fees.entity.FeeComponent c = new erp_backend.fees.entity.FeeComponent();
                c.setName("Exam Fee");
                c.setAmount(fee);
                c.setApplicableCondition("ALL");
                components.add(c);
                fs.setFeeComponents(components);

                fs = feeStructureRepository.save(fs);

                sf = new erp_backend.fees.entity.StudentFee();
                Student student = studentRepository.findById(reg.getStudentId()).orElse(null);
                if (student != null) {
                    sf.setStudent(student);
                    sf.setFeeStructure(fs);
                    sf.setAcademicYear(exam.getAcademicYear());
                    sf.setSemester(exam.getSemesterName());
                    sf.setTotalFee(fee);
                    sf.setAmountPaid(0.0);
                    sf.recomputeStatus();
                    studentFeeRepository.save(sf);
                }
            }

            registrationRepository.save(reg);
        }

        exam.setApprovalStatus("FEES_PUBLISHED");
        examinationRepository.save(exam);
    }

    public List<erp_backend.examcell.entity.ExamTimetable> getTimetableForExam(Long examId) {
        return timetableRepository.findByExaminationIdOrderByExamDateAsc(examId);
    }

    // ─── Hall Ticket Generation ───────────────────────────────────────────────

    public java.util.Map<String, Object> generateHallTicket(Long registrationId) {
        ExamRegistration reg = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found"));

        if (!"REGISTERED".equals(reg.getStatus()) && !"ELIGIBLE".equals(reg.getStatus())) {
            throw new IllegalStateException("Student is not eligible for a hall ticket.");
        }

        if (!"COE_APPROVED".equals(reg.getExamination().getApprovalStatus())) {
            throw new IllegalStateException("Examination is not COE Approved yet.");
        }

        if (!"VERIFIED".equals(reg.getVerificationStatus())) {
            throw new IllegalStateException(
                    "Hall ticket locked: examination fee payment is not manually verified by Exam Cell.");
        }

        Examination exam = reg.getExamination();
        List<erp_backend.examcell.entity.ExamTimetable> timetable = getTimetableForExam(exam.getId());

        java.util.Map<String, Object> hallTicket = new java.util.HashMap<>();
        hallTicket.put("studentId", reg.getStudentId());
        hallTicket.put("studentName", reg.getStudentName());
        hallTicket.put("registerNumber", reg.getRegisterNumber());
        hallTicket.put("department", exam.getDepartment());
        hallTicket.put("semester", exam.getSemesterName());
        hallTicket.put("examination", exam.getExamName());
        hallTicket.put("session", exam.getExamSession());
        hallTicket.put("timetable", timetable);

        return hallTicket;
    }

    // ─── Examination Attendance ───────────────────────────────────────────────

    public List<erp_backend.examcell.entity.ExamAttendance> getAttendanceForTimetable(Long timetableId) {
        erp_backend.examcell.entity.ExamTimetable timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new IllegalArgumentException("Timetable not found"));

        List<ExamRegistration> registered = registrationRepository
                .findByExaminationId(timetable.getExamination().getId());

        for (ExamRegistration reg : registered) {
            if ("REGISTERED".equals(reg.getStatus())) {
                Optional<erp_backend.examcell.entity.ExamAttendance> existing = attendanceRepository
                        .findByExamTimetableIdAndStudentId(timetableId, reg.getStudentId());
                if (existing.isEmpty()) {
                    erp_backend.examcell.entity.ExamAttendance attendance = new erp_backend.examcell.entity.ExamAttendance();
                    attendance.setExamTimetable(timetable);
                    attendance.setStudentId(reg.getStudentId());
                    attendance.setStatus("PRESENT"); // default
                    attendanceRepository.save(attendance);
                }
            }
        }
        return attendanceRepository.findByExamTimetableId(timetableId);
    }

    public List<java.util.Map<String, Object>> getAttendanceForTimetableMapped(Long timetableId) {
        List<erp_backend.examcell.entity.ExamAttendance> atts = getAttendanceForTimetable(timetableId);
        List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();

        erp_backend.examcell.entity.ExamTimetable timetable = timetableRepository.findById(timetableId).get();
        List<ExamRegistration> regs = registrationRepository.findByExaminationId(timetable.getExamination().getId());

        for (erp_backend.examcell.entity.ExamAttendance att : atts) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", att.getId());
            map.put("studentId", att.getStudentId());
            map.put("status", att.getStatus());
            map.put("remarks", att.getRemarks());

            ExamRegistration r = regs.stream().filter(x -> x.getStudentId().equals(att.getStudentId())).findFirst()
                    .orElse(null);
            if (r != null) {
                map.put("studentName", r.getStudentName());
                map.put("registerNumber", r.getRegisterNumber());
            }
            result.add(map);
        }
        return result;
    }

    public void markAttendance(Long attendanceId, String status, String remarks, String performedBy) {
        erp_backend.examcell.entity.ExamAttendance att = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new IllegalArgumentException("Attendance record not found"));
        att.setStatus(status);
        if (remarks != null)
            att.setRemarks(remarks);
        att.setMarkedBy(performedBy);
        att.setMarkedAt(LocalDateTime.now());
        attendanceRepository.save(att);
    }
}
