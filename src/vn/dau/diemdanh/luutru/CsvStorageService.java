package vn.dau.diemdanh.luutru;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vn.dau.diemdanh.exception.DiemDanhException;
import vn.dau.diemdanh.model.BuoiDiemDanh;
import vn.dau.diemdanh.model.PhienDiemDanh;
import vn.dau.diemdanh.model.SinhVien;
import vn.dau.diemdanh.model.TrangThaiDiemDanh;

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

    /**
     * F11: Xuất bảng kết quả điểm danh của buổi học ra tệp CSV.
     */
    public void xuatKetQuaBuoi(
            Path outputPath,
            PhienDiemDanh phien,
            Collection<SinhVien> dsLop,
            Set<String> setCoMat
    ) throws DiemDanhException {
        if (outputPath == null) {
            throw new DiemDanhException("Đường dẫn xuất kết quả không được null");
        }

        try {
            if (outputPath.getParent() != null) {
                Files.createDirectories(outputPath.getParent());
            }

            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8))) {
                writer.println("maSV,hoTen,lop,trangThai");
                if (dsLop != null) {
                    List<SinhVien> sorted = dsLop.stream()
                            .sorted(Comparator.comparing(SinhVien::maSV))
                            .toList();
                    for (SinhVien sv : sorted) {
                        boolean coMat = setCoMat != null && setCoMat.contains(sv.maSV());
                        String trangThai = coMat ? TrangThaiDiemDanh.CO_MAT.name() : TrangThaiDiemDanh.VANG.name();
                        writer.printf("%s,%s,%s,%s%n", sv.maSV(), sv.hoTen(), sv.lop(), trangThai);
                    }
                }
            }
        } catch (IOException e) {
            throw new DiemDanhException("Lỗi I/O khi xuất kết quả điểm danh CSV: " + e.getMessage(), e);
        }
    }

    /**
     * F15: Đọc lịch sử các buổi điểm danh từ thư mục lưu trữ CSV để phục vụ thống kê cả kỳ.
     */
    public List<BuoiDiemDanh> docLichSuCacBuoi(Path thuMucChuaCacBuoi) throws DiemDanhException {
        if (thuMucChuaCacBuoi == null || !Files.exists(thuMucChuaCacBuoi) || !Files.isDirectory(thuMucChuaCacBuoi)) {
            return List.of();
        }

        List<BuoiDiemDanh> ketQua = new ArrayList<>();

        try (var stream = Files.list(thuMucChuaCacBuoi)) {
            List<Path> csvFiles = stream
                    .filter(p -> p.getFileName().toString().endsWith(".csv"))
                    .sorted()
                    .toList();

            for (Path csvFile : csvFiles) {
                String fileName = csvFile.getFileName().toString();
                String maPhien = fileName.substring(0, fileName.lastIndexOf('.'));

                List<SinhVien> dsLop = new ArrayList<>();
                Set<String> setCoMat = new HashSet<>();

                try (BufferedReader reader = Files.newBufferedReader(csvFile, StandardCharsets.UTF_8)) {
                    String line;
                    int lineNum = 0;
                    while ((line = reader.readLine()) != null) {
                        lineNum++;
                        line = line.strip();
                        if (line.isEmpty() || (lineNum == 1 && line.toLowerCase().contains("masv"))) {
                            continue;
                        }

                        String[] parts = line.split(",", -1);
                        if (parts.length >= 4) {
                            String maSV = parts[0].strip();
                            String hoTen = parts[1].strip();
                            String lop = parts[2].strip();
                            String trangThai = parts[3].strip();

                            dsLop.add(new SinhVien(maSV, hoTen, lop));
                            if (TrangThaiDiemDanh.CO_MAT.name().equalsIgnoreCase(trangThai)) {
                                setCoMat.add(maSV);
                            }
                        }
                    }
                }

                ketQua.add(new BuoiDiemDanh(maPhien, LocalDate.now(), setCoMat, dsLop));
            }
        } catch (IOException e) {
            throw new DiemDanhException("Lỗi I/O khi đọc thư mục lịch sử điểm danh: " + e.getMessage(), e);
        }

        return ketQua;
    }
}
