package app;

import infrastructure.db.EntityManagerFactoryProvider;
import server.SocketServer;

public class ServerMain {
    public static void main(String[] args) {
        try {
            System.out.println("=====================================================");
            System.out.println("🏨 KHỞI ĐỘNG HOTEL SERVER - HỆ THỐNG PHÂN TÁN");
            System.out.println("=====================================================");

            System.out.println("\n[1/2] Đang khởi tạo kết nối Database (Hibernate/JPA)...");
            System.out.println("      (Vui lòng đợi 3-5 giây cho lần khởi động đầu tiên...)");

            EntityManagerFactoryProvider.getInstance();
            System.out.println("      ✅ Kết nối Database thành công! Các Entity đã sẵn sàng.");

            System.out.println("\n[2/2] Đang mở Socket Server...");
            int port = 9999;
            SocketServer server = new SocketServer(port);
            System.out.println("      ✅ Hotel Server đang lắng nghe tại cổng " + port);

            // --- ĐIỂM NÂNG CẤP: GRACEFUL SHUTDOWN ---
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n⚠️ Nhận tín hiệu tắt Server. Đang dọn dẹp tài nguyên...");
                try {
                    // Đóng Socket
                    server.stop(); // Hãy chắc chắn class SocketServer của bạn có hàm stop()

                    // Đóng EntityManagerFactory của Hibernate
                    if (EntityManagerFactoryProvider.getInstance().isOpen()) {
                        EntityManagerFactoryProvider.getInstance().close();
                    }
                    System.out.println("✅ Đã ngắt kết nối Database an toàn.");
                    System.out.println("💤 Server đã tắt hoàn toàn. Tạm biệt!");
                } catch (Exception e) {
                    System.err.println("❌ Có lỗi khi dọn dẹp tài nguyên: " + e.getMessage());
                }
            }));
            // -----------------------------------------

            System.out.println("\n🚀 HỆ THỐNG ĐÃ SẴN SÀNG! BẠN CÓ THỂ CHẠY CLIENT ĐỂ ĐĂNG NHẬP.");
            server.start();

        } catch (Exception e) {
            System.err.println("\n❌ LỖI NGHIÊM TRỌNG KHI KHỞI ĐỘNG SERVER:");
            System.err.println("💡 Gợi ý: Hãy kiểm tra xem MariaDB đã được bật chưa, hoặc xem lại mật khẩu trong persistence.xml");
            e.printStackTrace();
        }
    }
}