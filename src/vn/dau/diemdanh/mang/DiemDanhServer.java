package vn.dau.diemdanh.mang;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private Thread listenerThread;

    public DiemDanhServer(int port, DiemDanhService service) {
        this.port = port;
        this.service = service;
        this.isRunning = false;
    }

    /**
     * F07: Khởi động máy chủ Socket và bể luồng phục vụ kết nối máy khách.
     */
    public synchronized void start() throws IOException {
        if (isRunning) {
            return;
        }

        this.serverSocket = new ServerSocket(port);
        this.threadPool = Executors.newFixedThreadPool(15);
        this.isRunning = true;

        this.listenerThread = new Thread(() -> {
            while (isRunning && !serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    if (!isRunning) {
                        clientSocket.close();
                        break;
                    }
                    threadPool.submit(new ClientHandler(clientSocket, service));
                } catch (IOException e) {
                    if (!isRunning) {
                        break;
                    }
                }
            }
        }, "DiemDanhServer-Listener");
        this.listenerThread.setDaemon(true);
        this.listenerThread.start();
    }

    /**
     * F07: Dừng máy chủ, đóng ServerSocket và giải phóng bể luồng an toàn.
     */
    public synchronized void stop() {
        if (!isRunning) {
            return;
        }
        this.isRunning = false;

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ignored) {
        }

        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdownNow();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int getPort() {
        return serverSocket != null ? serverSocket.getLocalPort() : port;
    }
}
