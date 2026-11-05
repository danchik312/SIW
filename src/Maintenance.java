public interface Maintenance {
    double calculateMaintenanceCost(double costPerSquareMeter);
    boolean needsRenovation(int currentYear, int renovationAge);
}
