package erp_backend.examcell.service;

import erp_backend.entity.Student;
import erp_backend.examcell.entity.ExamRegistration;
import erp_backend.examcell.entity.Examination;
import erp_backend.examcell.repository.ExamRegistrationRepository;
import erp_backend.examcell.repository.ExaminationRepository;
import erp_backend.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExaminationService {

    private final ExaminationRepository examinationRepository;
    private final ExamRegistrationRepository registrationRepository;
    private final erp_backend.examcell.repository.ExamTimetableRepository timetableRepository;
    private final StudentRepository studentRepository;

    public ExaminationService(ExaminationRepository examinationRepository,
            ExamRegistrationRepository registrationRepository,
            erp_backend.examcell.repository.ExamTimetableRepository timetableRepository,
            StudentRepository studentRepository) {
        this.examinationRepository = examinationRepository;
        this.registrationRepository = registrationRepository;
        this.timetableRepository = timetableRepository;
        this.studentRepository = studentRepository;
    }

    // ─── Examination Management ────────────────────────────────────────────────

    public Examination createExamination(Examination exam, String performedBy) {
        exam.setCreatedBy(performedBy);
        exam.setCreatedAt(LocalDateTime.now());
        if (exam.getStatus() == null)
            exam.setStatus("DRAFT");
        return examinationRepository.save(exam);
    }

    public Examination updateExamination(Long id, Examination updated, String performedBy) {
        Examination existing = examinationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Examination not found"));

        existing.setAcademicYear(updated.getAcademicYear());
        existing.setSemesterName(updated.getSemesterName());
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

        // Fetch students dynamically for the given department
        // Note: For simplicity and assuming S1=1st Year mapping exists logically,
        // we'll fetch all active students in the department and check custom
        // eligibility here.
        // In a real scenario, this relies closely on the ERP's master data structure.
        List<Student> students = studentRepository.findByDepartment(exam.getDepartment());

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
        hallTicket.put("timetable", timetable); // List of exams configured

        return hallTicket;
    }
}
