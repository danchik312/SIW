import java.util.*;

public class BuildingService {

    private List<Building> buildings = new ArrayList<Building>();
    private Map<String, Building> buildingById = new HashMap<String, Building>();


    private Map<String, List<Building>> buildingsByPurpose =
            new HashMap<String, List<Building>>();

    private Map<String, Tenant> tenants = new HashMap<String, Tenant>();
    private Map<String, RentalContract> contracts = new HashMap<String, RentalContract>();


    private Map<String, List<RentalContract>> contractsByTenant =
            new HashMap<String, List<RentalContract>>();


    private Queue<RentalRequest> requests = new ArrayDeque<RentalRequest>();
    private Map<String, RentalRequest> requestsById = new HashMap<String, RentalRequest>();
    private int nextContractId = 1;



    public void addBuilding(Building building) {
        if (building == null) throw new IllegalArgumentException("Building cannot be null");
        if (buildingById.containsKey(building.getId())) {
            throw new IllegalArgumentException("Building ID already exists");
        }

        buildings.add(building);
        buildingById.put(building.getId(), building);

        String type = building.getBuildingPurpose();
        if (!buildingsByPurpose.containsKey(type)) {
            buildingsByPurpose.put(type, new ArrayList<Building>());
        }
        buildingsByPurpose.get(type).add(building);
    }


    public Building findBuilding(String id) {
        return buildingById.get(id);
    }

    public List<Building> getBuildings() {
        return new ArrayList<Building>(buildings);
    }

    public List<Building> getBuildingsByPurpose(String purpose) {
        List<Building> found = buildingsByPurpose.get(purpose);
        if (found == null) return new ArrayList<Building>();
        return new ArrayList<Building>(found);
    }

    public List<Building> filterByArea(double minimumArea) {
        List<Building> result = new ArrayList<Building>();
        for (Building building : buildings) {
            if (building.getArea() >= minimumArea) result.add(building);
        }
        return result;
    }

    public void updateBuilding(String id, String address, int floors, double area) {
        Building building = findBuilding(id);
        if (building == null) throw new NoSuchElementException("Building not found: " + id);

        building.updateInfo(address, floors, area);
    }

    public void deleteBuilding(String id) {
        Building building = findBuilding(id);
        if (building == null) throw new NoSuchElementException("Building not found: " + id);

        cancelRequestsForBuilding(id);
        if (building instanceof ResidentialBuilding) {
            ResidentialBuilding home = (ResidentialBuilding) building;
            for (RentalContract contract : home.getRentalContracts()) {
                deleteContract(contract.getId());
            }
        }

        buildings.remove(building);
        buildingById.remove(id);
        List<Building> group = buildingsByPurpose.get(building.getBuildingPurpose());
        group.remove(building);
        if (group.isEmpty()) buildingsByPurpose.remove(building.getBuildingPurpose());
    }


    public List<Building> sortBuildings(Comparator<Building> comparator) {
        List<Building> result = getBuildings();
        Collections.sort(result, comparator);
        return result;
    }

    public List<Building> sortBuildingsById() {
        List<Building> result = getBuildings();
        Collections.sort(result); // Comparable in Building
        return result;
    }



    public void addTenant(Tenant tenant) {
        if (tenant == null) throw new IllegalArgumentException("Tenant cannot be null");
        if (tenants.containsKey(tenant.getId())) {
            throw new IllegalArgumentException("Tenant ID already exists");
        }
        tenants.put(tenant.getId(), tenant);
    }

    public Tenant findTenant(String id) {
        return tenants.get(id);
    }

    public void updateTenant(String id, String name, String phone,
                             String passport, String workplace) {
        Tenant tenant = findTenant(id);
        if (tenant == null) throw new NoSuchElementException("Tenant not found: " + id);
        tenant.updateInfo(name, phone, passport, workplace);
    }

    public void deleteTenant(String id) {
        Tenant tenant = findTenant(id);
        if (tenant == null) throw new NoSuchElementException("Tenant not found: " + id);

        // Delete related contracts before deleting the tenant.
        List<RentalContract> list = getContractsForTenant(id);
        for (RentalContract contract : list) {
            deleteContract(contract.getId());
        }
        cancelRequestsForTenant(id);
        tenants.remove(id);
    }

    public void addRequest(Tenant tenant, RentalRequest request) {
        if (tenant == null || request == null ||
                findTenant(tenant.getId()) != tenant ||
                !tenant.getId().equals(request.getTenantId())) {
            throw new IllegalArgumentException("Only a registered tenant can send their own request");
        }
        if (requestsById.containsKey(request.getId())) {
            throw new IllegalArgumentException("Request ID already exists");
        }
        if (!(findBuilding(request.getBuildingId()) instanceof ResidentialBuilding)) {
            throw new IllegalArgumentException("Residential building not found");
        }
        requests.offer(request);
        requestsById.put(request.getId(), request);
    }

    public int getQueueSize() {
        return requests.size();
    }

    public RentalRequest findRequest(String id) {
        return requestsById.get(id);
    }

    public RentalRequest processNextRequest(Manager manager) {
        if (manager == null) throw new IllegalArgumentException("Manager is required");
        RentalRequest request = requests.poll(); // FIFO, null if queue is empty.
        if (request == null) return null;

        try {
            Tenant tenant = findTenant(request.getTenantId());
            ResidentialBuilding home = (ResidentialBuilding) findBuilding(request.getBuildingId());
            if (tenant == null || home == null) {
                throw new IllegalArgumentException("Tenant or building not found");
            }

            String contractId = "C" + nextContractId;
            // Building creates and owns the contract (composition).
            RentalContract contract = home.createRentalContract(contractId, tenant,
                    request.getApartmentNumber(), request.getMonths(), request.getMonthlyRent());
            nextContractId++;

            contracts.put(contractId, contract);
            if (!contractsByTenant.containsKey(tenant.getId())) {
                contractsByTenant.put(tenant.getId(), new ArrayList<RentalContract>());
            }
            contractsByTenant.get(tenant.getId()).add(contract);
            request.setStatus(RentalRequest.Status.APPROVED,
                    "Approved by " + manager.getName());
        } catch (IllegalArgumentException e) {
            request.setStatus(RentalRequest.Status.REJECTED, e.getMessage());
        } catch (IllegalStateException e) {
            request.setStatus(RentalRequest.Status.REJECTED, e.getMessage());
        }
        return request;
    }



    public RentalContract findContract(String id) {
        return contracts.get(id);
    }

    public List<RentalContract> getContractsForTenant(String tenantId) {
        List<RentalContract> found = contractsByTenant.get(tenantId);
        if (found == null) return new ArrayList<RentalContract>();
        return new ArrayList<RentalContract>(found);
    }

    public void updateContract(String id, int months, double monthlyRent) {
        RentalContract contract = findContract(id);
        if (contract == null) throw new NoSuchElementException("Contract not found: " + id);
        contract.updateTerms(months, monthlyRent);
    }

    public void deleteContract(String id) {
        RentalContract contract = findContract(id);
        if (contract == null) throw new NoSuchElementException("Contract not found: " + id);

        ResidentialBuilding home = (ResidentialBuilding) findBuilding(contract.getBuildingId());
        home.removeRentalContract(contract);
        contracts.remove(id);

        String tenantId = contract.getTenant().getId();
        List<RentalContract> list = contractsByTenant.get(tenantId);
        list.remove(contract);
        if (list.isEmpty()) contractsByTenant.remove(tenantId);
    }



    public Map<String, Integer> countBuildingsByPurpose() {
        Map<String, Integer> result = new TreeMap<String, Integer>();
        for (String purpose : buildingsByPurpose.keySet()) {
            result.put(purpose, buildingsByPurpose.get(purpose).size());
        }
        return result;
    }

    public int getBuildingsCount() { return buildings.size(); }
    public int getTenantsCount() { return tenants.size(); }
    public int getContractsCount() { return contracts.size(); }

    public double getTotalMonthlyRent() {
        double total = 0;
        for (RentalContract contract : contracts.values()) {
            total += contract.getMonthlyRent();
        }
        return total;
    }

    public double getAverageArea() {
        if (buildings.isEmpty()) return 0;
        double total = 0;
        for (Building building : buildings) total += building.getArea();
        return total / buildings.size();
    }

    public int getAvailableApartments() {
        int total = 0;
        for (Building building : buildings) {
            if (building instanceof ResidentialBuilding) {
                total += ((ResidentialBuilding) building).getAvailableUnits();
            }
        }
        return total;
    }


    private void cancelRequestsForBuilding(String id) {
        Iterator<RentalRequest> iterator = requests.iterator();
        while (iterator.hasNext()) {
            RentalRequest request = iterator.next();
            if (request.getBuildingId().equals(id)) {
                request.setStatus(RentalRequest.Status.CANCELLED, "Building deleted");
                iterator.remove();
            }
        }
    }

    private void cancelRequestsForTenant(String id) {
        Iterator<RentalRequest> iterator = requests.iterator();
        while (iterator.hasNext()) {
            RentalRequest request = iterator.next();
            if (request.getTenantId().equals(id)) {
                request.setStatus(RentalRequest.Status.CANCELLED, "Tenant deleted");
                iterator.remove();
            }
        }
    }
}
