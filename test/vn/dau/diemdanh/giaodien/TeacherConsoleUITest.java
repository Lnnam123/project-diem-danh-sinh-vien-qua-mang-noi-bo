package vn.dau.diemdanh.giaodien;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vn.dau.diemdanh.luutru.CsvStorageService;
import vn.dau.diemdanh.mang.DiemDanhServer;
import vn.dau.diemdanh.nghiepvu.ChongGianLanService;
import vn.dau.diemdanh.nghiepvu.DiemDanhService;
import vn.dau.diemdanh.nghiepvu.PhienManager;
import vn.dau.diemdanh.nghiepvu.ThongKeService;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử Giao diện Console Giảng viên - Chức năng F12")
class TeacherConsoleUITest {

    private PhienManager phienManager;
    private DiemDanhService service;
    private DiemDanhServer server;
    private CsvStorageService storage;
    private TeacherConsoleUI ui;

    @BeforeEach
    void setUp() {
        phienManager = new PhienManager();
        ChongGianLanService cgl = new ChongGianLanService();
        ThongKeService tk = new ThongKeService();
        service = new DiemDanhService(phienManager, cgl, tk);
        server = new DiemDanhServer(0, service);
        storage = new CsvStorageService();
        ui = new TeacherConsoleUI(service, server, storage);
    }

    @AfterEach
    void tearDown() {
        server.stop();
        phienManager.shutdown();
    }

    @Test
    @DisplayName("F12: Giao diện hiển thị menu và thoát an toàn khi chọn 0")
    void menuThoatAnToan() {
        String input = "0\n";
        BufferedReader in = new BufferedReader(new StringReader(input));
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes, true, StandardCharsets.UTF_8);

        ui.start(in, out);

        String consoleOutput = outBytes.toString(StandardCharsets.UTF_8);
        assertTrue(consoleOutput.contains("MENU ĐIỀU KHIỂN"));
        assertTrue(consoleOutput.contains("Đang tắt ứng dụng"));
    }

    @Test
    @DisplayName("F12: Điều khiển mở phiên, xem thống kê và đóng phiên từ menu")
    void dieuKhienMoVaDongPhien() {
        // Nhập chuỗi lệnh:
        // 2 (Mở phiên) -> P01 -> 15 (phút)
        // 4 (Xem thống kê)
        // 3 (Đóng phiên)
        // 0 (Thoát)
        String input = """
                2
                P01
                15
                4
                3
                0
                """;
        BufferedReader in = new BufferedReader(new StringReader(input));
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes, true, StandardCharsets.UTF_8);

        ui.start(in, out);

        String consoleOutput = outBytes.toString(StandardCharsets.UTF_8);
        assertTrue(consoleOutput.contains("Mở phiên điểm danh thành công"));
        assertTrue(consoleOutput.contains("BÁO CÁO TÌNH HÌNH ĐIỂM DANH"));
        assertTrue(consoleOutput.contains("Đóng phiên điểm danh thành công"));
    }
}
