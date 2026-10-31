package vn.dau.diemdanh.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Phục vụ chức năng F15: Đại diện cho dữ liệu của một buổi học đã điểm danh trong lịch sử.
 * Áp dụng kỹ thuật: record (Ch6), Set, List (Ch3), java.time (Ch6).
 */
public record BuoiDiemDanh(
        String maPhien,
        LocalDate ngayDiemDanh,
        Set<String> danhSachMaSVCoMat,
        List<SinhVien> danhSachLop
) {
}

