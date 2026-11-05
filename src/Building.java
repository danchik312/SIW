public abstract class Building implements Comparable<Building> {
    private final String id;
    private String address;
    private int floors;
    private double area;
    private int yearBuilt;
    private boolean occupied;

    public Building(String id, String address, int floors, double area,
                    int yearBuilt, boolean occupied) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Building ID cannot be empty");
        }
        this.id = id;
        updateInfo(address, floors, area);
        if (yearBuilt < 0) {
            throw new IllegalArgumentException("Invalid year");
        }
        this.yearBuilt = yearBuilt;
        this.occupied = occupied;
    }

    public String getId() { return id; }
    public String getAddress() { return address; }
    public int getFloors() { return floors; }
    public double getArea() { return area; }
    public int getYearBuilt() { return yearBuilt; }
    public boolean isOccupied() { return occupied; }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public void updateInfo(String address, int floors, double area) {
        if (address == null || address.trim().isEmpty() || floors <= 0 ||
                Double.isNaN(area) || Double.isInfinite(area) || area < 0) {
            throw new IllegalArgumentException("Invalid building data");
        }
        this.address = address.trim();
        this.floors = floors;
        this.area = area;
    }

    public void displayInfo() {
        System.out.println(id + " | " + getBuildingPurpose() + " | " + address +
                " | Floors: " + floors + " | Area: " + area);
    }

    public abstract String getBuildingPurpose();
    public abstract double calculateMonthlyOperatingCost(double costPerSquareMeter,
                                                         double costPerUnit);


    @Override
    public final int compareTo(Building other) {
        return id.compareTo(other.id);
    }


    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Building)) return false;
        Building other = (Building) obj;
        return id.equals(other.id);
    }

    @Override
    public final int hashCode() {
        return id.hashCode();
    }
}
