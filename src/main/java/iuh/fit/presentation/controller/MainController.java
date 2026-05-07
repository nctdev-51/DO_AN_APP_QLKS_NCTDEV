package iuh.fit.presentation.controller;

import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.chart.PieChart;
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

    private Label lblClock;

    // --- BẢNG MÀU UI/UX CAO CẤP TỪ BẢN THIẾT KẾ ---
    private final String COLOR_PRIMARY = "#2563eb"; // Blue hiện đại
    private final String COLOR_ACCENT = "#0ea5e9";  // Light Blue
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_SIDEBAR = "#0f172a"; // Đen nhám sang trọng
    private final String COLOR_BG_LIGHT = "#f1f5f9"; // Xám nhạt nền tảng
    private final String COLOR_CARD_BG = "#ffffff";

    private final String COLOR_AVAILABLE = "#10b981"; // Xanh ngọc
    private final String COLOR_OCCUPIED = "#f59e0b";  // Cam
    private final String COLOR_MAINTENANCE = "#64748b"; // Xám

    public MainController(Stage primaryStage, TaiKhoanDTO currentUser,
                          IKhachHangService khachHangService, INhanVienService nhanVienService,
                          IPhongService phongService, IPhieuDatPhongService phieuDatPhongService,
                          IDichVuService dichVuService, IHoaDonService hoaDonService,
                          IChiTietHoaDonService chiTietHoaDonService) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.khachHangService = khachHangService;
        this.nhanVienService = nhanVienService;
        this.phongService = phongService;
        this.phieuDatPhongService = phieuDatPhongService;
        this.dichVuService = dichVuService;
        this.hoaDonService = hoaDonService;
        this.chiTietHoaDonService = chiTietHoaDonService;
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

    // =========================================================================
    // 1. CẢI TIẾN HEADER & LOGO
    // =========================================================================
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
        logoTitleBox.setPrefWidth(240); // Khớp độ rộng với Sidebar

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

        // Tên KS to, đậm, màu gradient chéo bắt mắt
        Label lblTitle = new Label("TTV HOTEL");
        lblTitle.setFont(Font.font("Verdana", FontWeight.BLACK, 26));
        lblTitle.setTextFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web(COLOR_PRIMARY)), new Stop(1.0, Color.web(COLOR_ACCENT))));
        logoTitleBox.getChildren().add(lblTitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Đồng hồ hiện đại
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

    // =========================================================================
    // 2. CẢI TIẾN SIDEBAR (MENU TRÁI CAO CẤP)
    // =========================================================================
    private VBox createSidebar() {
        VBox sidebar = new VBox(5);
        sidebar.setPadding(new Insets(30, 15, 20, 15));
        sidebar.setPrefWidth(260);
        // Nền dải màu đen nhám (Slate) xuống Midnight
        sidebar.setStyle("-fx-background-color: linear-gradient(to bottom, #0f172a, #1e293b);");

        VBox profileBox = new VBox(8);
        profileBox.setAlignment(Pos.CENTER);
        profileBox.setPadding(new Insets(0, 0, 30, 0));

        Label lblAvatar = new Label("👨‍💼");
        lblAvatar.setFont(Font.font(50));
        lblAvatar.setStyle("-fx-background-color: #334155; -fx-background-radius: 50; -fx-padding: 10;");

        Label lblUser = new Label(currentUser.getHoTenNhanVien());
        lblUser.setTextFill(Color.WHITE);
        lblUser.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        Label lblRole = new Label("Quản lý / Lễ tân");
        lblRole.setTextFill(Color.web("#94a3b8"));
        lblRole.setFont(Font.font("Segoe UI", 12));
        profileBox.getChildren().addAll(lblAvatar, lblUser, lblRole);

        Label lblMenuSection = new Label("NGHIỆP VỤ CHÍNH");
        lblMenuSection.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 10 0 5 10;");

        // Các nút Menu kiểu bo tròn (Pill shape)
        Button btnTrangChu = createMenuButton("🏠 Trang Chủ", true);
        Button btnDatPhong = createMenuButton("🏨 Đặt & Nhận Phòng", false);
        Button btnPhong_ = createMenuButton("🔑 Chọn Phòng Nhanh", false);
        Button btnGoiDichVu = createMenuButton("🍽️ Gọi Dịch Vụ POS", false);
        Button btnTraPhong = createMenuButton("💳 Thanh Toán & Trả Phòng", false);
        Button btnThongKe = createMenuButton("📈 Thống Kê Doanh Thu", false);

        Label lblListSection = new Label("QUẢN LÝ DANH MỤC");
        lblListSection.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 20 0 5 10;");

        Button btnQuanLyPhieu = createMenuButton("📋 Quản lý Phiếu Đặt", false);
        Button btnPhong = createMenuButton("🚪 Quản Lý Phòng", false);
        Button btnKhachHang = createMenuButton("👥 Khách Hàng", false);
        Button btnNhanVien = createMenuButton("👔 Nhân Viên", false);

        Button btnDangXuat = createMenuButton("🚪 Đăng xuất", false);
        btnDangXuat.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 14px; -fx-alignment: center-left; -fx-padding: 12 15; -fx-cursor: hand;");
        btnDangXuat.setOnMouseEntered(e -> btnDangXuat.setStyle("-fx-background-color: #fee2e21A; -fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 8; -fx-alignment: center-left; -fx-padding: 12 15; -fx-cursor: hand;"));
        btnDangXuat.setOnMouseExited(e -> btnDangXuat.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 14px; -fx-alignment: center-left; -fx-padding: 12 15; -fx-cursor: hand;"));

        List<Button> menuButtons = Arrays.asList(btnTrangChu, btnDatPhong, btnPhong_, btnGoiDichVu, btnTraPhong, btnThongKe, btnQuanLyPhieu, btnPhong, btnKhachHang, btnNhanVien);

        btnTrangChu.setOnAction(e -> { setActiveMenu(btnTrangChu, menuButtons); showDashboard(); });
        btnDatPhong.setOnAction(e -> { setActiveMenu(btnDatPhong, menuButtons); loadQuanLyDatPhong(); });
        btnGoiDichVu.setOnAction(e -> { setActiveMenu(btnGoiDichVu, menuButtons); loadGoiDichVu(); });
        btnTraPhong.setOnAction(e -> { setActiveMenu(btnTraPhong, menuButtons); loadTraPhong(); });
        btnThongKe.setOnAction(e -> { setActiveMenu(btnThongKe, menuButtons); loadThongKeDoanHThu(); });
        btnQuanLyPhieu.setOnAction(e -> { setActiveMenu(btnQuanLyPhieu, menuButtons); showQuanLyPhieuDatScreen(); });
        btnPhong.setOnAction(e -> { setActiveMenu(btnPhong, menuButtons); loadQuanLyPhong(); });
        btnPhong_.setOnAction(e -> { setActiveMenu(btnPhong_, menuButtons); loadManHinhChonPhong(); });
        btnKhachHang.setOnAction(e -> { setActiveMenu(btnKhachHang, menuButtons); loadQuanLyKhachHang(); });
        btnNhanVien.setOnAction(e -> { setActiveMenu(btnNhanVien, menuButtons); loadQuanLyNhanVien(); });
        btnDangXuat.setOnAction(e -> handleLogout());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(profileBox, lblMenuSection, btnTrangChu, btnDatPhong, btnPhong_, btnGoiDichVu, btnTraPhong, btnThongKe, lblListSection, btnQuanLyPhieu, btnPhong, btnKhachHang, btnNhanVien, spacer, btnDangXuat);
        return sidebar;
    }

    private Button createMenuButton(String text, boolean isDefaultActive) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setCursor(Cursor.HAND);

        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 14px; -fx-font-weight: 600; -fx-padding: 12 15; -fx-background-radius: 8;";
        String activeStyle = "-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 15; -fx-background-radius: 8;";
        String hoverStyle = "-fx-background-color: #334155; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: 600; -fx-padding: 12 15; -fx-background-radius: 8;";

        btn.setStyle(isDefaultActive ? activeStyle : defaultStyle);
        btn.setUserData(isDefaultActive ? "active" : "inactive");

        btn.setOnMouseEntered(e -> { if (!btn.getUserData().equals("active")) btn.setStyle(hoverStyle); });
        btn.setOnMouseExited(e -> { if (!btn.getUserData().equals("active")) btn.setStyle(defaultStyle); });

        return btn;
    }

    private void setActiveMenu(Button activeBtn, List<Button> allButtons) {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 14px; -fx-font-weight: 600; -fx-padding: 12 15; -fx-background-radius: 8;";
        String activeStyle = "-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12 15; -fx-background-radius: 8;";

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
    // 3. CẢI TIẾN DASHBOARD (GIAO DIỆN CHÍNH THOÁNG & SẠCH SẼ)
    // =========================================================================
    private void showDashboard() {
        try {
            contentArea.getChildren().clear();
            BorderPane dashRoot = new BorderPane();
            dashRoot.setStyle("-fx-background-color: transparent;");

            VBox topArea = new VBox(10);
            topArea.setPadding(new Insets(10, 0, 30, 0));
            topArea.getChildren().add(createDashboardTitleBar());
            dashRoot.setTop(topArea);

            VBox centerArea = buildDashboardContent();
            dashRoot.setCenter(centerArea);
            contentArea.getChildren().add(dashRoot);

            dashRoot.setUserData(centerArea);
            loadDashboardDataAsync(dashRoot);
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
        sub.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 15));
        sub.setTextFill(Color.web(COLOR_TEXT_MUTED));

        titles.getChildren().addAll(greeting, sub);
        bar.getChildren().add(titles);
        return bar;
    }

    private VBox buildDashboardContent() {
        VBox col = new VBox(30);
        col.setFillWidth(true);

        HBox statRow = new HBox(20);
        statRow.setAlignment(Pos.CENTER);

        // Truyền cặp màu (Start - End) để đổ full ô
        VBox c1 = makeStatCard("TỔNG SỐ PHÒNG", "0", "🏢", "#3b82f6", "#2563eb");
        VBox c2 = makeStatCard("PHÒNG TRỐNG", "0", "✨", "#10b981", "#059669");
        VBox c3 = makeStatCard("ĐANG PHỤC VỤ", "0", "🔑", "#f59e0b", "#d97706");
        VBox c4 = makeStatCard("ĐANG BẢO TRÌ", "0", "🔧", "#64748b", "#475569");

        HBox.setHgrow(c1, Priority.ALWAYS); HBox.setHgrow(c2, Priority.ALWAYS);
        HBox.setHgrow(c3, Priority.ALWAYS); HBox.setHgrow(c4, Priority.ALWAYS);
        statRow.getChildren().addAll(c1, c2, c3, c4);

        VBox chartCard = new VBox(20);
        chartCard.setPadding(new Insets(30));
        chartCard.setAlignment(Pos.CENTER);
        chartCard.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-color: #e2e8f0;");

        Label chartLbl = new Label("TỈ LỆ TÌNH TRẠNG PHÒNG THỰC TẾ");
        chartLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        PieChart chart = new PieChart();
        chart.setLegendSide(javafx.geometry.Side.BOTTOM);
        chart.setLabelsVisible(true);
        chart.setPrefSize(500, 400);

        Label insight = new Label("Đang phân tích dữ liệu...");
        insight.setPadding(new Insets(15));
        insight.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-font-weight: bold;");

        chartCard.getChildren().addAll(chartLbl, chart, insight);
        col.getChildren().addAll(statRow, chartCard);

        col.setUserData(new Object[]{c1, c2, c3, c4, chart, insight});
        return col;
    }

    private VBox makeStatCard(String label, String value, String icon, String colorStart, String colorEnd) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(20, 25, 20, 25));
        // Đổ full màu nguyên ô bằng LinearGradient
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, " + colorStart + ", " + colorEnd + "); " +
                "-fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label lblIcon = new Label(icon);
        lblIcon.setFont(Font.font(28));
        lblIcon.setTextFill(Color.web("#ffffff", 0.7)); // Icon mờ nhẹ tinh tế

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lbl = new Label(label);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.WHITE);

        top.getChildren().addAll(lbl, spacer, lblIcon);

        Label val = new Label(value);
        val.setFont(Font.font("Segoe UI", FontWeight.BLACK, 38));
        val.setTextFill(Color.WHITE);

        card.getChildren().addAll(top, val);
        return card;
    }

    private void loadDashboardDataAsync(BorderPane dashRoot) {
        Task<List<PhongDTO>> task = new Task<>() {
            @Override protected List<PhongDTO> call() { return phongService.getAllPhong(); }
        };
        task.setOnSucceeded(evt -> updateDashboardSummary(dashRoot, task.getValue()));
        task.setOnFailed(evt -> showErrorBox("LỖI TẢI DỮ LIỆU", (Exception) task.getException()));
        new Thread(task).start();
    }

    private void updateDashboardSummary(BorderPane dashRoot, List<PhongDTO> rooms) {
        VBox centerCol = (VBox) dashRoot.getUserData();
        Object[] refs = (Object[]) centerCol.getUserData();
        VBox c1 = (VBox) refs[0], c2 = (VBox) refs[1], c3 = (VBox) refs[2], c4 = (VBox) refs[3];
        PieChart chart = (PieChart) refs[4];
        Label insight = (Label) refs[5];

        // 1. Tính toán cực nhanh bằng Stream
        long total = rooms.size();
        long available = rooms.stream().filter(r -> "Trống".equalsIgnoreCase(r.getTinhTrang())).count();
        long occupied = rooms.stream().filter(r -> "Đang ở".equalsIgnoreCase(r.getTinhTrang()) || "Đã Đặt".equalsIgnoreCase(r.getTinhTrang())).count();
        long maintenance = total - available - occupied;

        // 2. Cập nhật số liệu (Chạy trên UI Thread)
        Platform.runLater(() -> {
            ((Label) c1.getChildren().get(1)).setText(String.valueOf(total));
            ((Label) c2.getChildren().get(1)).setText(String.valueOf(available));
            ((Label) c3.getChildren().get(1)).setText(String.valueOf(occupied));
            ((Label) c4.getChildren().get(1)).setText(String.valueOf(maintenance));

            // Tạo dữ liệu PieChart
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                    new PieChart.Data("Trống (" + available + ")", available),
                    new PieChart.Data("Phục vụ (" + occupied + ")", occupied),
                    new PieChart.Data("Bảo trì (" + maintenance + ")", maintenance)
            );

            chart.setData(pieData);

            // Duyệt qua các miếng bánh và áp màu
            for (PieChart.Data data : chart.getData()) {
                String color = "#64748b"; // Mặc định xám
                if (data.getName().contains("Trống")) color = "#10b981";
                else if (data.getName().contains("Phục vụ")) color = "#f59e0b";

                data.getNode().setStyle("-fx-pie-color: " + color + "; -fx-border-color: white; -fx-border-width: 2;");
            }

            // Cập nhật insight
            insight.setText(available >= occupied ?
                    "💡 Hệ thống đang vận hành tốt. Số lượng phòng trống dồi dào." :
                    "🔥 Cảnh báo: Công suất phòng gần đạt ngưỡng tối đa!");
            insight.setTextFill(Color.web(available >= occupied ? "#10b981" : "#f59e0b"));
        });
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
            // Đổi RevenueController thành ThongKeDoanhThuController nếu file của bạn tên như vậy
            ThongKeDoanhThuController rc = new ThongKeDoanhThuController(hoaDonService);
            contentArea.getChildren().add(rc.createRevenueView());
        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorBox("LỖI HIỂN THỊ THỐNG KÊ", ex); // Thêm cái này để hiện lỗi ra màn hình cho dễ sửa
        }
    }

    // Đã thêm nhanVienService vào hàm tạo để khắc phục lỗi biên dịch
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