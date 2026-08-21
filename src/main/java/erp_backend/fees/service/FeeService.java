package erp_backend.fees.service;

import erp_backend.entity.Student;
import erp_backend.fees.dto.FeeDashboardDto;
import erp_backend.fees.dto.RecordPaymentRequest;
import erp_backend.fees.dto.StudentFeeDto;
import erp_backend.fees.entity.FeePayment;
import erp_backend.fees.entity.FeeStructure;
import erp_backend.fees.entity.StudentFee;
import erp_backend.fees.repository.FeePaymentRepository;
import erp_backend.fees.repository.FeeStructureRepository;
import erp_backend.fees.repository.StudentFeeRepository;
import erp_backend.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeeService {

    private final FeeStructureRepository feeStructureRepo;
    private final StudentFeeRepository studentFeeRepo;
    private final FeePaymentRepository feePaymentRepo;
    private final StudentRepository studentRepo;

    public FeeService(FeeStructureRepository feeStructureRepo,
            StudentFeeRepository studentFeeRepo,
            FeePaymentRepository feePaymentRepo,
            StudentRepository studentRepo) {
        this.feeStructureRepo = feeStructureRepo;
        this.studentFeeRepo = studentFeeRepo;
        this.feePaymentRepo = feePaymentRepo;
        this.studentRepo = studentRepo;
    }

    // ─────────────────────────────── FeeStructure ────────────────────────────────

    @Transactional
    public FeeStructure createFeeStructure(FeeStructure fs) {
        return feeStructureRepo.save(fs);
    }

    public List<FeeStructure> getAllFeeStructures() {
        return feeStructureRepo.findByIsActiveTrue();
    }

    public List<FeeStructure> getFeeStructuresForClass(String dept, String sem, String ay) {
        return feeStructureRepo.findByDepartmentAndSemesterAndAcademicYear(dept, sem, ay);
    }

    // ─────────────────────────────── StudentFee
    // ───────────────────────────────────

    /**
     * Assign a fee structure to a student for a specific semester.
     * Creates a new StudentFee linking the student and fee structure.
     */
    @Transactional
    public StudentFee assignFeeToStudent(String studentId, Long feeStructureId) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));
        FeeStructure fs = feeStructureRepo.findById(feeStructureId)
                .orElseThrow(() -> new IllegalArgumentException("Fee structure not found: " + feeStructureId));

        // Avoid duplicate assignment
        return studentFeeRepo.findByStudentIdAndFeeStructureId(studentId, feeStructureId)
                .orElseGet(() -> {
                    StudentFee sf = new StudentFee();
                    sf.setStudent(student);
                    sf.setFeeStructure(fs);
                    sf.setAcademicYear(fs.getAcademicYear());
                    sf.setSemester(fs.getSemester());
                    sf.setTotalFee(fs.getTotalAmount());
                    sf.setDueDate(fs.getDueDate());
                    sf.recomputeStatus();
                    return studentFeeRepo.save(sf);
                });
    }

    public List<StudentFeeDto> getFeesForStudent(String studentId) {
        return studentFeeRepo.findByStudentId(studentId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /** All fee records for a class section, for a given academic year */
    public List<StudentFeeDto> getFeesForClass(String dept, String sem, String sec, String academicYear) {
        return studentFeeRepo.findByClassSection(dept, sem, sec, academicYear)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    /** Only pending/overdue/partially-paid students */
    public List<StudentFeeDto> getPendingFeesForClass(String dept, String sem, String sec, String academicYear) {
        return studentFeeRepo.findPendingByClassSection(dept, sem, sec, academicYear)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // ─────────────────────────────── FeePayment
    // ───────────────────────────────────

    @Transactional
    public FeePayment recordPayment(RecordPaymentRequest req) {
        StudentFee sf = studentFeeRepo.findById(req.getStudentFeeId())
                .orElseThrow(() -> new IllegalArgumentException("StudentFee not found: " + req.getStudentFeeId()));
        Student student = studentRepo.findById(req.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + req.getStudentId()));

        FeePayment payment = new FeePayment();
        payment.setStudentFee(sf);
        payment.setStudent(student);
        payment.setAmountPaid(req.getAmountPaid());
        payment.setPaymentDate(LocalDate.parse(req.getPaymentDate()));
        payment.setPaymentMode(req.getPaymentMode());
        payment.setTransactionId(req.getTransactionId());
        payment.setPaymentReference(req.getPaymentReference());
        payment.setRemarks(req.getRemarks());
        payment.setRecordedBy(req.getRecordedBy());
        payment.setAcademicYear(req.getAcademicYear());
        payment.setSemester(req.getSemester());
        payment.setFeeCategory(req.getFeeCategory());
        feePaymentRepo.save(payment);

        // Update cumulative amountPaid in StudentFee
        double totalPaid = feePaymentRepo.findByStudentFeeId(sf.getId())
                .stream().mapToDouble(FeePayment::getAmountPaid).sum();
        sf.setAmountPaid(totalPaid);
        sf.recomputeStatus();
        studentFeeRepo.save(sf);

        return payment;
    }

    public List<FeePayment> getPaymentHistoryForStudent(String studentId) {
        return feePaymentRepo.findByStudentId(studentId);
    }

    // ─────────────────────────────── Dashboard
    // ────────────────────────────────────

    public FeeDashboardDto getDashboard(String dept, String sem, String sec, String academicYear) {
        List<StudentFee> fees = studentFeeRepo.findByClassSection(dept, sem, sec, academicYear);

        FeeDashboardDto dto = new FeeDashboardDto(dept, sem, sec, academicYear);
        dto.setTotalStudents(fees.size());

        int paid = 0, partial = 0, pending = 0, overdue = 0;
        double totalAmount = 0, collected = 0;

        for (StudentFee sf : fees) {
            totalAmount += sf.getTotalFee();
            collected += sf.getAmountPaid();
            switch (sf.getPaymentStatus()) {
                case "PAID" -> paid++;
                case "PARTIALLY_PAID" -> partial++;
                case "OVERDUE" -> overdue++;
                default -> pending++;
            }
        }

        dto.setPaidCount(paid);
        dto.setPartiallyPaidCount(partial);
        dto.setPendingCount(pending);
        dto.setOverdueCount(overdue);
        dto.setTotalFeeAmount(totalAmount);
        dto.setCollectedAmount(collected);
        dto.setOutstandingAmount(totalAmount - collected);
        return dto;
    }

    // ─────────────────────────────── Mapper
    // ───────────────────────────────────────

    private StudentFeeDto toDto(StudentFee sf) {
        StudentFeeDto d = new StudentFeeDto();
        d.setStudentFeeId(sf.getId());
        d.setStudentId(sf.getStudent().getId());
        d.setStudentName(sf.getStudent().getName());
        d.setRollNumber(sf.getStudent().getRollNumber());
        d.setDepartment(sf.getStudent().getDepartment());
        d.setSemester(sf.getSemester());
        d.setSection(sf.getStudent().getSection());
        d.setAcademicYear(sf.getAcademicYear());
        d.setFeeCategory(sf.getFeeStructure() != null ? sf.getFeeStructure().getFeeCategory() : "");
        d.setTotalFee(sf.getTotalFee());
        d.setAmountPaid(sf.getAmountPaid());
        d.setBalanceAmount(sf.getBalanceAmount());
        d.setPaymentStatus(sf.getPaymentStatus());
        d.setDueDate(sf.getDueDate() != null ? sf.getDueDate().toString() : null);
        d.setRemarks(sf.getRemarks());
        d.setUpdatedAt(sf.getUpdatedAt() != null ? sf.getUpdatedAt().toString() : null);
        return d;
    }

    @Transactional
    public void configureCustomFeesForStudent(
            String studentId, String academicYear, String semester,
            double tuitionFee, double messFee, double trainingFee,
            double otherFee, double transportFee, double hostelFee) {

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        saveOrUpdateCategoryFee(student, academicYear, semester, "Tuition Fee", tuitionFee);
        saveOrUpdateCategoryFee(student, academicYear, semester, "Mess Fee", messFee);
        saveOrUpdateCategoryFee(student, academicYear, semester, "Training Fee", trainingFee);
        saveOrUpdateCategoryFee(student, academicYear, semester, "Other Fee", otherFee);
        saveOrUpdateCategoryFee(student, academicYear, semester, "Transport Fee", transportFee);
        saveOrUpdateCategoryFee(student, academicYear, semester, "Hostel Fee", hostelFee);
    }

    private void saveOrUpdateCategoryFee(Student student, String academicYear, String semester, String category,
            double amount) {
        // Step 1: Find or create standard/dummy FeeStructure for this
        // category/dept/sem/ay
        FeeStructure fs = feeStructureRepo.findByDepartmentAndSemesterAndAcademicYear(
                student.getDepartment(), semester, academicYear).stream()
                .filter(f -> category.equalsIgnoreCase(f.getFeeCategory())).findFirst().orElseGet(() -> {
                    FeeStructure newFs = new FeeStructure();
                    newFs.setAcademicYear(academicYear);
                    newFs.setSemester(semester);
                    newFs.setDepartment(student.getDepartment());
                    newFs.setFeeCategory(category);
                    newFs.setTotalAmount(amount > 0 ? amount : 1000.0);
                    newFs.setActive(true);
                    newFs.setCreatedBy("ADMIN_CUSTOM");
                    return feeStructureRepo.save(newFs);
                });

        // Step 2: See if StudentFee already exists
        StudentFee sf = studentFeeRepo.findByStudentIdAndFeeStructureId(student.getId(), fs.getId()).orElse(null);

        if (sf != null) {
            if (amount <= 0) {
                if (sf.getAmountPaid() == 0) {
                    studentFeeRepo.delete(sf);
                } else {
                    sf.setTotalFee(0);
                    sf.recomputeStatus();
                    studentFeeRepo.save(sf);
                }
            } else {
                sf.setTotalFee(amount);
                sf.recomputeStatus();
                studentFeeRepo.save(sf);
            }
        } else if (amount > 0) {
            StudentFee newSf = new StudentFee();
            newSf.setStudent(student);
            newSf.setFeeStructure(fs);
            newSf.setAcademicYear(academicYear);
            newSf.setSemester(semester);
            newSf.setTotalFee(amount);
            newSf.setAmountPaid(0.0);
            newSf.recomputeStatus();
            studentFeeRepo.save(newSf);
        }
    }
}
