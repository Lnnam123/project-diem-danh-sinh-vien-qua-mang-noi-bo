package vn.dau.diemdanh.exception;

/**
 * Phục vụ chức năng F01, F02, F05: Ngoại lệ ném ra khi mã sinh viên không tồn tại trong danh sách lớp.
 * Áp dụng kỹ thuật: custom exception (Ch6).
 */
public class SinhVienKhongTonTaiException extends DiemDanhException {

    public SinhVienKhongTonTaiException(String message) {
        super(message);
    }
}

