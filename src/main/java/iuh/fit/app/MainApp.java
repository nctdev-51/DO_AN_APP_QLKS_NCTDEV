package iuh.fit.app;

import iuh.fit.core.repository.*;
import iuh.fit.core.service.*;
import iuh.fit.core.service.IHoaDonService;
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
        IDichVuRepository dvRepo = new DichVuRepositoryImpl();
        IHoaDonRepository hdRepo = new HoaDonRepositoryImpl();
        IChiTietHoaDonRepository cthdRepo = new ChiTietHoaDonRepositoryImpl();

        // Khởi tạo service
        IKhachHangService khService = new KhachHangServiceImpl(khRepo);
        INhanVienService nvService = new NhanVienServiceImpl(nvRepo);
        IPhieuDatPhongRepository phieuDatPhongRepo = new PhieuDatPhongRepositoryImpl();
        IPhongRepository phongRepository = new PhongRepositoryImpl();
        IPhongService phongService = new PhongServiceImpl(phongRepository, phieuDatPhongRepo);
        IPhieuDatPhongService phieuService = new PhieuDatPhongServiceImpl(phieuRepo);
        IAuthenticationService authService = new AuthenticationServiceImpl(tkRepo);
        IDichVuService dvService = new DichVuServiceImpl(dvRepo);
        IHoaDonService hdService = new HoaDonServiceImpl(hdRepo, cthdRepo, phieuRepo, phongRepo);
        IChiTietHoaDonService cthdService = new ChiTietHoaDonServiceImpl(cthdRepo);

        // Tạo LoginController và lấy Scene
        LoginController loginController = new LoginController(
                authService, khService, nvService, phongService, phieuService,
                dvService, hdService, cthdService
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