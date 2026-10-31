package vn.dau.diemdanh.exception;

/**
 * Phục vụ chức năng F01, F06: Ngoại lệ ném ra khi gói tin yêu cầu mạng sai định dạng cú pháp.
 * Áp dụng kỹ thuật: custom exception (Ch6).
 */
public class GiaoThucException extends DiemDanhException {

    public GiaoThucException(String message) {
        super(message);
    }
}

