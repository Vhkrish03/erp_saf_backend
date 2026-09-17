package erp_backend.coe.dto;

public class CoeDashboardResponse {
    private long totalExaminations;
    private long upcomingExaminations;
    private long ongoingExaminations;
    private long completedExaminations;
    private long pendingApproval;
    private long approvedBatches;
    private long rejectedBatches;
    private long publishedBatches;
    private long pendingPublicationAuth;

    public CoeDashboardResponse() {
    }

    public long getTotalExaminations() {
        return totalExaminations;
    }

    public void setTotalExaminations(long totalExaminations) {
        this.totalExaminations = totalExaminations;
    }

    public long getUpcomingExaminations() {
        return upcomingExaminations;
    }

    public void setUpcomingExaminations(long upcomingExaminations) {
        this.upcomingExaminations = upcomingExaminations;
    }

    public long getOngoingExaminations() {
        return ongoingExaminations;
    }

    public void setOngoingExaminations(long ongoingExaminations) {
        this.ongoingExaminations = ongoingExaminations;
    }

    public long getCompletedExaminations() {
        return completedExaminations;
    }

    public void setCompletedExaminations(long completedExaminations) {
        this.completedExaminations = completedExaminations;
    }

    public long getPendingApproval() {
        return pendingApproval;
    }

    public void setPendingApproval(long pendingApproval) {
        this.pendingApproval = pendingApproval;
    }

    public long getApprovedBatches() {
        return approvedBatches;
    }

    public void setApprovedBatches(long approvedBatches) {
        this.approvedBatches = approvedBatches;
    }

    public long getRejectedBatches() {
        return rejectedBatches;
    }

    public void setRejectedBatches(long rejectedBatches) {
        this.rejectedBatches = rejectedBatches;
    }

    public long getPublishedBatches() {
        return publishedBatches;
    }

    public void setPublishedBatches(long publishedBatches) {
        this.publishedBatches = publishedBatches;
    }

    public long getPendingPublicationAuth() {
        return pendingPublicationAuth;
    }

    public void setPendingPublicationAuth(long pendingPublicationAuth) {
        this.pendingPublicationAuth = pendingPublicationAuth;
    }
}
