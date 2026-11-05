package QuanLyChungTu;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public final class SystemLogger {
    private static final Path LOG_PATH = Path.of("log.txt");

    private SystemLogger() {
    }

    public static void ghiLog(String thaoTac) {
        try (BufferedWriter writer = Files.newBufferedWriter(LOG_PATH, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(LocalDateTime.now() + " - " + thaoTac);
            writer.newLine();
        } catch (IOException e) {
            throw new IllegalStateException("Không thể ghi log vào " + LOG_PATH.toAbsolutePath(), e);
        }
    }
}
