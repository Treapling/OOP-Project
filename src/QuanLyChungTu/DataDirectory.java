package QuanLyChungTu;

import java.nio.file.Files;
import java.nio.file.Path;

public final class DataDirectory {
    private DataDirectory() {
    }

    public static Path resolve() {
        String configured = System.getProperty("pharmacy.data.dir");
        if (configured != null && !configured.isBlank()) {
            return Path.of(configured).toAbsolutePath().normalize();
        }
        Path current = Path.of("").toAbsolutePath().normalize();
        Path found = findProjectData(current);
        if (found != null) {
            return found;
        }
        try {
            Path location = Path.of(DataDirectory.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).toAbsolutePath().normalize();
            found = findProjectData(location);
            if (found != null) {
                return found;
            }
        } catch (java.net.URISyntaxException e) {
            throw new IllegalStateException("Không xác định được thư mục chạy ứng dụng", e);
        }
        return current.resolve("data");
    }

    private static Path findProjectData(Path start) {
        Path candidate = Files.isDirectory(start) ? start : start.getParent();
        while (candidate != null) {
            if (Files.isDirectory(candidate.resolve("data"))
                    && Files.exists(candidate.resolve("src").resolve("Main.java"))) {
                return candidate.resolve("data");
            }
            candidate = candidate.getParent();
        }
        return null;
    }
}
