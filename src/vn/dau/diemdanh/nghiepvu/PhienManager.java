package vn.dau.diemdanh.nghiepvu;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import vn.dau.diemdanh.model.PhienDiemDanh;
import vn.dau.diemdanh.model.TrangThaiPhien;

/**
 * Phục vụ chức năng F03, F04: Quản lý trạng thái và luồng hẹn giờ tự động đóng phiên điểm danh.
 * Áp dụng kỹ thuật: ScheduledExecutorService (Ch4), ScheduledFuture (Ch4), volatile (Ch4), Optional (Ch6).
 */
public class PhienManager {

    private volatile boolean isDangMo;
    private PhienDiemDanh phienHienTai;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledFuture;

    public PhienManager() {
        this.isDangMo = false;
        this.phienHienTai = null;
    }

    /**
     * F03, F04: Mở phiên điểm danh tính theo phút.
     */
    public synchronized KetQua moPhien(String maPhien, int thoiLuongPhut, Runnable onTimeout) {
        return moPhienThoiGian(maPhien, thoiLuongPhut, TimeUnit.MINUTES, onTimeout);
    }

    /**
     * F03, F04: Mở phiên điểm danh linh hoạt đơn vị thời gian (hỗ trợ kiểm thử đơn vị).
     */
    public synchronized KetQua moPhienThoiGian(
            String maPhien,
            long thoiLuong,
            TimeUnit unit,
            Runnable onTimeout
    ) {
        if (isDangMo && phienHienTai != null && phienHienTai.trangThai() == TrangThaiPhien.DANG_MO) {
            return new KetQua.ThatBai("Đang có phiên điểm danh mở, hãy đóng phiên trước khi mở mới", "PHIEN_DANG_MO");
        }

        if (maPhien == null || maPhien.strip().isEmpty()) {
            return new KetQua.ThatBai("Mã phiên không được để trống", "MA_PHIEN_RONG");
        }

        if (thoiLuong <= 0 || unit == null) {
            return new KetQua.ThatBai("Thời lượng phiên điểm danh phải lớn hơn 0", "THOI_LUONG_KHONG_HOP_LE");
        }

        int thoiLuongPhut = (int) unit.toMinutes(thoiLuong);
        if (thoiLuongPhut <= 0) {
            thoiLuongPhut = 1; // Tối thiểu 1 phút hiển thị
        }

        this.phienHienTai = new PhienDiemDanh(
                maPhien.strip(),
                LocalDateTime.now(),
                thoiLuongPhut,
                TrangThaiPhien.DANG_MO
        );
        this.isDangMo = true;

        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }

        // Hủy tác vụ hẹn giờ cũ nếu còn tồn tại
        if (scheduledFuture != null && !scheduledFuture.isDone()) {
            scheduledFuture.cancel(false);
        }

        // Lên lịch luồng hẹn giờ tự động đóng phiên khi hết hạn
        this.scheduledFuture = scheduler.schedule(() -> {
            synchronized (PhienManager.this) {
                if (isDangMo) {
                    isDangMo = false;
                    if (phienHienTai != null) {
                        phienHienTai = new PhienDiemDanh(
                                phienHienTai.maPhien(),
                                phienHienTai.thoiGianMo(),
                                phienHienTai.thoiLuongPhut(),
                                TrangThaiPhien.DA_DONG
                        );
                    }
                }
            }
            if (onTimeout != null) {
                try {
                    onTimeout.run();
                } catch (Exception ignored) {
                }
            }
        }, thoiLuong, unit);

        return new KetQua.ThanhCong("Mở phiên điểm danh thành công", phienHienTai);
    }

    /**
     * F03: Giảng viên chủ động đóng phiên thủ công.
     */
    public synchronized KetQua dongPhien() {
        if (!isDangMo || phienHienTai == null) {
            return new KetQua.ThatBai("Không có phiên điểm danh nào đang mở", "KHONG_CO_PHIEN_MO");
        }

        // Hủy lịch hẹn giờ nếu chưa đến hạn
        if (scheduledFuture != null && !scheduledFuture.isDone()) {
            scheduledFuture.cancel(false);
        }

        this.isDangMo = false;
        this.phienHienTai = new PhienDiemDanh(
                phienHienTai.maPhien(),
                phienHienTai.thoiGianMo(),
                phienHienTai.thoiLuongPhut(),
                TrangThaiPhien.DA_DONG
        );

        return new KetQua.ThanhCong("Đóng phiên điểm danh thành công", phienHienTai);
    }

    public synchronized Optional<PhienDiemDanh> getPhienHienTai() {
        return Optional.ofNullable(phienHienTai);
    }

    public synchronized boolean isConMo() {
        return isDangMo && phienHienTai != null && phienHienTai.trangThai() == TrangThaiPhien.DANG_MO;
    }

    public synchronized void shutdown() {
        if (scheduledFuture != null && !scheduledFuture.isDone()) {
            scheduledFuture.cancel(false);
        }
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }
}
