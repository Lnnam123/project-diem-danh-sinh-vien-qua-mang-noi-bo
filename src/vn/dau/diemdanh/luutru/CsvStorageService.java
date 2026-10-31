package vn.dau.diemdanh.luutru;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vn.dau.diemdanh.exception.DiemDanhException;
import vn.dau.diemdanh.model.BuoiDiemDanh;
import vn.dau.diemdanh.model.PhienDiemDanh;
import vn.dau.diemdanh.model.SinhVien;

/**
 * Phục vụ chức năng F02, F11, F15: Xử lý đọc và ghi dữ liệu ra tệp định dạng CSV.
 * Áp dụng kỹ thuật: try-with-resources (Ch6), BufferedReader, PrintWriter (Ch5), custom exception (Ch6).
 */
public class CsvStorageService {

    public Map<String, SinhVien> docDanhSachLop(Path csvPath) throws DiemDanhException {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public void xuatKetQuaBuoi(
            Path outputPath,
            PhienDiemDanh phien,
            Collection<SinhVien> dsLop,
            Set<String> setCoMat
    ) throws DiemDanhException {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public List<BuoiDiemDanh> docLichSuCacBuoi(Path thuMucChuaCacBuoi) throws DiemDanhException {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}

