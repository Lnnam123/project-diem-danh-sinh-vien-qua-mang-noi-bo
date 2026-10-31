package vn.dau.diemdanh.nghiepvu;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Phục vụ chức năng F14: Kiểm soát và chống gửi hộ điểm danh thông qua địa chỉ IP mạng.
 * Áp dụng kỹ thuật: ConcurrentHashMap (Ch3, Ch4).
 */
public class ChongGianLanService {

    private final ConcurrentHashMap<String, String> ipToMaSVMap = new ConcurrentHashMap<>();

    public synchronized KetQua kiemTraVaGhiNhanIP(String ipAddress, String maSV) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public void reset() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}

