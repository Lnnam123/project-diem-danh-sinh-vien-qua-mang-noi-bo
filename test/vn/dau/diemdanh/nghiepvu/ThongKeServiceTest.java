package vn.dau.diemdanh.nghiepvu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vn.dau.diemdanh.model.SinhVien;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử Thống kê bằng Streams API - Chức năng F09, F10")
class ThongKeServiceTest {

    private ThongKeService thongKeService;

    @BeforeEach
    void setUp() {
        thongKeService = new ThongKeService();
    }

    @Test
    @DisplayName("F09: Lọc danh sách sinh viên vắng mặt bằng Streams API chính xác và có sắp xếp")
    void locDanhSachVangChinhXac() {
        SinhVien sv1 = new SinhVien("SV01", "Nguyễn Văn A", "21CNTT1");
        SinhVien sv2 = new SinhVien("SV02", "Trần Thị B", "21CNTT1");
        SinhVien sv3 = new SinhVien("SV03", "Lê Văn C", "21CNTT1");
        List<SinhVien> dsLop = List.of(sv3, sv1, sv2);

        Set<String> coMat = Set.of("SV02");

        List<SinhVien> vang = thongKeService.locDanhSachVang(dsLop, coMat);

        assertEquals(2, vang.size());
        assertEquals("SV01", vang.get(0).maSV());
        assertEquals("SV03", vang.get(1).maSV());
    }

    @Test
    @DisplayName("F09: Cả lớp có mặt 100% thì danh sách vắng rỗng")
    void caLopCoMatThiDanhSachVangRong() {
        SinhVien sv1 = new SinhVien("SV01", "Nguyễn Văn A", "21CNTT1");
        List<SinhVien> dsLop = List.of(sv1);
        Set<String> coMat = Set.of("SV01");

        List<SinhVien> vang = thongKeService.locDanhSachVang(dsLop, coMat);
        assertTrue(vang.isEmpty());
    }

    @Test
    @DisplayName("F09: Cả lớp vắng 100% thì danh sách vắng bằng danh sách lớp")
    void caLopVangThiDanhSachVangBangCaLop() {
        SinhVien sv1 = new SinhVien("SV01", "Nguyễn Văn A", "21CNTT1");
        SinhVien sv2 = new SinhVien("SV02", "Trần Thị B", "21CNTT1");
        List<SinhVien> dsLop = List.of(sv1, sv2);

        List<SinhVien> vang = thongKeService.locDanhSachVang(dsLop, Set.of());
        assertEquals(2, vang.size());
    }

    @Test
    @DisplayName("F10: Thống kê số lượng và tỷ lệ % có mặt / vắng buổi học chính xác")
    void thongKeBuoiChinhXac() {
        Map<String, Object> tk = thongKeService.thongKeBuoi(10, 8);

        assertEquals(10, tk.get("tongSoSV"));
        assertEquals(8, tk.get("soLuongCoMat"));
        assertEquals(2, tk.get("soLuongVang"));
        assertEquals(80.0, tk.get("tiLeCoMat"));
        assertEquals(20.0, tk.get("tiLeVang"));
    }

    @Test
    @DisplayName("F10: Xử lý an toàn khi sĩ số bằng 0 (không bị ArithmeticException)")
    void thongKeKhiSiSoBangKhong() {
        Map<String, Object> tk = thongKeService.thongKeBuoi(0, 0);

        assertEquals(0, tk.get("tongSoSV"));
        assertEquals(0, tk.get("soLuongCoMat"));
        assertEquals(0, tk.get("soLuongVang"));
        assertEquals(0.0, tk.get("tiLeCoMat"));
        assertEquals(0.0, tk.get("tiLeVang"));
    }
}
