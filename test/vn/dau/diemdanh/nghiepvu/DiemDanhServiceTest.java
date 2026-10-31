package vn.dau.diemdanh.nghiepvu;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vn.dau.diemdanh.model.SinhVien;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử Nghiệp vụ Điểm danh và Chống gian lận - Chức năng F05, F14")
class DiemDanhServiceTest {

    private PhienManager phienManager;
    private ChongGianLanService chongGianLanService;
    private ThongKeService thongKeService;
    private DiemDanhService diemDanhService;

    @BeforeEach
    void setUp() {
        phienManager = new PhienManager();
        chongGianLanService = new ChongGianLanService();
        thongKeService = new ThongKeService();
        diemDanhService = new DiemDanhService(phienManager, chongGianLanService, thongKeService);

        // Nạp danh sách lớp mẫu
        Map<String, SinhVien> dsLop = Map.of(
                "SV01", new SinhVien("SV01", "Nguyễn Văn An", "22CNTT1"),
                "SV02", new SinhVien("SV02", "Trần Thị Bình", "22CNTT1"),
                "SV03", new SinhVien("SV03", "Lê Hoàng Cường", "22CNTT2")
        );
        diemDanhService.napDanhSachLop(dsLop);
    }

    @AfterEach
    void tearDown() {
        phienManager.shutdown();
    }

    @Test
    @DisplayName("F05: Điểm danh thành công khi sinh viên có trong lớp và phiên đang mở")
    void diemDanhThanhCong() {
        diemDanhService.moPhien("P01", 15);

        KetQua kq = diemDanhService.ghiNhanDiemDanh("P01", "SV01", "192.168.1.10");

        assertTrue(kq.isThanhCong());
        assertEquals(1, diemDanhService.laySoLuongCoMat());
        assertTrue(diemDanhService.getSetMaSVCoMat().contains("SV01"));
    }

    @Test
    @DisplayName("F05: Thất bại khi điểm danh lúc phiên chưa mở hoặc đã đóng")
    void diemDanhKhiChuaMoPhien() {
        KetQua kq = diemDanhService.ghiNhanDiemDanh("P01", "SV01", "192.168.1.10");

        assertFalse(kq.isThanhCong());
        assertEquals("PHIEN_KHONG_MO", ((KetQua.ThatBai) kq).maLoi());
    }

    @Test
    @DisplayName("F05: Thất bại khi sinh viên gửi sai mã phiên đang mở")
    void diemDanhSaiMaPhien() {
        diemDanhService.moPhien("P01", 15);

        KetQua kq = diemDanhService.ghiNhanDiemDanh("P999", "SV01", "192.168.1.10");

        assertFalse(kq.isThanhCong());
        assertEquals("SAI_MA_PHIEN", ((KetQua.ThatBai) kq).maLoi());
    }

    @Test
    @DisplayName("F05: Thất bại khi sinh viên không có tên trong danh sách lớp")
    void diemDanhSinhVienKhongTonTai() {
        diemDanhService.moPhien("P01", 15);

        KetQua kq = diemDanhService.ghiNhanDiemDanh("P01", "SV_LA", "192.168.1.10");

        assertFalse(kq.isThanhCong());
        assertEquals("SV_KHONG_TON_TAI", ((KetQua.ThatBai) kq).maLoi());
    }

    @Test
    @DisplayName("F05: Thất bại khi sinh viên gửi điểm danh lần thứ hai (chống trùng lặp)")
    void chongTrungDiemDanhCungMotSinhVien() {
        diemDanhService.moPhien("P01", 15);

        KetQua kq1 = diemDanhService.ghiNhanDiemDanh("P01", "SV01", "192.168.1.10");
        assertTrue(kq1.isThanhCong());

        KetQua kq2 = diemDanhService.ghiNhanDiemDanh("P01", "SV01", "192.168.1.10");
        assertFalse(kq2.isThanhCong());
        assertEquals("DA_DIEM_DANH", ((KetQua.ThatBai) kq2).maLoi());
    }

    @Test
    @DisplayName("F14: Phát hiện nghi vấn gian lận gửi hộ khi 2 sinh viên khác nhau gửi từ cùng 1 IP")
    void chongGianLanGuiHoCungIP() {
        diemDanhService.moPhien("P01", 15);

        // Sinh viên 1 gửi từ IP 192.168.1.50 -> thành công
        KetQua kq1 = diemDanhService.ghiNhanDiemDanh("P01", "SV01", "192.168.1.50");
        assertTrue(kq1.isThanhCong());

        // Sinh viên 2 gửi từ cùng IP 192.168.1.50 -> từ chối vì trùng IP
        KetQua kq2 = diemDanhService.ghiNhanDiemDanh("P01", "SV02", "192.168.1.50");
        assertFalse(kq2.isThanhCong());
        assertEquals("GIAN_LAN_IP", ((KetQua.ThatBai) kq2).maLoi());
    }

    @Test
    @DisplayName("F05: Xử lý an toàn đa luồng khi nhiều yêu cầu gửi đồng thời (Concurrency test)")
    void diemDanhDongThoiAnToanLuong() throws InterruptedException {
        diemDanhService.moPhien("P01", 15);

        int soLuongLuong = 10;
        ExecutorService executor = Executors.newFixedThreadPool(soLuongLuong);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(soLuongLuong);

        AtomicInteger soThanhCong = new AtomicInteger(0);

        // Giả lập 10 luồng cùng cố gắng điểm danh cho SV01 với các IP khác nhau
        for (int i = 0; i < soLuongLuong; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    KetQua kq = diemDanhService.ghiNhanDiemDanh("P01", "SV01", "192.168.1." + (100 + index));
                    if (kq.isThanhCong()) {
                        soThanhCong.incrementAndGet();
                    }
                } catch (InterruptedException ignored) {
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // Bắt đầu đồng loạt
        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        // Đúng duy nhất 1 luồng được ghi nhận thành công, 9 luồng còn lại bị từ chối
        assertEquals(1, soThanhCong.get(), "Chỉ duy nhất 1 lần điểm danh được chấp nhận cho cùng 1 mã SV");
        assertEquals(1, diemDanhService.laySoLuongCoMat());
    }
}
