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
import java.util.Map;

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
}
