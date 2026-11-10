package QuanLyChungTu;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

public final class MaTuDong {
    private static final AtomicLong SEQUENCE = new AtomicLong();
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private MaTuDong() {
    }

    public static String taoMa(String prefix) {
        return prefix + "-" + LocalDateTime.now().format(TIMESTAMP) + "-"
                + SEQUENCE.incrementAndGet();
    }
}
