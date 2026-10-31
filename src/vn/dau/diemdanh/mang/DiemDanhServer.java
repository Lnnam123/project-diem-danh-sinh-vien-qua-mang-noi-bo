package vn.dau.diemdanh.mang;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import vn.dau.diemdanh.nghiepvu.DiemDanhService;

/**
 * Phục vụ chức năng F07: Máy chủ mạng TCP đa luồng sử dụng bể luồng thread pool tiếp nhận kết nối đồng thời.
 * Áp dụng kỹ thuật: ServerSocket, Socket (Ch5), ExecutorService (Ch4, Ch5), volatile (Ch4), try-with-resources (Ch6).
 */
public class DiemDanhServer {

    private final int port;
    private final DiemDanhService service;
    private volatile boolean isRunning;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;

    public DiemDanhServer(int port, DiemDanhService service) {
        this.port = port;
        this.service = service;
        this.isRunning = false;
    }

    public void start() throws IOException {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public void stop() {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    public boolean isRunning() {
        return isRunning;
    }
}

