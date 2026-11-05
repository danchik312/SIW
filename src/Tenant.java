public class Tenant {
    private final String id;
    private String name;
    private String phone;
    private String passport;
    private String workplace;

    public Tenant(String id, String name, String phone,
                  String passport, String workplace) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID cannot be empty");
        }
        this.id = id;
        updateInfo(name, phone, passport, workplace);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getPassport() { return passport; }
    public String getWorkplace() { return workplace; }

    public void updateInfo(String name, String phone, String passport, String workplace) {
        if (isEmpty(name) || isEmpty(phone) || isEmpty(passport) || isEmpty(workplace)) {
            throw new IllegalArgumentException("Tenant information cannot be empty");
        }
        this.name = name.trim();
        this.phone = phone.trim();
        this.passport = passport.trim();
        this.workplace = workplace.trim();
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }


    public void submitRequest(BuildingService service, RentalRequest request) {
        service.addRequest(this, request);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Tenant)) return false;
        Tenant other = (Tenant) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
