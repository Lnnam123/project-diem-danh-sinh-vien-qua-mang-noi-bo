package vn.dau.diemdanh.mang;

/**
 * Phục vụ chức năng F06: Đại diện cho cấu trúc gói tin yêu cầu đã được phân tích cú pháp.
 * Áp dụng kỹ thuật: record (Ch6).
 */
public record RequestCommand(
        String command,
        String maPhien,
        String maSV
) {
}

