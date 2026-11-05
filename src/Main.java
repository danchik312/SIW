import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

public class Main {
    public static void main(String[] args) {
        BuildingService service = new BuildingService();


        ResidentialBuilding home1 = new ResidentialBuilding(
                "B001", "22 Green Avenue", 10, 2500, 2018, true, 80, true, 0);
        ResidentialBuilding home2 = new ResidentialBuilding(
                "B002", "8 Park Road", 7, 1800, 2015, true, 50, false, 0);
        CommercialBuilding office = new CommercialBuilding(
                "B003", "101 Business Street", 6, 3200.5, 2012, true,
                "Office Center", 45, 32);
        SchoolBuilding school = new SchoolBuilding(
                "B004", "5 Education Road", 4, 2800, 2005, true, 24, "High School");


        System.out.println("\n1. ADD BUILDINGS (List + Map)");
        service.addBuilding(home1);
        service.addBuilding(home2);
        service.addBuilding(office);
        service.addBuilding(school);
        for (Building building : service.getBuildings()) building.displayInfo();

        System.out.println("\n2. SEARCH AND FILTER (Map)");
        Building found = service.findBuilding("B002");
        System.out.println("Found B002: " + found.getAddress());
        Building sameId = new ResidentialBuilding(
                "B001", "Another Address", 2, 100, 2020, true, 10, false, 0);
        System.out.println("Two buildings with same ID are equal: " + home1.equals(sameId));
        System.out.println("Same ID gives same hashCode: " +
                (home1.hashCode() == sameId.hashCode()));
        System.out.println("Unknown B999: " + service.findBuilding("B999"));
        System.out.println("Living buildings: " + service.getBuildingsByPurpose("Living").size());
        System.out.println("Buildings with area >= 2500: " + service.filterByArea(2500).size());

        System.out.println("\n3. SORTING (Comparable + 3 Comparators)");
        System.out.println("Natural order by ID:");
        printBuildings(service.sortBuildingsById());


        Comparator<Building> byAddress = (a, b) -> {
            int result = a.getAddress().compareToIgnoreCase(b.getAddress());
            if (result != 0) return result;
            return a.getId().compareTo(b.getId());
        };
        Comparator<Building> byArea = (a, b) -> {
            int result = Double.compare(b.getArea(), a.getArea());
            if (result != 0) return result;
            return a.getId().compareTo(b.getId());
        };
        Comparator<Building> byYearAndAddress = (a, b) -> {
            int result = Integer.compare(a.getYearBuilt(), b.getYearBuilt());
            if (result != 0) return result;
            return byAddress.compare(a, b);
        };

        System.out.println("Sort by address:");
        printBuildings(service.sortBuildings(byAddress));
        System.out.println("Sort by area (largest first):");
        printBuildings(service.sortBuildings(byArea));
        System.out.println("Sort by year, then address:");
        printBuildings(service.sortBuildings(byYearAndAddress));

        System.out.println("\n4. TENANTS AND REQUESTS (Queue)");
        Tenant alex = new Tenant("T001", "Alex Brown", "+996 555 111111",
                "AN1234567", "IT Company");
        Tenant maria = new Tenant("T002", "Maria Adams", "+996 555 222222",
                "AN7654321", "Hospital");
        service.addTenant(alex);
        service.addTenant(maria);
        Manager manager = new Manager("M001", "Property Manager");

        alex.submitRequest(service, new RentalRequest("R001", "T001", "B001", 15, 12, 500));
        maria.submitRequest(service, new RentalRequest("R002", "T002", "B001", 15, 6, 550));
        maria.submitRequest(service, new RentalRequest("R003", "T002", "B002", 7, 6, 650));
        System.out.println("Requests in queue: " + service.getQueueSize());

        RentalRequest current;
        while ((current = manager.processNext(service)) != null) {
            System.out.println(current.getId() + " -> " + current.getStatus() +
                    " (" + current.getMessage() + ")");
        }
        System.out.println("Queue after processing: " + service.getQueueSize());
        System.out.println("Empty queue returns: " + manager.processNext(service));

        System.out.println("\n5. CONTRACTS AND RELATIONSHIPS");
        RentalContract contract = service.getContractsForTenant("T001").get(0);
        contract.displayContract();
        System.out.println("Total rent for contract: " +
                new RentCalculator().calculateTotalRent(contract));
        System.out.println("Contracts in home1: " + home1.getContractsCount());
        System.out.println("Contracts for Alex: " + service.getContractsForTenant("T001").size());

        System.out.println("\n6. UPDATE (CRUD)");
        service.updateBuilding("B003", "103 Business Street", 7, 3300);
        service.updateTenant("T001", "Alex Brown", "+996 555 111111",
                "AN1234567", "New IT Company");
        service.updateContract(contract.getId(), 18, 575);
        System.out.println("Updated address: " + service.findBuilding("B003").getAddress());
        System.out.println("Updated workplace: " + service.findTenant("T001").getWorkplace());
        System.out.println("Updated monthly rent: " + service.findContract(contract.getId()).getMonthlyRent());

        System.out.println("\n7. STATISTICS");
        System.out.println("Buildings by type: " + service.countBuildingsByPurpose());
        System.out.println("Buildings: " + service.getBuildingsCount());
        System.out.println("Tenants: " + service.getTenantsCount());
        System.out.println("Contracts: " + service.getContractsCount());
        System.out.println("Available apartments: " + service.getAvailableApartments());
        System.out.printf("Average building area: %.2f%n", service.getAverageArea());
        System.out.printf("Monthly rent from contracts: %.2f%n", service.getTotalMonthlyRent());
        System.out.println("School renovation needed: " + school.needsRenovation(2026, 20));
        System.out.println("Residential capacity: " + home1.calculateResidents(3));
        System.out.println("Commercial employees: " + office.calculateEmployees(5));
        System.out.println("School students: " + school.calculateStudentCapacity(25));

        System.out.println("\n8. DELETE (CRUD) AND CHECK INDEXES");
        service.deleteContract(contract.getId());
        System.out.println("After deleting contract: home1 contracts = " + home1.getContractsCount());
        service.deleteTenant("T001");


        maria.submitRequest(service, new RentalRequest("R004", "T002", "B002", 9, 6, 600));
        service.deleteBuilding("B002");
        System.out.println("Waiting R004 after delete: " + service.findRequest("R004").getStatus());
        System.out.println("B002 after delete: " + service.findBuilding("B002"));
        System.out.println("Contracts left: " + service.getContractsCount());
        System.out.println("Maria still exists: " + (service.findTenant("T002") != null));

        System.out.println("\n9. ERROR HANDLING");
        try {
            service.addBuilding(home1);
        } catch (IllegalArgumentException e) {
            System.out.println("Duplicate ID: " + e.getMessage());
        }
        try {
            service.deleteBuilding("B999");
        } catch (NoSuchElementException e) {
            System.out.println("Missing building: " + e.getMessage());
        }
        System.out.println("\nPROGRAM FINISHED");
    }

    public static void printBuildings(List<Building> buildings) {
        for (Building building : buildings) {
            System.out.print(building.getId() + " (" + building.getAddress() + ")  ");
        }
        System.out.println();
    }
}
