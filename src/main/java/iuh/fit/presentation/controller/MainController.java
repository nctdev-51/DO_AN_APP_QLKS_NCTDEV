package iuh.fit.presentation.controller;

import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.IKhachHangService;
import iuh.fit.core.service.INhanVienService;
import iuh.fit.core.service.IPhieuDatPhongService;
import iuh.fit.core.service.IPhongService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;
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

    private Label lblClock;
    private VBox roomContainer;
    private List<PhongDTO> dashboardRoomsCache = new ArrayList<>();

    // --- CÁC MÃ MÀU HIỆN ĐẠI TÙY CHỈNH ---
    private final String COLOR_PRIMARY = "#0066cc";  // Xanh lam đậm khớp logo
    private final String COLOR_ACCENT = "#17a2b8";   // Xanh lục khớp logo
    private final String COLOR_PRIMARY_HOVER = "#004999";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_SIDEBAR = "#1e293b";
    private final String COLOR_SIDEBAR_HOVER = "#334155";
    private final String COLOR_BG_LIGHT = "#f8fafc";
    private final String COLOR_AVAILABLE = "#10b981";
    private final String COLOR_BOOKED = "#ef4444";
    private final String COLOR_OCCUPIED = "#f59e0b";
    private final String COLOR_MAINTENANCE = "#64748b";

    public MainController(Stage primaryStage, TaiKhoanDTO currentUser,
                          IKhachHangService khachHangService, INhanVienService nhanVienService,
                          IPhongService phongService, IPhieuDatPhongService phieuDatPhongService) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.khachHangService = khachHangService;
        this.nhanVienService = nhanVienService;
        this.phongService = phongService;
        this.phieuDatPhongService = phieuDatPhongService;
    }

    public void showMainScreen() {
        rootLayout = new BorderPane();
        rootLayout.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + ";");

        HBox header = createHeader();
        rootLayout.setTop(header);

        VBox sidebar = createSidebar();
        rootLayout.setLeft(sidebar);

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

        // Logo + Title
        HBox logoTitleBox = new HBox(6);
        logoTitleBox.setAlignment(Pos.CENTER_LEFT);

        // Tạo ImageView cho logo - To hơn
        ImageView logoImageView = new ImageView();
        boolean hasLogo = false;
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo_ttv.png"));
            if (logoImage.getWidth() > 0) {
                logoImageView.setImage(logoImage);
                logoImageView.setFitWidth(58);
                logoImageView.setFitHeight(58);
                logoImageView.setPreserveRatio(true);

                // Thêm shadow effect cho logo
                DropShadow logoShadow = new DropShadow();
                logoShadow.setColor(Color.web(COLOR_PRIMARY, 0.2));
                logoShadow.setRadius(6);
                logoShadow.setOffsetY(2);
                logoImageView.setEffect(logoShadow);

                hasLogo = true;
            }
        } catch (Exception e) {
            // Logo không tìm được
        }

        if (hasLogo) {
            logoTitleBox.getChildren().add(logoImageView);
        } else {
            Label logoEmoji = new Label("🏨");
            logoEmoji.setFont(Font.font(40));
            logoTitleBox.getChildren().add(logoEmoji);
        }

        Label lblTitle = new Label("TTV HOTEL");
        lblTitle.setFont(Font.font("Segoe UI Semibold", FontWeight.EXTRA_BOLD, 25));
        lblTitle.setTextFill(new LinearGradient(
                0, 0, 1, 0,
                true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web(COLOR_ACCENT)),
                new Stop(1.0, Color.web(COLOR_PRIMARY))
        ));

        // Thêm shadow effect cho text
        DropShadow titleShadow = new DropShadow();
        titleShadow.setColor(Color.web(COLOR_PRIMARY, 0.15));
        titleShadow.setRadius(4);
        titleShadow.setOffsetY(1);
        lblTitle.setEffect(titleShadow);

        logoTitleBox.getChildren().add(lblTitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        lblClock = new Label();
        lblClock.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 15));
        lblClock.setTextFill(Color.web("#475569"));
        startClock();

        Button btnCaLamViec = new Button("🔄 Nhận / Giao Ca");
        btnCaLamViec.setStyle("-fx-background-color: #f8fafc; -fx-text-fill: #475569; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-weight: bold; -fx-padding: 8 15; -fx-cursor: hand;");
        btnCaLamViec.setOnMouseEntered(e -> btnCaLamViec.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-weight: bold; -fx-padding: 8 15; -fx-cursor: hand;"));
        btnCaLamViec.setOnMouseExited(e -> btnCaLamViec.setStyle("-fx-background-color: #f8fafc; -fx-text-fill: #475569; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-font-weight: bold; -fx-padding: 8 15; -fx-cursor: hand;"));
        btnCaLamViec.setOnAction(e -> showPlaceholder("Module Giao Ca đang phát triển..."));

        HBox rightControls = new HBox(25);
        rightControls.setAlignment(Pos.CENTER_RIGHT);
        rightControls.getChildren().addAll(lblClock, btnCaLamViec);

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
        StackPane avatarPane = new StackPane(lblAvatar);
        avatarPane.setStyle("-fx-background-color: #334155; -fx-background-radius: 50%; -fx-min-width: 70; -fx-min-height: 70; -fx-max-width: 70; -fx-max-height: 70;");

        Label lblUser = new Label(currentUser.getHoTenNhanVien());
        lblUser.setTextFill(Color.WHITE);
        lblUser.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        Label lblRole = new Label(currentUser.getTaiKhoan().equals("admin") ? "Quản Lý" : "Lễ Tân");
        lblRole.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-padding: 3 10; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");

        profileBox.getChildren().addAll(avatarPane, lblUser, lblRole);

        // --- NHÓM NGHIỆP VỤ ---
        Label lblMenuSection = new Label("NGHIỆP VỤ");
        lblMenuSection.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 10 0 5 25;");

        Button btnTrangChu = createMenuButton("🏠 Trang Chủ", true);
        Button btnDatPhong = createMenuButton("🏨 Đặt & Nhận Phòng", false);
        Button btnGoiDichVu = createMenuButton("🍽️ Gọi Dịch Vụ", false);
        Button btnQuanLyPhieu = createMenuButton("📋 Quản lý Phiếu Đặt", false);
        // ĐÃ CHUYỂN NÚT PHÒNG LÊN ĐÂY
        Button btnPhong = createMenuButton("🚪 Quản Lý Phòng", false);

        // --- NHÓM DANH MỤC ---
        Label lblListSection = new Label("DANH MỤC");
        lblListSection.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 20 0 5 25;");

        Button btnKhachHang = createMenuButton("👥 Khách Hàng", false);
        Button btnNhanVien = createMenuButton("👨‍💼 Nhân Viên", false);

        Button btnDangXuat = new Button("🚪 Đăng xuất");
        btnDangXuat.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(btnDangXuat, new Insets(0, 20, 0, 20));
        btnDangXuat.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-border-color: #ef4444; -fx-border-radius: 6; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px; -fx-cursor: hand;");
        btnDangXuat.setOnMouseEntered(e -> btnDangXuat.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-border-color: #ef4444; -fx-border-radius: 6; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px; -fx-cursor: hand;"));
        btnDangXuat.setOnMouseExited(e -> btnDangXuat.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-border-color: #ef4444; -fx-border-radius: 6; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px; -fx-cursor: hand;"));

        List<Button> menuButtons = Arrays.asList(btnTrangChu, btnDatPhong, btnGoiDichVu, btnQuanLyPhieu, btnPhong, btnKhachHang, btnNhanVien);

        btnTrangChu.setOnAction(e -> { setActiveMenu(btnTrangChu, menuButtons); showDashboard(); });
        btnDatPhong.setOnAction(e -> { setActiveMenu(btnDatPhong, menuButtons); loadQuanLyDatPhong(); });
        btnPhong.setOnAction(e -> { setActiveMenu(btnPhong, menuButtons); loadQuanLyPhong(); });
        btnKhachHang.setOnAction(e -> { setActiveMenu(btnKhachHang, menuButtons); loadQuanLyKhachHang(); });
        btnNhanVien.setOnAction(e -> { setActiveMenu(btnNhanVien, menuButtons); loadQuanLyNhanVien(); });
        btnGoiDichVu.setOnAction(e -> { setActiveMenu(btnGoiDichVu, menuButtons); showGoiDichVuScreen(); });
        btnQuanLyPhieu.setOnAction(e -> { setActiveMenu(btnQuanLyPhieu, menuButtons); showQuanLyPhieuDatScreen(); });
        btnDangXuat.setOnAction(e -> handleLogout());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // ĐÃ CHUYỂN VỊ TRÍ CỦA btnPhong TRONG DANH SÁCH HIỂN THỊ
        sidebar.getChildren().addAll(
                profileBox,
                lblMenuSection, btnTrangChu, btnDatPhong, btnGoiDichVu, btnQuanLyPhieu, btnPhong,
                lblListSection, btnKhachHang, btnNhanVien,
                spacer, btnDangXuat
        );

        return sidebar;
    }

    private Button createMenuButton(String text, boolean isDefaultActive) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setCursor(Cursor.HAND);

        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #cbd5e1; -fx-font-size: 14px; -fx-padding: 12px 10px 12px 25px; -fx-border-width: 0 0 0 4px; -fx-border-color: transparent;";
        String hoverStyle = "-fx-background-color: " + COLOR_SIDEBAR_HOVER + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12px 10px 12px 25px; -fx-border-width: 0 0 0 4px; -fx-border-color: #94a3b8;";
        String activeStyle = "-fx-background-color: #2563eb1A; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12px 10px 12px 25px; -fx-border-width: 0 0 0 4px; -fx-border-color: " + COLOR_PRIMARY + "; -fx-font-weight: bold;";

        if (isDefaultActive) {
            btn.setStyle(activeStyle);
            btn.setUserData("active");
        } else {
            btn.setStyle(defaultStyle);
            btn.setUserData("inactive");
        }

        btn.setOnMouseEntered(e -> { if (!"active".equals(btn.getUserData())) btn.setStyle(hoverStyle); });
        btn.setOnMouseExited(e -> { if (!"active".equals(btn.getUserData())) btn.setStyle(defaultStyle); });

        return btn;
    }

    private void setActiveMenu(Button activeBtn, List<Button> allButtons) {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #cbd5e1; -fx-font-size: 14px; -fx-padding: 12px 10px 12px 25px; -fx-border-width: 0 0 0 4px; -fx-border-color: transparent;";
        String activeStyle = "-fx-background-color: #2563eb1A; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12px 10px 12px 25px; -fx-border-width: 0 0 0 4px; -fx-border-color: " + COLOR_PRIMARY + "; -fx-font-weight: bold;";

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
    // KHU VỰC LOAD GIAO DIỆN ĐÃ ĐƯỢC BỌC BẪY LỖI TOÀN DIỆN
    // =========================================================================

    // =========================================================================
// DASHBOARD - LAYOUT 2 CỘT: TRÁI = BIỂU ĐỒ, PHẢI = DANH SÁCH PHÒNG
// =========================================================================

    private void showDashboard() {
        try {
            contentArea.getChildren().clear();

            BorderPane dashRoot = new BorderPane();
            dashRoot.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + ";");

            // TOP: Header + Filter bar
            VBox topArea = new VBox(12);
            topArea.setPadding(new Insets(16, 16, 0, 16));
            topArea.getChildren().addAll(createDashboardTitleBar(), createFilterBar());
            dashRoot.setTop(topArea);

            // CENTER: 2 cột
            HBox twoCol = new HBox(14);
            twoCol.setPadding(new Insets(14, 16, 16, 16));
            BorderPane.setMargin(twoCol, new Insets(12, 0, 0, 0));

            // CỘT TRÁI: stat cards + pie chart + nhận xét
            VBox leftCol = buildLeftColumn();
            leftCol.setPrefWidth(260);
            leftCol.setMinWidth(230);
            leftCol.setMaxWidth(300);

            // CỘT PHẢI: danh sách phòng có scroll
            roomContainer = new VBox(18);
            roomContainer.setPadding(new Insets(4));

            ScrollPane roomScroll = new ScrollPane(roomContainer);
            roomScroll.setFitToWidth(true);
            roomScroll.setFitToHeight(false);
            roomScroll.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-control-inner-background: transparent;" +
                            "-fx-border-color: transparent;"
            );
            roomScroll.setBorder(Border.EMPTY);
            HBox.setHgrow(roomScroll, Priority.ALWAYS);

            twoCol.getChildren().addAll(leftCol, roomScroll);
            dashRoot.setCenter(twoCol);

            contentArea.getChildren().add(dashRoot);

            // Gắn reference để updateDashboardSummary tìm được
            dashRoot.setUserData(leftCol);

            loadDashboardDataAsync(dashRoot);

        } catch (Exception ex) {
            showErrorBox("LỖI HIỂN THỊ TRANG CHỦ", ex);
        }
    }

    // Title bar phía trên
    private HBox createDashboardTitleBar() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);

        VBox titles = new VBox(3);
        Label title = new Label("Tổng quan hệ thống");
        title.setFont(Font.font("Segoe UI Semibold", FontWeight.EXTRA_BOLD, 22));
        title.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label sub = new Label("Dashboard tình trạng phòng theo thời gian thực.");
        sub.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        sub.setTextFill(Color.web(COLOR_TEXT_MUTED));

        titles.getChildren().addAll(title, sub);
        bar.getChildren().add(titles);
        return bar;
    }

    // Filter bar gọn
    private HBox createFilterBar() {
        HBox card = new HBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10, 14, 10, 14));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 1;"
        );

        Label icon = new Label("🔍");
        icon.setFont(Font.font(13));

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Mã hoặc tên phòng...");
        txtSearch.setPrefWidth(160);
        txtSearch.setStyle("-fx-font-size: 13px; -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #cbd5e1; -fx-background-color: #f8fafc;");

        ComboBox<String> cbLoai = new ComboBox<>();
        cbLoai.getItems().addAll("Tất cả loại", "DON", "DOI", "GIADINH", "VIP");
        cbLoai.setValue("Tất cả loại");
        cbLoai.setStyle("-fx-font-size: 13px;");

        ComboBox<String> cbTrangThai = new ComboBox<>();
        cbTrangThai.getItems().addAll("Tất cả tình trạng", "Trống", "Đã Đặt", "Đang ở", "Bảo Trì");
        cbTrangThai.setValue("Tất cả tình trạng");
        cbTrangThai.setStyle("-fx-font-size: 13px;");

        Label lblIn = new Label("Nhận:");
        lblIn.setFont(Font.font("Segoe UI", 13));
        lblIn.setTextFill(Color.web(COLOR_TEXT_MUTED));
        DatePicker dpIn = new DatePicker(LocalDate.now());
        dpIn.setEditable(false); dpIn.setStyle("-fx-font-size: 13px;"); dpIn.setPrefWidth(130);

        Label lblOut = new Label("Trả:");
        lblOut.setFont(Font.font("Segoe UI", 13));
        lblOut.setTextFill(Color.web(COLOR_TEXT_MUTED));
        DatePicker dpOut = new DatePicker(LocalDate.now().plusDays(1));
        dpOut.setEditable(false); dpOut.setStyle("-fx-font-size: 13px;"); dpOut.setPrefWidth(130);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnSearch = new Button("Tìm kiếm");
        btnSearch.setStyle(
                "-fx-background-color: " + COLOR_PRIMARY + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 7 18;" +
                        "-fx-background-radius: 7;" +
                        "-fx-cursor: hand;"
        );
        btnSearch.setOnMouseEntered(e -> btnSearch.setStyle(
                "-fx-background-color: " + COLOR_PRIMARY_HOVER + ";" +
                        "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;" +
                        "-fx-padding: 7 18; -fx-background-radius: 7; -fx-cursor: hand;"
        ));
        btnSearch.setOnMouseExited(e -> btnSearch.setStyle(
                "-fx-background-color: " + COLOR_PRIMARY + ";" +
                        "-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;" +
                        "-fx-padding: 7 18; -fx-background-radius: 7; -fx-cursor: hand;"
        ));

        btnSearch.setOnAction(e -> {
            LocalDate in = dpIn.getValue();
            LocalDate out = dpOut.getValue();
            List<PhongDTO> source;
            if (in != null && out != null && out.isAfter(in)) {
                source = phongService.findAvailableRooms(in, out, 0, Double.MAX_VALUE);
            } else {
                source = new ArrayList<>(dashboardRoomsCache);
            }
            String kw = txtSearch.getText() != null ? txtSearch.getText().trim() : "";
            String loai = cbLoai.getValue();
            String tt = cbTrangThai.getValue();
            List<PhongDTO> filtered = source.stream()
                    .filter(p -> kw.isEmpty()
                            || removeAccents((p.getMaPhong() != null ? p.getMaPhong() : "").toLowerCase()).contains(removeAccents(kw.toLowerCase()))
                            || removeAccents((p.getTenPhong() != null ? p.getTenPhong() : "").toLowerCase()).contains(removeAccents(kw.toLowerCase())))
                    .filter(p -> loai.startsWith("Tất cả") || loai.equalsIgnoreCase(p.getMaLoaiPhong()))
                    .filter(p -> tt.startsWith("Tất cả") || tt.equalsIgnoreCase(p.getTinhTrang()))
                    .collect(Collectors.toList());
            renderRoomsByFloor(filtered, in, out);
        });

        card.getChildren().addAll(icon, txtSearch, cbLoai, cbTrangThai, lblIn, dpIn, lblOut, dpOut, spacer, btnSearch);
        return card;
    }

    // Cột trái: stat cards + pie chart
    private VBox buildLeftColumn() {
        VBox col = new VBox(12);
        col.setFillWidth(true);

        // 4 stat cards - 2x2 grid
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        VBox c1 = makeStatCard("Tổng phòng",   "0", "#3b82f6", "#1d4ed8");
        VBox c2 = makeStatCard("Phòng trống",  "0", "#10b981", "#047857");
        VBox c3 = makeStatCard("Đang phục vụ", "0", "#f59e0b", "#b45309");
        VBox c4 = makeStatCard("Bảo trì",      "0", "#64748b", "#334155");

        GridPane.setHgrow(c1, Priority.ALWAYS); GridPane.setHgrow(c2, Priority.ALWAYS);
        GridPane.setHgrow(c3, Priority.ALWAYS); GridPane.setHgrow(c4, Priority.ALWAYS);
        grid.add(c1, 0, 0); grid.add(c2, 1, 0);
        grid.add(c3, 0, 1); grid.add(c4, 1, 1);

        // Pie chart card
        VBox chartCard = new VBox(10);
        chartCard.setPadding(new Insets(14));
        chartCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 1;"
        );

        Label chartLbl = new Label("Biểu đồ tình trạng phòng");
        chartLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        chartLbl.setTextFill(Color.web(COLOR_TEXT_MAIN));

        PieChart chart = new PieChart();
        chart.setLegendSide(javafx.geometry.Side.BOTTOM);
        chart.setLabelsVisible(false);
        chart.setPrefSize(240, 220);
        chart.setMinSize(200, 180);
        chart.setStyle("-fx-font-size: 11px;");
        chart.getData().addAll(
                new PieChart.Data("Trống", 1),
                new PieChart.Data("Phục vụ", 1),
                new PieChart.Data("Bảo trì", 1)
        );

        // Nhận xét
        Label insight = new Label("Đang tải dữ liệu...");
        insight.setWrapText(true);
        insight.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        insight.setTextFill(Color.web(COLOR_TEXT_MUTED));
        insight.setPadding(new Insets(8, 10, 8, 10));
        insight.setStyle(
                "-fx-background-color: #f8fafc;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 1;"
        );

        chartCard.getChildren().addAll(chartLbl, chart, insight);

        col.getChildren().addAll(grid, chartCard);

        // Lưu reference để update sau
        col.setUserData(new Object[]{c1, c2, c3, c4, chart, insight});
        return col;
    }

    private VBox makeStatCard(String label, String value, String colorStart, String colorEnd) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(12, 14, 12, 14));
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, " + colorStart + ", " + colorEnd + ");" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;"
        );

        Label lbl = new Label(label);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        lbl.setTextFill(Color.web("#ffffff", 0.85));

        Label val = new Label(value);
        val.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 28));
        val.setTextFill(Color.WHITE);

        card.getChildren().addAll(lbl, val);
        return card;
    }

    // Async load data
    private void loadDashboardDataAsync(BorderPane dashRoot) {
        Task<List<PhongDTO>> task = new Task<>() {
            @Override protected List<PhongDTO> call() { return phongService.getAllPhong(); }
        };
        task.setOnSucceeded(evt -> {
            List<PhongDTO> rooms = task.getValue();
            dashboardRoomsCache = new ArrayList<>(rooms);
            renderRoomsByFloor(rooms, null, null);
            updateDashboardSummary(dashRoot, rooms);
        });
        task.setOnFailed(evt -> {
            Throwable ex = task.getException();
            showErrorBox("LỖI TẢI DỮ LIỆU", ex instanceof Exception ? (Exception) ex : new Exception(ex));
        });
        Thread t = new Thread(task, "dashboard-load");
        t.setDaemon(true);
        t.start();
    }

    private void updateDashboardSummary(BorderPane dashRoot, List<PhongDTO> rooms) {
        VBox leftCol = (VBox) dashRoot.getUserData();
        if (leftCol == null) return;
        Object[] refs = (Object[]) leftCol.getUserData();
        if (refs == null || refs.length < 6) return;

        VBox c1 = (VBox) refs[0], c2 = (VBox) refs[1];
        VBox c3 = (VBox) refs[2], c4 = (VBox) refs[3];
        PieChart chart = (PieChart) refs[4];
        Label insight = (Label) refs[5];

        long total      = rooms.size();
        long available  = rooms.stream().filter(r -> "Trống".equalsIgnoreCase(r.getTinhTrang())).count();
        long occupied   = rooms.stream().filter(r -> "Đang ở".equalsIgnoreCase(r.getTinhTrang()) || "Đã Đặt".equalsIgnoreCase(r.getTinhTrang())).count();
        long maintenance = total - available - occupied;

        ((Label) c1.getChildren().get(1)).setText(String.valueOf(total));
        ((Label) c2.getChildren().get(1)).setText(String.valueOf(available));
        ((Label) c3.getChildren().get(1)).setText(String.valueOf(occupied));
        ((Label) c4.getChildren().get(1)).setText(String.valueOf(maintenance));

        chart.getData().setAll(
                new PieChart.Data("Trống (" + available + ")", available),
                new PieChart.Data("Phục vụ (" + occupied + ")", occupied),
                new PieChart.Data("Bảo trì (" + maintenance + ")", maintenance)
        );

        String msg;
        if (available >= occupied && available >= maintenance)
            msg = "Phòng trống đang nhiều nhất — nhiều cơ hội nhận booking mới.";
        else if (occupied >= maintenance)
            msg = "Số phòng đang phục vụ cao — theo dõi sát lịch nhận/trả phòng.";
        else
            msg = "Số phòng bảo trì tăng — nên kiểm tra lại kế hoạch vận hành.";
        insight.setText(msg);
    }

    private VBox createStatCard(String labelText, String valueText, String startColor, String endColor) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(14));
        card.setPrefWidth(190);
        card.setStyle("-fx-background-color: linear-gradient(to right bottom, " + startColor + ", " + endColor + "); -fx-background-radius: 12; -fx-border-radius: 12;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.06));
        shadow.setRadius(8);
        shadow.setOffsetY(3);
        card.setEffect(shadow);

        Label lbl = new Label(labelText);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.web("#ffffff", 0.92));

        Label value = new Label(valueText);
        value.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 32));
        value.setTextFill(Color.WHITE);

        card.getChildren().addAll(lbl, value);
        return card;
    }

    private HBox createFilterBox(VBox dashboard) {
        VBox filterCard = new VBox(12);
        filterCard.setAlignment(Pos.CENTER_LEFT);
        filterCard.setPadding(new Insets(16, 20, 16, 20));
        filterCard.setMaxWidth(Double.MAX_VALUE);
        filterCard.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #e2e8f0;");

        DropShadow shadow = new DropShadow(); shadow.setColor(Color.color(0, 0, 0, 0.03)); shadow.setRadius(5); shadow.setOffsetY(2);
        filterCard.setEffect(shadow);

        Label filterTitle = new Label("Lọc & tìm kiếm phòng");
        filterTitle.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 16));
        filterTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblSearch = new Label("Mã/Tên phòng:"); lblSearch.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Nhập mã hoặc tên phòng...");
        txtSearch.setPrefWidth(180);
        txtSearch.setStyle("-fx-font-size: 13px; -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #cbd5e1; -fx-background-color: #f8fafc;");

        Label lblIn = new Label("Nhận phòng:"); lblIn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        DatePicker dpCheckIn = new DatePicker(LocalDate.now()); dpCheckIn.setStyle("-fx-font-size: 13px;");
        dpCheckIn.setEditable(false);

        Label lblOut = new Label("Trả phòng:"); lblOut.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        DatePicker dpCheckOut = new DatePicker(LocalDate.now().plusDays(1)); dpCheckOut.setStyle("-fx-font-size: 13px;");
        dpCheckOut.setEditable(false);

        Label lblGia = new Label("Mức giá:"); lblGia.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        Slider priceSlider = new Slider(0, 10_000_000, 10_000_000); priceSlider.setPrefWidth(120);
        Label lblPrice = new Label("≤ 10Tr VNĐ"); lblPrice.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");

        Label lblLoai = new Label("Loại phòng:"); lblLoai.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        ComboBox<String> cbLoai = new ComboBox<>();
        cbLoai.getItems().addAll("Tất cả", "DON", "DOI", "GIADINH", "VIP");
        cbLoai.setValue("Tất cả");
        cbLoai.setStyle("-fx-font-size: 13px;");

        Label lblTrangThai = new Label("Tình trạng:"); lblTrangThai.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        ComboBox<String> cbTrangThai = new ComboBox<>();
        cbTrangThai.getItems().addAll("Tất cả", "Trống", "Đã Đặt", "Đang ở", "Bảo Trì");
        cbTrangThai.setValue("Tất cả");
        cbTrangThai.setStyle("-fx-font-size: 13px;");

        priceSlider.valueProperty().addListener((obs, old, newVal) -> {
            double val = newVal.doubleValue();
            if(val >= 1000000) lblPrice.setText(String.format("≤ %.1fTr", val/1000000));
            else lblPrice.setText(String.format("≤ %,.0fđ", val));
        });

        Button btnSearch = new Button("🔍 Tìm Kiếm");
        btnSearch.setStyle("-fx-background-color: linear-gradient(to right, " + COLOR_ACCENT + ", " + COLOR_PRIMARY + "); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 9 20; -fx-background-radius: 8; -fx-cursor: hand;");

        btnSearch.setOnAction(e -> {
            LocalDate in = dpCheckIn.getValue();
            LocalDate out = dpCheckOut.getValue();
            double maxPrice = priceSlider.getValue();

            List<PhongDTO> source;
            if (in != null && out != null && out.isAfter(in)) {
                source = phongService.findAvailableRooms(in, out, 0, maxPrice);
            } else {
                source = dashboardRoomsCache.stream().filter(p -> p.getGiaPhong() <= maxPrice).collect(Collectors.toList());
            }

            String keyword = txtSearch.getText() != null ? txtSearch.getText().trim() : "";
            String loai = cbLoai.getValue();
            String tt = cbTrangThai.getValue();

            List<PhongDTO> filtered = source.stream()
                    .filter(p -> keyword.isEmpty()
                            || removeAccents((p.getMaPhong() != null ? p.getMaPhong() : "").toLowerCase()).contains(removeAccents(keyword.toLowerCase()))
                            || removeAccents((p.getTenPhong() != null ? p.getTenPhong() : "").toLowerCase()).contains(removeAccents(keyword.toLowerCase())))
                    .filter(p -> "Tất cả".equals(loai) || loai.equalsIgnoreCase(p.getMaLoaiPhong()))
                    .filter(p -> "Tất cả".equals(tt) || tt.equalsIgnoreCase(p.getTinhTrang()))
                    .collect(Collectors.toList());

            renderRoomsByFloor(filtered, in, out);
        });

        HBox row1 = new HBox(12, lblSearch, txtSearch, lblLoai, cbLoai, lblTrangThai, cbTrangThai);
        row1.setAlignment(Pos.CENTER_LEFT);

        HBox row2 = new HBox(12, lblIn, dpCheckIn, lblOut, dpCheckOut, lblGia, priceSlider, lblPrice, btnSearch);
        row2.setAlignment(Pos.CENTER_LEFT);

        filterCard.getChildren().addAll(filterTitle, row1, row2);
        HBox wrapper = new HBox(filterCard);
        wrapper.setAlignment(Pos.CENTER_LEFT);
        return wrapper;
    }

    private void renderRoomsByFloor(List<PhongDTO> rooms, LocalDate in, LocalDate out) {
        roomContainer.getChildren().clear();
        if (rooms.isEmpty()) {
            Label emptyLbl = new Label("Không tìm thấy phòng trống phù hợp với tiêu chí.");
            emptyLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b; -fx-padding: 20;");
            roomContainer.getChildren().add(emptyLbl);
            return;
        }

        Map<String, List<PhongDTO>> byFloor = new LinkedHashMap<>();
        for (PhongDTO p : rooms) {
            String ma = p.getMaPhong() != null ? p.getMaPhong() : "";
            String floorNum = ma.replaceAll("[^0-9]", "");
            String floor = "TẦNG " + (floorNum.length() > 0 ? floorNum.substring(0, 1) : "0");
            byFloor.computeIfAbsent(floor, k -> new ArrayList<>()).add(p);
        }

        List<String> sortedFloors = byFloor.keySet().stream()
                .sorted((f1, f2) -> {
                    try {
                        int n1 = Integer.parseInt(f1.replace("TẦNG ", "").trim());
                        int n2 = Integer.parseInt(f2.replace("TẦNG ", "").trim());
                        return Integer.compare(n1, n2);
                    } catch (Exception e) {
                        return f1.compareTo(f2);
                    }
                }).collect(Collectors.toList());

        for (String floor : sortedFloors) {
            VBox floorBox = new VBox(10);

            Label lblFloor = new Label(floor);
            lblFloor.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            lblFloor.setTextFill(Color.web("#334155"));

            HBox titleBox = new HBox(10); titleBox.setAlignment(Pos.CENTER_LEFT);
            Region line = new Region(); HBox.setHgrow(line, Priority.ALWAYS);
            line.setStyle("-fx-border-width: 1 0 0 0; -fx-border-color: #cbd5e1;");
            titleBox.getChildren().addAll(lblFloor, line);

            TilePane tilePane = new TilePane();
            tilePane.setHgap(15); tilePane.setVgap(15); tilePane.setPrefColumns(8);

            for (PhongDTO p : byFloor.get(floor)) {
                tilePane.getChildren().add(createRoomCard(p, in, out));
            }
            floorBox.getChildren().addAll(titleBox, tilePane);
            roomContainer.getChildren().add(floorBox);
        }
    }

    private VBox createRoomCard(PhongDTO p, LocalDate in, LocalDate out) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12)); card.setAlignment(Pos.CENTER); card.setPrefSize(140, 110);

        String trangThai = p.getTinhTrang() != null ? p.getTinhTrang() : "Không xác định";
        String statusColorHex; String statusIcon;
        switch (trangThai.trim()) {
            case "Trống" -> { statusColorHex = COLOR_AVAILABLE; statusIcon = "✓"; }
            case "Đã Đặt" -> { statusColorHex = COLOR_BOOKED; statusIcon = "📅"; }
            case "Đang ở" -> { statusColorHex = COLOR_OCCUPIED; statusIcon = "🔑"; }
            default -> { statusColorHex = COLOR_MAINTENANCE; statusIcon = "🔧"; }
        }

        DropShadow shadow = new DropShadow(); shadow.setColor(Color.color(0, 0, 0, 0.1)); shadow.setRadius(5); shadow.setOffsetY(3);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-width: 4 1 1 1; -fx-border-color: " + statusColorHex + " #e2e8f0 #e2e8f0 #e2e8f0; -fx-cursor: hand;");
        card.setEffect(shadow);

        card.setOnMouseEntered(e -> { card.setTranslateY(-3); shadow.setRadius(8); shadow.setColor(Color.color(0, 0, 0, 0.15)); });
        card.setOnMouseExited(e -> { card.setTranslateY(0); shadow.setRadius(5); shadow.setColor(Color.color(0, 0, 0, 0.1)); });

        HBox topRow = new HBox(); topRow.setAlignment(Pos.CENTER);
        Label lblMa = new Label(p.getMaPhong()); lblMa.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 18)); lblMa.setTextFill(Color.web("#1e293b"));
        Label lblStatus = new Label(" " + statusIcon); lblStatus.setTextFill(Color.web(statusColorHex)); lblStatus.setFont(Font.font(14));
        topRow.getChildren().addAll(lblMa, lblStatus);

        Label lblLoai = new Label(p.getMaLoaiPhong()); lblLoai.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12)); lblLoai.setTextFill(Color.web("#64748b"));
        Label lblGia = new Label(String.format("%,.0f đ", p.getGiaPhong())); lblGia.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14)); lblGia.setTextFill(Color.web(COLOR_PRIMARY));

        Label lblStatusText = new Label(trangThai);
        lblStatusText.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 11)); lblStatusText.setTextFill(Color.web(statusColorHex));
        lblStatusText.setStyle("-fx-background-color: " + statusColorHex + "20; -fx-padding: 2 6; -fx-background-radius: 10;");

        card.getChildren().addAll(topRow, lblLoai, lblGia, lblStatusText);

        card.setOnMouseClicked(e -> {
            if (trangThai.trim().equalsIgnoreCase("Trống")) {
                loadQuanLyDatPhong();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông báo"); alert.setHeaderText(null);
                alert.setContentText("Phòng " + p.getMaPhong() + " hiện đang " + trangThai + ".");
                alert.show();
            }
        });
        return card;
    }

    private void loadQuanLyKhachHang() {
        try {
            contentArea.getChildren().clear();
            QuanLyKhachHangController khController = new QuanLyKhachHangController(khachHangService);
            contentArea.getChildren().add(khController.createQuanLyKhachHangView());
        } catch (Throwable ex) {
            ex.printStackTrace();
            showErrorBox("LỖI KHI MỞ GIAO DIỆN KHÁCH HÀNG", new Exception(ex));
        }
    }

    private void loadQuanLyNhanVien() {
        try {
            contentArea.getChildren().clear();
            QuanLyNhanVienController nvController = new QuanLyNhanVienController(nhanVienService);
            contentArea.getChildren().add(nvController.createQuanLyNhanVienView());
        } catch (Throwable ex) {
            ex.printStackTrace();
            showErrorBox("LỖI KHI MỞ GIAO DIỆN NHÂN VIÊN", new Exception(ex));
        }
    }

    private void loadQuanLyDatPhong() {
        try {
            contentArea.getChildren().clear();
            QuanLyDatPhongController dpController = new QuanLyDatPhongController(
                    phongService, phieuDatPhongService, khachHangService, currentUser
            );
            BorderPane datPhongRoot = (BorderPane) dpController.createQuanLyDatPhongScene().getRoot();
            datPhongRoot.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            contentArea.getChildren().setAll(datPhongRoot);
        } catch (Exception ex) {
            showErrorBox("LỖI KHI MỞ GIAO DIỆN ĐẶT PHÒNG", ex);
        }
    }

    private void loadQuanLyPhong() {
        try {
            contentArea.getChildren().clear();
            QuanLyPhongController pController = new QuanLyPhongController(phongService);
            contentArea.getChildren().add(pController.createQuanLyPhongView());
        } catch (Throwable ex) {
            ex.printStackTrace();
            showErrorBox("LỖI KHI MỞ GIAO DIỆN PHÒNG", new Exception(ex));
        }
    }

    private void showErrorBox(String title, Exception ex) {
        ex.printStackTrace();

        VBox errorBox = new VBox(15);
        errorBox.setAlignment(Pos.CENTER);

        Label lblTitle = new Label("⚠️ " + title);
        lblTitle.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 20px; -fx-font-weight: bold;");

        TextArea txtError = new TextArea("Nguyên nhân:\n" + ex.getMessage() + "\n\nChi tiết:\n" + Arrays.toString(ex.getStackTrace()));
        txtError.setWrapText(true);
        txtError.setEditable(false);
        txtError.setPrefSize(800, 400);
        txtError.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 13px;");

        errorBox.getChildren().addAll(lblTitle, txtError);
        contentArea.getChildren().add(errorBox);
    }

    private void showPlaceholder(String message) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(createModuleComingSoonPage("🚧", "Chức năng đang phát triển", message,
                "Màn hình này sẽ được hoàn thiện theo đúng quy trình nghiệp vụ."));
    }

    private void showQuanLyPhieuDatScreen() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(createModuleComingSoonPage(
                "📋", "Quản lý Phiếu Đặt", "Xem danh sách, lọc, duyệt và in phiếu đặt phòng",
                "Khu vực này đã được dựng giao diện để bạn dễ mở rộng nghiệp vụ sau này."));
    }

    private void showGoiDichVuScreen() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(createModuleComingSoonPage(
                "🍽️", "Gọi Dịch Vụ", "Quản lý món ăn, đồ uống và các dịch vụ đi kèm phòng",
                "Bạn có thể bổ sung bảng dịch vụ, danh mục món và phần gọi món ngay tại đây."));
    }

    private VBox createModuleComingSoonPage(String iconText, String title, String subtitle, String detail) {
        VBox wrapper = new VBox(18);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(30));
        wrapper.setStyle("-fx-background-color: transparent;");

        VBox card = new VBox(14);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(820);
        card.setPadding(new Insets(32));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 18; -fx-border-radius: 18; -fx-border-color: #e2e8f0;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.08));
        shadow.setRadius(18);
        shadow.setOffsetY(8);
        card.setEffect(shadow);

        Label icon = new Label(iconText);
        icon.setFont(Font.font(44));

        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("Segoe UI Semibold", FontWeight.EXTRA_BOLD, 28));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblSub = new Label(subtitle);
        lblSub.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 15));
        lblSub.setTextFill(Color.web(COLOR_ACCENT));

        Label lblDetail = new Label(detail);
        lblDetail.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        lblDetail.setTextFill(Color.web(COLOR_TEXT_MUTED));
        lblDetail.setWrapText(true);
        lblDetail.setMaxWidth(650);
        lblDetail.setAlignment(Pos.CENTER);

        HBox featureRow = new HBox(12);
        featureRow.setAlignment(Pos.CENTER);
        featureRow.getChildren().addAll(
                createFeatureChip("Giao diện rõ ràng", COLOR_PRIMARY),
                createFeatureChip("Dễ mở rộng", COLOR_ACCENT),
                createFeatureChip("Đồng bộ màu logo", "#0ea5a4")
        );

        Button btnBackHome = new Button("🏠 Về trang chủ");
        btnBackHome.setCursor(Cursor.HAND);
        btnBackHome.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8;");
        btnBackHome.setOnMouseEntered(e -> btnBackHome.setStyle("-fx-background-color: " + COLOR_PRIMARY_HOVER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8;"));
        btnBackHome.setOnMouseExited(e -> btnBackHome.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8;"));
        btnBackHome.setOnAction(e -> showDashboard());

        card.getChildren().addAll(icon, lblTitle, lblSub, lblDetail, featureRow, btnBackHome);
        wrapper.getChildren().add(card);
        return wrapper;
    }

    private Label createFeatureChip(String text, String colorHex) {
        Label chip = new Label(text);
        chip.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        chip.setTextFill(Color.web(colorHex));
        chip.setStyle("-fx-background-color: " + colorHex + "18; -fx-background-radius: 999; -fx-padding: 7 14; -fx-border-radius: 999; -fx-border-color: " + colorHex + "33;");
        return chip;
    }

    private String removeAccents(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalized)
                .replaceAll("")
                .replace('đ', 'd')
                .replace('Đ', 'D');
    }

    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận đăng xuất");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc chắn muốn đăng xuất khỏi hệ thống không?");

        ButtonType yesBtn = new ButtonType("Đăng xuất", ButtonBar.ButtonData.OK_DONE);
        ButtonType noBtn = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(yesBtn, noBtn);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == yesBtn) {
            primaryStage.close();
            System.out.println("Đã đăng xuất hệ thống!");
        }
    }
}