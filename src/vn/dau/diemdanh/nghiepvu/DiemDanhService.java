package vn.dau.diemdanh.nghiepvu;

import java.time.LocalDateTime;
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

    /**
     * F02: Nạp danh sách sinh viên lớp học vào bộ nhớ.
     */
    public void napDanhSachLop(Map<String, SinhVien> danhSach) {
        mapSinhVien.clear();
        if (danhSach != null) {
            mapSinhVien.putAll(danhSach);
        }
        setMaSVCoMat.clear();
        danhSachLuotDiemDanh.clear();
        chongGianLanService.reset();
    }

    /**
     * F03, F04: Mở phiên điểm danh mới.
     */
    public KetQua moPhien(String maPhien, int thoiLuongPhut) {
        setMaSVCoMat.clear();
        danhSachLuotDiemDanh.clear();
        chongGianLanService.reset();
        return phienManager.moPhien(maPhien, thoiLuongPhut, null);
    }

    /**
     * F03: Đóng phiên điểm danh thủ công.
     */
    public KetQua dongPhien() {
        return phienManager.dongPhien();
    }

    /**
     * F05, F14: Tiếp nhận điểm danh, chống trùng mã sinh viên và chống gian lận IP.
     */
    public KetQua ghiNhanDiemDanh(String maPhien, String maSV, String ipAddress) {
        // 1. Kiểm tra trạng thái phiên
        if (!phienManager.isConMo()) {
            return new KetQua.ThatBai("Phiên điểm danh hiện không mở hoặc đã hết hạn", "PHIEN_KHONG_MO");
        }

        Optional<PhienDiemDanh> optPhien = phienManager.getPhienHienTai();
        if (optPhien.isEmpty() || !optPhien.get().maPhien().equalsIgnoreCase(maPhien != null ? maPhien.strip() : "")) {
            return new KetQua.ThatBai("Mã phiên không khớp với phiên hiện tại", "SAI_MA_PHIEN");
        }
        PhienDiemDanh phien = optPhien.get();

        // 2. Kiểm tra mã sinh viên
        if (maSV == null || maSV.strip().isEmpty()) {
            return new KetQua.ThatBai("Mã sinh viên không được để trống", "MA_SV_RONG");
        }
        String cleanMaSV = maSV.strip();

        SinhVien sv = mapSinhVien.get(cleanMaSV);
        if (sv == null) {
            return new KetQua.ThatBai("Sinh viên không tồn tại trong danh sách lớp: " + cleanMaSV, "SV_KHONG_TON_TAI");
        }

        // 3. Chống trùng lặp mã sinh viên (Concurrent Set)
        if (setMaSVCoMat.contains(cleanMaSV)) {
            return new KetQua.ThatBai("Sinh viên " + cleanMaSV + " đã điểm danh trước đó", "DA_DIEM_DANH");
        }

        // 4. Kiểm tra chống gian lận IP (F14)
        KetQua checkIP = chongGianLanService.kiemTraVaGhiNhanIP(ipAddress, cleanMaSV);
        if (!checkIP.isThanhCong()) {
            return checkIP;
        }

        // 5. Ghi nhận có mặt đồng thời an toàn luồng
        boolean added = setMaSVCoMat.add(cleanMaSV);
        if (!added) {
            return new KetQua.ThatBai("Sinh viên " + cleanMaSV + " đã điểm danh trước đó", "DA_DIEM_DANH");
        }

        LuotDiemDanh luot = new LuotDiemDanh(
                cleanMaSV,
                phien.maPhien(),
                LocalDateTime.now(),
                ipAddress != null ? ipAddress.strip() : "127.0.0.1"
        );
        danhSachLuotDiemDanh.add(luot);

        return new KetQua.ThanhCong("Điểm danh thành công cho sinh viên: " + sv.hoTen(), luot);
    }

    /**
     * F09: Lọc danh sách sinh viên vắng mặt bằng Streams API.
     */
    public List<SinhVien> layDanhSachVang() {
        return thongKeService.locDanhSachVang(mapSinhVien.values(), setMaSVCoMat);
    }

    public int laySoLuongCoMat() {
        return setMaSVCoMat.size();
    }

    public int layTongSoLop() {
        return mapSinhVien.size();
    }

    public Optional<PhienDiemDanh> getPhienHienTai() {
        return phienManager.getPhienHienTai();
    }

    public Collection<SinhVien> getDanhSachSinhVienLop() {
        return mapSinhVien.values();
    }

    public Set<String> getSetMaSVCoMat() {
        return setMaSVCoMat;
    }
}
