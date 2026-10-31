package vn.dau.diemdanh.giaodien;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import vn.dau.diemdanh.exception.DiemDanhException;
import vn.dau.diemdanh.luutru.CsvStorageService;
import vn.dau.diemdanh.mang.DiemDanhServer;
import vn.dau.diemdanh.model.BuoiDiemDanh;
import vn.dau.diemdanh.model.CanhBaoChuyenCan;
import vn.dau.diemdanh.model.PhienDiemDanh;
import vn.dau.diemdanh.model.SinhVien;
import vn.dau.diemdanh.nghiepvu.ChongGianLanService;
import vn.dau.diemdanh.nghiepvu.DiemDanhService;
import vn.dau.diemdanh.nghiepvu.KetQua;
import vn.dau.diemdanh.nghiepvu.PhienManager;
import vn.dau.diemdanh.nghiepvu.ThongKeService;

/**
 * Phục vụ chức năng F12: Giao diện dòng lệnh tương tác phía Giảng viên (Console CLI).
 * Áp dụng kỹ thuật: switch expression (Ch6), text block (Ch6), BufferedReader (Ch5), try-with-resources (Ch6).
 */
public class TeacherConsoleUI {

    private final DiemDanhService service;
    private final DiemDanhServer server;
    private final CsvStorageService storage;

    public TeacherConsoleUI(
            DiemDanhService service,
            DiemDanhServer server,
            CsvStorageService storage
    ) {
        this.service = service;
        this.server = server;
        this.storage = storage;
    }

    public void start() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            start(reader, System.out);
        } catch (Exception e) {
            System.err.println("Lỗi giao diện điều khiển: " + e.getMessage());
        }
    }

    public void start(BufferedReader in, PrintStream out) {
        boolean tiepTuc = true;
        out.println("====================================================================");
        out.println("   HỆ THỐNG ĐIỂM DANH LỚP HỌC QUA MẠNG NỘI BỘ - TEACHER CONSOLE");
        out.println("====================================================================");

        while (tiepTuc) {
            inMenu(out);
            out.print("👉 Lựa chọn chức năng [0-6]: ");
            String choice = "";
            try {
                choice = in.readLine();
                if (choice == null) {
                    break;
                }
                choice = choice.strip();
            } catch (Exception e) {
                out.println("Lỗi đọc dữ liệu nhập: " + e.getMessage());
                break;
            }

            switch (choice) {
                case "1" -> xuLyNapDanhSach(in, out);
                case "2" -> xuLyMoPhien(in, out);
                case "3" -> xuLyDongPhien(out);
                case "4" -> xuLyXemThongKe(out);
                case "5" -> xuLyXuatKetQuaBuoi(in, out);
                case "6" -> xuLyThongKeCaKy(in, out);
                case "0" -> {
                    out.println("Đang tắt ứng dụng và đóng máy chủ...");
                    tiepTuc = false;
                }
                default -> out.println("⚠️ Lựa chọn không hợp lệ, vui lòng chọn từ 0 đến 6.");
            }
        }
    }

    private void inMenu(PrintStream out) {
        out.println();
        out.println("------------------------- MENU ĐIỀU KHIỂN --------------------------");
        out.println("1. Nạp danh sách lớp từ file CSV");
        out.println("2. Mở phiên điểm danh lớp học");
        out.println("3. Đóng phiên điểm danh thủ công");
        out.println("4. Xem thống kê tình hình điểm danh hiện tại");
        out.println("5. Xuất bảng kết quả điểm danh buổi học ra file CSV");
        out.println("6. Thống kê chuyên cần cả kỳ & Cảnh báo sinh viên vắng >= 20%");
        out.println("0. Thoát chương trình");
        out.println("--------------------------------------------------------------------");
    }

    private void xuLyNapDanhSach(BufferedReader in, PrintStream out) {
        try {
            out.print("Nhập đường dẫn file CSV danh sách lớp (Enter để dùng 'data/danh-sach-lop.csv'): ");
            String pathStr = in.readLine();
            if (pathStr == null || pathStr.strip().isEmpty()) {
                pathStr = "data/danh-sach-lop.csv";
            }
            Path path = Paths.get(pathStr.strip());
            Map<String, SinhVien> ds = storage.docDanhSachLop(path);
            service.napDanhSachLop(ds);
            out.printf("✅ Đã nạp thành công %d sinh viên từ file: %s%n", ds.size(), path.toString());
        } catch (DiemDanhException e) {
            out.println("❌ Lỗi nạp danh sách: " + e.getMessage());
        } catch (Exception e) {
            out.println("❌ Lỗi không xác định: " + e.getMessage());
        }
    }

    private void xuLyMoPhien(BufferedReader in, PrintStream out) {
        try {
            if (service.layTongSoLop() == 0) {
                out.println("⚠️ Cảnh báo: Danh sách lớp hiện đang rỗng. Hãy nạp danh sách lớp (menu 1) trước!");
            }

            out.print("Nhập mã phiên điểm danh (ví dụ: P01): ");
            String maPhien = in.readLine();
            if (maPhien == null || maPhien.strip().isEmpty()) {
                out.println("❌ Mã phiên không được để trống!");
                return;
            }

            out.print("Nhập thời lượng mở phiên (phút): ");
            String thoiLuongStr = in.readLine();
            int thoiLuong;
            try {
                thoiLuong = Integer.parseInt(thoiLuongStr.strip());
            } catch (Exception e) {
                out.println("❌ Thời lượng phải là số nguyên dương!");
                return;
            }

            KetQua kq = service.moPhien(maPhien.strip(), thoiLuong);
            if (kq.isThanhCong()) {
                out.printf("✅ %s (Mã phiên: %s, Thời lượng: %d phút)%n", kq.thongDiep(), maPhien.strip(), thoiLuong);
            } else {
                out.printf("❌ Thất bại: %s%n", kq.thongDiep());
            }
        } catch (Exception e) {
            out.println("❌ Lỗi mở phiên: " + e.getMessage());
        }
    }

    private void xuLyDongPhien(PrintStream out) {
        KetQua kq = service.dongPhien();
        if (kq.isThanhCong()) {
            out.printf("✅ %s%n", kq.thongDiep());
        } else {
            out.printf("❌ Thất bại: %s%n", kq.thongDiep());
        }
    }

    private void xuLyXemThongKe(PrintStream out) {
        Optional<PhienDiemDanh> optPhien = service.getPhienHienTai();
        out.println();
        out.println("=== BÁO CÁO TÌNH HÌNH ĐIỂM DANH ===");
        if (optPhien.isPresent()) {
            PhienDiemDanh p = optPhien.get();
            out.printf("Phiên hiện tại: %s | Trạng thái: %s | Mở lúc: %s%n",
                    p.maPhien(),
                    p.isConHieuLuc() ? "ĐANG MỞ" : "ĐÃ ĐÓNG / HẾT HẠN",
                    p.thoiGianMo());
        } else {
            out.println("Chưa có phiên điểm danh nào được tạo.");
        }

        int tongSo = service.layTongSoLop();
        int coMat = service.laySoLuongCoMat();
        int vang = Math.max(0, tongSo - coMat);
        double tiLe = tongSo > 0 ? ((double) coMat / tongSo) * 100.0 : 0.0;

        out.printf("Sĩ số: %d | Có mặt: %d (%.1f%%) | Vắng mặt: %d%n", tongSo, coMat, tiLe, vang);

        List<SinhVien> dsVang = service.layDanhSachVang();
        if (!dsVang.isEmpty()) {
            out.println("--- Danh sách sinh viên vắng mặt ---");
            for (SinhVien sv : dsVang) {
                out.printf(" - %s | %s | %s%n", sv.maSV(), sv.hoTen(), sv.lop());
            }
        } else if (tongSo > 0) {
            out.println("🎉 Toàn bộ sinh viên đã điểm danh có mặt đầy đủ!");
        }
    }

    private void xuLyXuatKetQuaBuoi(BufferedReader in, PrintStream out) {
        Optional<PhienDiemDanh> optPhien = service.getPhienHienTai();
        if (optPhien.isEmpty()) {
            out.println("❌ Chưa có phiên điểm danh nào để xuất kết quả.");
            return;
        }

        PhienDiemDanh phien = optPhien.get();
        try {
            out.print("Nhập tên tệp kết quả (Enter để dùng 'data/ket-qua/ketqua-" + phien.maPhien() + ".csv'): ");
            String pathStr = in.readLine();
            if (pathStr == null || pathStr.strip().isEmpty()) {
                pathStr = "data/ket-qua/ketqua-" + phien.maPhien() + ".csv";
            }
            Path path = Paths.get(pathStr.strip());

            Collection<SinhVien> dsLop = service.getDanhSachSinhVienLop();
            Set<String> setCoMat = service.getSetMaSVCoMat();

            storage.xuatKetQuaBuoi(path, phien, dsLop, setCoMat);
            out.printf("✅ Đã xuất kết quả thành công ra tệp: %s%n", path.toString());
        } catch (Exception e) {
            out.println("❌ Lỗi xuất kết quả: " + e.getMessage());
        }
    }

    private void xuLyThongKeCaKy(BufferedReader in, PrintStream out) {
        try {
            out.print("Nhập thư mục chứa các tệp kết quả buổi (Enter để dùng 'data/ket-qua'): ");
            String dirStr = in.readLine();
            if (dirStr == null || dirStr.strip().isEmpty()) {
                dirStr = "data/ket-qua";
            }
            Path dir = Paths.get(dirStr.strip());

            List<BuoiDiemDanh> dsBuoi = storage.docLichSuCacBuoi(dir);
            if (dsBuoi.isEmpty()) {
                out.println("⚠️ Không tìm thấy file buổi điểm danh (.csv) nào trong thư mục: " + dir);
                return;
            }

            ThongKeService tkService = new ThongKeService();
            List<CanhBaoChuyenCan> dsCanhBao = tkService.thongKeChuyenCanCaKy(dsBuoi, 20.0);

            out.printf("%n=== KẾT QUẢ THỐNG KÊ CHUYÊN CẦN CẢ KỲ (%d buổi) ===%n", dsBuoi.size());
            if (dsCanhBao.isEmpty()) {
                out.println("✅ Tuyệt vời: Không có sinh viên nào vắng quá 20% số buổi!");
            } else {
                out.printf("⚠️ CẢNH BÁO: Phát hiện %d sinh viên vắng >= 20%% số buổi:%n", dsCanhBao.size());
                out.printf("%-10s %-22s %-10s %-12s %-10s%n", "Mã SV", "Họ Tên", "Lớp", "Vắng/Tổng", "Tỷ lệ vắng");
                out.println("------------------------------------------------------------------");
                for (CanhBaoChuyenCan cb : dsCanhBao) {
                    out.printf("%-10s %-22s %-10s %2d/%-9d %5.1f%%%n",
                            cb.maSV(), cb.hoTen(), cb.lop(), cb.soBuoiVang(), cb.tongSoBuoi(), cb.tiLeVang());
                }
            }
        } catch (Exception e) {
            out.println("❌ Lỗi thống kê cả kỳ: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }

        PhienManager phienManager = new PhienManager();
        ChongGianLanService chongGianLan = new ChongGianLanService();
        ThongKeService thongKe = new ThongKeService();
        DiemDanhService service = new DiemDanhService(phienManager, chongGianLan, thongKe);
        DiemDanhServer server = new DiemDanhServer(8888, service);
        CsvStorageService storage = new CsvStorageService();

        TeacherConsoleUI ui = new TeacherConsoleUI(service, server, storage);

        try {
            server.start();
            System.out.println("🚀 Máy chủ điểm danh TCP đã khởi động thành công trên cổng 8888.");
            ui.start();
        } catch (Exception e) {
            System.err.println("❌ Lỗi máy chủ: " + e.getMessage());
        } finally {
            server.stop();
            phienManager.shutdown();
            System.out.println("🛑 Hệ thống đã dừng an toàn.");
        }
    }
}
