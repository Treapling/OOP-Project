package QuanLyChungTu;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class DonThuoc {
    private String prescriptionId;
    private String customerId;
    private String patientName;
    private String doctorName;
    private LocalDate issueDate;
    private List<String> medicineIds = new ArrayList<>();

    public DonThuoc() {
        this.issueDate = LocalDate.now();
    }

    public DonThuoc(String prescriptionId, String customerId, String patientName,
                       String doctorName, LocalDate issueDate) {
        this.prescriptionId = prescriptionId;
        this.customerId = customerId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.issueDate = issueDate;
    }

    public String getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(String prescriptionId) { this.prescriptionId = prescriptionId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public List<String> getMedicineIds() { return medicineIds; }
    public void setMedicineIds(List<String> medicineIds) { this.medicineIds = medicineIds; }

    public void addMedicine(String medicineId) {
        if (!medicineIds.contains(medicineId)) {
            medicineIds.add(medicineId);
        }
    }

    @Override
    public String toString() {
        return "DonThuoc{" +
                "prescriptionId='" + prescriptionId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", patientName='" + patientName + '\'' +
                ", doctorName='" + doctorName + '\'' +
                ", issueDate=" + issueDate +
                ", medicineIds=" + medicineIds +
                '}';
    }
}
