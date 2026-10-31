package vn.dau.diemdanh.model;

import java.time.LocalDateTime;
import java.util.Objects;

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

    public LuotDiemDanh {
        Objects.requireNonNull(maSV, "maSV không được null");
        Objects.requireNonNull(maPhien, "maPhien không được null");
        Objects.requireNonNull(thoiGianGui, "thoiGianGui không được null");
        Objects.requireNonNull(ipAddress, "ipAddress không được null");
        maSV = maSV.strip();
        maPhien = maPhien.strip();
        ipAddress = ipAddress.strip();
        if (maSV.isEmpty()) {
            throw new IllegalArgumentException("maSV không được để trống");
        }
        if (maPhien.isEmpty()) {
            throw new IllegalArgumentException("maPhien không được để trống");
        }
        if (ipAddress.isEmpty()) {
            throw new IllegalArgumentException("ipAddress không được để trống");
        }
    }
}
