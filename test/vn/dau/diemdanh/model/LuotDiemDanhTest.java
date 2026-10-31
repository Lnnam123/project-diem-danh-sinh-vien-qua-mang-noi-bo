package vn.dau.diemdanh.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử cho mô hình LuotDiemDanh (F01).
 */
@DisplayName("Kiểm thử mô hình LuotDiemDanh (F01)")
class LuotDiemDanhTest {

    @Test
    @DisplayName("Khởi tạo lượt điểm danh hợp lệ và cắt khoảng trắng thừa")
    void taoLuotDiemDanhHopLe() {
        LocalDateTime now = LocalDateTime.now();
        LuotDiemDanh luot = new LuotDiemDanh("  SV001  ", "  P01  ", now, "  192.168.1.10  ");

        assertEquals("SV001", luot.maSV());
        assertEquals("P01", luot.maPhien());
        assertEquals(now, luot.thoiGianGui());
        assertEquals("192.168.1.10", luot.ipAddress());
    }

    @Test
    @DisplayName("Ném ngoại lệ khi có trường thông tin bị null hoặc rỗng")
    void nemNgoaiLeKhiTruongKhongHopLe() {
        LocalDateTime now = LocalDateTime.now();

        assertThrows(NullPointerException.class, () -> new LuotDiemDanh(null, "P01", now, "127.0.0.1"));
        assertThrows(NullPointerException.class, () -> new LuotDiemDanh("SV001", null, now, "127.0.0.1"));
        assertThrows(NullPointerException.class, () -> new LuotDiemDanh("SV001", "P01", null, "127.0.0.1"));
        assertThrows(NullPointerException.class, () -> new LuotDiemDanh("SV001", "P01", now, null));

        assertThrows(IllegalArgumentException.class, () -> new LuotDiemDanh("  ", "P01", now, "127.0.0.1"));
        assertThrows(IllegalArgumentException.class, () -> new LuotDiemDanh("SV001", "  ", now, "127.0.0.1"));
        assertThrows(IllegalArgumentException.class, () -> new LuotDiemDanh("SV001", "P01", now, "  "));
    }

    @Test
    @DisplayName("Hai lượt điểm danh cùng thông tin có equals và hashCode bằng nhau")
    void kiemTraEqualsVaHashCode() {
        LocalDateTime now = LocalDateTime.now();
        LuotDiemDanh luot1 = new LuotDiemDanh("SV001", "P01", now, "127.0.0.1");
        LuotDiemDanh luot2 = new LuotDiemDanh("SV001", "P01", now, "127.0.0.1");
        LuotDiemDanh luot3 = new LuotDiemDanh("SV002", "P01", now, "127.0.0.1");

        assertEquals(luot1, luot2);
        assertEquals(luot1.hashCode(), luot2.hashCode());
        assertNotEquals(luot1, luot3);
    }
}

