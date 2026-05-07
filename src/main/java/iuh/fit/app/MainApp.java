package iuh.fit.app;

// Import đích danh các Interface (Giao diện)
import iuh.fit.core.repository.IChiTietHoaDonRepository;
import iuh.fit.core.repository.IDichVuRepository;
import iuh.fit.core.repository.IHoaDonRepository;
import iuh.fit.core.repository.IKhachHangRepository;
import iuh.fit.core.repository.INhanVienRepository;
import iuh.fit.core.repository.IPhieuDatPhongRepository;
import iuh.fit.core.repository.IPhongRepository;
import iuh.fit.core.repository.ITaiKhoanRepository;

import iuh.fit.core.service.IAuthenticationService;
import iuh.fit.core.service.IChiTietHoaDonService;
import iuh.fit.core.service.IDichVuService;
import iuh.fit.core.service.IHoaDonService;
import iuh.fit.core.service.IKhachHangService;
import iuh.fit.core.service.INhanVienService;
import iuh.fit.core.service.IPhieuDatPhongService;
import iuh.fit.core.service.IPhongService;

// Import đích danh các Class thực thi (Impl) ĐỂ TRÁNH LỖI AMBIGUOUS
import iuh.fit.core.service.impl.AuthenticationServiceImpl;
import iuh.fit.core.service.impl.ChiTietHoaDonServiceImpl;
import iuh.fit.core.service.impl.DichVuServiceImpl;
import iuh.fit.core.service.impl.HoaDonServiceImpl;
import iuh.fit.core.service.impl.KhachHangServiceImpl;
import iuh.fit.core.service.impl.NhanVienServiceImpl;
import iuh.fit.core.service.impl.PhieuDatPhongServiceImpl;
import iuh.fit.core.service.impl.PhongServiceImpl;

import iuh.fit.infrastructure.persistence.ChiTietHoaDonRepositoryImpl;
import iuh.fit.infrastructure.persistence.DichVuRepositoryImpl;
import iuh.fit.infrastructure.persistence.HoaDonRepositoryImpl;
import iuh.fit.infrastructure.persistence.KhachHangRepositoryImpl;
import iuh.fit.infrastructure.persistence.NhanVienRepositoryImpl;
import iuh.fit.infrastructure.persistence.PhieuDatPhongRepositoryImpl;
import iuh.fit.infrastructure.persistence.PhongRepositoryImpl;
import iuh.fit.infrastructure.persistence.TaiKhoanRepositoryImpl;

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
        IPhongService phongService = new PhongServiceImpl(phongRepo);
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