package vn.dau.diemdanh.mang;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import java.util.Scanner;

/**
 * Phục vụ chức năng F08: Ứng dụng dòng lệnh phía sinh viên kết nối gửi điểm danh tới máy chủ qua Socket TCP.
 * Áp dụng kỹ thuật: Socket, BufferedReader, PrintWriter (Ch5), Scanner (Ch1), try-with-resources (Ch6).
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
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }

        // Chế độ 1: Nhận tham số trực tiếp từ dòng lệnh
        if (args.length >= 4) {
            String host = args[0];
            int port;
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("❌ LỖI: Port phải là số nguyên hợp lệ.");
                return;
            }
            String maPhien = args[2];
            String maSV = args[3];

            System.out.printf("📡 Đang gửi điểm danh tới %s:%d (Phiên: %s, Mã SV: %s)...%n", host, port, maPhien, maSV);
            String response = guiDiemDanh(host, port, maPhien, maSV);
            System.out.println("Kết quả: " + response);
            return;
        }

        // Chế độ 2: Giao diện tương tác Console thân thiện khi không truyền đủ tham số
        System.out.println("====================================================================");
        System.out.println("   ỨNG DỤNG ĐIỂM DANH SINH VIÊN QUA MẠNG NỘI BỘ (STUDENT CLIENT)    ");
        System.out.println("====================================================================");

        try (Scanner scanner = new Scanner(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            System.out.print("🌐 Nhập IP Máy chủ [Enter để lấy mặc định 127.0.0.1]: ");
            String hostInput = scanner.nextLine().strip();
            String host = hostInput.isEmpty() ? "127.0.0.1" : hostInput;

            System.out.print("🔌 Nhập Cổng kết nối [Enter để lấy mặc định 8888]: ");
            String portInput = scanner.nextLine().strip();
            int port = 8888;
            if (!portInput.isEmpty()) {
                try {
                    port = Integer.parseInt(portInput);
                } catch (NumberFormatException e) {
                    System.out.println("⚠️ Cổng không hợp lệ, tự động dùng cổng mặc định 8888.");
                    port = 8888;
                }
            }

            while (true) {
                System.out.println("--------------------------------------------------------------------");
                System.out.print("🔑 Nhập Mã phiên điểm danh (ví dụ P01, hoặc 'exit' để thoát): ");
                String maPhien = scanner.nextLine().strip();
                if (maPhien.equalsIgnoreCase("exit") || maPhien.equalsIgnoreCase("0")) {
                    break;
                }
                if (maPhien.isEmpty()) {
                    System.out.println("⚠️ Mã phiên không được để trống!");
                    continue;
                }

                System.out.print("🎓 Nhập Mã sinh viên (ví dụ 22CNTT01): ");
                String maSV = scanner.nextLine().strip();
                if (maSV.isEmpty()) {
                    System.out.println("⚠️ Mã sinh viên không được để trống!");
                    continue;
                }

                System.out.printf("📡 Đang gửi tới máy chủ %s:%d...%n", host, port);
                String response = guiDiemDanh(host, port, maPhien, maSV);

                if (response.startsWith("SUCCESS:")) {
                    System.out.println("✅ THÀNH CÔNG: " + response.substring("SUCCESS:".length()).trim());
                } else if (response.startsWith("ERROR:")) {
                    System.out.println("❌ THẤT BẠI: " + response.substring("ERROR:".length()).trim());
                } else {
                    System.out.println("ℹ️ PHẢN HỒI: " + response);
                }

                System.out.print("\n👉 Bạn có muốn gửi tiếp lượt điểm danh khác? [Enter: Tiếp tục / Gõ 0: Thoát]: ");
                String tiep = scanner.nextLine().strip();
                if (tiep.equalsIgnoreCase("0") || tiep.equalsIgnoreCase("k") || tiep.equalsIgnoreCase("n")) {
                    break;
                }
            }
        }
        System.out.println("👋 Tạm biệt!");
    }
}
