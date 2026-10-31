package vn.dau.diemdanh.model;

import java.time.LocalDateTime;
import java.util.Objects;

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

    public PhienDiemDanh {
        Objects.requireNonNull(maPhien, "maPhien không được null");
        Objects.requireNonNull(thoiGianMo, "thoiGianMo không được null");
        Objects.requireNonNull(trangThai, "trangThai không được null");
        maPhien = maPhien.strip();
        if (maPhien.isEmpty()) {
            throw new IllegalArgumentException("maPhien không được để trống");
        }
        if (thoiLuongPhut <= 0) {
            throw new IllegalArgumentException("thoiLuongPhut phải lớn hơn 0");
        }
    }

    public boolean isConHieuLuc() {
        return trangThai == TrangThaiPhien.DANG_MO && LocalDateTime.now().isBefore(thoiGianKetThuc());
    }

    public LocalDateTime thoiGianKetThuc() {
        return thoiGianMo.plusMinutes(thoiLuongPhut);
    }
}
