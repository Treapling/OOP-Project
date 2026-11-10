package QuanLyChungTu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class QuanLyDonThuoc {
    private final Map<String, DonThuoc> prescriptions = new LinkedHashMap<>();
    private String filePath;

    public boolean addPrescription(DonThuoc prescription) {
        if (prescription == null || prescription.getPrescriptionId() == null || prescription.getPrescriptionId().isBlank()
                || prescriptions.containsKey(prescription.getPrescriptionId())) {
            return false;
        }
        prescriptions.put(prescription.getPrescriptionId(), prescription);
        saveIfConfigured();
        SystemLogger.ghiLog("Tạo toa thuốc " + prescription.getPrescriptionId());
        return true;
    }

    public DonThuoc getPrescription(String prescriptionId) {
        return prescriptions.get(prescriptionId);
    }

    public boolean isMedicineIncluded(String prescriptionId, String medicineId) {
        DonThuoc prescription = prescriptions.get(prescriptionId);
        if (prescription == null) {
            return false;
        }
        return prescription.getMedicineIds().contains(medicineId);
    }

    public List<DonThuoc> getAllPrescriptions() {
        return new ArrayList<>(prescriptions.values());
    }

    public void docFile(String path) throws java.io.IOException {
        Map<String, DonThuoc> loaded = new LinkedHashMap<>();
        List<List<String>> rows = TextFileUtil.readRows(path);
        for (int i = 0; i < rows.size(); i++) {
            try {
                List<String> row = rows.get(i);
                if (row.size() < 5) {
                    throw new IllegalArgumentException("Cần ít nhất 5 trường");
                }
                String id = row.get(0);
                if (id.isBlank() || loaded.containsKey(id)) {
                    throw new IllegalArgumentException("Mã toa rỗng hoặc bị trùng");
                }
                DonThuoc prescription = new DonThuoc(id, row.get(1), row.get(2), row.get(3),
                        java.time.LocalDate.parse(row.get(4)));
                prescription.setMedicineIds(new ArrayList<>(row.subList(5, row.size())));
                loaded.put(id, prescription);
            } catch (RuntimeException e) {
                throw TextFileUtil.invalidRow("toa thuốc", i + 1, e);
            }
        }
        prescriptions.clear();
        prescriptions.putAll(loaded);
        filePath = path;
    }

    public void ghiFile(String path) throws java.io.IOException {
        List<List<String>> rows = new ArrayList<>();
        for (DonThuoc prescription : prescriptions.values()) {
            List<String> row = new ArrayList<>(java.util.Arrays.asList(prescription.getPrescriptionId(),
                    prescription.getCustomerId(), prescription.getPatientName(), prescription.getDoctorName(),
                    prescription.getIssueDate().toString()));
            row.addAll(prescription.getMedicineIds());
            rows.add(row);
        }
        TextFileUtil.writeRows(path, rows);
        filePath = path;
    }

    private void saveIfConfigured() {
        if (filePath != null) {
            try {
                ghiFile(filePath);
            } catch (java.io.IOException e) {
                throw new IllegalStateException("Không thể lưu dữ liệu toa thuốc vào " + filePath, e);
            }
        }
    }
}
