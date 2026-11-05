package QuanLyDoiTac;

public class NhanVienKho extends Nguoi {
    private String role;
    private double baseSalary;
    private double commission;

    public NhanVienKho() {
    }

    public NhanVienKho(String employeeId, String name, String role, String phone, double baseSalary, double commission) {
        super(employeeId, name, phone);
        this.role = role;
        this.baseSalary = baseSalary;
        this.commission = commission;
    }

    public String getEmployeeId() { return getId(); }
    public void setEmployeeId(String employeeId) { setId(employeeId); }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public double getBaseSalary() { return baseSalary; }
    public void setBaseSalary(double baseSalary) { this.baseSalary = baseSalary; }
    public double getCommission() { return commission; }
    public void setCommission(double commission) { this.commission = commission; }

    public double calculateMonthlySalary() {
        return baseSalary + commission;
    }

    @Override
    public String toString() {
        return "NhanVienKho{" +
                "employeeId='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", role='" + role + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", baseSalary=" + baseSalary +
                ", commission=" + commission +
                '}';
    }
}
