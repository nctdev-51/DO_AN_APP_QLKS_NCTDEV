package iuh.fit.presentation.controller;

import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.IKhachHangService;
import iuh.fit.core.service.INhanVienService;
import iuh.fit.core.service.IPhieuDatPhongService;
import iuh.fit.core.service.IPhongService;
import iuh.fit.core.service.IDichVuService;
import iuh.fit.core.service.IHoaDonService;
import iuh.fit.core.service.IChiTietHoaDonService;
import javafx.application.Platform;
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
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

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

    // --- CÁC MÃ MÀU HIỆN ĐẠI TÙY CHỈNH ---
    private final String COLOR_PRIMARY = "#0066cc";
    private final String COLOR_ACCENT = "#17a2b8";
    private final String COLOR_PRIMARY_HOVER = "#004999";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_SIDEBAR = "#1e293b";
    private final String COLOR_SIDEBAR_HOVER = "#334155";
    private final String COLOR_BG_LIGHT = "#f8fafc";

    private final String COLOR_AVAILABLE = "#10b981";
    private final String COLOR_OCCUPIED = "#f59e0b";
    private final String COLOR_DANGER = "#ef4444";

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
        contentArea.setPadding(new Insets(20));
        rootLayout.setCenter(contentArea);

        showDashboard();

        javafx.geometry.Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(rootLayout, screenBounds.getWidth(), screenBounds.getHeight());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Quản Lý Khách Sạn TTV - " + currentUser.getHoTenNhanVien());
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 25, 15, 25));
        header.setStyle("-fx-background-color: white; -fx-border-width: 0 0 1px 0; -fx-border-color: #e2e8f0;");
        header.setAlignment(Pos.CENTER_LEFT);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.05));
        shadow.setRadius(5);
        shadow.setOffsetY(2);
        header.setEffect(shadow);

        HBox logoTitleBox = new HBox(6);
        logoTitleBox.setAlignment(Pos.CENTER_LEFT);

        ImageView logoImageView = new ImageView();
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo_ttv.png"));
            if (logoImage.getWidth() > 0) {
                logoImageView.setImage(logoImage);
                logoImageView.setFitWidth(58);
                logoImageView.setFitHeight(58);
                logoImageView.setPreserveRatio(true);
                logoTitleBox.getChildren().add(logoImageView);
            }
        } catch (Exception e) {
            Label logoEmoji = new Label("🏨");
            logoEmoji.setFont(Font.font(40));
            logoTitleBox.getChildren().add(logoEmoji);
        }

        Label lblTitle = new Label("TTV HOTEL");
        lblTitle.setFont(Font.font("Segoe UI Semibold", FontWeight.EXTRA_BOLD, 25));
        lblTitle.setTextFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web(COLOR_ACCENT)), new Stop(1.0, Color.web(COLOR_PRIMARY))));
        logoTitleBox.getChildren().add(lblTitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        lblClock = new Label();
        lblClock.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 15));
        lblClock.setTextFill(Color.web("#475569"));
        startClock();

        HBox rightControls = new HBox(25);
        rightControls.setAlignment(Pos.CENTER_RIGHT);
        rightControls.getChildren().add(lblClock);

        header.getChildren().addAll(logoTitleBox, spacer, rightControls);
        return header;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(5);
        sidebar.setPadding(new Insets(25, 0, 20, 0));
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: " + COLOR_SIDEBAR + ";");

        VBox profileBox = new VBox(8);
        profileBox.setAlignment(Pos.CENTER);
        profileBox.setPadding(new Insets(0, 0, 25, 0));

        Label lblAvatar = new Label("👤");
        lblAvatar.setFont(Font.font(45));
        lblAvatar.setTextFill(Color.web("#94a3b8"));
        Label lblUser = new Label(currentUser.getHoTenNhanVien());
        lblUser.setTextFill(Color.WHITE);
        lblUser.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        profileBox.getChildren().addAll(lblAvatar, lblUser);

        Label lblMenuSection = new Label("NGHIỆP VỤ");
        lblMenuSection.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 10 0 5 25;");

        Button btnTrangChu = createMenuButton("🏠 Trang Chủ", true);
        Button btnDatPhong = createMenuButton("🏨 Đặt & Nhận Phòng", false);
        Button btnPhong_ = createMenuButton("Phòng", false);

        Button btnGoiDichVu = createMenuButton("🍽️ Gọi Dịch Vụ POS", false);
        Button btnTraPhong = createMenuButton("💳 Trả Phòng & Thanh Toán", false);
        Button btnThongKe = createMenuButton("💰 Thống Kê Doanh Thu", false);
        Button btnQuanLyPhieu = createMenuButton("📋 Quản lý Phiếu Đặt", false);
        Button btnPhong = createMenuButton("🚪 Quản Lý Phòng", false);

        Label lblListSection = new Label("DANH MỤC");
        lblListSection.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 20 0 5 25;");

        Button btnKhachHang = createMenuButton("👥 Khách Hàng", false);
        Button btnNhanVien = createMenuButton("👨‍💼 Nhân Viên", false);

        Button btnDangXuat = createMenuButton("🚪 Đăng xuất", false);
        btnDangXuat.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-padding: 12 25;");

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

        sidebar.getChildren().addAll(profileBox, lblMenuSection, btnTrangChu, btnDatPhong, btnPhong_, btnGoiDichVu, btnTraPhong, btnThongKe, btnQuanLyPhieu, btnPhong, lblListSection, btnKhachHang, btnNhanVien, spacer, btnDangXuat);
        return sidebar;
    }

    private Button createMenuButton(String text, boolean isDefaultActive) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setCursor(Cursor.HAND);
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #cbd5e1; -fx-font-size: 14px; -fx-padding: 12px 10px 12px 25px; -fx-border-width: 0 0 0 4px; -fx-border-color: transparent;";
        String activeStyle = "-fx-background-color: #2563eb1A; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12px 10px 12px 25px; -fx-border-width: 0 0 0 4px; -fx-border-color: " + COLOR_PRIMARY + "; -fx-font-weight: bold;";
        btn.setStyle(isDefaultActive ? activeStyle : defaultStyle);
        btn.setUserData(isDefaultActive ? "active" : "inactive");
        return btn;
    }

    private void setActiveMenu(Button activeBtn, List<Button> allButtons) {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #cbd5e1; -fx-font-size: 14px; -fx-padding: 12px 10px 12px 25px; -fx-border-width: 0 0 0 4px; -fx-border-color: transparent;";
        String activeStyle = "-fx-background-color: #2563eb1A; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12px 10px 12px 25px; -fx-border-width: 0 0 0 4px; -fx-border-color: " + COLOR_PRIMARY + "; -fx-font-weight: bold;";
        for (Button btn : allButtons) { btn.setStyle(defaultStyle); btn.setUserData("inactive"); }
        activeBtn.setStyle(activeStyle); activeBtn.setUserData("active");
    }

    private void startClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss  |  dd/MM/yyyy");
        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, e -> lblClock.setText(LocalDateTime.now().format(formatter))), new KeyFrame(Duration.seconds(1)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    // =========================================================================
    // DASHBOARD - TRANG CHỦ MỚI
    // =========================================================================

    private void showDashboard() {
        try {
            contentArea.getChildren().clear();
            BorderPane dashRoot = new BorderPane();
            dashRoot.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + ";");

            VBox topArea = new VBox(12);
            topArea.setPadding(new Insets(16));
            topArea.getChildren().add(createDashboardTitleBar()); // 👉 ĐÃ THÊM LẠI THÂN HÀM PHƯƠNG THỨC NÀY Ở DƯỚI
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

    // 👉 THÂN HÀM BỊ THIẾU NÈ TÚ ƠI:
    private HBox createDashboardTitleBar() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);

        VBox titles = new VBox(3);
        Label title = new Label("Tổng quan hệ thống");
        title.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 26));
        title.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label sub = new Label("Dashboard thống kê tình trạng phòng theo thời gian thực.");
        sub.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 15));
        sub.setTextFill(Color.web(COLOR_TEXT_MUTED));

        titles.getChildren().addAll(title, sub);
        bar.getChildren().add(titles);
        return bar;
    }

    private VBox buildDashboardContent() {
        VBox col = new VBox(30);
        col.setPadding(new Insets(10, 30, 20, 30));
        col.setFillWidth(true);

        HBox statRow = new HBox(20);
        statRow.setAlignment(Pos.CENTER);
        VBox c1 = makeStatCard("Tổng Số Phòng", "0", "#3b82f6", "#1d4ed8");
        VBox c2 = makeStatCard("Phòng Trống", "0", "#10b981", "#047857");
        VBox c3 = makeStatCard("Đang Phục Vụ", "0", "#f59e0b", "#b45309");
        VBox c4 = makeStatCard("Đang Bảo Trì", "0", "#64748b", "#334155");
        HBox.setHgrow(c1, Priority.ALWAYS); HBox.setHgrow(c2, Priority.ALWAYS);
        HBox.setHgrow(c3, Priority.ALWAYS); HBox.setHgrow(c4, Priority.ALWAYS);
        statRow.getChildren().addAll(c1, c2, c3, c4);

        VBox chartCard = new VBox(15);
        chartCard.setPadding(new Insets(25));
        chartCard.setAlignment(Pos.CENTER);
        chartCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #e2e8f0; -fx-border-width: 1;");
        DropShadow shadow = new DropShadow(); shadow.setColor(Color.web("#000000", 0.03)); shadow.setRadius(10); shadow.setOffsetY(4);
        chartCard.setEffect(shadow);
        VBox.setVgrow(chartCard, Priority.ALWAYS);

        Label chartLbl = new Label("BIỂU ĐỒ TÌNH TRẠNG PHÒNG");
        chartLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        PieChart chart = new PieChart();
        chart.setLegendSide(javafx.geometry.Side.BOTTOM);
        chart.setLabelsVisible(true);
        chart.setPrefSize(400, 350);

        Label insight = new Label("Đang tải dữ liệu...");
        insight.setWrapText(true); insight.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 15));
        insight.setPadding(new Insets(15, 30, 15, 30));
        insight.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e2e8f0;");
        insight.setMaxWidth(800); insight.setAlignment(Pos.CENTER);

        chartCard.getChildren().addAll(chartLbl, chart, insight);
        col.getChildren().addAll(statRow, chartCard);
        col.setUserData(new Object[]{c1, c2, c3, c4, chart, insight});
        return col;
    }

    private VBox makeStatCard(String label, String value, String colorStart, String colorEnd) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(18, 20, 18, 20));
        card.setStyle("-fx-background-color: linear-gradient(to right bottom, " + colorStart + ", " + colorEnd + "); -fx-background-radius: 12; -fx-border-radius: 12;");
        Label lbl = new Label(label.toUpperCase());
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.web("#ffffff", 0.9));
        Label val = new Label(value);
        val.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 36));
        val.setTextFill(Color.WHITE);
        card.getChildren().addAll(lbl, val);
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
        if (centerCol == null) return;
        Object[] refs = (Object[]) centerCol.getUserData();
        VBox c1 = (VBox) refs[0], c2 = (VBox) refs[1], c3 = (VBox) refs[2], c4 = (VBox) refs[3];
        PieChart chart = (PieChart) refs[4];
        Label insight = (Label) refs[5];

        long total = rooms.size();
        long available = rooms.stream().filter(r -> "Trống".equalsIgnoreCase(r.getTinhTrang())).count();
        long occupied = rooms.stream().filter(r -> "Đang ở".equalsIgnoreCase(r.getTinhTrang()) || "Đã Đặt".equalsIgnoreCase(r.getTinhTrang())).count();
        long maintenance = total - available - occupied;

        ((Label) c1.getChildren().get(1)).setText(String.valueOf(total));
        ((Label) c2.getChildren().get(1)).setText(String.valueOf(available));
        ((Label) c3.getChildren().get(1)).setText(String.valueOf(occupied));
        ((Label) c4.getChildren().get(1)).setText(String.valueOf(maintenance));

        chart.getData().setAll(new PieChart.Data("Trống", available), new PieChart.Data("Phục vụ", occupied), new PieChart.Data("Bảo trì", maintenance));

        if (available >= occupied) {
            insight.setText("💡 Khách sạn hiện đang có nhiều phòng trống, sẵn sàng đón lượt khách mới.");
            insight.setTextFill(Color.web(COLOR_AVAILABLE));
        } else {
            insight.setText("🔥 Công suất phục vụ đang ở mức cao. Vui lòng theo dõi sát sao lịch trả phòng.");
            insight.setTextFill(Color.web(COLOR_OCCUPIED));
        }
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
            QuanLyDichVuController gd = new QuanLyDichVuController(dichVuService, phieuDatPhongService, chiTietHoaDonService);
            contentArea.getChildren().add(gd.createView());
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadTraPhong() {
        try {
            contentArea.getChildren().clear();
            QuanLyPhieuDatTraPhongController controller = new QuanLyPhieuDatTraPhongController(phieuDatPhongService, phongService, khachHangService, hoaDonService, chiTietHoaDonService, dichVuService, currentUser);
            BorderPane view = controller.createMainView();
            view.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            contentArea.getChildren().setAll(view);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadThongKeDoanHThu() {
        try {
            contentArea.getChildren().clear();
            RevenueController rc = new RevenueController(hoaDonService);
            contentArea.getChildren().add(rc.createRevenueView());
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void showQuanLyPhieuDatScreen() {
        try {
            contentArea.getChildren().clear();
            QuanLyPhieuDatPhongController qlPhieuController = new QuanLyPhieuDatPhongController(phieuDatPhongService, phongService, khachHangService, currentUser);
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

    private void loadQuanLyDichVu() {
        try {
            contentArea.getChildren().clear();
            QuanLyDichVuController dvController = new QuanLyDichVuController(dichVuService, phieuDatPhongService, chiTietHoaDonService);
            VBox view = dvController.createView();
            view.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            contentArea.getChildren().setAll(view);
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

    private void showPlaceholder(String message) {
        contentArea.getChildren().clear();
        Label lbl = new Label("🚧 " + message); lbl.setFont(Font.font(24));
        contentArea.getChildren().add(lbl);
    }

    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có muốn đăng xuất?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) primaryStage.close();
    }
}