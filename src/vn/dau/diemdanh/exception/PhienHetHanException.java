package vn.dau.diemdanh.exception;

/**
 * Phục vụ chức năng F01, F03, F05: Ngoại lệ ném ra khi sinh viên gửi điểm danh vào phiên đã đóng hoặc hết hạn.
 * Áp dụng kỹ thuật: custom exception (Ch6).
 */
public class PhienHetHanException extends DiemDanhException {

    public PhienHetHanException(String message) {
        super(message);
    }
}

