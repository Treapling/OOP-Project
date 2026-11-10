package QuanLyPhanLoai;

import QuanLyChungTu.SystemLogger;
import QuanLyChungTu.TextFileUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuanLyLoaiThuoc {
    private final Map<String, LoaiThuoc> categories = new LinkedHashMap<>();
    private String filePath;

    public boolean addCategory(LoaiThuoc category) {
        if (category == null || category.getCategoryId() == null || category.getCategoryId().isBlank()
                || categories.containsKey(category.getCategoryId())) {
            return false;
        }
        categories.put(category.getCategoryId(), category);
        saveIfConfigured();
        SystemLogger.ghiLog("Thêm loại thuốc " + category.getCategoryId());
        return true;
    }

    public LoaiThuoc getCategory(String categoryId) {
        return categories.get(categoryId);
    }

    public boolean updateCategory(String categoryId, String newName, String newDescription) {
        LoaiThuoc category = categories.get(categoryId);
        if (category == null) {
            return false;
        }
        if (newName != null && !newName.isBlank()) {
            category.setName(newName);
        }
        if (newDescription != null) {
            category.setDescription(newDescription);
        }
        saveIfConfigured();
        SystemLogger.ghiLog("Cập nhật loại thuốc " + categoryId);
        return true;
    }

    public boolean deleteCategory(String categoryId) {
        if (categories.remove(categoryId) == null) {
            return false;
        }
        saveIfConfigured();
        SystemLogger.ghiLog("Xóa loại thuốc " + categoryId);
        return true;
    }

    public List<LoaiThuoc> getAllCategories() {
        return new ArrayList<>(categories.values());
    }

    public void docFile(String path) throws IOException {
        Map<String, LoaiThuoc> loaded = new LinkedHashMap<>();
        List<List<String>> rows = TextFileUtil.readRows(path);
        for (int i = 0; i < rows.size(); i++) {
            try {
                List<String> row = rows.get(i);
                TextFileUtil.requireFieldCount(row, 3);
                String id = row.get(0);
                if (id.isBlank() || loaded.containsKey(id)) {
                    throw new IllegalArgumentException("Mã loại rỗng hoặc bị trùng");
                }
                loaded.put(id, new LoaiThuoc(id, row.get(1), row.get(2)));
            } catch (RuntimeException e) {
                throw TextFileUtil.invalidRow("loại thuốc", i + 1, e);
            }
        }
        categories.clear();
        categories.putAll(loaded);
        filePath = path;
    }

    public void ghiFile(String path) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        for (LoaiThuoc category : categories.values()) {
            rows.add(java.util.Arrays.asList(category.getCategoryId(), category.getName(), category.getDescription()));
        }
        TextFileUtil.writeRows(path, rows);
        filePath = path;
    }

    private void saveIfConfigured() {
        if (filePath != null) {
            try {
                ghiFile(filePath);
            } catch (IOException e) {
                throw new IllegalStateException("Không thể lưu dữ liệu loại thuốc vào " + filePath, e);
            }
        }
    }
}
