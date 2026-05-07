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
        try {
            // 1. Khởi tạo các Implementation của Repository
            iuh.fit.core.repository.ITaiKhoanRepository taiKhoanRepo = new iuh.fit.infrastructure.persistence.TaiKhoanRepositoryImpl();
            iuh.fit.core.repository.IKhachHangRepository khachHangRepo = new iuh.fit.infrastructure.persistence.KhachHangRepositoryImpl();
            iuh.fit.core.repository.INhanVienRepository nhanVienRepo = new iuh.fit.infrastructure.persistence.NhanVienRepositoryImpl();
            iuh.fit.core.repository.IPhongRepository phongRepo = new iuh.fit.infrastructure.persistence.PhongRepositoryImpl();
            iuh.fit.core.repository.IPhieuDatPhongRepository phieuRepo = new iuh.fit.infrastructure.persistence.PhieuDatPhongRepositoryImpl();

            // (Nếu có DichVuRepository thì khởi tạo tương tự ở đây)
            IDichVuRepository dichVuRepo = new DichVuRepositoryImpl();
            IDichVuService dichVuService = new DichVuServiceImpl(dichVuRepo);

            // 2. Khởi tạo các Implementation của Service
            IAuthenticationService authService = new iuh.fit.core.service.impl.AuthenticationServiceImpl(taiKhoanRepo);
            IKhachHangService khService = new iuh.fit.core.service.impl.KhachHangServiceImpl(khachHangRepo);
            INhanVienService nvService = new iuh.fit.core.service.impl.NhanVienServiceImpl(nhanVienRepo);
            IPhongService phongService = new iuh.fit.core.service.impl.PhongServiceImpl(phongRepo);
            IPhieuDatPhongService phieuService = new iuh.fit.core.service.impl.PhieuDatPhongServiceImpl(phieuRepo);

            // 3. TIÊM (INJECT) CÁC SERVICE VÀO LOGIN CONTROLLER
            LoginController loginController = new LoginController(
                    authService, khService, nvService, phongService, phieuService, dichVuService
            );

            // 4. Hiển thị màn hình Login
            Scene loginScene = loginController.createLoginScene(); // Gọi hàm tạo giao diện của bạn
            primaryStage.setScene(loginScene);
            primaryStage.setTitle("Đăng nhập Hệ thống");
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}