package vn.dau.diemdanh.nghiepvu;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import vn.dau.diemdanh.model.LuotDiemDanh;
import vn.dau.diemdanh.model.PhienDiemDanh;
import vn.dau.diemdanh.model.SinhVien;

/**
 * Phục vụ chức năng F01, F02, F03, F04, F05, F09, F10: Tầng Nghiệp vụ trung tâm xử lý điểm danh và điều phối hệ thống.
 * Áp dụng kỹ thuật: ConcurrentHashMap (Ch3, Ch4), Set (Ch3), synchronized (Ch4), Streams API (Ch2), Optional (Ch6).
 */
public class DiemDanhService {

    private final Map<String, SinhVien> mapSinhVien = new ConcurrentHashMap<>();
    private final Set<String> setMaSVCoMat = ConcurrentHashMap.newKeySet();
    private final List<LuotDiemDanh> danhSachLuotDiemDanh = new CopyOnWriteArrayList<>();

    private final PhienManager phienManager;
    private final ChongGianLanService chongGianLanService;
    private final ThongKeService thongKeService;

    public DiemDanhService(
            PhienManager phienManager,
            ChongGianLanService chongGianLanService,
            ThongKeService thongKeService
    ) {
        this.phienManager = phienManager;
        this.chongGianLanService = chongGianLanService;
        this.thongKeService = thongKeService;
    }

    public void napDanhSachLop(Map<String, SinhVien> danhSach) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public KetQua moPhien(String maPhien, int thoiLuongPhut) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public KetQua dongPhien() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public KetQua ghiNhanDiemDanh(String maPhien, String maSV, String ipAddress) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public List<SinhVien> layDanhSachVang() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public int laySoLuongCoMat() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public int layTongSoLop() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public Optional<PhienDiemDanh> getPhienHienTai() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public Collection<SinhVien> getDanhSachSinhVienLop() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public Set<String> getSetMaSVCoMat() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}

