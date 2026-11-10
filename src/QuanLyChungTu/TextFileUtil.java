package QuanLyChungTu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public final class TextFileUtil {
    private TextFileUtil() {
    }

    public static List<List<String>> readRows(String path) throws IOException {
        List<List<String>> rows = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(path), StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isEmpty()) {
                    continue;
                }
                try {
                    rows.add(parseRow(line));
                } catch (IllegalArgumentException e) {
                    throw new IOException("Dữ liệu không hợp lệ tại dòng " + lineNumber + " của " + path, e);
                }
            }
        }
        return rows;
    }

    public static void writeRows(String path, List<List<String>> rows) throws IOException {
        Path file = Path.of(path);
        Path absoluteFile = file.toAbsolutePath();
        Path parent = absoluteFile.getParent();
        Files.createDirectories(parent);
        String prefix = file.getFileName().toString();
        while (prefix.length() < 3) {
            prefix += "_";
        }
        Path temporaryFile = Files.createTempFile(parent, prefix, ".tmp");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temporaryFile, StandardCharsets.UTF_8)) {
                for (List<String> row : rows) {
                    for (int i = 0; i < row.size(); i++) {
                        if (i > 0) {
                            writer.write('\t');
                        }
                        writer.write(escape(row.get(i)));
                    }
                    writer.newLine();
                }
            }
            try {
                Files.move(temporaryFile, absoluteFile, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, absoluteFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    public static IOException invalidRow(String entity, int rowNumber, RuntimeException cause) {
        return new IOException("Bản ghi " + entity + " không hợp lệ tại dòng " + rowNumber, cause);
    }

    public static void requireFieldCount(List<String> row, int fieldCount) {
        if (row.size() != fieldCount) {
            throw new IllegalArgumentException("Cần đúng " + fieldCount + " trường");
        }
    }

    private static String escape(String value) {
        String normalized = value == null ? "" : value;
        return normalized.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static List<String> parseRow(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (escaped) {
                switch (current) {
                    case '\\': field.append('\\'); break;
                    case 't': field.append('\t'); break;
                    case 'n': field.append('\n'); break;
                    case 'r': field.append('\r'); break;
                    default: throw new IllegalArgumentException("Ký tự escape không hợp lệ");
                }
                escaped = false;
            } else if (current == '\\') {
                escaped = true;
            } else if (current == '\t') {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(current);
            }
        }
        if (escaped) {
            throw new IllegalArgumentException("Thiếu ký tự sau dấu escape");
        }
        fields.add(field.toString());
        return fields;
    }
}
