public class RentalContract {
    private final String id;
    private final Tenant tenant;
    private final String buildingId;
    private final int apartmentNumber;
    private int months;
    private double monthlyRent;

    public RentalContract(String id, Tenant tenant, String buildingId,
                          int apartmentNumber, int months, double monthlyRent) {
        if (id == null || id.trim().isEmpty() || tenant == null ||
                buildingId == null || buildingId.trim().isEmpty() || apartmentNumber <= 0) {
            throw new IllegalArgumentException("Invalid contract information");
        }
        this.id = id;
        this.tenant = tenant;
        this.buildingId = buildingId;
        this.apartmentNumber = apartmentNumber;
        updateTerms(months, monthlyRent);
    }

    public String getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public String getBuildingId() { return buildingId; }
    public int getApartmentNumber() { return apartmentNumber; }
    public int getMonths() { return months; }
    public double getMonthlyRent() { return monthlyRent; }

    public void updateTerms(int months, double monthlyRent) {
        if (months <= 0 || monthlyRent < 0 ||
                Double.isNaN(monthlyRent) || Double.isInfinite(monthlyRent)) {
            throw new IllegalArgumentException("Invalid rental terms");
        }
        this.months = months;
        this.monthlyRent = monthlyRent;
    }

    public void displayContract() {
        System.out.println("Contract " + id + ": " + tenant.getName() +
                ", apartment " + apartmentNumber + ", building " + buildingId +
                ", monthly rent: " + monthlyRent);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RentalContract)) return false;
        RentalContract other = (RentalContract) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
