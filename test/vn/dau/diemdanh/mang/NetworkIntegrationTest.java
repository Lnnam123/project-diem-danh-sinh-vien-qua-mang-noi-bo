package vn.dau.diemdanh.mang;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vn.dau.diemdanh.model.SinhVien;
import vn.dau.diemdanh.nghiepvu.ChongGianLanService;
import vn.dau.diemdanh.nghiepvu.DiemDanhService;
import vn.dau.diemdanh.nghiepvu.PhienManager;
import vn.dau.diemdanh.nghiepvu.ThongKeService;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử Tích hợp Mạng TCP Máy chủ - Máy khách (F07, F08)")
class NetworkIntegrationTest {

    private PhienManager phienManager;
    private ChongGianLanService chongGianLanService;
    private ThongKeService thongKeService;
    private DiemDanhService diemDanhService;
    private DiemDanhServer server;
    private int port;

    @BeforeEach
    void setUp() throws IOException {
        phienManager = new PhienManager();
        chongGianLanService = new ChongGianLanService();
        thongKeService = new ThongKeService();
        diemDanhService = new DiemDanhService(phienManager, chongGianLanService, thongKeService);

        diemDanhService.napDanhSachLop(Map.of(
                "SV01", new SinhVien("SV01", "Nguyen Van A", "22CNTT1"),
                "SV02", new SinhVien("SV02", "Tran Thi B", "22CNTT1")
        ));

        // Khởi động server với port ngẫu nhiên (port 0) để tránh xung đột cổng
        server = new DiemDanhServer(0, diemDanhService);
        server.start();
        port = server.getPort();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop();
        }
        if (phienManager != null) {
            phienManager.shutdown();
        }
    }

    @Test
    @DisplayName("F07 & F08: Máy khách gửi điểm danh thành công tới máy chủ qua Socket TCP")
    void diemDanhThanhCongQuaMang() {
        diemDanhService.moPhien("P01", 15);

        String response = StudentClient.guiDiemDanh("127.0.0.1", port, "P01", "SV01");

        assertNotNull(response);
        assertTrue(response.startsWith("SUCCESS:"), "Phản hồi phải bắt đầu bằng SUCCESS:");
        assertEquals(1, diemDanhService.laySoLuongCoMat());
    }

    @Test
    @DisplayName("F07 & F08: Máy khách nhận thông báo lỗi khi phiên chưa mở")
    void diemDanhLoiKhiPhienChuaMo() {
        String response = StudentClient.guiDiemDanh("127.0.0.1", port, "P01", "SV01");

        assertNotNull(response);
        assertTrue(response.startsWith("ERROR:"), "Phản hồi phải bắt đầu bằng ERROR:");
    }

    @Test
    @DisplayName("F07 & F08: Máy khách nhận lỗi khi sinh viên không tồn tại trong danh sách lớp")
    void diemDanhSinhVienKhongTonTai() {
        diemDanhService.moPhien("P01", 15);

        String response = StudentClient.guiDiemDanh("localhost", port, "P01", "SV999");

        assertNotNull(response);
        assertTrue(response.startsWith("ERROR:"), "Phản hồi phải bắt đầu bằng ERROR:");
    }

    @Test
    @DisplayName("F08: Xử lý an toàn khi không kết nối được tới máy chủ (sai cổng)")
    void ketNoiSaiPortTraVeThongBaoLoi() {
        String response = StudentClient.guiDiemDanh("localhost", 59999, "P01", "SV01");

        assertNotNull(response);
        assertTrue(response.startsWith("ERROR:"), "Phải trả về thông điệp lỗi kết nối");
    }
}
