package QuanLySanPham;

import QuanLyChungTu.SystemLogger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DanhSachThuoc {
    private final Map<String, SanPhamYTe> medicines = new LinkedHashMap<>();

    public boolean addMedicine(SanPhamYTe medicine) {
        if (medicine == null || medicine.getMedicineId() == null || medicine.getMedicineId().isBlank()) {
            return false;
        }
        medicines.put(medicine.getMedicineId(), medicine);
        SystemLogger.ghiLog("Thêm thuốc " + medicine.getMedicineId());
        return true;
    }

    public SanPhamYTe getMedicine(String medicineId) {
        return medicines.get(medicineId);
    }

    public boolean updateMedicine(SanPhamYTe medicine) {
        if (medicine == null || medicine.getMedicineId() == null) {
            return false;
        }
        medicines.put(medicine.getMedicineId(), medicine);
        SystemLogger.ghiLog("Cập nhật thuốc " + medicine.getMedicineId());
        return true;
    }

    public boolean deleteMedicine(String medicineId) {
        if (medicines.remove(medicineId) == null) {
            return false;
        }
        SystemLogger.ghiLog("Xóa thuốc " + medicineId);
        return true;
    }

    public List<SanPhamYTe> getAllMedicines() {
        return new ArrayList<>(medicines.values());
    }

    public boolean reduceStock(String medicineId, int quantity) {
        SanPhamYTe medicine = medicines.get(medicineId);
        if (medicine == null || quantity <= 0 || medicine.getQuantityInStock() < quantity) {
            return false;
        }
        medicine.setQuantityInStock(medicine.getQuantityInStock() - quantity);
        SystemLogger.ghiLog("Xuất kho thuốc " + medicineId + ", số lượng " + quantity);
        return true;
    }

    public boolean increaseStock(String medicineId, int quantity) {
        SanPhamYTe medicine = medicines.get(medicineId);
        if (medicine == null || quantity <= 0) {
            return false;
        }
        medicine.setQuantityInStock(medicine.getQuantityInStock() + quantity);
        SystemLogger.ghiLog("Nhập kho thuốc " + medicineId + ", số lượng " + quantity);
        return true;
    }

    public List<SanPhamYTe> filterExpiringSoon(int daysAhead) {
        LocalDate threshold = LocalDate.now().plusDays(daysAhead);
        return filterExpiringBy(threshold);
    }

    public List<SanPhamYTe> filterExpiringBy(LocalDate deadline) {
        if (deadline == null) {
            throw new IllegalArgumentException("Ngày giới hạn không được để trống.");
        }
        List<SanPhamYTe> result = new ArrayList<>();
        for (SanPhamYTe medicine : medicines.values()) {
            if (medicine.getExpiryDate() != null && !medicine.getExpiryDate().isAfter(deadline)) {
                result.add(medicine);
            }
        }
        return result;
    }

    public void docFile(String path) throws IOException {
        Map<String, SanPhamYTe> loadedMedicines = new LinkedHashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(path), StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                try {
                    List<String> fields = parseCsvLine(line);
                    if (fields.size() != 8) {
                        throw new IllegalArgumentException("Cần đúng 8 cột");
                    }
                    String id = fields.get(0);
                    if (id.isBlank() || loadedMedicines.containsKey(id)) {
                        throw new IllegalArgumentException("Mã thuốc rỗng hoặc bị trùng");
                    }
                    boolean prescriptionRequired = parseBoolean(fields.get(7));
                    LocalDate expiryDate = fields.get(6).isEmpty() ? null : LocalDate.parse(fields.get(6));
                    SanPhamYTe medicine = prescriptionRequired
                            ? new ThuocKeDon(id, fields.get(1), fields.get(2), fields.get(3),
                                    Double.parseDouble(fields.get(4)), Integer.parseInt(fields.get(5)), expiryDate)
                            : new ThuocKhongKeDon(id, fields.get(1), fields.get(2), fields.get(3),
                                    Double.parseDouble(fields.get(4)), Integer.parseInt(fields.get(5)), expiryDate);
                    loadedMedicines.put(id, medicine);
                } catch (IllegalArgumentException | DateTimeParseException e) {
                    throw new IOException("Dữ liệu thuốc không hợp lệ tại dòng " + lineNumber, e);
                }
            }
        }
        medicines.clear();
        medicines.putAll(loadedMedicines);
    }

    public void ghiFile(String path) throws IOException {
        Path file = Path.of(path);
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (SanPhamYTe medicine : medicines.values()) {
                writer.write(csv(medicine.getMedicineId()));
                writer.write(',');
                writer.write(csv(medicine.getName()));
                writer.write(',');
                writer.write(csv(medicine.getCategoryId()));
                writer.write(',');
                writer.write(csv(medicine.getManufacturer()));
                writer.write(',');
                writer.write(Double.toString(medicine.getUnitPrice()));
                writer.write(',');
                writer.write(Integer.toString(medicine.getQuantityInStock()));
                writer.write(',');
                writer.write(medicine.getExpiryDate() == null ? "" : medicine.getExpiryDate().toString());
                writer.write(',');
                writer.write(Boolean.toString(medicine.isPrescriptionRequired()));
                writer.newLine();
            }
        }
    }

    private static String csv(String value) {
        String normalized = value == null ? "" : value;
        return "\"" + normalized.replace("\"", "\"\"") + "\"";
    }

    private static boolean parseBoolean(String value) {
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new IllegalArgumentException("Giá trị kê đơn không hợp lệ");
    }

    private static List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (quoted) {
                if (current == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    field.append('"');
                    i++;
                } else if (current == '"') {
                    quoted = false;
                } else {
                    field.append(current);
                }
            } else if (current == ',' ) {
                fields.add(field.toString());
                field.setLength(0);
            } else if (current == '"' && field.length() == 0) {
                quoted = true;
            } else {
                field.append(current);
            }
        }
        if (quoted) {
            throw new IllegalArgumentException("Dấu nháy CSV chưa đóng");
        }
        fields.add(field.toString());
        return fields;
    }
}
