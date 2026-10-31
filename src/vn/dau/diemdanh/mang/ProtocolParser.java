package vn.dau.diemdanh.mang;

import vn.dau.diemdanh.exception.GiaoThucException;

/**
 * Phục vụ chức năng F06: Phân tích và định dạng các thông điệp trao đổi qua
 * giao thức mạng văn bản TCP.
 * Áp dụng kỹ thuật: switch expression (Ch6), text block (Ch6), Arrays utility
 * (Ch1), custom exception (Ch6).
 */
public class ProtocolParser {

    public static RequestCommand parse(String rawLine) throws GiaoThucException {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public static String formatResponse(String status, String message) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}
