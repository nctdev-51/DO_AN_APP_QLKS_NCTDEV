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
        VBox sidebar = new VBox(5);
        sidebar.setPadding(new Insets(20, 15, 15, 15));
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: linear-gradient(to bottom, #0f172a, #1e293b);");

        // ==========================================================
        // 1. KIỂM TRA QUYỀN HẠN (ROLE) ĐỂ HIỂN THỊ NÚT
        // ==========================================================
        boolean isManager = false;
        String roleName = "Lễ tân";
        try {
            if (currentUser.getTenDangNhap().equalsIgnoreCase("admin")) {
                isManager = true;
                roleName = "Quản trị viên (Admin)";
            } else {
                iuh.fit.core.dto.NhanVienDTO nv = nhanVienService.getNhanVienById(currentUser.getMaNhanVien());
                if (nv != null && (nv.getLoaiNhanVien().contains("QUAN_LY") || nv.getLoaiNhanVien().contains("GIAM_DOC"))) {
                    isManager = true;
                    roleName = "Quản lý";
                }
            }
        } catch (Exception ignored) {}

        // ==========================================================
        // 2. KHU VỰC THÔNG TIN TÀI KHOẢN (PROFILE)
        // ==========================================================
        VBox profileBox = new VBox(5);
        profileBox.setAlignment(Pos.CENTER);
        profileBox.setPadding(new Insets(0, 0, 15, 0));

        Label lblAvatar = new Label("👨‍💼");
        lblAvatar.setFont(Font.font(38));
        lblAvatar.setStyle("-fx-background-color: #334155; -fx-background-radius: 50; -fx-padding: 8;");

        Label lblUser = new Label(currentUser.getHoTenNhanVien());
        lblUser.setTextFill(Color.WHITE);
        lblUser.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));

        Label lblRole = new Label(roleName); // Hiển thị chức danh động
        lblRole.setTextFill(Color.web("#94a3b8"));
        lblRole.setFont(Font.font("Segoe UI", 11));
        profileBox.getChildren().addAll(lblAvatar, lblUser, lblRole);

        // ==========================================================
        // 3. KHỞI TẠO CÁC NÚT (BUTTON) DÙNG CHUNG
        // ==========================================================
        Label lblMenuSection = new Label("NGHIỆP VỤ CHÍNH");
        lblMenuSection.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 5 0 2 10;");

        Button btnTrangChu = createMenuButton("🏠 Trang Chủ", true);
        Button btnDatPhong = createMenuButton("🏨 Đặt & Nhận Phòng", false);
        Button btnPhong_ = createMenuButton("🔑 Chọn Phòng Nhanh", false);
        Button btnGoiDichVu = createMenuButton("🛒 Gọi Dịch Vụ POS", false);
        Button btnTraPhong = createMenuButton("💳 Thanh Toán & Trả Phòng", false);

        Label lblListSection = new Label("QUẢN LÝ DANH MỤC");
        lblListSection.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 10 0 2 10;");

        Button btnQuanLyPhieu = createMenuButton("📋 Quản Lý Phiếu Đặt", false);
        Button btnPhong = createMenuButton("🚪 Quản Lý Phòng", false);
        Button btnKhachHang = createMenuButton("👥 Quản Lý Khách Hàng", false);

        // ==========================================================
        // 4. KHỞI TẠO CÁC NÚT DÀNH RIÊNG CHO QUẢN LÝ VÀ LỄ TÂN
        // ==========================================================
        Button btnThongKe = createMenuButton("📈 Thống Kê Doanh Thu", false);
        Button btnNhanVien = createMenuButton("👔 Quản Lý Nhân Viên", false);
        Button btnPhanCong = createMenuButton("📅 Phân Công Ca", false);
        Button btnHopThu = createMenuButton("📥 Hộp Thư Báo Cáo", false);
        Button btnGuiBaoCao = createMenuButton("📝 Gửi Báo Cáo Sự Cố", false);

        Button btnDangXuat = createMenuButton("🚪 Đăng xuất", false);
        btnDangXuat.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 13px; -fx-alignment: center-left; -fx-padding: 10 15; -fx-cursor: hand;");
        btnDangXuat.setOnMouseEntered(e -> btnDangXuat.setStyle("-fx-background-color: #fee2e21A; -fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 8; -fx-alignment: center-left; -fx-padding: 10 15; -fx-cursor: hand;"));
        btnDangXuat.setOnMouseExited(e -> btnDangXuat.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 13px; -fx-alignment: center-left; -fx-padding: 10 15; -fx-cursor: hand;"));

        List<Button> menuButtons = Arrays.asList(btnTrangChu, btnDatPhong, btnPhong_, btnGoiDichVu, btnTraPhong, btnThongKe, btnQuanLyPhieu, btnPhong, btnKhachHang, btnNhanVien, btnPhanCong, btnHopThu, btnGuiBaoCao);

        // Set action cho các nút chung
        btnTrangChu.setOnAction(e -> { setActiveMenu(btnTrangChu, menuButtons); showDashboard(); });
        btnDatPhong.setOnAction(e -> { setActiveMenu(btnDatPhong, menuButtons); loadQuanLyDatPhong(); });
        btnGoiDichVu.setOnAction(e -> { setActiveMenu(btnGoiDichVu, menuButtons); loadGoiDichVu(); });
        btnTraPhong.setOnAction(e -> { setActiveMenu(btnTraPhong, menuButtons); loadTraPhong(); });
        btnQuanLyPhieu.setOnAction(e -> { setActiveMenu(btnQuanLyPhieu, menuButtons); showQuanLyPhieuDatScreen(); });
        btnPhong.setOnAction(e -> { setActiveMenu(btnPhong, menuButtons); loadQuanLyPhong(); });
        btnPhong_.setOnAction(e -> { setActiveMenu(btnPhong_, menuButtons); loadManHinhChonPhong(); });
        btnKhachHang.setOnAction(e -> { setActiveMenu(btnKhachHang, menuButtons); loadQuanLyKhachHang(); });
        btnDangXuat.setOnAction(e -> handleLogout());

        // ==========================================================
        // 5. GẮN CÁC NÚT VÀO THANH SIDEBAR DỰA VÀO QUYỀN
        // ==========================================================
        sidebar.getChildren().addAll(
                profileBox,
                lblMenuSection, btnTrangChu, btnDatPhong, btnPhong_, btnGoiDichVu, btnTraPhong,
                lblListSection, btnQuanLyPhieu, btnPhong, btnKhachHang
        );

        // Khởi tạo Service Báo Cáo dùng chung
        iuh.fit.core.repository.IBaoCaoRepository bcRepo = new iuh.fit.infrastructure.persistence.BaoCaoRepositoryImpl();
        iuh.fit.core.service.IBaoCaoService bcService = new iuh.fit.core.service.impl.BaoCaoServiceImpl(bcRepo);

        if (isManager) {
            // NẾU LÀ QUẢN LÝ: Gắn thêm Thống kê, Nhân viên, Phân công, Hộp thư
            iuh.fit.core.repository.IPhanCongRepository pcRepo = new iuh.fit.infrastructure.persistence.PhanCongRepositoryImpl();
            iuh.fit.core.service.IPhanCongService pcService = new iuh.fit.core.service.impl.PhanCongServiceImpl(pcRepo);
            iuh.fit.core.service.ICaLamViecService caService = new iuh.fit.core.service.impl.CaLamViecServiceImpl();

            btnNhanVien.setOnAction(e -> { setActiveMenu(btnNhanVien, menuButtons); loadQuanLyNhanVien(); });
            btnThongKe.setOnAction(e -> { setActiveMenu(btnThongKe, menuButtons); loadThongKeDoanHThu(); });
            btnPhanCong.setOnAction(e -> {
                setActiveMenu(btnPhanCong, menuButtons);
                contentArea.getChildren().clear();
                QuanLyPhanCongCaController controller = new QuanLyPhanCongCaController(pcService, nhanVienService, caService);
                contentArea.getChildren().add(controller.createView());
            });
            btnHopThu.setOnAction(e -> {
                setActiveMenu(btnHopThu, menuButtons);
                contentArea.getChildren().clear();
                QuanLyBaoCaoController controller = new QuanLyBaoCaoController(bcService);
                contentArea.getChildren().add(controller.createView());
            });

            sidebar.getChildren().addAll(btnNhanVien, btnPhanCong, btnThongKe, btnHopThu);
        } else {
            // NẾU LÀ LỄ TÂN: Giấu các nút quản lý, chỉ thêm nút "Gửi Báo Cáo"
            btnGuiBaoCao.setOnAction(e -> {
                setActiveMenu(btnGuiBaoCao, menuButtons);
                new TaoBaoCaoDialog(bcService, currentUser).showDialog();
            });
            sidebar.getChildren().add(btnGuiBaoCao);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().addAll(spacer, btnDangXuat);

        return sidebar;
    }

    private Button createMenuButton(String text, boolean isDefaultActive) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setCursor(Cursor.HAND);

        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-font-weight: 600; -fx-padding: 10 15; -fx-background-radius: 8;";
        String activeStyle = "-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 10 15; -fx-background-radius: 8;";
        String hoverStyle = "-fx-background-color: #334155; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: 600; -fx-padding: 10 15; -fx-background-radius: 8;";

        btn.setStyle(isDefaultActive ? activeStyle : defaultStyle);
        btn.setUserData(isDefaultActive ? "active" : "inactive");

        btn.setOnMouseEntered(e -> { if (!btn.getUserData().equals("active")) btn.setStyle(hoverStyle); });
        btn.setOnMouseExited(e -> { if (!btn.getUserData().equals("active")) btn.setStyle(defaultStyle); });

        return btn;
    }

    private void setActiveMenu(Button activeBtn, List<Button> allButtons) {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-font-weight: 600; -fx-padding: 10 15; -fx-background-radius: 8;";
        String activeStyle = "-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 10 15; -fx-background-radius: 8;";

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

    private void loadManHinhChonPhong() {
        try {
            contentArea.getChildren().clear();
            ChonPhongController controller = new ChonPhongController(phongService, khachHangService, phieuDatPhongService, currentUser, primaryStage);
            HBox view = controller.createView();
            view.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            contentArea.getChildren().setAll(view);
        } catch (Exception ex) { ex.printStackTrace(); }
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

    private void loadTraPhong() {
        try {
            contentArea.getChildren().clear();
            ThanhToanTraPhongController controller = new ThanhToanTraPhongController(phieuDatPhongService, phongService, khachHangService, hoaDonService, chiTietHoaDonService, dichVuService, currentUser);
            BorderPane view = controller.createMainView();
            view.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            contentArea.getChildren().setAll(view);
        } catch (Exception ex) { ex.printStackTrace(); }
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

    private void showQuanLyPhieuDatScreen() {
        try {
            contentArea.getChildren().clear();
            QuanLyPhieuDatPhongController qlPhieuController = new QuanLyPhieuDatPhongController(phieuDatPhongService, phongService, khachHangService, nhanVienService, currentUser);
            VBox view = qlPhieuController.createQuanLyPhieuView();
            view.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            contentArea.getChildren().setAll(view);
        } catch (Exception ex) { ex.printStackTrace(); }
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