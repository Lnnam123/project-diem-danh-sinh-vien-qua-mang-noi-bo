package vn.dau.diemdanh.mang;

import java.util.Arrays;
import vn.dau.diemdanh.exception.GiaoThucException;

/**
 * Phục vụ chức năng F06: Phân tích và định dạng các thông điệp trao đổi qua
 * giao thức mạng văn bản TCP.
 * Áp dụng kỹ thuật: switch expression (Ch6), text block (Ch6), Arrays utility
 * (Ch1), custom exception (Ch6).
 */
public class ProtocolParser {

    /**
     * F06: Phân tích cú pháp chuỗi văn bản dòng lệnh từ client gửi lên qua Socket.
     */
    public static RequestCommand parse(String rawLine) throws GiaoThucException {
        if (rawLine == null || rawLine.strip().isEmpty()) {
            throw new GiaoThucException("Lệnh rỗng hoặc không có nội dung");
        }

        String[] tokens = Arrays.stream(rawLine.strip().split("\\s+"))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        if (tokens.length == 0) {
            throw new GiaoThucException("Lệnh không chứa từ khóa hợp lệ");
        }

        String cmd = tokens[0].toUpperCase();

        return switch (cmd) {
            case "DIEMDANH" -> {
                if (tokens.length < 3) {
                    throw new GiaoThucException("Lệnh DIEMDANH yêu cầu 2 tham số: <maPhien> <maSV>");
                }
                yield new RequestCommand("DIEMDANH", tokens[1].strip(), tokens[2].strip());
            }
            case "STATUS" -> {
                String maPhien = tokens.length > 1 ? tokens[1].strip() : "";
                yield new RequestCommand("STATUS", maPhien, "");
            }
            case "PING" -> new RequestCommand("PING", "", "");
            default -> throw new GiaoThucException("Lệnh không được hỗ trợ: " + cmd);
        };
    }

    /**
     * F06: Định dạng chuỗi phản hồi từ máy chủ gửi về máy khách.
     */
    public static String formatResponse(String status, String message) {
        String st = status != null ? status.strip().toUpperCase() : "INFO";
        String msg = message != null ? message.strip() : "";
        return st + ": " + msg;
    }
}
