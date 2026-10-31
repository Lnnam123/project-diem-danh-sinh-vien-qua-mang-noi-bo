package vn.dau.diemdanh.mang;

import java.net.Socket;
import vn.dau.diemdanh.nghiepvu.DiemDanhService;

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
        throw new UnsupportedOperationException("Chưa cài đặt");
    }

    protected String xuLyYeuCau(String line, String clientIP) {
        throw new UnsupportedOperationException("Chưa cài đặt");
    }
}

