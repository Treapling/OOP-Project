package QuanLyDoiTac;

import QuanLyChungTu.SystemLogger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class QuanLyNhaCungCap {
    private final Map<String, NhaCungCap> suppliers = new LinkedHashMap<>();

    public boolean addSupplier(NhaCungCap supplier) {
        if (supplier == null || supplier.getSupplierId() == null || supplier.getSupplierId().isBlank()) {
            return false;
        }
        suppliers.put(supplier.getSupplierId(), supplier);
        SystemLogger.ghiLog("Thêm nhà cung cấp " + supplier.getSupplierId());
        return true;
    }

    public NhaCungCap getSupplier(String supplierId) {
        return suppliers.get(supplierId);
    }

    public boolean updateSupplier(NhaCungCap supplier) {
        if (supplier == null || supplier.getSupplierId() == null) {
            return false;
        }
        suppliers.put(supplier.getSupplierId(), supplier);
        SystemLogger.ghiLog("Cập nhật nhà cung cấp " + supplier.getSupplierId());
        return true;
    }

    public boolean deleteSupplier(String supplierId) {
        if (suppliers.remove(supplierId) == null) {
            return false;
        }
        SystemLogger.ghiLog("Xóa nhà cung cấp " + supplierId);
        return true;
    }

    public List<NhaCungCap> getAllSuppliers() {
        return new ArrayList<>(suppliers.values());
    }

    public void replaceAllSuppliers(List<NhaCungCap> loadedSuppliers) {
        suppliers.clear();
        for (NhaCungCap supplier : loadedSuppliers) {
            suppliers.put(supplier.getSupplierId(), supplier);
        }
    }
}
