package iuh.fit.app;

import iuh.fit.core.repository.*;
import iuh.fit.core.service.*;
import iuh.fit.core.service.impl.*;
import iuh.fit.infrastructure.persistence.*;
import iuh.fit.presentation.controller.LoginController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Khởi tạo repository
        IKhachHangRepository khRepo = new KhachHangRepositoryImpl();
        INhanVienRepository nvRepo = new NhanVienRepositoryImpl();
        IPhongRepository phongRepo = new PhongRepositoryImpl();
        IPhieuDatPhongRepository phieuRepo = new PhieuDatPhongRepositoryImpl();
        ITaiKhoanRepository tkRepo = new TaiKhoanRepositoryImpl();

        // Khởi tạo service
        IKhachHangService khService = new KhachHangServiceImpl(khRepo);
        INhanVienService nvService = new NhanVienServiceImpl(nvRepo);
        IPhongService phongService = new PhongServiceImpl(phongRepo);
        IPhieuDatPhongService phieuService = new PhieuDatPhongServiceImpl(phieuRepo);
        IAuthenticationService authService = new AuthenticationServiceImpl(tkRepo);

        // Tạo LoginController và lấy Scene
        LoginController loginController = new LoginController(
                authService, khService, nvService, phongService, phieuService
        );
        Scene loginScene = loginController.createLoginScene();

        // Cấu hình và hiển thị Stage
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Quản lý khách sạn TTV - Đăng nhập");
        primaryStage.setResizable(false);
        try {
            // Tải logo TTV từ resources
            Image logo = new Image(getClass().getResourceAsStream("/images/logo_ttv.png"));
            if (logo.getWidth() > 0) {
                primaryStage.getIcons().add(logo);
            } else {
                throw new Exception("Logo not found");
            }
        } catch (Exception e) {
            // Nếu logo không tìm được, sử dụng logo mặc định từ Internet
            try {
                primaryStage.getIcons().add(new Image("https://cdn-icons-png.flaticon.com/512/2117/2117267.png"));
            } catch (Exception ex) {
                // Ignore if icon fails to load
            }
        }
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}