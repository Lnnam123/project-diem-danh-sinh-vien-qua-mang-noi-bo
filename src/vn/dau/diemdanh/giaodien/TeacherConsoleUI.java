package vn.dau.diemdanh.giaodien;

import vn.dau.diemdanh.luutru.CsvStorageService;
import vn.dau.diemdanh.mang.DiemDanhServer;
import vn.dau.diemdanh.nghiepvu.DiemDanhService;

/**
 * Phục vụ chức năng F12: Giao diện dòng lệnh tương tác phía Giảng viên (Console CLI).
 * Áp dụng kỹ thuật: switch expression (Ch6), BufferedReader (Ch5), try-with-resources (Ch6).
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
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public static void main(String[] args) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}

