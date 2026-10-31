package vn.dau.diemdanh.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kiểm thử đơn vị cho thực thể SinhVien (record - Ch6).
 */
@DisplayName("Kiểm thử thực thể SinhVien")
class SinhVienTest {

    @Test
    @DisplayName("Khởi tạo sinh viên hợp lệ thành công")
    void taoSinhVienHopLe() {
        SinhVien sv = new SinhVien("SV001", "Nguyen Van A", "21CNTT1");
        assertNotNull(sv);
        assertEquals("SV001", sv.maSV());
        assertEquals("Nguyen Van A", sv.hoTen());
        assertEquals("21CNTT1", sv.lop());
    }

    @Test
    @DisplayName("Chuỗi trắng thừa ở hai đầu được cắt gọn tự động (strip)")
    void catKhoangTrangThua() {
        SinhVien sv = new SinhVien("  SV002  ", "  Tran Thi B  ", "  21CNTT2  ");
        assertEquals("SV002", sv.maSV());
        assertEquals("Tran Thi B", sv.hoTen());
        assertEquals("21CNTT2", sv.lop());
    }

    @Test
    @DisplayName("Ném NullPointerException khi có trường thông tin bị null")
    void nemNgoaiLeKhiTruongNull() {
        assertThrows(NullPointerException.class, () -> new SinhVien(null, "Nguyen Van C", "21CNTT1"));
        assertThrows(NullPointerException.class, () -> new SinhVien("SV003", null, "21CNTT1"));
        assertThrows(NullPointerException.class, () -> new SinhVien("SV003", "Nguyen Van C", null));
    }

    @Test
    @DisplayName("Hai sinh viên cùng thông tin có equals và hashCode bằng nhau (record equality)")
    void kiemTraEqualsVaHashCode() {
        SinhVien sv1 = new SinhVien("SV001", "Nguyen Van A", "21CNTT1");
        SinhVien sv2 = new SinhVien("SV001", "Nguyen Van A", "21CNTT1");
        SinhVien sv3 = new SinhVien("SV002", "Nguyen Van A", "21CNTT1");

        assertEquals(sv1, sv2);
        assertEquals(sv1.hashCode(), sv2.hashCode());
        assertNotEquals(sv1, sv3);
    }
}
