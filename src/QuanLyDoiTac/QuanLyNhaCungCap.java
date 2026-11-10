package QuanLyDoiTac;

import QuanLyChungTu.SystemLogger;
import QuanLyChungTu.TextFileUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class QuanLyNhaCungCap {
    private final Map<String, NhaCungCap> suppliers = new LinkedHashMap<>();
    private String filePath;

    public boolean addSupplier(NhaCungCap supplier) {
        if (supplier == null || supplier.getSupplierId() == null || supplier.getSupplierId().isBlank()
                || suppliers.containsKey(supplier.getSupplierId())) {
            return false;
        }
        suppliers.put(supplier.getSupplierId(), supplier);
        saveIfConfigured();
        SystemLogger.ghiLog("Thêm nhà cung cấp " + supplier.getSupplierId());
        return true;
    }

    public NhaCungCap getSupplier(String supplierId) {
        return suppliers.get(supplierId);
    }

    public boolean updateSupplier(NhaCungCap supplier) {
        if (supplier == null || supplier.getSupplierId() == null
                || !suppliers.containsKey(supplier.getSupplierId())) {
            return false;
        }
        suppliers.put(supplier.getSupplierId(), supplier);
        saveIfConfigured();
        SystemLogger.ghiLog("Cập nhật nhà cung cấp " + supplier.getSupplierId());
        return true;
    }

    public boolean deleteSupplier(String supplierId) {
        if (suppliers.remove(supplierId) == null) {
            return false;
        }
        saveIfConfigured();
        SystemLogger.ghiLog("Xóa nhà cung cấp " + supplierId);
        return true;
    }

    public List<NhaCungCap> getAllSuppliers() {
        return new ArrayList<>(suppliers.values());
    }

    public void docFile(String path) throws IOException {
        Map<String, NhaCungCap> loaded = new LinkedHashMap<>();
        List<List<String>> rows = TextFileUtil.readRows(path);
        for (int i = 0; i < rows.size(); i++) {
            try {
                List<String> row = rows.get(i);
                TextFileUtil.requireFieldCount(row, 4);
                String id = row.get(0);
                if (id.isBlank() || loaded.containsKey(id)) {
                    throw new IllegalArgumentException("Mã nhà cung cấp rỗng hoặc bị trùng");
                }
                loaded.put(id, new NhaCungCap(id, row.get(1), row.get(2), row.get(3)));
            } catch (RuntimeException e) {
                throw TextFileUtil.invalidRow("nhà cung cấp", i + 1, e);
            }
        }
        suppliers.clear();
        suppliers.putAll(loaded);
        filePath = path;
    }

    public void ghiFile(String path) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        for (NhaCungCap supplier : suppliers.values()) {
            rows.add(java.util.Arrays.asList(supplier.getSupplierId(), supplier.getName(),
                    supplier.getPhone(), supplier.getAddress()));
        }
        TextFileUtil.writeRows(path, rows);
        filePath = path;
    }

    private void saveIfConfigured() {
        if (filePath != null) {
            try {
                ghiFile(filePath);
            } catch (IOException e) {
                throw new IllegalStateException("Không thể lưu dữ liệu nhà cung cấp vào " + filePath, e);
            }
        }
    }
}
