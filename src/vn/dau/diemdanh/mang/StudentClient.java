package vn.dau.diemdanh.mang;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Phục vụ chức năng F08: Ứng dụng dòng lệnh phía sinh viên kết nối gửi điểm danh tới máy chủ qua Socket TCP.
 * Áp dụng kỹ thuật: Socket, BufferedReader, PrintWriter (Ch5), try-with-resources (Ch6).
 */
public class StudentClient {

    /**
     * F08: Gửi bản tin điểm danh tới máy chủ qua TCP Socket và nhận kết quả phản hồi.
     */
    public static String guiDiemDanh(String host, int port, String maPhien, String maSV) {
        if (host == null || host.strip().isEmpty()) {
            return "ERROR: Địa chỉ máy chủ (host) không được rỗng";
        }
        if (port <= 0 || port > 65535) {
            return "ERROR: Cổng mạng (port) không hợp lệ: " + port;
        }
        if (maPhien == null || maPhien.strip().isEmpty()) {
            return "ERROR: Mã phiên điểm danh không được rỗng";
        }
        if (maSV == null || maSV.strip().isEmpty()) {
            return "ERROR: Mã sinh viên không được rỗng";
        }

        try (
                Socket socket = new Socket(host.strip(), port);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)
        ) {
            // Gửi thông điệp theo giao thức
            out.println("DIEMDANH " + maPhien.strip() + " " + maSV.strip());

            // Đọc phản hồi từ máy chủ
            String response = in.readLine();
            return response != null ? response : "ERROR: Máy chủ đóng kết nối mà không phản hồi";
        } catch (Exception e) {
            return "ERROR: Lỗi kết nối tới máy chủ (" + host + ":" + port + "): " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("CÚ PHÁP: java vn.dau.diemdanh.mang.StudentClient <IP_Server> <Port> <Mã_Phiên> <Mã_SV>");
            System.out.println("VÍ DỤ:  java vn.dau.diemdanh.mang.StudentClient localhost 8888 P01 22CNTT01");
            return;
        }

        String host = args[0];
        int port;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("LỖI: Port phải là số nguyên hợp lệ.");
            return;
        }
        String maPhien = args[2];
        String maSV = args[3];

        System.out.printf("Đang gửi điểm danh tới %s:%d (Phiên: %s, Mã SV: %s)...%n", host, port, maPhien, maSV);
        String response = guiDiemDanh(host, port, maPhien, maSV);
        System.out.println("KẾT QUẢ: " + response);
    }
}
