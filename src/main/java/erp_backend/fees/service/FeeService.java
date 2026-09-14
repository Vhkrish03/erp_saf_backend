package erp_backend.fees.service;

import erp_backend.entity.Student;
import erp_backend.fees.dto.FeeDashboardDto;
import erp_backend.fees.dto.RecordPaymentRequest;
import erp_backend.fees.dto.StudentFeeDto;
import erp_backend.fees.entity.FeePayment;
import erp_backend.fees.entity.FeeStructure;
import erp_backend.fees.entity.FeeComponent;
import erp_backend.fees.entity.StudentFee;
import erp_backend.fees.entity.StudentFeeComponent;
import erp_backend.fees.repository.FeePaymentRepository;
import erp_backend.fees.repository.FeeStructureRepository;
import erp_backend.fees.repository.StudentFeeRepository;
import erp_backend.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
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
        if (sf.getFeeComponents() != null && !sf.getFeeComponents().isEmpty()) {
            d.setFeeCategory(sf.getFeeComponents().stream().map(StudentFeeComponent::getName).collect(Collectors.joining(", ")));
        } else {
            d.setFeeCategory("Total Fee");
        }
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

        FeeStructure fs = new FeeStructure();
        fs.setAcademicYear(academicYear);
        fs.setSemester(semester);
        fs.setDepartment(student.getDepartment());
        fs.setTotalAmount(tuitionFee + messFee + trainingFee + otherFee + transportFee + hostelFee);
        fs.setActive(true);
        fs.setCreatedBy("ADMIN_CUSTOM");
        fs.setStatus("PUBLISHED");
        
        List<FeeComponent> components = new ArrayList<>();
        if (tuitionFee > 0) addComponent(components, "Tuition Fee", tuitionFee);
        if (messFee > 0) addComponent(components, "Mess Fee", messFee);
        if (trainingFee > 0) addComponent(components, "Training Fee", trainingFee);
        if (otherFee > 0) addComponent(components, "Other Fee", otherFee);
        if (transportFee > 0) addComponent(components, "Transport Fee", transportFee);
        if (hostelFee > 0) addComponent(components, "Hostel Fee", hostelFee);
        
        fs.setFeeComponents(components);
        fs = feeStructureRepo.save(fs);
        
        StudentFee newSf = new StudentFee();
        newSf.setStudent(student);
        newSf.setFeeStructure(fs);
        newSf.setAcademicYear(academicYear);
        newSf.setSemester(semester);
        newSf.setTotalFee(fs.getTotalAmount());
        newSf.setAmountPaid(0.0);
        
        List<StudentFeeComponent> sfcList = new ArrayList<>();
        for (FeeComponent fc : components) {
            StudentFeeComponent sfc = new StudentFeeComponent();
            sfc.setName(fc.getName());
            sfc.setAmount(fc.getAmount());
            sfcList.add(sfc);
        }
        newSf.setFeeComponents(sfcList);
        
        newSf.recomputeStatus();
        studentFeeRepo.save(newSf);
    }
    
    private void addComponent(List<FeeComponent> list, String name, double amount) {
        FeeComponent c = new FeeComponent();
        c.setName(name);
        c.setAmount(amount);
        c.setApplicableCondition("ALL");
        list.add(c);
    }

    @Transactional
    public void applyFeeStructureToTarget(FeeStructure fs) {
        List<Student> targetStudents;
        if (fs.getSection() != null && !fs.getSection().trim().isEmpty()) {
            targetStudents = studentRepo.findByDepartmentAndSemesterAndSection(fs.getDepartment(), fs.getSemester(), fs.getSection());
        } else {
            targetStudents = studentRepo.findByDepartmentAndSemester(fs.getDepartment(), fs.getSemester());
        }

        for (Student s : targetStudents) {
            // Calculate applicable components
            List<StudentFeeComponent> applicableComponents = new ArrayList<>();
            double totalDemand = 0.0;

            if (fs.getFeeComponents() != null) {
                for (FeeComponent fc : fs.getFeeComponents()) {
                    boolean applies = false;
                    String cond = fc.getApplicableCondition() != null ? fc.getApplicableCondition().toUpperCase() : "ALL";
                    switch (cond) {
                        case "ALL":
                            applies = true;
                            break;
                        case "HOSTELLER":
                            applies = "HOSTELLER".equalsIgnoreCase(s.getResidencyType());
                            break;
                        case "DAY_SCHOLAR":
                            applies = "DAY_SCHOLAR".equalsIgnoreCase(s.getResidencyType());
                            break;
                        case "TRANSPORT_REQUIRED":
                        case "BUS":
                            applies = Boolean.TRUE.equals(s.getTransportRequired());
                            break;
                        default:
                            applies = true;
                    }

                    if (applies) {
                        StudentFeeComponent sfc = new StudentFeeComponent();
                        sfc.setName(fc.getName());
                        sfc.setAmount(fc.getAmount());
                        applicableComponents.add(sfc);
                        totalDemand += fc.getAmount();
                    }
                }
            }

            if (!applicableComponents.isEmpty()) {
                // Check if already exists
                StudentFee sf = studentFeeRepo.findByStudentIdAndFeeStructureId(s.getId(), fs.getId()).orElse(new StudentFee());
                sf.setStudent(s);
                sf.setFeeStructure(fs);
                sf.setAcademicYear(fs.getAcademicYear());
                sf.setSemester(fs.getSemester());
                sf.setTotalFee(totalDemand);
                // Retain amountPaid
                sf.setAmountPaid(sf.getAmountPaid() > 0 ? sf.getAmountPaid() : 0.0);
                sf.setDueDate(fs.getDueDate());
                
                // update components
                if (sf.getFeeComponents() != null) {
                    sf.getFeeComponents().clear();
                } else {
                    sf.setFeeComponents(new ArrayList<>());
                }
                
                for (StudentFeeComponent sfc : applicableComponents) {
                    sfc.setStudentFee(sf);
                    sf.getFeeComponents().add(sfc);
                }

                sf.recomputeStatus();
                studentFeeRepo.save(sf);
            }
        }
    }
}
