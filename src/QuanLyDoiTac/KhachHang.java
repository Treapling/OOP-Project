package QuanLyDoiTac;

public class KhachHang extends Nguoi {
    private int loyaltyPoints;
    private String memberTier;

    public KhachHang() {
        this.memberTier = "Bronze";
    }

    public KhachHang(String customerId, String name, String phone, int loyaltyPoints) {
        super(customerId, name, phone);
        this.loyaltyPoints = loyaltyPoints;
        updateTier();
    }

    public String getCustomerId() { return getId(); }
    public void setCustomerId(String customerId) { setId(customerId); }
    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; updateTier(); }
    public String getMemberTier() { return memberTier; }
    public void setMemberTier(String memberTier) { this.memberTier = memberTier; }

    public void addPoints(int points) {
        if (points <= 0) {
            return;
        }
        this.loyaltyPoints += points;
        updateTier();
    }

    private void updateTier() {
        if (loyaltyPoints >= 1000) {
            memberTier = "Gold";
        } else if (loyaltyPoints >= 500) {
            memberTier = "Silver";
        } else {
            memberTier = "Bronze";
        }
    }

    @Override
    public String toString() {
        return "KhachHang{" +
                "customerId='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", loyaltyPoints=" + loyaltyPoints +
                ", memberTier='" + memberTier + '\'' +
                '}';
    }
}
