package erp_backend.accountant.service;

import erp_backend.fees.dto.RecordPaymentRequest;
import erp_backend.fees.dto.StudentFeeDto;
import erp_backend.fees.entity.FeePayment;
import erp_backend.fees.service.FeeService;
import erp_backend.finance_department.dto.FinanceDashboardDto;
import erp_backend.finance_department.service.FinanceDepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service orchestrator specifically tailored for the Accountant operations.
 * Separates accountant flow completely from the Finance Head operations.
 */
@Service
public class AccountantService {

    private final FeeService feeService;
    private final FinanceDepartmentService financeDepartmentService;

    public AccountantService(FeeService feeService, FinanceDepartmentService financeDepartmentService) {
        this.feeService = feeService;
        this.financeDepartmentService = financeDepartmentService;
    }

    /**
     * Get operational summary for an accountant's day-to-day metrics.
     */
    public FinanceDashboardDto getAccountantDashboard() {
        // Here we just delegate to the general financial overview,
        // but this allows future Accountant-specific metrics isolation.
        return financeDepartmentService.getDashboardData();
    }

    /**
     * Retrieve all pending and paid fee blocks for a particular student.
     */
    public List<StudentFeeDto> getStudentFees(String studentId) {
        return feeService.getFeesForStudent(studentId);
    }

    public List<StudentFeeDto> getFeesByFilter(String department, String academicYear, String semester,
            boolean pendingOnly) {
        return feeService.getFeesByFilter(department, academicYear, semester, pendingOnly);
    }

    /**
     * Post an external payment (CASH, ONLINE, DD) from the Accountant desk.
     */
    public FeePayment recordPayment(RecordPaymentRequest request) {
        // Enforce verification steps or logs explicit to Accountants before saving
        return feeService.recordPayment(request);
    }
}
