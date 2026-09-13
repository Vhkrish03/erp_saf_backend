package erp_backend.management.dto;

import java.util.Map;

public class StaffOverviewDTO {
    private long totalTeachingStaff;
    private long totalNonTeachingStaff;
    private Map<String, Long> departmentWiseTeachingStaff;

    public StaffOverviewDTO() {
    }

    public long getTotalTeachingStaff() {
        return totalTeachingStaff;
    }

    public void setTotalTeachingStaff(long totalTeachingStaff) {
        this.totalTeachingStaff = totalTeachingStaff;
    }

    public long getTotalNonTeachingStaff() {
        return totalNonTeachingStaff;
    }

    public void setTotalNonTeachingStaff(long totalNonTeachingStaff) {
        this.totalNonTeachingStaff = totalNonTeachingStaff;
    }

    public Map<String, Long> getDepartmentWiseTeachingStaff() {
        return departmentWiseTeachingStaff;
    }

    public void setDepartmentWiseTeachingStaff(Map<String, Long> departmentWiseTeachingStaff) {
        this.departmentWiseTeachingStaff = departmentWiseTeachingStaff;
    }
}
