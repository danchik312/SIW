public class SchoolBuilding extends Building implements Maintenance {
    private int classrooms;
    private String schoolType;

    public SchoolBuilding(String id, String address, int floors, double area,
                          int yearBuilt, boolean occupied, int classrooms, String schoolType) {
        super(id, address, floors, area, yearBuilt, occupied);
        if (classrooms <= 0 || schoolType == null || schoolType.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid school information");
        }
        this.classrooms = classrooms;
        this.schoolType = schoolType;
    }

    public int getClassrooms() { return classrooms; }
    public String getSchoolType() { return schoolType; }

    @Override
    public String getBuildingPurpose() { return "Education"; }

    @Override
    public double calculateMonthlyOperatingCost(double costPerSquareMeter,
                                                double costPerClassroom) {
        if (costPerSquareMeter < 0 || costPerClassroom < 0) return 0;
        return getArea() * costPerSquareMeter + classrooms * costPerClassroom;
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

    public int calculateStudentCapacity(int studentsPerClassroom) {
        if (studentsPerClassroom <= 0) return 0;
        return classrooms * studentsPerClassroom;
    }

    public int calculateTeachersNeeded(int classroomsPerTeacher) {
        if (classroomsPerTeacher <= 0) return 0;
        return (classrooms + classroomsPerTeacher - 1) / classroomsPerTeacher;
    }

    public int calculateClassesPerFloor() {
        return classrooms / getFloors();
    }
}
