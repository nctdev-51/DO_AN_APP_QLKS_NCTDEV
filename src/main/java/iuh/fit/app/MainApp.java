package iuh.fit.app;

import iuh.fit.core.repository.*;
import iuh.fit.core.service.*;
import iuh.fit.core.service.impl.*;
import iuh.fit.infrastructure.persistence.*;
import iuh.fit.presentation.controller.LoginController;
import javafx.application.Application;
import javafx.scene.Scene;
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
        primaryStage.setTitle("Quản lý khách sạn TATP - Đăng nhập");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}