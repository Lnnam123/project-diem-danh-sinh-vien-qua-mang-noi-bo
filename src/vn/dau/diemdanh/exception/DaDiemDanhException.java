package vn.dau.diemdanh.exception;

/**
 * Phục vụ chức năng F01, F05: Ngoại lệ ném ra khi sinh viên gửi điểm danh trùng lặp mã trong cùng một phiên.
 * Áp dụng kỹ thuật: custom exception (Ch6).
 */
public class DaDiemDanhException extends DiemDanhException {

    public DaDiemDanhException(String message) {
        super(message);
    }
}

