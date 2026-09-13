package erp_backend.finance_department.controller;

import erp_backend.finance_department.dto.FinanceDashboardDto;
import erp_backend.finance_department.service.FinanceDepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/finance_department")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FinanceDepartmentController {

    private final FinanceDepartmentService financeDepartmentService;

    public FinanceDepartmentController(FinanceDepartmentService financeDepartmentService) {
        this.financeDepartmentService = financeDepartmentService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<FinanceDashboardDto> getDashboardOverview() {
        return ResponseEntity.ok(financeDepartmentService.getDashboardData());
    }
}
