package QuanLyChungTu;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class InBaoCao {
    private final List<PhieuXuat> invoiceHistory;

    public InBaoCao(List<PhieuXuat> invoiceHistory) {
        this.invoiceHistory = invoiceHistory == null ? new ArrayList<>() : new ArrayList<>(invoiceHistory);
    }

    public double getDailyRevenue(LocalDate date) {
        double total = 0;
        for (PhieuXuat invoice : invoiceHistory) {
            if (invoice != null && invoice.getCreatedAt() != null && invoice.getCreatedAt().toLocalDate().equals(date)) {
                total += invoice.getTotalAmount();
            }
        }
        return total;
    }

    public double getMonthlyRevenue(int year, int month) {
        double total = 0;
        for (PhieuXuat invoice : invoiceHistory) {
            if (invoice != null && invoice.getCreatedAt() != null) {
                if (invoice.getCreatedAt().getYear() == year && invoice.getCreatedAt().getMonthValue() == month) {
                    total += invoice.getTotalAmount();
                }
            }
        }
        return total;
    }
}
