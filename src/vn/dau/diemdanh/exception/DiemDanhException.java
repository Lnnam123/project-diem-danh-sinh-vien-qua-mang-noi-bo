package vn.dau.diemdanh.exception;

/**
 * Phục vụ chức năng F01: Lớp ngoại lệ cơ sở cho toàn bộ các ngoại lệ nghiệp vụ điểm danh.
 * Áp dụng kỹ thuật: custom exception (Ch6).
 */
public class DiemDanhException extends Exception {

    public DiemDanhException(String message) {
        super(message);
    }

    public DiemDanhException(String message, Throwable cause) {
        super(message, cause);
    }
}

