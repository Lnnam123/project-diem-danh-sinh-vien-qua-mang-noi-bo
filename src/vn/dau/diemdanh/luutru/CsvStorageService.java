package vn.dau.diemdanh.luutru;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
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

    /**
     * F02: Đọc danh sách lớp từ tệp CSV và nạp vào bộ nhớ.
     */
    public Map<String, SinhVien> docDanhSachLop(Path csvPath) throws DiemDanhException {
        if (csvPath == null || !Files.exists(csvPath)) {
            throw new DiemDanhException("Tệp danh sách lớp không tồn tại hoặc đường dẫn null: " + csvPath);
        }

        Map<String, SinhVien> danhSach = new LinkedHashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.strip();
                if (line.isEmpty()) {
                    continue;
                }

                // Bỏ qua dòng tiêu đề nếu có
                if (lineNumber == 1 && line.toLowerCase().contains("masv")) {
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length < 3) {
                    throw new DiemDanhException("Dòng " + lineNumber + " sai định dạng CSV (thiếu cột): " + line);
                }

                String maSV = parts[0].strip();
                String hoTen = parts[1].strip();
                String lop = parts[2].strip();

                if (maSV.isEmpty() || hoTen.isEmpty() || lop.isEmpty()) {
                    throw new DiemDanhException("Dòng " + lineNumber + " chứa trường thông tin rỗng: " + line);
                }

                if (danhSach.containsKey(maSV)) {
                    throw new DiemDanhException("Trùng lặp mã sinh viên tại dòng " + lineNumber + ": " + maSV);
                }

                danhSach.put(maSV, new SinhVien(maSV, hoTen, lop));
            }
        } catch (IOException e) {
            throw new DiemDanhException("Lỗi I/O khi đọc tệp danh sách lớp CSV: " + e.getMessage(), e);
        }

        return danhSach;
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
