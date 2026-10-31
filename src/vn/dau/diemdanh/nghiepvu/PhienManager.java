package vn.dau.diemdanh.nghiepvu;

import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import vn.dau.diemdanh.model.PhienDiemDanh;

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

    public synchronized KetQua moPhien(String maPhien, int thoiLuongPhut, Runnable onTimeout) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public synchronized KetQua dongPhien() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public Optional<PhienDiemDanh> getPhienHienTai() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public boolean isConMo() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public void shutdown() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}

