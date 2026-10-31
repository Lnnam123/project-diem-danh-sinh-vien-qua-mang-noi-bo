package vn.dau.diemdanh.nghiepvu;

/**
 * Phục vụ chức năng F01: Kiểu kết quả nghiệp vụ dùng chung toàn hệ thống.
 * Áp dụng kỹ thuật: sealed interface (Ch6), record (Ch6).
 */
public sealed interface KetQua permits KetQua.ThanhCong, KetQua.ThatBai {

    boolean isThanhCong();
    String thongDiep();

    record ThanhCong(String thongDiep, Object duLieu) implements KetQua {
        @Override
        public boolean isThanhCong() {
            return true;
        }
    }

    record ThatBai(String lyDo, String maLoi) implements KetQua {
        @Override
        public boolean isThanhCong() {
            return false;
        }

        @Override
        public String thongDiep() {
            return lyDo;
        }
    }
}

