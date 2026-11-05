package QuanLyChungTu;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class QuanLyDonThuoc {
    private final Map<String, DonThuoc> prescriptions = new LinkedHashMap<>();

    public boolean addPrescription(DonThuoc prescription) {
        if (prescription == null || prescription.getPrescriptionId() == null || prescription.getPrescriptionId().isBlank()) {
            return false;
        }
        prescriptions.put(prescription.getPrescriptionId(), prescription);
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
}
