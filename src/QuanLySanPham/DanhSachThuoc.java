package QuanLySanPham;

import QuanLyChungTu.SystemLogger;
import QuanLyChungTu.TextFileUtil;
import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DanhSachThuoc {
    private final Map<String, SanPhamYTe> medicines = new LinkedHashMap<>();
    private final List<LoThuoc> batches = new ArrayList<>();
    private String filePath;
    private String batchFilePath;
    private static long generatedBatchNumber;

    public boolean addMedicine(SanPhamYTe medicine) {
        if (!isValidMedicine(medicine) || medicine.getMedicineId() == null
                || medicine.getMedicineId().isBlank() || medicines.containsKey(medicine.getMedicineId())) {
            return false;
        }
        medicines.put(medicine.getMedicineId(), medicine);
        if (hasBatchTracking()) {
            if (medicine.getExpiryDate() == null) {
                medicines.remove(medicine.getMedicineId());
                return false;
            }
            batches.add(new LoThuoc(medicine.getMedicineId(), "INIT-" + medicine.getMedicineId(),
                    medicine.getExpiryDate(), medicine.getQuantityInStock()));
            saveBatches();
        }
        saveIfConfigured();
        SystemLogger.ghiLog("Thêm thuốc " + medicine.getMedicineId());
        return true;
    }

    public SanPhamYTe getMedicine(String medicineId) {
        return medicines.get(medicineId);
    }

    public boolean updateMedicine(SanPhamYTe medicine) {
        if (!isValidMedicine(medicine) || medicine.getMedicineId() == null) {
            return false;
        }
        SanPhamYTe current = medicines.get(medicine.getMedicineId());
        if (current == null || (hasBatchTracking()
                && current.getQuantityInStock() != medicine.getQuantityInStock())) {
            return false;
        }
        medicines.put(medicine.getMedicineId(), medicine);
        if (hasBatchTracking()) {
            syncMedicineStock(medicine.getMedicineId());
        }
        saveIfConfigured();
        SystemLogger.ghiLog("Cập nhật thuốc " + medicine.getMedicineId());
        return true;
    }

    public boolean deleteMedicine(String medicineId) {
        if (hasBatchTracking() && batches.stream().anyMatch(batch -> batch.getMedicineId().equals(medicineId))) {
            return false;
        }
        if (medicines.remove(medicineId) == null) {
            return false;
        }
        saveIfConfigured();
        SystemLogger.ghiLog("Xóa thuốc " + medicineId);
        return true;
    }

    public List<SanPhamYTe> getAllMedicines() {
        return new ArrayList<>(medicines.values());
    }

    public List<SanPhamYTe> searchMedicines(String keyword, String categoryId, String manufacturer,
                                            Boolean prescriptionRequired, boolean lowStockOnly) {
        String normalizedKeyword = normalize(keyword);
        String normalizedManufacturer = normalize(manufacturer);
        return medicines.values().stream().filter(medicine -> {
            boolean keywordMatches = normalizedKeyword.isEmpty()
                    || normalize(medicine.getMedicineId()).contains(normalizedKeyword)
                    || normalize(medicine.getName()).contains(normalizedKeyword)
                    || normalize(medicine.getManufacturer()).contains(normalizedKeyword);
            boolean categoryMatches = categoryId == null || categoryId.isBlank()
                    || categoryId.equalsIgnoreCase(medicine.getCategoryId());
            boolean manufacturerMatches = normalizedManufacturer.isEmpty()
                    || normalize(medicine.getManufacturer()).contains(normalizedManufacturer);
            boolean prescriptionMatches = prescriptionRequired == null
                    || prescriptionRequired == medicine.isPrescriptionRequired();
            boolean stockMatches = !lowStockOnly || getAvailableStock(medicine.getMedicineId()) < medicine.getReorderLevel();
            return keywordMatches && categoryMatches && manufacturerMatches && prescriptionMatches && stockMatches;
        }).toList();
    }

    public boolean reduceStock(String medicineId, int quantity) {
        return consumeStock(medicineId, quantity) != null;
    }

    public Map<String, Integer> consumeStock(String medicineId, int quantity) {
        SanPhamYTe medicine = medicines.get(medicineId);
        if (medicine == null || quantity <= 0 || medicine.getQuantityInStock() < quantity) {
            return null;
        }
        Map<String, Integer> allocations = new LinkedHashMap<>();
        if (!hasBatchTracking()) {
            if (medicine.getExpiryDate() != null && medicine.getExpiryDate().isBefore(LocalDate.now())) {
                return null;
            }
            medicine.setQuantityInStock(medicine.getQuantityInStock() - quantity);
        } else {
            List<LoThuoc> available = getBatches(medicineId).stream()
                    .filter(batch -> batch.getQuantity() > 0 && !batch.isRecalled()
                            && !batch.getExpiryDate().isBefore(LocalDate.now()))
                    .sorted(Comparator.comparing(LoThuoc::getExpiryDate).thenComparing(LoThuoc::getBatchNumber))
                    .toList();
            if (available.stream().mapToInt(LoThuoc::getQuantity).sum() < quantity) {
                return null;
            }
            int remaining = quantity;
            for (LoThuoc batch : available) {
                int deduction = Math.min(remaining, batch.getQuantity());
                if (deduction > 0) {
                    batch.setQuantity(batch.getQuantity() - deduction);
                    allocations.put(batch.getBatchNumber(), deduction);
                    remaining -= deduction;
                }
                if (remaining == 0) {
                    break;
                }
            }
            syncMedicineStock(medicineId);
            saveBatches();
        }
        saveIfConfigured();
        SystemLogger.ghiLog("Xuất kho thuốc " + medicineId + ", số lượng " + quantity);
        return allocations;
    }

    public boolean increaseStock(String medicineId, int quantity) {
        SanPhamYTe medicine = medicines.get(medicineId);
        if (hasBatchTracking()) {
            if (medicine == null || medicine.getExpiryDate() == null) {
                return false;
            }
            return increaseStock(medicineId, "LOT-" + (++generatedBatchNumber),
                    medicine.getExpiryDate(), quantity);
        }
        if (medicine == null || quantity <= 0
                || quantity > Integer.MAX_VALUE - medicine.getQuantityInStock()) {
            return false;
        }
        medicine.setQuantityInStock(medicine.getQuantityInStock() + quantity);
        saveIfConfigured();
        SystemLogger.ghiLog("Nhập kho thuốc " + medicineId + ", số lượng " + quantity);
        return true;
    }

    public boolean increaseStock(String medicineId, String batchNumber, LocalDate expiryDate, int quantity) {
        SanPhamYTe medicine = medicines.get(medicineId);
        if (!hasBatchTracking()) {
            return increaseStock(medicineId, quantity);
        }
        if (medicine == null || batchNumber == null || batchNumber.isBlank() || expiryDate == null
                || expiryDate.isBefore(LocalDate.now()) || quantity <= 0
                || quantity > Integer.MAX_VALUE - medicine.getQuantityInStock()
                || batches.stream().anyMatch(batch -> batchNumber.equals(batch.getBatchNumber()))) {
            return false;
        }
        batches.add(new LoThuoc(medicineId, batchNumber, expiryDate, quantity));
        syncMedicineStock(medicineId);
        saveBatches();
        saveIfConfigured();
        SystemLogger.ghiLog("Nhập lô " + batchNumber + " của thuốc " + medicineId);
        return true;
    }

    public List<LoThuoc> getBatches(String medicineId) {
        return batches.stream().filter(batch -> batch.getMedicineId().equals(medicineId)).toList();
    }

    public List<LoThuoc> getAllBatches() {
        return new ArrayList<>(batches);
    }

    public int getAvailableStock(String medicineId) {
        SanPhamYTe medicine = medicines.get(medicineId);
        if (medicine == null) return 0;
        if (!hasBatchTracking()) {
            return medicine.getExpiryDate() != null && medicine.getExpiryDate().isBefore(LocalDate.now())
                    ? 0 : medicine.getQuantityInStock();
        }
        return getBatches(medicineId).stream()
                .filter(batch -> !batch.isRecalled() && !batch.getExpiryDate().isBefore(LocalDate.now()))
                .mapToInt(LoThuoc::getQuantity).sum();
    }

    public boolean recallBatch(String batchNumber) {
        for (LoThuoc batch : batches) {
            if (batch.getBatchNumber().equals(batchNumber) && !batch.isRecalled()) {
                batch.setRecalled(true);
                saveBatches();
                SystemLogger.ghiLog("Thu hồi lô " + batchNumber + " của thuốc " + batch.getMedicineId());
                return true;
            }
        }
        return false;
    }

    public boolean hasBatchTracking() {
        return batchFilePath != null;
    }

    public void taoLoBanDau(String path) throws IOException {
        batches.clear();
        for (SanPhamYTe medicine : medicines.values()) {
            LocalDate expiry = medicine.getExpiryDate();
            if (expiry == null) {
                throw new IOException("Thuốc " + medicine.getMedicineId() + " chưa có hạn dùng để tạo lô ban đầu");
            }
            batches.add(new LoThuoc(medicine.getMedicineId(), "INIT-" + medicine.getMedicineId(),
                    expiry, medicine.getQuantityInStock()));
        }
        ghiBatchFile(path);
    }

    public List<SanPhamYTe> getLowStockMedicines() {
        return medicines.values().stream()
                .filter(medicine -> getAvailableStock(medicine.getMedicineId()) < medicine.getReorderLevel())
                .toList();
    }

    public List<LoThuoc> filterExpiringBatches(int daysAhead) {
        LocalDate threshold = LocalDate.now().plusDays(daysAhead);
        return batches.stream().filter(batch -> batch.getQuantity() > 0
                && !batch.getExpiryDate().isAfter(threshold)).toList();
    }

    public List<SanPhamYTe> filterExpiringSoon(int daysAhead) {
        LocalDate threshold = LocalDate.now().plusDays(daysAhead);
        List<SanPhamYTe> result = new ArrayList<>();
        for (SanPhamYTe medicine : medicines.values()) {
            boolean expiring = hasBatchTracking()
                    ? getBatches(medicine.getMedicineId()).stream().anyMatch(batch -> batch.getQuantity() > 0
                        && !batch.getExpiryDate().isAfter(threshold))
                    : medicine.getExpiryDate() != null && !medicine.getExpiryDate().isAfter(threshold);
            if (expiring) {
                result.add(medicine);
            }
        }
        return result;
    }

    public void docBatchFile(String path) throws IOException {
        List<LoThuoc> loaded = new ArrayList<>();
        Map<String, Integer> stockTotals = new LinkedHashMap<>();
        List<List<String>> rows = TextFileUtil.readRows(path);
        for (int i = 0; i < rows.size(); i++) {
            try {
                List<String> row = rows.get(i);
                if (row.size() != 4 && row.size() != 5) {
                    throw new IllegalArgumentException("Cần 4 hoặc 5 trường lô thuốc");
                }
                int quantity = Integer.parseInt(row.get(3));
                if (!medicines.containsKey(row.get(0)) || row.get(1).isBlank()
                        || quantity < 0 || row.get(2).isBlank()
                        || loaded.stream().anyMatch(batch -> batch.getBatchNumber().equals(row.get(1)))) {
                    throw new IllegalArgumentException("Mã thuốc/lô không hợp lệ hoặc lô bị trùng");
                }
                LoThuoc batch = new LoThuoc(row.get(0), row.get(1), LocalDate.parse(row.get(2)), quantity);
                if (row.size() == 5) batch.setRecalled(parseBoolean(row.get(4)));
                loaded.add(batch);
                stockTotals.merge(row.get(0), quantity, Math::addExact);
            } catch (RuntimeException e) {
                throw TextFileUtil.invalidRow("lô thuốc", i + 1, e);
            }
        }
        for (SanPhamYTe medicine : medicines.values()) {
            if (stockTotals.getOrDefault(medicine.getMedicineId(), 0) != medicine.getQuantityInStock()) {
                throw new IOException("Tổng số lượng lô không khớp tồn kho của thuốc "
                        + medicine.getMedicineId() + " trong " + path);
            }
        }
        batches.clear();
        batches.addAll(loaded);
        batchFilePath = path;
        for (SanPhamYTe medicine : medicines.values()) {
            syncMedicineStock(medicine.getMedicineId());
        }
    }

    public void ghiBatchFile(String path) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        for (LoThuoc batch : batches) {
            rows.add(java.util.Arrays.asList(batch.getMedicineId(), batch.getBatchNumber(),
                    batch.getExpiryDate().toString(), Integer.toString(batch.getQuantity()),
                    Boolean.toString(batch.isRecalled())));
        }
        TextFileUtil.writeRows(path, rows);
        batchFilePath = path;
    }

    public void docFile(String path) throws IOException {
        Map<String, SanPhamYTe> loadedMedicines = new LinkedHashMap<>();
        List<List<String>> rows = TextFileUtil.readRows(path);
        for (int i = 0; i < rows.size(); i++) {
            try {
                List<String> fields = rows.get(i);
                if (fields.size() != 8 && fields.size() != 14) {
                    throw new IllegalArgumentException("Cần 8 trường cũ hoặc 14 trường thuốc");
                }
                String id = fields.get(0);
                if (id.isBlank() || loadedMedicines.containsKey(id)) {
                    throw new IllegalArgumentException("Mã thuốc rỗng hoặc bị trùng");
                }
                boolean prescriptionRequired = parseBoolean(fields.get(7));
                LocalDate expiryDate = fields.get(6).isEmpty() ? null : LocalDate.parse(fields.get(6));
                double price = Double.parseDouble(fields.get(4));
                int quantity = Integer.parseInt(fields.get(5));
                int reorderLevel = fields.size() == 14 ? Integer.parseInt(fields.get(8)) : 0;
                if (Double.isNaN(price) || Double.isInfinite(price) || price < 0
                        || quantity < 0 || reorderLevel < 0) {
                    throw new IllegalArgumentException("Giá, tồn kho hoặc định mức không hợp lệ");
                }
                SanPhamYTe medicine = prescriptionRequired
                        ? new ThuocKeDon(id, fields.get(1), fields.get(2), fields.get(3), price, quantity, expiryDate)
                        : new ThuocKhongKeDon(id, fields.get(1), fields.get(2), fields.get(3), price, quantity, expiryDate);
                medicine.setReorderLevel(reorderLevel);
                if (fields.size() == 14) {
                    medicine.setUnitConfiguration(fields.get(9), fields.get(10),
                            Integer.parseInt(fields.get(11)), fields.get(12), Integer.parseInt(fields.get(13)));
                }
                loadedMedicines.put(id, medicine);
            } catch (RuntimeException e) {
                throw TextFileUtil.invalidRow("thuốc", i + 1, e);
            }
        }
        medicines.clear();
        medicines.putAll(loadedMedicines);
        filePath = path;
    }

    public void ghiFile(String path) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        for (SanPhamYTe medicine : medicines.values()) {
            rows.add(java.util.Arrays.asList(medicine.getMedicineId(), medicine.getName(),
                    medicine.getCategoryId(), medicine.getManufacturer(), Double.toString(medicine.getUnitPrice()),
                    Integer.toString(medicine.getQuantityInStock()),
                    medicine.getExpiryDate() == null ? "" : medicine.getExpiryDate().toString(),
                    Boolean.toString(medicine.isPrescriptionRequired()), Integer.toString(medicine.getReorderLevel()),
                    medicine.getBaseUnitName(), medicine.getMiddleUnitName(),
                    Integer.toString(medicine.getMiddleToBaseFactor()), medicine.getOuterUnitName(),
                    Integer.toString(medicine.getOuterToMiddleFactor())));
        }
        TextFileUtil.writeRows(path, rows);
        filePath = path;
    }

    private void saveIfConfigured() {
        if (filePath != null) {
            try {
                ghiFile(filePath);
            } catch (IOException e) {
                throw new IllegalStateException("Không thể lưu dữ liệu thuốc vào " + filePath, e);
            }
        }
    }

    private void saveBatches() {
        if (batchFilePath != null) {
            try {
                ghiBatchFile(batchFilePath);
            } catch (IOException e) {
                throw new IllegalStateException("Không thể lưu dữ liệu lô thuốc vào " + batchFilePath, e);
            }
        }
    }

    private void syncMedicineStock(String medicineId) {
        SanPhamYTe medicine = medicines.get(medicineId);
        int quantity = 0;
        LocalDate earliestExpiry = null;
        for (LoThuoc batch : getBatches(medicineId)) {
            quantity = Math.addExact(quantity, batch.getQuantity());
            if (batch.getQuantity() > 0
                    && (earliestExpiry == null || batch.getExpiryDate().isBefore(earliestExpiry))) {
                earliestExpiry = batch.getExpiryDate();
            }
        }
        medicine.setQuantityInStock(quantity);
        medicine.setExpiryDate(earliestExpiry);
    }

    private static boolean isValidMedicine(SanPhamYTe medicine) {
        return medicine != null && !Double.isNaN(medicine.getUnitPrice())
                && !Double.isInfinite(medicine.getUnitPrice()) && medicine.getUnitPrice() >= 0
                && medicine.getQuantityInStock() >= 0 && medicine.getReorderLevel() >= 0;
    }

    private static boolean parseBoolean(String value) {
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;
        throw new IllegalArgumentException("Giá trị kê đơn không hợp lệ");
    }

    private static String normalize(String value) {
        if (value == null) return "";
        String decomposed = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").toLowerCase(java.util.Locale.ROOT);
        return decomposed.replace("đ", "d").trim();
    }
}
