package QuanLyDoiTac;

public class TaiKhoan {
    private String username;
    private String password;
    private String role;
    private String employeeId;
    private boolean active;

    public TaiKhoan() {
        this.active = true;
    }

    public TaiKhoan(String username, String password, String role, String employeeId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeId = employeeId;
        this.active = true;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean checkPassword(String inputPassword) {
        return password.equals(inputPassword);
    }

    @Override
    public String toString() {
        return "TaiKhoan{" +
                "username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", active=" + active +
                '}';
    }
}
