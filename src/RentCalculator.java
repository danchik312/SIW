public class RentCalculator {
    public double calculateTotalRent(RentalContract contract) {
        if (contract == null) return 0;
        return contract.getMonthlyRent() * contract.getMonths();
    }
}
