package QuanLyDoiTac;

import QuanLyChungTu.SystemLogger;
import QuanLyChungTu.TextFileUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class QuanLyNhanVienKho {
    private final Map<String, NhanVienKho> employees = new LinkedHashMap<>();
    private String filePath;

    public boolean addEmployee(NhanVienKho employee) {
        if (employee == null || employee.getEmployeeId() == null || employee.getEmployeeId().isBlank()
                || !validSalary(employee)
                || employees.containsKey(employee.getEmployeeId())) {
            return false;
        }
        employees.put(employee.getEmployeeId(), employee);
        saveIfConfigured();
        SystemLogger.ghiLog("Thêm nhân viên kho " + employee.getEmployeeId());
        return true;
    }

    public NhanVienKho getEmployee(String employeeId) {
        return employees.get(employeeId);
    }

    public boolean updateEmployee(NhanVienKho employee) {
        if (employee == null || employee.getEmployeeId() == null
                || !validSalary(employee)
                || !employees.containsKey(employee.getEmployeeId())) {
            return false;
        }
        employees.put(employee.getEmployeeId(), employee);
        saveIfConfigured();
        SystemLogger.ghiLog("Cập nhật nhân viên kho " + employee.getEmployeeId());
        return true;
    }

    public boolean deleteEmployee(String employeeId) {
        if (employees.remove(employeeId) == null) {
            return false;
        }
        saveIfConfigured();
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

    public void docFile(String path) throws IOException {
        Map<String, NhanVienKho> loaded = new LinkedHashMap<>();
        List<List<String>> rows = TextFileUtil.readRows(path);
        for (int i = 0; i < rows.size(); i++) {
            try {
                List<String> row = rows.get(i);
                TextFileUtil.requireFieldCount(row, 6);
                String id = row.get(0);
                double salary = Double.parseDouble(row.get(4));
                double commission = Double.parseDouble(row.get(5));
                if (id.isBlank() || loaded.containsKey(id) || salary < 0 || commission < 0) {
                    throw new IllegalArgumentException("Mã nhân viên trùng/rỗng hoặc lương âm");
                }
                loaded.put(id, new NhanVienKho(id, row.get(1), row.get(2), row.get(3), salary, commission));
            } catch (RuntimeException e) {
                throw TextFileUtil.invalidRow("nhân viên", i + 1, e);
            }
        }
        employees.clear();
        employees.putAll(loaded);
        filePath = path;
    }

    public void ghiFile(String path) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        for (NhanVienKho employee : employees.values()) {
            rows.add(java.util.Arrays.asList(employee.getEmployeeId(), employee.getName(), employee.getRole(),
                    employee.getPhone(), Double.toString(employee.getBaseSalary()),
                    Double.toString(employee.getCommission())));
        }
        TextFileUtil.writeRows(path, rows);
        filePath = path;
    }

    private void saveIfConfigured() {
        if (filePath != null) {
            try {
                ghiFile(filePath);
            } catch (IOException e) {
                throw new IllegalStateException("Không thể lưu dữ liệu nhân viên vào " + filePath, e);
            }
        }
    }

    private static boolean validSalary(NhanVienKho employee) {
        return !Double.isNaN(employee.getBaseSalary()) && !Double.isInfinite(employee.getBaseSalary())
                && !Double.isNaN(employee.getCommission()) && !Double.isInfinite(employee.getCommission())
                && employee.getBaseSalary() >= 0 && employee.getCommission() >= 0;
    }
}
