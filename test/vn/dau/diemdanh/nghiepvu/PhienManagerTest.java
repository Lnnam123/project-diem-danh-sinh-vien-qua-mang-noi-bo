package vn.dau.diemdanh.nghiepvu;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vn.dau.diemdanh.model.PhienDiemDanh;
import vn.dau.diemdanh.model.TrangThaiPhien;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử Quản lý phiên và Luồng hẹn giờ - Chức năng F03, F04")
class PhienManagerTest {

    private PhienManager phienManager;

    @BeforeEach
    void setUp() {
        phienManager = new PhienManager();
    }

    @AfterEach
    void tearDown() {
        phienManager.shutdown();
    }

    @Test
    @DisplayName("F03: Mở phiên điểm danh hợp lệ thành công")
    void moPhienHopLeThanhCong() {
        KetQua kq = phienManager.moPhien("P01", 15, () -> {});

        assertTrue(kq.isThanhCong());
        assertTrue(phienManager.isConMo());

        Optional<PhienDiemDanh> opt = phienManager.getPhienHienTai();
        assertTrue(opt.isPresent());
        assertEquals("P01", opt.get().maPhien());
        assertEquals(15, opt.get().thoiLuongPhut());
        assertEquals(TrangThaiPhien.DANG_MO, opt.get().trangThai());
    }

    @Test
    @DisplayName("F03: Thất bại khi mở phiên với mã rỗng hoặc thời lượng <= 0")
    void moPhienVoiThongSoKhongHopLe() {
        KetQua kq1 = phienManager.moPhien("", 15, () -> {});
        assertFalse(kq1.isThanhCong());

        KetQua kq2 = phienManager.moPhien("P01", 0, () -> {});
        assertFalse(kq2.isThanhCong());

        KetQua kq3 = phienManager.moPhien("P01", -5, () -> {});
        assertFalse(kq3.isThanhCong());

        assertFalse(phienManager.isConMo());
    }

    @Test
    @DisplayName("F03: Không cho phép mở phiên mới khi phiên hiện tại đang mở")
    void khongChoMoPhienKhiDangCoPhienMo() {
        KetQua kq1 = phienManager.moPhien("P01", 15, () -> {});
        assertTrue(kq1.isThanhCong());

        KetQua kq2 = phienManager.moPhien("P02", 10, () -> {});
        assertFalse(kq2.isThanhCong());
        assertEquals("P01", phienManager.getPhienHienTai().get().maPhien());
    }

    @Test
    @DisplayName("F03: Đóng phiên thủ công thành công khi đang có phiên mở")
    void dongPhienThuCongThanhCong() {
        phienManager.moPhien("P01", 15, () -> {});
        assertTrue(phienManager.isConMo());

        KetQua kq = phienManager.dongPhien();
        assertTrue(kq.isThanhCong());
        assertFalse(phienManager.isConMo());
        assertEquals(TrangThaiPhien.DA_DONG, phienManager.getPhienHienTai().get().trangThai());
    }

    @Test
    @DisplayName("F03: Đóng phiên khi không có phiên nào mở trả về thất bại")
    void dongPhienKhiChuaMoTraVeThatBai() {
        KetQua kq = phienManager.dongPhien();
        assertFalse(kq.isThanhCong());
    }

    @Test
    @DisplayName("F04: Luồng hẹn giờ tự động đóng phiên và kích hoạt callback onTimeout")
    void tuDongDongPhienKhiHetHan() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean timeoutTriggered = new AtomicBoolean(false);

        // Mở phiên với phương thức hỗ trợ kiểm thử tính bằng giây/mili-giây
        KetQua kq = phienManager.moPhienThoiGian(
                "P_TEST",
                1, // 1 giây
                TimeUnit.SECONDS,
                () -> {
                    timeoutTriggered.set(true);
                    latch.countDown();
                }
        );
        assertTrue(kq.isThanhCong());
        assertTrue(phienManager.isConMo());

        boolean completed = latch.await(3, TimeUnit.SECONDS);
        assertTrue(completed, "Callback onTimeout phải được gọi sau khi hết hạn");
        assertTrue(timeoutTriggered.get());
        assertFalse(phienManager.isConMo());
        assertEquals(TrangThaiPhien.DA_DONG, phienManager.getPhienHienTai().get().trangThai());
    }

    @Test
    @DisplayName("F04: Giảng viên đóng phiên thủ công trước giờ thì hủy hẹn giờ thành công")
    void dongThuCongTruocGioHuyHenGio() throws InterruptedException {
        AtomicBoolean timeoutTriggered = new AtomicBoolean(false);

        KetQua kq = phienManager.moPhienThoiGian(
                "P_TEST2",
                2, // 2 giây
                TimeUnit.SECONDS,
                () -> timeoutTriggered.set(true)
        );
        assertTrue(kq.isThanhCong());

        // Đóng ngay sau 200ms
        Thread.sleep(200);
        KetQua dongKq = phienManager.dongPhien();
        assertTrue(dongKq.isThanhCong());
        assertFalse(phienManager.isConMo());

        // Chờ qua thời hạn 2 giây xem callback có bị gọi không
        Thread.sleep(2200);
        assertFalse(timeoutTriggered.get(), "Callback không được kích hoạt vì đã bị hủy");
    }
}
