package QuanLyDoiTac;

import QuanLyChungTu.SystemLogger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class QuanLyNhanVienKho {
    private final Map<String, NhanVienKho> employees = new LinkedHashMap<>();

    public boolean addEmployee(NhanVienKho employee) {
        if (employee == null || employee.getEmployeeId() == null || employee.getEmployeeId().isBlank()) {
            return false;
        }
        employees.put(employee.getEmployeeId(), employee);
        SystemLogger.ghiLog("Thêm nhân viên kho " + employee.getEmployeeId());
        return true;
    }

    public NhanVienKho getEmployee(String employeeId) {
        return employees.get(employeeId);
    }

    public boolean updateEmployee(NhanVienKho employee) {
        if (employee == null || employee.getEmployeeId() == null) {
            return false;
        }
        employees.put(employee.getEmployeeId(), employee);
        SystemLogger.ghiLog("Cập nhật nhân viên kho " + employee.getEmployeeId());
        return true;
    }

    public boolean deleteEmployee(String employeeId) {
        if (employees.remove(employeeId) == null) {
            return false;
        }
        SystemLogger.ghiLog("Xóa nhân viên kho " + employeeId);
        return true;
    }

    public List<NhanVienKho> getAllEmployees() {
        return new ArrayList<>(employees.values());
    }

    public double calculateSalary(String employeeId) {
        NhanVienKho employee = employees.get(employeeId);
        if (employee == null) {
            return 0;
        }
        return employee.calculateMonthlySalary();
    }
}
