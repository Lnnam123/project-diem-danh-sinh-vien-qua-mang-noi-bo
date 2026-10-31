package vn.dau.diemdanh.model;

import java.time.LocalDateTime;

/**
 * Phục vụ chức năng F01, F03: Thông tin phiên điểm danh.
 * Áp dụng kỹ thuật: record (Ch6), java.time (Ch6).
 */
public record PhienDiemDanh(
        String maPhien,
        LocalDateTime thoiGianMo,
        int thoiLuongPhut,
        TrangThaiPhien trangThai
) {

    public boolean isConHieuLuc() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public LocalDateTime thoiGianKetThuc() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}

