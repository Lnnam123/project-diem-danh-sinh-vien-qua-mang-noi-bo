package vn.dau.diemdanh.nghiepvu;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vn.dau.diemdanh.model.BuoiDiemDanh;
import vn.dau.diemdanh.model.CanhBaoChuyenCan;
import vn.dau.diemdanh.model.SinhVien;

/**
 * Phục vụ chức năng F09, F10, F15: Xử lý thống kê danh sách vắng, tính tỷ lệ chuyên cần bằng Streams API.
 * Áp dụng kỹ thuật: Streams API, filter, map, sorted, collect (Ch2), groupingBy, counting (Ch2), Comparator (Ch3).
 */
public class ThongKeService {

    /**
     * F09: Lọc danh sách sinh viên vắng mặt bằng Streams API.
     */
    public List<SinhVien> locDanhSachVang(Collection<SinhVien> dsLop, Set<String> setMaSVCoMat) {
        if (dsLop == null) {
            return List.of();
        }
        Set<String> coMat = setMaSVCoMat != null ? setMaSVCoMat : Set.of();
        return dsLop.stream()
                .filter(sv -> !coMat.contains(sv.maSV()))
                .sorted(Comparator.comparing(SinhVien::maSV))
                .toList();
    }

    /**
     * F10: Thống kê số lượng và tính tỷ lệ chuyên cần theo buổi học.
     */
    public Map<String, Object> thongKeBuoi(int tongSoSV, int soLuongCoMat) {
        int soLuongVang = Math.max(0, tongSoSV - soLuongCoMat);
        double tiLeCoMat = tongSoSV > 0 ? ((double) soLuongCoMat / tongSoSV) * 100.0 : 0.0;
        double tiLeVang = tongSoSV > 0 ? ((double) soLuongVang / tongSoSV) * 100.0 : 0.0;

        tiLeCoMat = Math.round(tiLeCoMat * 10.0) / 10.0;
        tiLeVang = Math.round(tiLeVang * 10.0) / 10.0;

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("tongSoSV", tongSoSV);
        map.put("soLuongCoMat", soLuongCoMat);
        map.put("soLuongVang", soLuongVang);
        map.put("tiLeCoMat", tiLeCoMat);
        map.put("tiLeVang", tiLeVang);
        return map;
    }

    /**
     * F15: Thống kê chuyên cần cả kỳ theo nhiều buổi và lọc cảnh báo vắng >= nguongTiLeVang (mặc định 20%).
     */
    public List<CanhBaoChuyenCan> thongKeChuyenCanCaKy(List<BuoiDiemDanh> danhSachCacBuoi, double nguongTiLeVang) {
        if (danhSachCacBuoi == null || danhSachCacBuoi.isEmpty()) {
            return List.of();
        }

        int tongSoBuoi = danhSachCacBuoi.size();

        // Thu thập danh sách sinh viên duy nhất
        Map<String, SinhVien> mapSV = new LinkedHashMap<>();
        for (BuoiDiemDanh buoi : danhSachCacBuoi) {
            if (buoi.danhSachLop() != null) {
                for (SinhVien sv : buoi.danhSachLop()) {
                    mapSV.putIfAbsent(sv.maSV(), sv);
                }
            }
        }

        return mapSV.values().stream()
                .map(sv -> {
                    long soBuoiVang = danhSachCacBuoi.stream()
                            .filter(buoi -> buoi.danhSachMaSVCoMat() == null || !buoi.danhSachMaSVCoMat().contains(sv.maSV()))
                            .count();
                    double tiLeVang = tongSoBuoi > 0 ? ((double) soBuoiVang / tongSoBuoi) * 100.0 : 0.0;
                    tiLeVang = Math.round(tiLeVang * 10.0) / 10.0;
                    double tiLeChuyenCan = Math.round((100.0 - tiLeVang) * 10.0) / 10.0;
                    return new CanhBaoChuyenCan(
                            sv.maSV(),
                            sv.hoTen(),
                            sv.lop(),
                            tongSoBuoi,
                            (int) soBuoiVang,
                            tiLeVang,
                            tiLeChuyenCan
                    );
                })
                .filter(cb -> cb.tiLeVang() >= nguongTiLeVang)
                .sorted(Comparator.comparing(CanhBaoChuyenCan::tiLeVang).reversed()
                        .thenComparing(CanhBaoChuyenCan::maSV))
                .toList();
    }
}
