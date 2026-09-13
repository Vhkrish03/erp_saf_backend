package erp_backend.management.dto;

public class TransportOverviewDTO {
    private long totalBuses;
    private long activeRoutes;

    public TransportOverviewDTO() {
    }

    public long getTotalBuses() {
        return totalBuses;
    }

    public void setTotalBuses(long totalBuses) {
        this.totalBuses = totalBuses;
    }

    public long getActiveRoutes() {
        return activeRoutes;
    }

    public void setActiveRoutes(long activeRoutes) {
        this.activeRoutes = activeRoutes;
    }
}
