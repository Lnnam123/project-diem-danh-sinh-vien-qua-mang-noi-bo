package vn.dau.diemdanh.nghiepvu;

import java.util.Collection;
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

    public List<SinhVien> locDanhSachVang(Collection<SinhVien> dsLop, Set<String> setMaSVCoMat) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public Map<String, Object> thongKeBuoi(int tongSoSV, int soLuongCoMat) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public List<CanhBaoChuyenCan> thongKeChuyenCanCaKy(List<BuoiDiemDanh> danhSachCacBuoi, double nguongTiLeVang) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}

