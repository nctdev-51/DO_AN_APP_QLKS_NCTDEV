package app;

import infrastructure.db.EntityManagerFactoryProvider;
import server.SocketServer;

public class ServerMain {
    public static void main(String[] args) {
        try {
            System.out.println("=====================================================");
            System.out.println("🏨 KHỞI ĐỘNG HOTEL SERVER - HỆ THỐNG PHÂN TÁN");
            System.out.println("=====================================================");

            // BƯỚC 1: Đánh thức Hibernate và kết nối MariaDB
            System.out.println("\n[1/2] Đang khởi tạo kết nối Database (Hibernate/JPA)...");
            System.out.println("      (Vui lòng đợi 3-5 giây cho lần khởi động đầu tiên...)");

            // Lệnh này ép hệ thống đọc persistence.xml và nổ máy JPA ngay lập tức
            EntityManagerFactoryProvider.getInstance();

            System.out.println("      ✅ Kết nối Database thành công! Các Entity đã sẵn sàng.");

            // BƯỚC 2: Bật Socket Server để lắng nghe Client
            System.out.println("\n[2/2] Đang mở Socket Server...");
            int port = 9999;
            SocketServer server = new SocketServer(port);

            System.out.println("      ✅ Hotel Server đang lắng nghe tại cổng " + port);
            System.out.println("\n🚀 HỆ THỐNG ĐÃ SẴN SÀNG! BẠN CÓ THỂ CHẠY CLIENT ĐỂ ĐĂNG NHẬP.");

            // Lệnh start() thường chứa vòng lặp while(true) để chờ Client nên nó sẽ chặn luồng ở đây
            server.start();

        } catch (Exception e) {
            System.err.println("\n❌ LỖI NGHIÊM TRỌNG KHI KHỞI ĐỘNG SERVER:");
            System.err.println("💡 Gợi ý: Hãy kiểm tra xem MariaDB đã được bật chưa, hoặc xem lại mật khẩu trong persistence.xml");
            e.printStackTrace(); // In chi tiết lỗi để bắt bệnh
        }
    }
}