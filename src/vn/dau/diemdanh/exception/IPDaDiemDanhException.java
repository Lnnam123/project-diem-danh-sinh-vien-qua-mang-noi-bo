package vn.dau.diemdanh.exception;

/**
 * Phục vụ chức năng F01, F14: Ngoại lệ ném ra khi phát hiện 1 địa chỉ IP gửi điểm danh cho mã sinh viên thứ hai (nghi vấn điểm danh hộ).
 * Áp dụng kỹ thuật: custom exception (Ch6).
 */
public class IPDaDiemDanhException extends DiemDanhException {

    public IPDaDiemDanhException(String message) {
        super(message);
    }
}

