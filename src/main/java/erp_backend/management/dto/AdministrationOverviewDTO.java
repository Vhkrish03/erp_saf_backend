package erp_backend.management.dto;

public class AdministrationOverviewDTO {
    private long activeNotices;
    private long pendingApprovals;
    private long completedApprovals;

    public AdministrationOverviewDTO() {
    }

    public long getActiveNotices() {
        return activeNotices;
    }

    public void setActiveNotices(long activeNotices) {
        this.activeNotices = activeNotices;
    }

    public long getPendingApprovals() {
        return pendingApprovals;
    }

    public void setPendingApprovals(long pendingApprovals) {
        this.pendingApprovals = pendingApprovals;
    }

    public long getCompletedApprovals() {
        return completedApprovals;
    }

    public void setCompletedApprovals(long completedApprovals) {
        this.completedApprovals = completedApprovals;
    }
}
