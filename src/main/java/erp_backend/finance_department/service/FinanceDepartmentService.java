package erp_backend.finance_department.service;

import erp_backend.fees.entity.FeePayment;
import erp_backend.fees.entity.FeeStructure;
import erp_backend.fees.entity.StudentFee;
import erp_backend.fees.repository.FeePaymentRepository;
import erp_backend.fees.repository.FeeStructureRepository;
import erp_backend.fees.repository.StudentFeeRepository;
import erp_backend.finance_department.dto.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FinanceDepartmentService {

    private final StudentFeeRepository studentFeeRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final erp_backend.fees.service.FeeService feeService;

    public FinanceDepartmentService(StudentFeeRepository studentFeeRepository,
                                    FeePaymentRepository feePaymentRepository,
                                    FeeStructureRepository feeStructureRepository,
                                    erp_backend.fees.service.FeeService feeService) {
        this.studentFeeRepository = studentFeeRepository;
        this.feePaymentRepository = feePaymentRepository;
        this.feeStructureRepository = feeStructureRepository;
        this.feeService = feeService;
    }

    public FinanceDashboardDto getDashboardData() {
        FinanceDashboardDto dto = new FinanceDashboardDto();
        List<StudentFee> allFees = studentFeeRepository.findAll();
        List<FeePayment> allPayments = feePaymentRepository.findAll();
        List<FeeStructure> allStructures = feeStructureRepository.findAll();

        BigDecimal demand = BigDecimal.ZERO;
        BigDecimal collected = BigDecimal.ZERO;
        BigDecimal pending = BigDecimal.ZERO;
        BigDecimal overdue = BigDecimal.ZERO;

        Map<String, BigDecimal> deptDemand = new HashMap<>();
        Map<String, BigDecimal> deptCollected = new HashMap<>();
        Map<String, BigDecimal> deptPending = new HashMap<>();

        Map<String, BigDecimal> feeBreakdownExpected = new HashMap<>();
        Map<String, BigDecimal> feeBreakdownCollected = new HashMap<>();
        Map<String, BigDecimal> feeBreakdownPending = new HashMap<>();

        for (StudentFee fee : allFees) {
            BigDecimal total = BigDecimal.valueOf(fee.getTotalFee());
            BigDecimal amtPaid = BigDecimal.valueOf(fee.getAmountPaid());
            BigDecimal bal = BigDecimal.valueOf(fee.getBalanceAmount());
            
            demand = demand.add(total);
            collected = collected.add(amtPaid);
            pending = pending.add(bal);
            
            if ("OVERDUE".equals(fee.getPaymentStatus())) {
                overdue = overdue.add(bal);
            }

            // Department aggregations
            String dept = fee.getStudent() != null ? fee.getStudent().getDepartment() : "UNKNOWN";
            deptDemand.merge(dept, total, BigDecimal::add);
            deptCollected.merge(dept, amtPaid, BigDecimal::add);
            deptPending.merge(dept, bal, BigDecimal::add);

            // Category breakdown
            String cat = "Total Fee";
            if (fee.getFeeComponents() != null && !fee.getFeeComponents().isEmpty()) {
                cat = fee.getFeeComponents().get(0).getName();
            }
            feeBreakdownExpected.merge(cat, total, BigDecimal::add);
            feeBreakdownCollected.merge(cat, amtPaid, BigDecimal::add);
            feeBreakdownPending.merge(cat, bal, BigDecimal::add);
        }

        dto.setTotalDemand(demand);
        dto.setTotalCollected(collected);
        dto.setTotalPending(pending);
        dto.setTotalOverdue(overdue);
        
        if (demand.compareTo(BigDecimal.ZERO) > 0) {
            dto.setCollectionRate(collected.doubleValue() / demand.doubleValue() * 100);
        }

        // Today's Collection
        LocalDate today = LocalDate.now();
        BigDecimal todaysCol = allPayments.stream()
            .filter(p -> p.getPaymentDate() != null && p.getPaymentDate().isEqual(today))
            .map(p -> BigDecimal.valueOf(p.getAmountPaid()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTodaysCollection(todaysCol);

        // Department Collection DTOs
        List<DepartmentCollectionDto> deptList = new ArrayList<>();
        for (String dept : deptDemand.keySet()) {
            double rate = deptDemand.get(dept).compareTo(BigDecimal.ZERO) > 0 ? 
                (deptCollected.get(dept).doubleValue() / deptDemand.get(dept).doubleValue() * 100) : 0;
            deptList.add(new DepartmentCollectionDto(dept, deptDemand.get(dept), deptCollected.get(dept), deptPending.get(dept), rate));
        }
        dto.setDepartmentCollections(deptList);

        // Fee Breakdown DTOs
        List<FeeCategoryBreakdownDto> breakList = new ArrayList<>();
        for (String cat : feeBreakdownExpected.keySet()) {
            breakList.add(new FeeCategoryBreakdownDto(cat, feeBreakdownExpected.get(cat), feeBreakdownCollected.get(cat), feeBreakdownPending.get(cat)));
        }
        dto.setFeeBreakdown(breakList);

        // Trends (By Month)
        Map<String, BigDecimal> trends = new LinkedHashMap<>(); // Keep ordered
        for(int i=5; i>=0; i--) {
            LocalDate monthDate = today.minusMonths(i);
            String label = monthDate.getMonth().name().substring(0,3) + " " + monthDate.getYear();
            trends.put(label, BigDecimal.ZERO);
        }

        for (FeePayment p : allPayments) {
            if (p.getPaymentDate() != null) {
                String label = p.getPaymentDate().getMonth().name().substring(0,3) + " " + p.getPaymentDate().getYear();
                if (trends.containsKey(label)) {
                    trends.put(label, trends.get(label).add(BigDecimal.valueOf(p.getAmountPaid())));
                }
            }
        }

        List<CollectionTrendDto> trendDtos = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> t : trends.entrySet()) {
            trendDtos.add(new CollectionTrendDto(t.getKey(), t.getValue()));
        }
        dto.setCollectionTrend(trendDtos);

        // Approvals Overview
        long pendingStructures = allStructures.stream().filter(f -> "UNDER_REVIEW".equals(f.getStatus())).count();
        dto.setPendingFeeStructures(pendingStructures);
        
        dto.setUnverifiedPayments(0); // Implementation depends on manual payments

        return dto;
    }

    public List<FeeStructure> getAllFeeStructures() {
        return feeStructureRepository.findAll();
    }

    public FeeStructure approveFeeStructure(Long id, String approvedBy) {
        FeeStructure fs = feeStructureRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Fee structure not found"));
        if (!"UNDER_REVIEW".equals(fs.getStatus())) {
            throw new IllegalArgumentException("Fee structure is not UNDER_REVIEW");
        }
        fs.setStatus("APPROVED");
        fs.setApprovedBy(approvedBy);
        fs.setApprovedAt(LocalDateTime.now());
        return feeStructureRepository.save(fs);
    }

    public FeeStructure rejectFeeStructure(Long id, String rejectedBy, String reason) {
        FeeStructure fs = feeStructureRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Fee structure not found"));
        if (!"UNDER_REVIEW".equals(fs.getStatus())) {
            throw new IllegalArgumentException("Fee structure is not UNDER_REVIEW");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Rejection reason is required");
        }
        fs.setStatus("REJECTED");
        fs.setRejectionReason(reason);
        // Note: keeping published/approved info empty if rejected before, or maybe nullifying it.
        return feeStructureRepository.save(fs);
    }

    public FeeStructure publishFeeStructure(Long id, String publishedBy) {
        FeeStructure fs = feeStructureRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Fee structure not found"));
        if (!"APPROVED".equals(fs.getStatus())) {
            throw new IllegalArgumentException("Only APPROVED structures can be published");
        }
        fs.setStatus("PUBLISHED");
        fs.setPublishedBy(publishedBy);
        fs.setPublishedAt(LocalDateTime.now());
        
        fs = feeStructureRepository.save(fs);
        
        // apply the structure to applicable students
        feeService.applyFeeStructureToTarget(fs);
        
        return fs;
    }
}
