package vn.dau.diemdanh.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử cho mô hình PhienDiemDanh (F01).
 */
@DisplayName("Kiểm thử mô hình PhienDiemDanh (F01)")
class PhienDiemDanhTest {

    @Test
    @DisplayName("Khởi tạo phiên điểm danh hợp lệ thành công")
    void taoPhienDiemDanhHopLe() {
        LocalDateTime now = LocalDateTime.now();
        PhienDiemDanh phien = new PhienDiemDanh("P01", now, 15, TrangThaiPhien.DANG_MO);

        assertNotNull(phien);
        assertEquals("P01", phien.maPhien());
        assertEquals(now, phien.thoiGianMo());
        assertEquals(15, phien.thoiLuongPhut());
        assertEquals(TrangThaiPhien.DANG_MO, phien.trangThai());
    }

    @Test
    @DisplayName("Tính toán thời gian kết thúc chính xác theo thời lượng phút")
    void tinhThoiGianKetThuc() {
        LocalDateTime thoiGianMo = LocalDateTime.of(2026, 10, 31, 8, 0, 0);
        PhienDiemDanh phien = new PhienDiemDanh("P01", thoiGianMo, 20, TrangThaiPhien.DANG_MO);

        LocalDateTime expectedKetThuc = LocalDateTime.of(2026, 10, 31, 8, 20, 0);
        assertEquals(expectedKetThuc, phien.thoiGianKetThuc());
    }

    @Test
    @DisplayName("Phiên đang mở và còn trong thời hạn thì isConHieuLuc là true")
    void phienDangMoConHieuLuc() {
        LocalDateTime thoiGianMo = LocalDateTime.now().minusMinutes(5);
        PhienDiemDanh phien = new PhienDiemDanh("P01", thoiGianMo, 15, TrangThaiPhien.DANG_MO);

        assertTrue(phien.isConHieuLuc());
    }

    @Test
    @DisplayName("Phiên đã đóng thì isConHieuLuc là false")
    void phienDaDongKhongConHieuLuc() {
        LocalDateTime thoiGianMo = LocalDateTime.now().minusMinutes(5);
        PhienDiemDanh phien = new PhienDiemDanh("P01", thoiGianMo, 15, TrangThaiPhien.DA_DONG);

        assertFalse(phien.isConHieuLuc());
    }

    @Test
    @DisplayName("Phiên quá thời lượng thì isConHieuLuc là false")
    void phienQuaGioKhongConHieuLuc() {
        LocalDateTime thoiGianMo = LocalDateTime.now().minusMinutes(30);
        PhienDiemDanh phien = new PhienDiemDanh("P01", thoiGianMo, 15, TrangThaiPhien.DANG_MO);

        assertFalse(phien.isConHieuLuc());
    }

    @Test
    @DisplayName("Ném ngoại lệ khi thông tin bắt buộc bị null hoặc rỗng")
    void nemNgoaiLeKhiDuLieuKhongHopLe() {
        LocalDateTime now = LocalDateTime.now();

        assertThrows(NullPointerException.class, () -> new PhienDiemDanh(null, now, 15, TrangThaiPhien.DANG_MO));
        assertThrows(NullPointerException.class, () -> new PhienDiemDanh("P01", null, 15, TrangThaiPhien.DANG_MO));
        assertThrows(NullPointerException.class, () -> new PhienDiemDanh("P01", now, 15, null));
        assertThrows(IllegalArgumentException.class, () -> new PhienDiemDanh("   ", now, 15, TrangThaiPhien.DANG_MO));
    }

    @Test
    @DisplayName("Ném ngoại lệ khi thời lượng phiên <= 0")
    void nemNgoaiLeKhiThoiLuongKhongHopLe() {
        LocalDateTime now = LocalDateTime.now();

        assertThrows(IllegalArgumentException.class, () -> new PhienDiemDanh("P01", now, 0, TrangThaiPhien.DANG_MO));
        assertThrows(IllegalArgumentException.class, () -> new PhienDiemDanh("P01", now, -5, TrangThaiPhien.DANG_MO));
    }
}

