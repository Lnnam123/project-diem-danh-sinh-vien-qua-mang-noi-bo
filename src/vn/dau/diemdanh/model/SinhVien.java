package vn.dau.diemdanh.model;

import java.util.Objects;

/**
 * Phục vụ chức năng F01, F02: Mô hình thực thể sinh viên bất biến.
 * Áp dụng kỹ thuật: record (Ch6), equals và hashCode (Ch3).
 */
public record SinhVien(String maSV, String hoTen, String lop) {

    public SinhVien {
        Objects.requireNonNull(maSV, "maSV không được null");
        Objects.requireNonNull(hoTen, "hoTen không được null");
        Objects.requireNonNull(lop, "lop không được null");
        maSV = maSV.strip();
        hoTen = hoTen.strip();
        lop = lop.strip();
    }
}

