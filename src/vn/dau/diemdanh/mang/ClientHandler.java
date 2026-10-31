package vn.dau.diemdanh.mang;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import vn.dau.diemdanh.exception.GiaoThucException;
import vn.dau.diemdanh.nghiepvu.DiemDanhService;
import vn.dau.diemdanh.nghiepvu.KetQua;

/**
 * Phục vụ chức năng F07: Tác vụ phục vụ một kết nối máy khách độc lập trên một luồng của bể luồng.
 * Áp dụng kỹ thuật: Runnable (Ch4), Socket, BufferedReader, PrintWriter (Ch5), try-with-resources (Ch6).
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final DiemDanhService service;

    public ClientHandler(Socket socket, DiemDanhService service) {
        this.socket = socket;
        this.service = service;
    }

    @Override
    public void run() {
        try (
                socket;
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)
        ) {
            String clientIP = socket.getInetAddress() != null ? socket.getInetAddress().getHostAddress() : "UNKNOWN";
            String line = in.readLine();
            if (line != null) {
                String response = xuLyYeuCau(line, clientIP);
                out.println(response);
            }
        } catch (Exception ignored) {
            // Ngắt kết nối an toàn khi mạng có sự cố hoặc client đóng socket
        }
    }

    protected String xuLyYeuCau(String line, String clientIP) {
        try {
            RequestCommand cmd = ProtocolParser.parse(line);
            return switch (cmd.command()) {
                case "DIEMDANH" -> {
                    KetQua kq = service.ghiNhanDiemDanh(cmd.maPhien(), cmd.maSV(), clientIP);
                    if (kq.isThanhCong()) {
                        yield ProtocolParser.formatResponse("SUCCESS", kq.thongDiep());
                    } else {
                        yield ProtocolParser.formatResponse("ERROR", kq.thongDiep());
                    }
                }
                case "STATUS" -> {
                    var optPhien = service.getPhienHienTai();
                    if (optPhien.isPresent() && optPhien.get().isConHieuLuc()) {
                        yield ProtocolParser.formatResponse("SUCCESS", "Phiên " + optPhien.get().maPhien() + " đang mở (" + service.laySoLuongCoMat() + "/" + service.layTongSoLop() + ")");
                    } else {
                        yield ProtocolParser.formatResponse("INFO", "Không có phiên điểm danh nào đang mở");
                    }
                }
                case "PING" -> ProtocolParser.formatResponse("PONG", "Máy chủ đang hoạt động");
                default -> ProtocolParser.formatResponse("ERROR", "Lệnh không hỗ trợ: " + cmd.command());
            };
        } catch (GiaoThucException e) {
            return ProtocolParser.formatResponse("ERROR", e.getMessage());
        } catch (Exception e) {
            return ProtocolParser.formatResponse("ERROR", "Lỗi nội bộ máy chủ: " + e.getMessage());
        }
    }
}
