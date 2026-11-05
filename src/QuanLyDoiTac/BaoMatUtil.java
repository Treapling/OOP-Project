package QuanLyDoiTac;

import java.util.LinkedHashMap;
import java.util.Map;


public class BaoMatUtil {
    private final Map<String, TaiKhoan> accounts = new LinkedHashMap<>();

    public boolean registerAccount(TaiKhoan account) {
        if (account == null || account.getUsername() == null || account.getUsername().isBlank()) {
            return false;
        }
        accounts.put(account.getUsername(), account);
        return true;
    }

    public TaiKhoan login(String username, String password) {
        TaiKhoan account = accounts.get(username);
        if (account == null || !account.isActive()) {
            return null;
        }
        if (account.checkPassword(password)) {
            return account;
        }
        return null;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        TaiKhoan account = accounts.get(username);
        if (account == null || !account.checkPassword(oldPassword) || newPassword == null || newPassword.isBlank()) {
            return false;
        }
        account.setPassword(newPassword);
        return true;
    }

    public boolean hasAccess(String username, String requiredRole) {
        TaiKhoan account = accounts.get(username);
        return account != null && account.isActive() && requiredRole.equalsIgnoreCase(account.getRole());
    }
}
