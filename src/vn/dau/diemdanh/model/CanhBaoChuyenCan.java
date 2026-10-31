package vn.dau.diemdanh.model;

/**
 * Phục vụ chức năng F15: Bản ghi cảnh báo sinh viên vắng quá 20% (chuyên cần dưới 80%).
 * Áp dụng kỹ thuật: record (Ch6).
 */
public record CanhBaoChuyenCan(
        String maSV,
        String hoTen,
        String lop,
        int tongSoBuoi,
        int soBuoiVang,
        double tiLeVang,
        double tiLeChuyenCan
) {
}

