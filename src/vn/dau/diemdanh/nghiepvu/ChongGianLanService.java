package vn.dau.diemdanh.nghiepvu;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Phục vụ chức năng F14: Kiểm soát và chống gửi hộ điểm danh thông qua địa chỉ IP mạng.
 * Áp dụng kỹ thuật: ConcurrentHashMap (Ch3, Ch4).
 */
public class ChongGianLanService {

    private final ConcurrentHashMap<String, String> ipToMaSVMap = new ConcurrentHashMap<>();

    public synchronized KetQua kiemTraVaGhiNhanIP(String ipAddress, String maSV) {
        if (ipAddress == null || maSV == null) {
            return new KetQua.ThatBai("Địa chỉ IP hoặc mã sinh viên null", "THONG_TIN_NULL");
        }

        String ip = ipAddress.strip();
        String sv = maSV.strip();

        // Chuẩn hóa địa chỉ localhost IPv6 về dạng chuẩn
        if (ip.equals("0:0:0:0:0:0:0:1") || ip.equals("::1")) {
            ip = "127.0.0.1";
        }

        String svDaDung = ipToMaSVMap.putIfAbsent(ip, sv);
        if (svDaDung == null) {
            // IP này chưa từng được dùng trong phiên -> Hợp lệ
            return new KetQua.ThanhCong("Ghi nhận IP hợp lệ", ip);
        }

        if (svDaDung.equalsIgnoreCase(sv)) {
            // Cùng một sinh viên gửi lại từ IP này -> Không coi là gian lận gửi hộ
            return new KetQua.ThanhCong("Cùng sinh viên trên IP này", ip);
        }

        // Sinh viên khác gửi từ IP đã có người dùng -> Nghi vấn gian lận gửi hộ
        return new KetQua.ThatBai(
                "Phát hiện nghi vấn gửi hộ: IP " + ip + " đã được dùng bởi sinh viên " + svDaDung,
                "GIAN_LAN_IP"
        );
    }

    public void reset() {
        ipToMaSVMap.clear();
    }
}
