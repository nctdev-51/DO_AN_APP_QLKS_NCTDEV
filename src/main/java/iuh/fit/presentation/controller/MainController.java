package iuh.fit.presentation.controller;

import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.*;
import iuh.fit.core.service.impl.CaLamViecServiceImpl;
import iuh.fit.core.service.impl.PhanCongServiceImpl;
import javafx.application.Platform;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainController {

    private TaiKhoanDTO currentUser;
    private Stage primaryStage;
    private BorderPane rootLayout;
    private StackPane contentArea;

    private IKhachHangService khachHangService;
    private INhanVienService nhanVienService;
    private IPhongService phongService;
    private IPhieuDatPhongService phieuDatPhongService;
    private IDichVuService dichVuService;
    private IHoaDonService hoaDonService;
    private IChiTietHoaDonService chiTietHoaDonService;
    private IGiaoCaService giaoCaService;
    private ChonPhongController chonPhongController;


    private Label lblClock;

    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_ACCENT = "#0ea5e9";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_CARD_BG = "#ffffff";
    private final String COLOR_BG_LIGHT = "#f1f5f9";

    public MainController(Stage primaryStage, TaiKhoanDTO currentUser,
                          IKhachHangService khachHangService, INhanVienService nhanVienService,
                          IPhongService phongService, IPhieuDatPhongService phieuDatPhongService,
                          IDichVuService dichVuService, IHoaDonService hoaDonService,
                          IChiTietHoaDonService chiTietHoaDonService, IGiaoCaService giaoCaService) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.khachHangService = khachHangService;
        this.nhanVienService = nhanVienService;
        this.phongService = phongService;
        this.phieuDatPhongService = phieuDatPhongService;
        this.dichVuService = dichVuService;
        this.hoaDonService = hoaDonService;
        this.chiTietHoaDonService = chiTietHoaDonService;
        this.giaoCaService = giaoCaService;
    }

    public void showMainScreen() {
        rootLayout = new BorderPane();
        rootLayout.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + ";");

        Button btnQuickReport = new Button("⚠ BÁO SỰ CỐ");
        btnQuickReport.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 30; -fx-padding: 10 20; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        // Sự kiện mở Dialog báo cáo (Sử dụng Service đã khởi tạo)
        btnQuickReport.setOnAction(e -> {
            // Tự động khởi tạo Service nếu cần
            iuh.fit.core.repository.IBaoCaoRepository bcRepo = new iuh.fit.infrastructure.persistence.BaoCaoRepositoryImpl();
            iuh.fit.core.service.IBaoCaoService bcService = new iuh.fit.core.service.impl.BaoCaoServiceImpl(bcRepo);
            new TaoBaoCaoDialog(bcService, currentUser).showDialog();
        });

        // Ép nút vào góc dưới bên phải màn hình
        StackPane.setAlignment(btnQuickReport, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(btnQuickReport, new Insets(0, 30, 30, 0));

        // 🚀 ĐÃ FIX: Đảm bảo contentArea luôn có thực thể trước khi nhét vào layoutContainer
        if (contentArea == null) {
            contentArea = new StackPane();
        }

        // Bọc contentArea vào một StackPane để nút nổi lên trên
        StackPane layoutContainer = new StackPane();
        layoutContainer.getChildren().addAll(contentArea, btnQuickReport);

        rootLayout.setCenter(layoutContainer);

        rootLayout.setTop(createHeader());
        rootLayout.setLeft(createSidebar());

        contentArea = new StackPane();
        contentArea.setPadding(new Insets(25));
        rootLayout.setCenter(contentArea);

        showDashboard();

        javafx.geometry.Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(rootLayout, screenBounds.getWidth(), screenBounds.getHeight());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Hệ Thống Quản Lý Khách Sạn TTV");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(12, 30, 12, 20));
        header.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-border-width: 0 0 1px 0; -fx-border-color: #e2e8f0;");
        header.setAlignment(Pos.CENTER_LEFT);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.04));
        shadow.setRadius(8);
        shadow.setOffsetY(2);
        header.setEffect(shadow);

        HBox logoTitleBox = new HBox(12);
        logoTitleBox.setAlignment(Pos.CENTER_LEFT);
        logoTitleBox.setPrefWidth(240);

        ImageView logoImageView = new ImageView();
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo_ttv.png"));
            if (logoImage.getWidth() > 0) {
                logoImageView.setImage(logoImage);
                logoImageView.setFitWidth(50);
                logoImageView.setFitHeight(50);
                logoImageView.setPreserveRatio(true);
                logoTitleBox.getChildren().add(logoImageView);
            }
        } catch (Exception e) {
            Label logoEmoji = new Label("🏨");
            logoEmoji.setFont(Font.font(36));
            logoTitleBox.getChildren().add(logoEmoji);
        }

        Label lblTitle = new Label("TTV HOTEL");
        lblTitle.setFont(Font.font("Verdana", FontWeight.BLACK, 26));
        lblTitle.setTextFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web(COLOR_PRIMARY)), new Stop(1.0, Color.web(COLOR_ACCENT))));
        logoTitleBox.getChildren().add(lblTitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        lblClock = new Label();
        lblClock.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        lblClock.setTextFill(Color.web(COLOR_TEXT_MAIN));
        lblClock.setStyle("-fx-background-color: #f1f5f9; -fx-padding: 8 16; -fx-background-radius: 20;");
        startClock();

        HBox rightControls = new HBox(25);
        rightControls.setAlignment(Pos.CENTER_RIGHT);
        rightControls.getChildren().add(lblClock);

        header.getChildren().addAll(logoTitleBox, spacer, rightControls);
        return header;
    }

    private VBox createSidebar() {
        // Giảm spacing giữa các thành phần từ 5 xuống 2
        VBox sidebar = new VBox(2);
        sidebar.setPadding(new Insets(15, 12, 10, 12));
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: linear-gradient(to bottom, #0f172a, #1e293b);");

        // ==========================================================
        // 1. KIỂM TRA QUYỀN HẠN (Giữ nguyên logic của Tú)
        // ==========================================================
        boolean isManager = false;
        String roleName = "Lễ tân";
        try {
            if (currentUser.getTenDangNhap().equalsIgnoreCase("admin")) {
                isManager = true; roleName = "Admin";
            } else {
                iuh.fit.core.dto.NhanVienDTO nv = nhanVienService.getNhanVienById(currentUser.getMaNhanVien());
                if (nv != null && (nv.getLoaiNhanVien().contains("QUAN_LY") || nv.getLoaiNhanVien().contains("GIAM_DOC"))) {
                    isManager = true; roleName = "Quản lý";
                }
            }
        } catch (Exception ignored) {}

        // ==========================================================
        // 2. PROFILE (Thu nhỏ lại để tiết kiệm diện tích)
        // ==========================================================
        VBox profileBox = new VBox(3);
        profileBox.setAlignment(Pos.CENTER);
        profileBox.setPadding(new Insets(0, 0, 10, 0));

        Label lblAvatar = new Label("👨‍💼");
        lblAvatar.setFont(Font.font(30)); // Thu nhỏ từ 38 xuống 30
        lblAvatar.setStyle("-fx-background-color: #334155; -fx-background-radius: 40; -fx-padding: 6;");

        Label lblUser = new Label(currentUser.getHoTenNhanVien());
        lblUser.setTextFill(Color.WHITE);
        lblUser.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13)); // Thu nhỏ font

        Label lblRole = new Label(roleName);
        lblRole.setTextFill(Color.web("#94a3b8"));
        lblRole.setFont(Font.font("Segoe UI", 10));
        profileBox.getChildren().addAll(lblAvatar, lblUser, lblRole);

        // ==========================================================
        // 3. KHỞI TẠO CÁC NÚT (Giảm padding và font trong hàm createMenuButton phía dưới)
        // ==========================================================
        Label lblMenuSection = new Label("NGHIỆP VỤ CHÍNH");
        lblMenuSection.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 5 0 2 10;");

        Button btnTrangChu = createMenuButton("🏠 Trang Chủ", true);
        Button btnDatPhong = createMenuButton("🏨 Đặt & Nhận Phòng", false);
        Button btnPhong_ = createMenuButton("🔑 Chọn Phòng Nhanh", false);
        Button btnGoiDichVu = createMenuButton("🛒 Gọi Dịch Vụ POS", false);
        Button btnTraPhong = createMenuButton("💳 Thanh Toán & Trả Phòng", false);

        Label lblListSection = new Label("QUẢN LÝ DANH MỤC");
        lblListSection.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 8 0 2 10;");

        Button btnQuanLyPhieu = createMenuButton("📋 Quản Lý Phiếu Đặt", false);
        Button btnHoaDon = createMenuButton("🧾 Quản Lý Hóa Đơn", false); // 👉 NÚT MỚI
        Button btnPhong = createMenuButton("🚪 Quản Lý Phòng", false);
        Button btnKhachHang = createMenuButton("👥 Quản Lý Khách Hàng", false);

        // Nút điều khiển chung
        List<Button> menuButtons = new ArrayList<>(Arrays.asList(
                btnTrangChu, btnDatPhong, btnPhong_, btnGoiDichVu, btnTraPhong,
                btnQuanLyPhieu, btnHoaDon, btnPhong, btnKhachHang
        ));

        // ==========================================================
        // 4. CÁC NÚT QUẢN LÝ (Nếu có)
        // ==========================================================
        Button btnThongKe = createMenuButton("📈 Thống Kê Doanh Thu", false);
        Button btnNhanVien = createMenuButton("👔 Quản Lý Nhân Viên", false);
        Button btnPhanCong = createMenuButton("📅 Phân Công Ca", false);
        Button btnHopThu = createMenuButton("📥 Hộp Thư Báo Cáo", false);
        Button btnGuiBaoCao = createMenuButton("📝 Gửi Báo Cáo Sự Cố", false);

        // Gán sự kiện
        btnTrangChu.setOnAction(e -> { setActiveMenu(btnTrangChu, menuButtons); showDashboard(); });
        btnDatPhong.setOnAction(e -> { setActiveMenu(btnDatPhong, menuButtons); loadQuanLyDatPhong(); });
        btnPhong_.setOnAction(e -> { setActiveMenu(btnPhong_, menuButtons); loadManHinhChonPhong(); });
        btnGoiDichVu.setOnAction(e -> { setActiveMenu(btnGoiDichVu, menuButtons); loadGoiDichVu(); });
        btnTraPhong.setOnAction(e -> {
            setActiveMenu(btnTraPhong, menuButtons);
            loadTraPhong(null); // Bấm từ menu thì không có mã phiếu nào được chọn sẵn cả
        });
        btnQuanLyPhieu.setOnAction(e -> { setActiveMenu(btnQuanLyPhieu, menuButtons); showQuanLyPhieuDatScreen(); });
        btnHoaDon.setOnAction(e -> { setActiveMenu(btnHoaDon, menuButtons); loadQuanLyHoaDon(); }); // 👉 Link hàm
        btnPhong.setOnAction(e -> { setActiveMenu(btnPhong, menuButtons); loadQuanLyPhong(); });
        btnKhachHang.setOnAction(e -> { setActiveMenu(btnKhachHang, menuButtons); loadQuanLyKhachHang(); });

        // Thêm các nút vào sidebar
        sidebar.getChildren().addAll(profileBox, lblMenuSection, btnTrangChu, btnDatPhong, btnPhong_, btnGoiDichVu, btnTraPhong,
                lblListSection, btnQuanLyPhieu, btnHoaDon, btnPhong, btnKhachHang);

        // Xử lý quyền Quản lý
        if (isManager) {
            menuButtons.addAll(Arrays.asList(btnNhanVien, btnPhanCong, btnThongKe, btnHopThu));
            btnNhanVien.setOnAction(e -> { setActiveMenu(btnNhanVien, menuButtons); loadQuanLyNhanVien(); });
            btnThongKe.setOnAction(e -> { setActiveMenu(btnThongKe, menuButtons); loadThongKeDoanHThu(); });
            // ... (Giữ các sự kiện phân công/hộp thư của Tú)
            sidebar.getChildren().addAll(btnNhanVien, btnPhanCong, btnThongKe, btnHopThu);
        } else {
            menuButtons.add(btnGuiBaoCao);
            btnGuiBaoCao.setOnAction(e -> { setActiveMenu(btnGuiBaoCao, menuButtons); /* gọi dialog */ });
            sidebar.getChildren().add(btnGuiBaoCao);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnDangXuat = createMenuButton("🚪 Đăng xuất", false);
        btnDangXuat.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 8 15;");
        btnDangXuat.setOnAction(e -> handleLogout());

        sidebar.getChildren().addAll(spacer, btnDangXuat);
        return sidebar;
    }

    private Button createMenuButton(String text, boolean isDefaultActive) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setCursor(Cursor.HAND);

        // GIẢM PADDING: từ 10 15 xuống 6 12 | GIẢM FONT: từ 13 xuống 12
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: 600; -fx-padding: 6 12; -fx-background-radius: 6;";
        String activeStyle = "-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 6;";
        String hoverStyle = "-fx-background-color: #334155; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 600; -fx-padding: 6 12; -fx-background-radius: 6;";

        btn.setStyle(isDefaultActive ? activeStyle : defaultStyle);
        btn.setUserData(isDefaultActive ? "active" : "inactive");

        btn.setOnMouseEntered(e -> { if (!"active".equals(btn.getUserData())) btn.setStyle(hoverStyle); });
        btn.setOnMouseExited(e -> { if (!"active".equals(btn.getUserData())) btn.setStyle(defaultStyle); });

        return btn;
    }

    private void setActiveMenu(Button activeBtn, List<Button> allButtons) {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: 600; -fx-padding: 6 12; -fx-background-radius: 6;";
        String activeStyle = "-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 6 12; -fx-background-radius: 6;";

        for (Button btn : allButtons) {
            btn.setStyle(defaultStyle);
            btn.setUserData("inactive");
        }
        activeBtn.setStyle(activeStyle);
        activeBtn.setUserData("active");
    }

    private void startClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss  |  dd/MM/yyyy");
        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, e -> lblClock.setText(LocalDateTime.now().format(formatter))), new KeyFrame(Duration.seconds(1)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    // =========================================================================
    // CẢI TIẾN: GỌI TRỰC TIẾP TỪ DASHBOARD_CONTROLLER THAY VÌ HARDCODE
    // =========================================================================
    private void showDashboard() {
        try {
            contentArea.getChildren().clear();
            BorderPane dashRoot = new BorderPane();
            dashRoot.setStyle("-fx-background-color: transparent;");

            // Giữ lại Header lời chào cực đẹp
            VBox topArea = new VBox(10);
            topArea.setPadding(new Insets(10, 0, 20, 0));
            topArea.getChildren().add(createDashboardTitleBar());
            dashRoot.setTop(topArea);

            // Truyền hoaDonService vào theo yêu cầu
            DashboardController dashController = new DashboardController(phongService, hoaDonService, targetScreen -> {
                if ("BOOKING".equals(targetScreen)) loadQuanLyDatPhong();
                else if ("ROOM_MAP".equals(targetScreen)) loadManHinhChonPhong();
                else if ("MANAGE_ORDERS".equals(targetScreen)) showQuanLyPhieuDatScreen();
                else if ("MANAGE_ROOMS".equals(targetScreen)) loadQuanLyPhong();
                else if ("REPORT".equals(targetScreen)) loadThongKeDoanHThu();
            });

            // Bọc lại Dashboard bằng ScrollPane vì Dashboard hiện tại chứa nhiều dữ liệu & chart
            ScrollPane scrollPane = new ScrollPane(dashController.createDashboardView());
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: " + COLOR_BG_LIGHT + "; -fx-border-color: transparent;");

            dashRoot.setCenter(scrollPane);
            contentArea.getChildren().add(dashRoot);

        } catch (Exception ex) {
            showErrorBox("LỖI HIỂN THỊ TRANG CHỦ", ex);
        }
    }

    private HBox createDashboardTitleBar() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);

        VBox titles = new VBox(5);
        Label greeting = new Label("👋 Chào mừng trở lại, " + currentUser.getHoTenNhanVien() + "!");
        greeting.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 28));
        greeting.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label sub = new Label("Theo dõi nhanh tình hình kinh doanh và quản lý lưu trú hôm nay.");
        sub.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        sub.setTextFill(Color.web(COLOR_TEXT_MUTED));

        titles.getChildren().addAll(greeting, sub);
        bar.getChildren().add(titles);
        return bar;
    }

    // =========================================================================
    // ĐIỀU HƯỚNG MODULES
    // =========================================================================

    // Trong MainController.java

    private void loadManHinhChonPhong() {
        try {
            contentArea.getChildren().clear();

            // 👉 KIỂM TRA: Nếu chưa có thì mới tạo mới, có rồi thì dùng lại cái cũ để giữ dữ liệu
            if (this.chonPhongController == null) {
                this.chonPhongController = new ChonPhongController(
                        phongService, khachHangService, phieuDatPhongService, currentUser, primaryStage
                );
            }

            // Lấy View từ controller (Danh sách chờ sẽ được bảo toàn trong biến instance)
            HBox view = chonPhongController.createView();
            view.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            contentArea.getChildren().setAll(view);

        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorBox("LỖI KHI MỞ GIAO DIỆN CHỌN PHÒNG", ex);
        }
    }

    private void loadQuanLyDatPhong() {
        try {
            contentArea.getChildren().clear();
            DatNhanPhongController controller = new DatNhanPhongController(phongService, phieuDatPhongService, khachHangService, hoaDonService, chiTietHoaDonService, dichVuService, currentUser, primaryStage);
            VBox view = controller.createView();
            view.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            contentArea.getChildren().setAll(view);
        } catch (Exception ex) { showErrorBox("LỖI KHI MỞ GIAO DIỆN ĐẶT & NHẬN PHÒNG", ex); }
    }

    private void loadGoiDichVu() {
        try {
            contentArea.getChildren().clear();
            GoiDichVuController gdController = new GoiDichVuController(dichVuService, phieuDatPhongService, chiTietHoaDonService);
            contentArea.getChildren().add(gdController.createGoiDichVuView());
        } catch (Exception ex) { ex.printStackTrace(); showErrorBox("LỖI KHI MỞ GIAO DIỆN GỌI DỊCH VỤ", ex); }
    }

    // 1. Cập nhật hàm loadTraPhong để nhận thêm tham số maPhieu
    private void loadTraPhong(String maPhieuCanThanhToan) {
        try {
            contentArea.getChildren().clear();
            ThanhToanTraPhongController controller = new ThanhToanTraPhongController(
                    phieuDatPhongService, phongService, khachHangService,
                    hoaDonService, chiTietHoaDonService, dichVuService, currentUser,
                    maPhieuCanThanhToan // 👉 Truyền mã phiếu vào đây
            );
            BorderPane view = controller.createMainView();
            view.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            contentArea.getChildren().setAll(view);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadThongKeDoanHThu() {
        try {
            contentArea.getChildren().clear();
            ThongKeDoanhThuController rc = new ThongKeDoanhThuController(hoaDonService);
            contentArea.getChildren().add(rc.createRevenueView());
        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorBox("LỖI HIỂN THỊ THỐNG KÊ", ex);
        }
    }

    // 3. Sửa hàm showQuanLyPhieuDatScreen để lắng nghe tín hiệu
    // 2. Cập nhật hàm showQuanLyPhieuDatScreen
    private void showQuanLyPhieuDatScreen() {
        try {
            contentArea.getChildren().clear();
            QuanLyPhieuDatPhongController qlPhieuController = new QuanLyPhieuDatPhongController(
                    phieuDatPhongService, phongService, khachHangService, nhanVienService, currentUser,
                    // 👉 Đây chính là hàm callback. Khi Quản lý Phiếu gọi onNavigateToCheckout.accept(maPhieu), khối lệnh này sẽ chạy.
                    maPhieu -> {
                        loadTraPhong(maPhieu); // Chuyển sang màn hình trả phòng và truyền mã phiếu
                    }
            );
            VBox view = qlPhieuController.createQuanLyPhieuView();
            view.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            contentArea.getChildren().setAll(view);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadQuanLyHoaDon() {
        try {
            contentArea.getChildren().clear();
            // Truyền thêm khachHangService vào tham số thứ hai
            QuanLyHoaDonController controller = new QuanLyHoaDonController(hoaDonService, khachHangService);
            VBox view = controller.createView();
            view.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            contentArea.getChildren().setAll(view);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadQuanLyPhong() {
        try {
            contentArea.getChildren().clear();
            QuanLyPhongController pController = new QuanLyPhongController(phongService);
            contentArea.getChildren().add(pController.createQuanLyPhongView());
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadQuanLyKhachHang() {
        try {
            contentArea.getChildren().clear();
            QuanLyKhachHangController khController = new QuanLyKhachHangController(khachHangService);
            contentArea.getChildren().add(khController.createQuanLyKhachHangView());
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadQuanLyNhanVien() {
        try {
            contentArea.getChildren().clear();
            QuanLyNhanVienController nvController = new QuanLyNhanVienController(nhanVienService);
            contentArea.getChildren().add(nvController.createQuanLyNhanVienView());
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void showErrorBox(String title, Exception ex) {
        ex.printStackTrace();
        VBox errorBox = new VBox(15); errorBox.setAlignment(Pos.CENTER);
        Label lblTitle = new Label("⚠️ " + title); lblTitle.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 20px; -fx-font-weight: bold;");
        TextArea txtError = new TextArea(ex.getMessage()); txtError.setEditable(false);
        errorBox.getChildren().addAll(lblTitle, txtError);
        contentArea.getChildren().add(errorBox);
    }

    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?");
        confirm.setHeaderText(null);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) primaryStage.close();
    }
}