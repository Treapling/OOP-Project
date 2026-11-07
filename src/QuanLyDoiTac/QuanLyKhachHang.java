package QuanLyDoiTac;

import QuanLyChungTu.SystemLogger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class QuanLyKhachHang {
    private final Map<String, KhachHang> customers = new LinkedHashMap<>();

    public boolean addCustomer(KhachHang customer) {
        if (customer == null || customer.getCustomerId() == null || customer.getCustomerId().isBlank()) {
            return false;
        }
        customers.put(customer.getCustomerId(), customer);
        SystemLogger.ghiLog("Thêm khách hàng " + customer.getCustomerId());
        return true;
    }

    public KhachHang getCustomer(String customerId) {
        return customers.get(customerId);
    }

    public boolean updateCustomer(KhachHang customer) {
        if (customer == null || customer.getCustomerId() == null) {
            return false;
        }
        customers.put(customer.getCustomerId(), customer);
        SystemLogger.ghiLog("Cập nhật khách hàng " + customer.getCustomerId());
        return true;
    }

    public boolean deleteCustomer(String customerId) {
        if (customers.remove(customerId) == null) {
            return false;
        }
        SystemLogger.ghiLog("Xóa khách hàng " + customerId);
        return true;
    }

    public List<KhachHang> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    public void replaceAllCustomers(List<KhachHang> loadedCustomers) {
        customers.clear();
        for (KhachHang customer : loadedCustomers) {
            customers.put(customer.getCustomerId(), customer);
        }
    }

    public void addPoints(String customerId, int points) {
        KhachHang customer = customers.get(customerId);
        if (customer != null) {
            customer.addPoints(points);
            SystemLogger.ghiLog("Cộng điểm khách hàng " + customerId + ", số điểm " + points);
        }
    }
}
