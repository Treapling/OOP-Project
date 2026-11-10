package QuanLyDoiTac;

import QuanLyChungTu.SystemLogger;
import QuanLyChungTu.TextFileUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class QuanLyKhachHang {
    private final Map<String, KhachHang> customers = new LinkedHashMap<>();
    private String filePath;

    public boolean addCustomer(KhachHang customer) {
        if (customer == null || customer.getCustomerId() == null || customer.getCustomerId().isBlank()
                || customer.getLoyaltyPoints() < 0
                || customers.containsKey(customer.getCustomerId())) {
            return false;
        }
        customers.put(customer.getCustomerId(), customer);
        saveIfConfigured();
        SystemLogger.ghiLog("Thêm khách hàng " + customer.getCustomerId());
        return true;
    }

    public KhachHang getCustomer(String customerId) {
        return customers.get(customerId);
    }

    public boolean updateCustomer(KhachHang customer) {
        if (customer == null || customer.getCustomerId() == null
                || customer.getLoyaltyPoints() < 0
                || !customers.containsKey(customer.getCustomerId())) {
            return false;
        }
        customers.put(customer.getCustomerId(), customer);
        saveIfConfigured();
        SystemLogger.ghiLog("Cập nhật khách hàng " + customer.getCustomerId());
        return true;
    }

    public boolean deleteCustomer(String customerId) {
        if (customers.remove(customerId) == null) {
            return false;
        }
        saveIfConfigured();
        SystemLogger.ghiLog("Xóa khách hàng " + customerId);
        return true;
    }

    public List<KhachHang> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    public void addPoints(String customerId, int points) {
        KhachHang customer = customers.get(customerId);
        if (customer == null || points <= 0) {
            return;
        }
        customer.addPoints(points);
        saveIfConfigured();
        SystemLogger.ghiLog("Cộng điểm khách hàng " + customerId + ", số điểm " + points);
    }

    public void docFile(String path) throws IOException {
        Map<String, KhachHang> loaded = new LinkedHashMap<>();
        List<List<String>> rows = TextFileUtil.readRows(path);
        for (int i = 0; i < rows.size(); i++) {
            try {
                List<String> row = rows.get(i);
                TextFileUtil.requireFieldCount(row, 5);
                String id = row.get(0);
                int points = Integer.parseInt(row.get(3));
                String expectedTier = points >= 1000 ? "Gold" : points >= 500 ? "Silver" : "Bronze";
                if (id.isBlank() || loaded.containsKey(id) || points < 0 || !expectedTier.equals(row.get(4))) {
                    throw new IllegalArgumentException("Mã khách trùng/rỗng hoặc điểm âm");
                }
                KhachHang customer = new KhachHang(id, row.get(1), row.get(2), points);
                customer.setMemberTier(row.get(4));
                loaded.put(id, customer);
            } catch (RuntimeException e) {
                throw TextFileUtil.invalidRow("khách hàng", i + 1, e);
            }
        }
        customers.clear();
        customers.putAll(loaded);
        filePath = path;
    }

    public void ghiFile(String path) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        for (KhachHang customer : customers.values()) {
            rows.add(java.util.Arrays.asList(customer.getCustomerId(), customer.getName(), customer.getPhone(),
                    Integer.toString(customer.getLoyaltyPoints()), customer.getMemberTier()));
        }
        TextFileUtil.writeRows(path, rows);
        filePath = path;
    }

    private void saveIfConfigured() {
        if (filePath != null) {
            try {
                ghiFile(filePath);
            } catch (IOException e) {
                throw new IllegalStateException("Không thể lưu dữ liệu khách hàng vào " + filePath, e);
            }
        }
    }
}
