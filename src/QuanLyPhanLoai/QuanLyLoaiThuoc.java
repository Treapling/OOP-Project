package QuanLyPhanLoai;

import QuanLyChungTu.SystemLogger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class QuanLyLoaiThuoc {
    private final Map<String, LoaiThuoc> categories = new LinkedHashMap<>();

    public boolean addCategory(LoaiThuoc category) {
        if (category == null || category.getCategoryId() == null || category.getCategoryId().isBlank()) {
            return false;
        }
        categories.put(category.getCategoryId(), category);
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
        SystemLogger.ghiLog("Cập nhật loại thuốc " + categoryId);
        return true;
    }

    public boolean deleteCategory(String categoryId) {
        if (categories.remove(categoryId) == null) {
            return false;
        }
        SystemLogger.ghiLog("Xóa loại thuốc " + categoryId);
        return true;
    }

    public List<LoaiThuoc> getAllCategories() {
        return new ArrayList<>(categories.values());
    }

    public void replaceAllCategories(List<LoaiThuoc> loadedCategories) {
        categories.clear();
        for (LoaiThuoc category : loadedCategories) {
            categories.put(category.getCategoryId(), category);
        }
    }
}
