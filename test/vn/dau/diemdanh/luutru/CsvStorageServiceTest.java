package vn.dau.diemdanh.luutru;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import vn.dau.diemdanh.exception.DiemDanhException;
import vn.dau.diemdanh.model.SinhVien;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử Tầng Lưu Trữ CSV - Chức năng F02")
class CsvStorageServiceTest {

    private CsvStorageService storageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        storageService = new CsvStorageService();
    }

    @Test
    @DisplayName("Đọc file CSV danh sách lớp hợp lệ thành công với tiếng Việt UTF-8")
    void docDanhSachLopHopLe() throws IOException, DiemDanhException {
        Path fileCsv = tempDir.resolve("danh-sach.csv");
        String content = """
                maSV,hoTen,lop
                22CNTT01,Nguyễn Văn An,22CNTT1
                22CNTT02,Trần Thị Bình,22CNTT1
                22CNTT03,Lê Hoàng Cường,22CNTT2
                """;
        Files.writeString(fileCsv, content, StandardCharsets.UTF_8);

        Map<String, SinhVien> ketQua = storageService.docDanhSachLop(fileCsv);

        assertNotNull(ketQua);
        assertEquals(3, ketQua.size());
        assertTrue(ketQua.containsKey("22CNTT01"));
        assertEquals("Nguyễn Văn An", ketQua.get("22CNTT01").hoTen());
        assertEquals("22CNTT1", ketQua.get("22CNTT01").lop());
    }

    @Test
    @DisplayName("Ném ngoại lệ khi đường dẫn null hoặc file không tồn tại")
    void nemNgoaiLeKhiFileKhongTonTai() {
        assertThrows(DiemDanhException.class, () -> storageService.docDanhSachLop(null));

        Path fileKhongTonTai = tempDir.resolve("khong-ton-tai.csv");
        assertThrows(DiemDanhException.class, () -> storageService.docDanhSachLop(fileKhongTonTai));
    }

    @Test
    @DisplayName("Đọc file CSV rỗng (hoặc chỉ có header) trả về Map rỗng")
    void docFileRongTraVeMapRong() throws IOException, DiemDanhException {
        Path fileRong = tempDir.resolve("rong.csv");
        Files.writeString(fileRong, "", StandardCharsets.UTF_8);

        Map<String, SinhVien> ketQua = storageService.docDanhSachLop(fileRong);
        assertNotNull(ketQua);
        assertTrue(ketQua.isEmpty());

        Path fileChiCoHeader = tempDir.resolve("header.csv");
        Files.writeString(fileChiCoHeader, "maSV,hoTen,lop\n", StandardCharsets.UTF_8);
        Map<String, SinhVien> ketQuaHeader = storageService.docDanhSachLop(fileChiCoHeader);
        assertNotNull(ketQuaHeader);
        assertTrue(ketQuaHeader.isEmpty());
    }

    @Test
    @DisplayName("Ném ngoại lệ khi dòng CSV sai định dạng (thiếu cột)")
    void nemNgoaiLeKhiDongCsvSaiDinhDang() throws IOException {
        Path fileLoi = tempDir.resolve("sai-dinh-dang.csv");
        String content = """
                maSV,hoTen,lop
                22CNTT01,Nguyễn Văn An
                """;
        Files.writeString(fileLoi, content, StandardCharsets.UTF_8);

        assertThrows(DiemDanhException.class, () -> storageService.docDanhSachLop(fileLoi));
    }

    @Test
    @DisplayName("Ném ngoại lệ khi trùng lặp mã sinh viên trong file CSV")
    void nemNgoaiLeKhiTrungLapMaSV() throws IOException {
        Path fileTrung = tempDir.resolve("trung-lap.csv");
        String content = """
                maSV,hoTen,lop
                22CNTT01,Nguyễn Văn An,22CNTT1
                22CNTT01,Nguyễn Văn An Trùng,22CNTT1
                """;
        Files.writeString(fileTrung, content, StandardCharsets.UTF_8);

        assertThrows(DiemDanhException.class, () -> storageService.docDanhSachLop(fileTrung));
    }

    @Test
    @DisplayName("F11: Xuất kết quả buổi điểm danh ra file CSV chính xác")
    void xuatKetQuaBuoiChinhXac() throws DiemDanhException, IOException {
        Path xuatFile = tempDir.resolve("ket-qua/buoi-01.csv");
        vn.dau.diemdanh.model.PhienDiemDanh phien = new vn.dau.diemdanh.model.PhienDiemDanh(
                "P01",
                java.time.LocalDateTime.now(),
                15,
                vn.dau.diemdanh.model.TrangThaiPhien.DA_DONG
        );
        SinhVien sv1 = new SinhVien("SV01", "Nguyễn Văn A", "22CNTT1");
        SinhVien sv2 = new SinhVien("SV02", "Trần Thị B", "22CNTT1");
        List<SinhVien> dsLop = List.of(sv1, sv2);
        Set<String> setCoMat = Set.of("SV01");

        storageService.xuatKetQuaBuoi(xuatFile, phien, dsLop, setCoMat);

        assertTrue(Files.exists(xuatFile));
        List<String> lines = Files.readAllLines(xuatFile, StandardCharsets.UTF_8);
        assertTrue(lines.size() >= 3);
        assertEquals("maSV,hoTen,lop,trangThai", lines.get(0));
        assertTrue(lines.get(1).contains("SV01") && lines.get(1).contains("CO_MAT"));
        assertTrue(lines.get(2).contains("SV02") && lines.get(2).contains("VANG"));
    }

    @Test
    @DisplayName("F15: Đọc lịch sử các buổi học từ thư mục CSV để phục vụ thống kê cả kỳ")
    void docLichSuCacBuoi() throws DiemDanhException, IOException {
        Path thuMucLichSu = tempDir.resolve("lich-su");
        Files.createDirectories(thuMucLichSu);

        Path b1 = thuMucLichSu.resolve("buoi-P01.csv");
        String c1 = """
                maSV,hoTen,lop,trangThai
                SV01,Nguyễn Văn A,22CNTT1,CO_MAT
                SV02,Trần Thị B,22CNTT1,VANG
                """;
        Files.writeString(b1, c1, StandardCharsets.UTF_8);

        List<vn.dau.diemdanh.model.BuoiDiemDanh> dsBuoi = storageService.docLichSuCacBuoi(thuMucLichSu);

        assertEquals(1, dsBuoi.size());
        assertEquals("buoi-P01", dsBuoi.get(0).maPhien());
        assertTrue(dsBuoi.get(0).danhSachMaSVCoMat().contains("SV01"));
        assertFalse(dsBuoi.get(0).danhSachMaSVCoMat().contains("SV02"));
    }
}
