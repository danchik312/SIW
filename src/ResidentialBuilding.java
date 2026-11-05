import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.List;

public class ResidentialBuilding extends Building implements Rentable, Maintenance {
    private int apartments;
    private boolean hasParking;
    private int rentedApartments;


    private Set<RentalContract> rentalContracts = new LinkedHashSet<RentalContract>();

    public ResidentialBuilding(String id, String address, int floors, double area,
                               int yearBuilt, boolean occupied, int apartments,
                               boolean hasParking, int rentedApartments) {
        super(id, address, floors, area, yearBuilt, occupied);
        if (apartments <= 0 || rentedApartments < 0 || rentedApartments > apartments) {
            throw new IllegalArgumentException("Invalid number of apartments");
        }
        this.apartments = apartments;
        this.hasParking = hasParking;
        this.rentedApartments = rentedApartments;
    }

    public int getApartments() { return apartments; }
    public int getRentedApartments() { return rentedApartments; }
    public boolean hasParking() { return hasParking; }
    public int getContractsCount() { return rentalContracts.size(); }

    public List<RentalContract> getRentalContracts() {
        return new ArrayList<RentalContract>(rentalContracts);
    }


    RentalContract createRentalContract(String id, Tenant tenant, int apartment,
                                        int months, double monthlyRent) {
        if (apartment <= 0 || apartment > apartments) {
            throw new IllegalArgumentException("Invalid apartment number");
        }
        if (getAvailableUnits() == 0) {
            throw new IllegalStateException("No available apartments");
        }
        for (RentalContract contract : rentalContracts) {
            if (contract.getApartmentNumber() == apartment) {
                throw new IllegalStateException("Apartment is already rented");
            }
        }

        RentalContract contract = new RentalContract(id, tenant, getId(),
                                                     apartment, months, monthlyRent);
        rentalContracts.add(contract);
        rentedApartments++;
        return contract;
    }

    void removeRentalContract(RentalContract contract) {
        if (rentalContracts.remove(contract)) {
            rentedApartments--;
        }
    }

    @Override
    public String getBuildingPurpose() { return "Living"; }

    @Override
    public double calculateMonthlyOperatingCost(double costPerSquareMeter, double costPerUnit) {
        if (costPerSquareMeter < 0 || costPerUnit < 0) return 0;
        return getArea() * costPerSquareMeter + apartments * costPerUnit;
    }

    @Override
    public int getAvailableUnits() { return apartments - rentedApartments; }

    @Override
    public double calculatePotentialMonthlyRent(double rentPerUnit) {
        if (rentPerUnit < 0) return 0;
        return getAvailableUnits() * rentPerUnit;
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

    public int calculateResidents(int peoplePerApartment) {
        if (peoplePerApartment <= 0) return 0;
        return apartments * peoplePerApartment;
    }

    public int calculateParkingSpaces(int apartmentsPerParkingSpace) {
        if (!hasParking || apartmentsPerParkingSpace <= 0) return 0;
        return apartments / apartmentsPerParkingSpace;
    }
}
