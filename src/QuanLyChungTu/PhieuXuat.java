package QuanLyChungTu;

import java.time.LocalDateTime;


public class PhieuXuat implements IThanhToan {
    private String invoiceId;
    private String customerId;
    private String employeeId;
    private LocalDateTime createdAt;
    private double totalAmount;
    private String paymentMethod;
    private boolean paid;

    public PhieuXuat() {
        this.createdAt = LocalDateTime.now();
    }

    public PhieuXuat(String invoiceId, String customerId, String employeeId, double totalAmount,
                   String paymentMethod, boolean paid) {
        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.createdAt = LocalDateTime.now();
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paid = paid;
    }

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    @Override
    public void xuatHoaDon() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        return "PhieuXuat{" +
                "invoiceId='" + invoiceId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", createdAt=" + createdAt +
                ", totalAmount=" + totalAmount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paid=" + paid +
                '}';
    }
}
