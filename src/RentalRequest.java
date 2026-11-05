public class RentalRequest {
    public enum Status { PENDING, APPROVED, REJECTED, CANCELLED }

    private final String id;
    private final String tenantId;
    private final String buildingId;
    private final int apartmentNumber;
    private final int months;
    private final double monthlyRent;
    private Status status = Status.PENDING;
    private String message = "";

    public RentalRequest(String id, String tenantId, String buildingId,
                         int apartmentNumber, int months, double monthlyRent) {
        if (empty(id) || empty(tenantId) || empty(buildingId) ||
                apartmentNumber <= 0 || months <= 0 || monthlyRent < 0 ||
                Double.isNaN(monthlyRent) || Double.isInfinite(monthlyRent)) {
            throw new IllegalArgumentException("Invalid rental request");
        }
        this.id = id;
        this.tenantId = tenantId;
        this.buildingId = buildingId;
        this.apartmentNumber = apartmentNumber;
        this.months = months;
        this.monthlyRent = monthlyRent;
    }

    private boolean empty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public String getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getBuildingId() { return buildingId; }
    public int getApartmentNumber() { return apartmentNumber; }
    public int getMonths() { return months; }
    public double getMonthlyRent() { return monthlyRent; }
    public Status getStatus() { return status; }
    public String getMessage() { return message; }

    public void setStatus(Status newStatus, String message) {
        if (status != Status.PENDING || newStatus == null || newStatus == Status.PENDING) {
            throw new IllegalStateException("Request already processed or invalid status");
        }
        status = newStatus;
        this.message = message;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RentalRequest)) return false;
        RentalRequest other = (RentalRequest) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
