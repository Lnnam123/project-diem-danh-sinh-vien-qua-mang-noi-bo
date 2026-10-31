package vn.dau.diemdanh.model;

import java.time.LocalDateTime;

/**
 * Phục vụ chức năng F01, F05: Bản ghi một lượt điểm danh thành công của sinh viên.
 * Áp dụng kỹ thuật: record (Ch6), java.time (Ch6).
 */
public record LuotDiemDanh(
        String maSV,
        String maPhien,
        LocalDateTime thoiGianGui,
        String ipAddress
) {
}

