package server;

import server.handler.ActionDispatcher;
import server.handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SocketServer {
    private final int port;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final ActionDispatcher dispatcher = new ActionDispatcher();

    // Đưa ServerSocket ra ngoài để hàm stop() có thể gọi tới
    private ServerSocket serverSocket;

    // Cờ đánh dấu trạng thái hoạt động của Server
    private volatile boolean running = false;

    public SocketServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        System.out.println("Hotel Server is listening on port " + port);

        // Thay vì while(true), ta dùng while(running) để có thể thoát vòng lặp
        while (running) {
            try {
                // Server sẽ đứng đợi ở đây cho đến khi có Client kết nối
                Socket client = serverSocket.accept();
                pool.submit(new ClientHandler(client, dispatcher));
            } catch (SocketException e) {
                // Khi ta gọi serverSocket.close() ở hàm stop(), hàm accept() sẽ văng ra lỗi này.
                // Ta bắt lỗi này lại để server không bị crash.
                if (!running) {
                    System.out.println("Đã đóng luồng chấp nhận Client mới.");
                } else {
                    e.printStackTrace();
                }
            }
        }
    }

    // Hàm này sẽ được gọi từ ServerMain (ShutdownHook)
    public void stop() {
        running = false; // Bật cờ yêu cầu dừng
        try {
            // 1. Đóng cổng Socket (Ngay lập tức phá vỡ trạng thái chờ đợi của hàm accept())
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            // 2. Tắt Thread Pool một cách êm ái
            pool.shutdown(); // Từ chối nhận thêm yêu cầu mới

            // Chờ tối đa 5 giây để các Client đang xử lý dở dang hoàn tất công việc
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Nếu sau 5 giây vẫn còn ngoan cố không xong, ép buộc đóng
            }
            System.out.println("Đã đóng SocketServer an toàn.");

        } catch (IOException | InterruptedException e) {
            System.err.println("Lỗi khi đóng SocketServer: " + e.getMessage());
            pool.shutdownNow(); // Ép đóng nếu có lỗi
            Thread.currentThread().interrupt();
        }
    }
}