public class CommercialBuilding extends Building implements Rentable, Maintenance {
    private String businessType;
    private int offices;
    private int rentedOffices;

    public CommercialBuilding(String id, String address, int floors, double area,
                              int yearBuilt, boolean occupied, String businessType,
                              int offices, int rentedOffices) {
        super(id, address, floors, area, yearBuilt, occupied);
        if (businessType == null || businessType.trim().isEmpty() ||
                offices <= 0 || rentedOffices < 0 || rentedOffices > offices) {
            throw new IllegalArgumentException("Invalid office information");
        }
        this.businessType = businessType;
        this.offices = offices;
        this.rentedOffices = rentedOffices;
    }

    public String getBusinessType() { return businessType; }
    public int getOffices() { return offices; }
    public int getRentedOffices() { return rentedOffices; }

    @Override
    public String getBuildingPurpose() { return "Business"; }

    @Override
    public double calculateMonthlyOperatingCost(double costPerSquareMeter,
                                                double costPerOffice) {
        if (costPerSquareMeter < 0 || costPerOffice < 0) return 0;
        return getArea() * costPerSquareMeter + offices * costPerOffice;
    }

    @Override
    public int getAvailableUnits() { return offices - rentedOffices; }

    @Override
    public double calculatePotentialMonthlyRent(double rentPerOffice) {
        if (rentPerOffice < 0) return 0;
        return getAvailableUnits() * rentPerOffice;
    }

    @Override
    public double calculateMaintenanceCost(double costPerSquareMeter) {
        if (costPerSquareMeter < 0) return 0;
        return getArea() * costPerSquareMeter;
    }

    @Override
    public boolean needsRenovation(int currentYear, int renovationAge) {
        return renovationAge > 0 && currentYear >= getYearBuilt() &&
                currentYear - getYearBuilt() >= renovationAge;
    }

    public int calculateEmployees(int employeesPerOffice) {
        if (employeesPerOffice <= 0) return 0;
        return offices * employeesPerOffice;
    }

    public int calculateDailyVisitors(int visitorsPerOffice) {
        if (visitorsPerOffice <= 0) return 0;
        return offices * visitorsPerOffice;
    }
}
