package vn.dau.diemdanh.mang;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vn.dau.diemdanh.exception.GiaoThucException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử Giao thức Mạng Văn bản - Chức năng F06")
class ProtocolParserTest {

    @Test
    @DisplayName("Phân tích thành công lệnh DIEMDANH với đầy đủ tham số")
    void parseLenhDiemDanhHopLe() throws GiaoThucException {
        RequestCommand cmd = ProtocolParser.parse("DIEMDANH P01 22CNTT01");

        assertNotNull(cmd);
        assertEquals("DIEMDANH", cmd.command());
        assertEquals("P01", cmd.maPhien());
        assertEquals("22CNTT01", cmd.maSV());
    }

    @Test
    @DisplayName("Tự động cắt khoảng trắng thừa giữa các tham số")
    void parseCatKhoangTrangThua() throws GiaoThucException {
        RequestCommand cmd = ProtocolParser.parse("   DIEMDANH    P01     22CNTT01   ");

        assertEquals("DIEMDANH", cmd.command());
        assertEquals("P01", cmd.maPhien());
        assertEquals("22CNTT01", cmd.maSV());
    }

    @Test
    @DisplayName("Phân tích lệnh không tham số như PING hoặc STATUS")
    void parseLenhKhongThamSo() throws GiaoThucException {
        RequestCommand ping = ProtocolParser.parse("PING");
        assertEquals("PING", ping.command());

        RequestCommand status = ProtocolParser.parse("STATUS");
        assertEquals("STATUS", status.command());
    }

    @Test
    @DisplayName("Ném GiaoThucException khi lệnh rỗng hoặc toàn khoảng trắng")
    void nemNgoaiLeKhiLenhRong() {
        assertThrows(GiaoThucException.class, () -> ProtocolParser.parse(null));
        assertThrows(GiaoThucException.class, () -> ProtocolParser.parse(""));
        assertThrows(GiaoThucException.class, () -> ProtocolParser.parse("    "));
    }

    @Test
    @DisplayName("Ném GiaoThucException khi lệnh không được hỗ trợ")
    void nemNgoaiLeKhiLenhKhongHopLe() {
        assertThrows(GiaoThucException.class, () -> ProtocolParser.parse("UNKNOWN_CMD P01 SV01"));
    }

    @Test
    @DisplayName("Ném GiaoThucException khi lệnh DIEMDANH thiếu tham số bắt buộc")
    void nemNgoaiLeKhiThieuThamSo() {
        assertThrows(GiaoThucException.class, () -> ProtocolParser.parse("DIEMDANH"));
        assertThrows(GiaoThucException.class, () -> ProtocolParser.parse("DIEMDANH P01"));
    }

    @Test
    @DisplayName("Định dạng thông điệp phản hồi chuẩn giao thức mạng")
    void formatResponseChuan() {
        String resOk = ProtocolParser.formatResponse("SUCCESS", "Điểm danh thành công");
        assertEquals("SUCCESS: Điểm danh thành công", resOk);

        String resErr = ProtocolParser.formatResponse("ERROR", "Sinh viên không tồn tại");
        assertEquals("ERROR: Sinh viên không tồn tại", resErr);
    }
}
