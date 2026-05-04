package iuh.fit.app;

import iuh.fit.core.service.AuthenticationServiceImpl;
import iuh.fit.core.service.KhachHangServiceImpl;
import iuh.fit.core.service.NhanVienServiceImpl;
import iuh.fit.infrastructure.db.JpaConfig;
import iuh.fit.infrastructure.persistence.TaiKhoanRepositoryImpl;
import iuh.fit.infrastructure.persistence.KhachHangRepositoryImpl;
import iuh.fit.infrastructure.persistence.NhanVienRepositoryImpl;
import iuh.fit.presentation.controller.LoginController;
import iuh.fit.presentation.controller.QuanLyKhachHangController;
import iuh.fit.presentation.controller.QuanLyNhanVienController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Logger;

/**
 * Application: MainApp (Entry Point)
 * 
 * Tầng: APP - Application Bootstrap Layer
 * Trách nhiệm: Khởi tạo ứng dụng, setup Dependency Injection, và hiển thị giao diện
 * 
 * Quy trình khởi động:
 * 1. main() → launch() → start()
 * 2. Khởi tạo JPA (JpaConfig)
 * 3. Tạo repositories (infrastructure layer)
 * 4. Tạo services (core layer)
 * 5. Tạo controllers (presentation layer)
 * 6. Hiển thị login screen
 * 7. Khi shutdown → đóng JPA
 * 
 * ARCHITECTURE FLOW:
 * 
 * LoginScreen
 *     ↓ (call)
 * LoginController
 *     ↓ (use)
 * IAuthenticationService
 *     ↓ (implement)
 * AuthenticationServiceImpl
 *     ↓ (use)
 * ITaiKhoanRepository
 *     ↓ (implement)
 * TaiKhoanRepositoryImpl
 *     ↓ (use)
 * JPA/Hibernate
 *     ↓
 * MariaDB
 */
public class MainApp extends Application {
    
    private static final Logger logger = Logger.getLogger(MainApp.class.getName());
    private Stage primaryStage;
    
    /**
     * Entry point của ứng dụng
     */
    public static void main(String[] args) {
        logger.info("🚀 Khởi động ứng dụng Quản Lý Khách Sạn");
        launch(args);
    }
    
    /**
     * start() - JavaFX lifecycle method
     * Được gọi tự động bởi JavaFX runtime khi ứng dụng khởi động
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        try {
            logger.info("📝 Khởi tạo ứng dụng...");
            
            // ============ STEP 1: Khởi tạo JPA ============
            logger.info("🔗 Kết nối database...");
            JpaConfig.getEntityManagerFactory();
            
            // ============ STEP 2: Setup Dependency Injection ============
            logger.info("🔧 Setup Dependency Injection...");
            setupDependencyInjection();
            
            // ============ STEP 3: Hiển thị Login Screen ============
            logger.info("🎨 Hiển thị login screen...");
            showLoginScreen();
            
            logger.info("✅ Ứng dụng khởi động thành công!");
            
        } catch (Exception e) {
            logger.severe("❌ Lỗi khởi động ứng dụng: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Setup Dependency Injection
     * Nơi tạo tất cả instances của repositories, services, controllers
     */
    private void setupDependencyInjection() {
        // Repositories sẽ được tạo khi cần (trong examples này, tạo inline)
        // Trong production, nên sử dụng Spring Framework hoặc một DI Container
    }
    
    /**
     * Hiển thị Login Screen
     */
    private void showLoginScreen() {
        try {
            // ============ CREATE REPOSITORY & SERVICE ============
            var taiKhoanRepository = new TaiKhoanRepositoryImpl();
            var authService = new AuthenticationServiceImpl(taiKhoanRepository);
            
            // ============ CREATE CONTROLLER ============
            var loginController = new LoginController(authService);
            
            // ============ CREATE SCENE ============
            Scene loginScene = loginController.createLoginScene();
            
            // ============ SETUP STAGE ============
            primaryStage.setTitle("Quản Lý Khách Sạn - Đăng Nhập");
            primaryStage.setScene(loginScene);
            primaryStage.setWidth(500);
            primaryStage.setHeight(400);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            
            // ============ HANDLE CLOSE EVENT ============
            primaryStage.setOnCloseRequest(e -> handleWindowClose());
            
            primaryStage.show();
            
        } catch (Exception e) {
            logger.severe("❌ Lỗi hiển thị login screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Hiển thị Main Screen (sau khi đăng nhập)
     * Method này có thể được gọi từ LoginController sau khi xác thực thành công
     */
    public void showMainScreen() {
        try {
            // ============ CREATE REPOSITORIES & SERVICES ============
            var khachHangRepository = new KhachHangRepositoryImpl();
            var khachHangService = new KhachHangServiceImpl(khachHangRepository);
            
            // ============ CREATE CONTROLLER ============
            var quanLyKhachHangController = new QuanLyKhachHangController(khachHangService);
            
            // ============ CREATE SCENE ============
            Scene mainScene = quanLyKhachHangController.createQuanLyKhachHangScene();
            
            // ============ SETUP STAGE ============
            primaryStage.setTitle("Quản Lý Khách Sạn - Trang Chính");
            primaryStage.setScene(mainScene);
            primaryStage.setWidth(900);
            primaryStage.setHeight(700);
            primaryStage.show();
            
        } catch (Exception e) {
            logger.severe("❌ Lỗi hiển thị main screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Hiển thị Screen Quản Lý Nhân Viên
     */
    public void showQuanLyNhanVienScreen() {
        try {
            // ============ CREATE REPOSITORIES & SERVICES ============
            var nhanVienRepository = new NhanVienRepositoryImpl();
            var nhanVienService = new NhanVienServiceImpl(nhanVienRepository);
            
            // ============ CREATE CONTROLLER ============
            var quanLyNhanVienController = new QuanLyNhanVienController(nhanVienService);
            
            // ============ CREATE SCENE từ FXML ============
            Scene quanLyNhanVienScene = quanLyNhanVienController.createQuanLyNhanVienScene();
            
            // ============ SETUP STAGE ============
            primaryStage.setTitle("Quản Lý Khách Sạn - Quản Lý Nhân Viên");
            primaryStage.setScene(quanLyNhanVienScene);
            primaryStage.setWidth(1000);
            primaryStage.setHeight(700);
            primaryStage.show();
            
        } catch (Exception e) {
            logger.severe("❌ Lỗi hiển thị screen quản lý nhân viên: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Xử lý khi đóng ứng dụng
     */
    private void handleWindowClose() {
        try {
            logger.info("🛑 Đóng ứng dụng...");
            
            // ============ CLEANUP ============
            // Đóng database connections
            JpaConfig.closeEntityManagerFactory();
            
            logger.info("✅ Ứng dụng đã tắt");
        } catch (Exception e) {
            logger.severe("❌ Lỗi đóng ứng dụng: " + e.getMessage());
        }
        System.exit(0);
    }
    
    /**
     * stop() - JavaFX lifecycle method
     * Được gọi khi ứng dụng sắp tắt
     */
    @Override
    public void stop() throws Exception {
        handleWindowClose();
        super.stop();
    }
}

