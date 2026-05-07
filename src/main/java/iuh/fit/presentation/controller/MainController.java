package iuh.fit.presentation.controller;

import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.dto.HoaDonDTO;
import iuh.fit.core.dto.KhachHangDTO;
import iuh.fit.core.service.IKhachHangService;
import iuh.fit.core.service.INhanVienService;
import iuh.fit.core.service.IPhieuDatPhongService;
import iuh.fit.core.service.IPhongService;
import iuh.fit.core.service.IDichVuService;
import iuh.fit.core.service.IHoaDonService;
import iuh.fit.core.service.IChiTietHoaDonService;
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
import javafx.stage.Window;
import javafx.stage.Modality;
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
    private IDichVuService dichVuService;
    private IHoaDonService hoaDonService;
    private IChiTietHoaDonService chiTietHoaDonService;

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
      private final String COLOR_DANGER = "#ef4444";
      private final String COLOR_OCCUPIED = "#f59e0b";
      private final String COLOR_MAINTENANCE = "#64748b";
      private final String COLOR_SUCCESS = "#10b981";

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

        Label lblRole = new Label(currentUser.getTenDangNhap().equals("admin") ? "Quản Lý" : "Lễ Tân");
        lblRole.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-padding: 3 10; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");

        profileBox.getChildren().addAll(avatarPane, lblUser, lblRole);

        // --- NHÓM NGHIỆP VỤ ---
        Label lblMenuSection = new Label("NGHIỆP VỤ");
        lblMenuSection.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 10 0 5 25;");

        Button btnTrangChu = createMenuButton("🏠 Trang Chủ", true);
        Button btnDatPhong = createMenuButton("🏨 Đặt & Nhận Phòng", false);
        Button btnGoiDichVu = createMenuButton("🍽️ Gọi Dịch Vụ", false);
        Button btnTraPhong = createMenuButton("💳 Trả Phòng & Thanh Toán", false);
        Button btnThongKe = createMenuButton("💰 Thống Kê Doanh Thu", false);
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

        List<Button> menuButtons = Arrays.asList(btnTrangChu, btnDatPhong, btnGoiDichVu, btnTraPhong, btnThongKe, btnQuanLyPhieu, btnPhong, btnKhachHang, btnNhanVien);

        btnTrangChu.setOnAction(e -> { setActiveMenu(btnTrangChu, menuButtons); showDashboard(); });
        btnDatPhong.setOnAction(e -> { setActiveMenu(btnDatPhong, menuButtons); loadQuanLyDatPhong(); });
        btnPhong.setOnAction(e -> { setActiveMenu(btnPhong, menuButtons); loadQuanLyPhong(); });
        btnKhachHang.setOnAction(e -> { setActiveMenu(btnKhachHang, menuButtons); loadQuanLyKhachHang(); });
        btnNhanVien.setOnAction(e -> { setActiveMenu(btnNhanVien, menuButtons); loadQuanLyNhanVien(); });
        btnGoiDichVu.setOnAction(e -> { setActiveMenu(btnGoiDichVu, menuButtons); loadGoiDichVu(); });
        btnTraPhong.setOnAction(e -> { setActiveMenu(btnTraPhong, menuButtons); loadTraPhong(); });
        btnThongKe.setOnAction(e -> { setActiveMenu(btnThongKe, menuButtons); loadThongKeDoanHThu(); });
        btnQuanLyPhieu.setOnAction(e -> { setActiveMenu(btnQuanLyPhieu, menuButtons); showQuanLyPhieuDatScreen(); });
        btnDangXuat.setOnAction(e -> handleLogout());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // ĐÃ CHUYỂN VỊ TRÍ CỦA btnPhong TRONG DANH SÁCH HIỂN THỊ
        sidebar.getChildren().addAll(
                profileBox,
                lblMenuSection, btnTrangChu, btnDatPhong, btnGoiDichVu, btnTraPhong, btnThongKe, btnQuanLyPhieu, btnPhong,
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

    // =========================================================================
    // STATE-DRIVEN UI: HIỂN THỊ CHI TIẾT PHÒNG THEO TRẠNG THÁI
    // =========================================================================
     private void showRoomDetailModal(PhongDTO phong, LocalDate checkInDate, LocalDate checkOutDate) {
         try {
             String trangThai = phong.getTinhTrang() != null ? phong.getTinhTrang().trim() : "Không xác định";

             Stage stage = new Stage();
             stage.setTitle("Chi Tiết Phòng - " + phong.getMaPhong());
             stage.setResizable(true);
             stage.setWidth(650);
             stage.setHeight(600);

             VBox mainContent = new VBox(0);
             mainContent.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + ";");

             // Header
             HBox header = createModalHeader(phong, trangThai);
             mainContent.getChildren().add(header);

             // Content dựa trên trạng thái
             VBox content = new VBox(0);
             VBox.setVgrow(content, Priority.ALWAYS);
             content.setPadding(new Insets(20));
             content.setStyle("-fx-background-color: white;");

             switch (trangThai) {
                 case "Trống":
                     content.getChildren().add(createEmptyRoomContent(phong));
                     break;
                 case "Đang ở":
                     content.getChildren().add(createOccupiedRoomContent(phong));
                     break;
                 case "Đã Đặt":
                     content.getChildren().add(createBookedRoomContent(phong));
                     break;
                 case "Bảo Trì":
                     content.getChildren().add(createMaintenanceRoomContent(phong));
                     break;
                 default:
                     Label defaultLabel = new Label("Không xác định trạng thái phòng");
                     defaultLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
                     content.getChildren().add(defaultLabel);
             }

             mainContent.getChildren().add(content);

             ScrollPane scrollPane = new ScrollPane(mainContent);
             scrollPane.setFitToWidth(true);
             scrollPane.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent;");

             Scene scene = new Scene(scrollPane);
             stage.setScene(scene);
             stage.show();

         } catch (Exception ex) {
             ex.printStackTrace();
             showErrorBox("LỖI KHI HIỂN THỊ CHI TIẾT PHÒNG", ex);
         }
     }

     // Header của modal
     private HBox createModalHeader(PhongDTO phong, String trangThai) {
         HBox header = new HBox(15);
         header.setStyle("-fx-background-color: white; -fx-border-width: 0 0 1 0; -fx-border-color: #e2e8f0; -fx-padding: 16 20;");
         header.setAlignment(Pos.CENTER_LEFT);

         String statusColorHex;
         String statusText;
         switch (trangThai) {
             case "Trống" -> { statusColorHex = COLOR_AVAILABLE; statusText = "✓ Có sẵn"; }
             case "Đã Đặt" -> { statusColorHex = COLOR_BOOKED; statusText = "📅 Đã đặt"; }
             case "Đang ở" -> { statusColorHex = COLOR_OCCUPIED; statusText = "🔑 Đang phục vụ"; }
             case "Bảo Trì" -> { statusColorHex = COLOR_MAINTENANCE; statusText = "🔧 Bảo trì"; }
             default -> { statusColorHex = COLOR_TEXT_MUTED; statusText = "❓ Không xác định"; }
         }

         Label lblMa = new Label(phong.getMaPhong());
         lblMa.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 20));
         lblMa.setTextFill(Color.web(COLOR_TEXT_MAIN));

         Label lblStatus = new Label(statusText);
         lblStatus.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
         lblStatus.setTextFill(Color.web(statusColorHex));
         lblStatus.setStyle("-fx-background-color: " + statusColorHex + "20; -fx-padding: 5 12; -fx-background-radius: 15;");

         Label lblLoai = new Label(phong.getMaLoaiPhong());
         lblLoai.setFont(Font.font("Segoe UI", 12));
         lblLoai.setTextFill(Color.web(COLOR_TEXT_MUTED));

         Label lblGia = new Label(String.format("%,.0f đ", phong.getGiaPhong()));
         lblGia.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
         lblGia.setTextFill(Color.web(COLOR_PRIMARY));

         Region spacer = new Region();
         HBox.setHgrow(spacer, Priority.ALWAYS);

         header.getChildren().addAll(lblMa, lblStatus, lblLoai, lblGia);
         return header;
     }

     // ===== NỘI DUNG PHÒNG TRỐNG =====
     private VBox createEmptyRoomContent(PhongDTO phong) {
         VBox content = new VBox(18);

         Label title = new Label("Đặt Phòng Mới");
         title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
         title.setTextFill(Color.web(COLOR_TEXT_MAIN));

         Label subtitle = new Label("Phòng này có sẵn. Nhấn nút bên dưới để bắt đầu đặt phòng.");
         subtitle.setFont(Font.font("Segoe UI", 12));
         subtitle.setTextFill(Color.web(COLOR_TEXT_MUTED));
         subtitle.setWrapText(true);

         // Thông tin phòng
         VBox infoBox = createInfoBox(
             new String[]{"Loại phòng", "Giá phòng", "Trạng thái"},
             new String[]{phong.getMaLoaiPhong(), String.format("%,.0f đ", phong.getGiaPhong()), "Có sẵn"}
         );

         Region spacer = new Region();
         VBox.setVgrow(spacer, Priority.ALWAYS);

         Button btnBook = new Button("🏨 Bắt Đầu Đặt Phòng");
         btnBook.setPrefWidth(Double.MAX_VALUE);
         btnBook.setPrefHeight(45);
         btnBook.setStyle("-fx-background-color: " + COLOR_AVAILABLE + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
         btnBook.setOnMouseEntered(e -> btnBook.setStyle("-fx-background-color: " + COLOR_AVAILABLE + "DD; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
         btnBook.setOnMouseExited(e -> btnBook.setStyle("-fx-background-color: " + COLOR_AVAILABLE + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
         btnBook.setOnAction(e -> {
             // 1. Đóng cái bảng chi tiết phòng hiện tại
             Window window = btnBook.getScene().getWindow();
             if (window instanceof Stage) {
                 ((Stage) window).close();
             }
             // 2. Chuyển sang màn hình Đặt phòng Full và tự động thêm phòng này vào Giỏ Hàng
             openFullBookingView(phong);
         });

         content.getChildren().addAll(title, subtitle, infoBox, spacer, btnBook);
         return content;
     }

    private void openFullBookingView(PhongDTO phong) {
        try {
            contentArea.getChildren().clear();

            // 1. Khởi tạo ĐÚNG 6 THAM SỐ giống như trên
            QuanLyDatPhongController dpController = new QuanLyDatPhongController(
                    phongService, phieuDatPhongService, khachHangService, dichVuService, currentUser, this::showDashboard
            );

            // 2. Gọi hàm createQuanLyDatPhongView()
            Pane bookingView = dpController.createQuanLyDatPhongView();

            // 3. Tự động đưa phòng vừa click vào Giỏ Hàng
            dpController.preselectRoom(phong);

            // 4. Hiển thị full lên vùng làm việc chính
            contentArea.getChildren().add(bookingView);

        } catch (Exception ex) {
            showErrorBox("Lỗi mở giao diện Đặt Phòng", ex);
        }
    }

    // ===== NỘI DUNG PHÒNG ĐANG PHỤC VỤ =====
     private VBox createOccupiedRoomContent(PhongDTO phong) {
         VBox content = new VBox(18);

         Label title = new Label("Chi Tiết Khách Đang Ở");
         title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
         title.setTextFill(Color.web(COLOR_TEXT_MAIN));

         // Lấy thông tin phiếu đặt và khách
         List<PhieuDatPhongDTO> phieu = phieuDatPhongService.getPhieuDatPhongByPhong(phong.getMaPhong());
         PhieuDatPhongDTO currentPhieu = null;
         KhachHangDTO khachHang = null;

         if (!phieu.isEmpty()) {
             for (PhieuDatPhongDTO p : phieu) {
                 if ("Nhận Phòng".equalsIgnoreCase(p.getTrangThai())) {
                     currentPhieu = p;
                     khachHang = khachHangService.getKhachHangById(p.getMaKhachHang());
                     break;
                 }
             }
         }

          if (currentPhieu != null && khachHang != null) {
              // Create final references for lambda
              final PhieuDatPhongDTO finalPhieu = currentPhieu;
              final KhachHangDTO finalKhachHang = khachHang;

              // Thông tin khách
              VBox khachBox = createInfoBox(
                  new String[]{"Khách", "Số điện thoại", "Nhận phòng", "Dự kiến trả"},
                  new String[]{
                      khachHang.getHoTen(),
                      khachHang.getSoDienThoai(),
                      currentPhieu.getNgayNhan().toString(),
                      currentPhieu.getNgayTra().toString()
                  }
              );

             // Dịch vụ đã sử dụng
             Label serviceTitle = new Label("Dịch Vụ Đã Sử Dụng");
             serviceTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
             serviceTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

             List<HoaDonDTO> hoaDons = hoaDonService.getHoaDonByPhieuDat(currentPhieu.getMaPhieu());
             VBox serviceBox = createServiceList(hoaDons);

             // Tính toán tổng tiền hiện tại
             double totalCurrent = currentPhieu.getTongTien();
             Label totalLabel = new Label(String.format("Tổng tiền hiện tại: %,.0f đ", totalCurrent));
             totalLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
             totalLabel.setTextFill(Color.web(COLOR_PRIMARY));
             totalLabel.setStyle("-fx-background-color: #f0f7ff; -fx-padding: 12 14; -fx-background-radius: 8;");

             // Các nút hành động
             HBox buttonBox = new HBox(12);
             buttonBox.setPrefWidth(Double.MAX_VALUE);

             Button btnAddService = createActionButton("➕ Thêm Dịch Vụ", COLOR_ACCENT);
             Button btnChangeRoom = createActionButton("🔄 Chuyển Phòng", COLOR_ACCENT);
             Button btnCheckOut = createActionButton("💳 Trả Phòng", COLOR_BOOKED);

               btnAddService.setOnAction(e -> showMessage("Chức năng thêm dịch vụ", "Tính năng này sẽ được cải thiện."));
               btnChangeRoom.setOnAction(e -> showMessage("Chuyển phòng", "Tính năng này sẽ được cải thiện."));
              btnCheckOut.setOnAction(e -> {
                    Window currentWindow = btnCheckOut.getScene().getWindow();
                    if (currentWindow instanceof Stage) {
                        showCheckoutDialog(finalPhieu, phong, finalKhachHang, (Stage) currentWindow);
                    }
                });

             buttonBox.getChildren().addAll(btnAddService, btnChangeRoom, btnCheckOut);

             Region spacer = new Region();
             VBox.setVgrow(spacer, Priority.ALWAYS);

             content.getChildren().addAll(title, khachBox, serviceTitle, serviceBox, totalLabel, spacer, buttonBox);
         } else {
             Label noData = new Label("Không tìm thấy thông tin khách ở.");
             noData.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
             content.getChildren().add(noData);
         }

         return content;
     }

     // ===== NỘI DUNG PHÒNG ĐÃ ĐẶT =====
     private VBox createBookedRoomContent(PhongDTO phong) {
         VBox content = new VBox(18);

         Label title = new Label("Thông Tin Đặt Chỗ");
         title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
         title.setTextFill(Color.web(COLOR_TEXT_MAIN));

         // Lấy thông tin phiếu đặt
         List<PhieuDatPhongDTO> phieu = phieuDatPhongService.getPhieuDatPhongByPhong(phong.getMaPhong());
         PhieuDatPhongDTO bookedPhieu = null;
         KhachHangDTO khachHang = null;

         if (!phieu.isEmpty()) {
             for (PhieuDatPhongDTO p : phieu) {
                 if ("Đặt Phòng".equalsIgnoreCase(p.getTrangThai())) {
                     bookedPhieu = p;
                     khachHang = khachHangService.getKhachHangById(p.getMaKhachHang());
                     break;
                 }
             }
         }

         if (bookedPhieu != null && khachHang != null) {
             VBox bookingBox = createInfoBox(
                 new String[]{"Mã phiếu", "Khách hàng", "Ngày đặt", "Ngày nhận", "Ngày trả", "Tổng giá"},
                 new String[]{
                     bookedPhieu.getMaPhieu(),
                     khachHang.getHoTen(),
                     bookedPhieu.getNgayDat().toString(),
                     bookedPhieu.getNgayNhan().toString(),
                     bookedPhieu.getNgayTra().toString(),
                     String.format("%,.0f đ", bookedPhieu.getTongTien())
                 }
             );

             Region spacer = new Region();
             VBox.setVgrow(spacer, Priority.ALWAYS);

             HBox buttonBox = new HBox(12);
             buttonBox.setPrefWidth(Double.MAX_VALUE);

             Button btnCheckIn = createActionButton("🔑 Nhận Phòng (Check-in)", COLOR_AVAILABLE);
             Button btnCancel = createActionButton("❌ Hủy Đặt Phòng", COLOR_DANGER);
             
             final PhieuDatPhongDTO finalBookedPhieu = bookedPhieu;
             
             btnCheckIn.setOnAction(e -> {
                 QuanLyPhieuDatTraPhongController controller = new QuanLyPhieuDatTraPhongController(
                         phieuDatPhongService, phongService, khachHangService,
                         hoaDonService, chiTietHoaDonService, dichVuService, currentUser
                 );
                 if (controller.processCheckIn(finalBookedPhieu.getMaPhieu())) {
                     Window window = btnCheckIn.getScene().getWindow();
                     if (window instanceof Stage) ((Stage) window).close();
                     showDashboard();
                 }
             });

             btnCancel.setOnAction(e -> {
                 QuanLyPhieuDatTraPhongController controller = new QuanLyPhieuDatTraPhongController(
                         phieuDatPhongService, phongService, khachHangService,
                         hoaDonService, chiTietHoaDonService, dichVuService, currentUser
                 );
                 if (controller.cancelBooking(finalBookedPhieu.getMaPhieu())) {
                     Window window = btnCancel.getScene().getWindow();
                     if (window instanceof Stage) ((Stage) window).close();
                     showDashboard();
                 }
             });

             buttonBox.getChildren().addAll(btnCheckIn, btnCancel);

             content.getChildren().addAll(title, bookingBox, spacer, buttonBox);
         } else {
             Label noData = new Label("Không tìm thấy thông tin đặt phòng.");
             noData.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
             content.getChildren().add(noData);
         }

         return content;
     }

     // ===== NỘI DUNG PHÒNG BẢO TRÌ =====
     private VBox createMaintenanceRoomContent(PhongDTO phong) {
         VBox content = new VBox(18);

         Label title = new Label("Thông Tin Bảo Trì");
         title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
         title.setTextFill(Color.web(COLOR_TEXT_MAIN));

         Label subtitle = new Label("Phòng đang trong quá trình bảo trì và không khả dụng cho khách.");
         subtitle.setFont(Font.font("Segoe UI", 12));
         subtitle.setTextFill(Color.web(COLOR_TEXT_MUTED));
         subtitle.setWrapText(true);

         VBox infoBox = createInfoBox(
             new String[]{"Phòng", "Loại phòng", "Trạng thái"},
             new String[]{phong.getMaPhong(), phong.getMaLoaiPhong(), "Bảo Trì"}
         );

         Label noteLabel = new Label("Ghi chú kỹ thuật:");
         noteLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
         noteLabel.setTextFill(Color.web(COLOR_TEXT_MAIN));

         TextArea noteArea = new TextArea();
         noteArea.setWrapText(true);
         noteArea.setEditable(false);
         noteArea.setPrefHeight(80);
         noteArea.setText("Chưa có ghi chú kỹ thuật.");
         noteArea.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b; -fx-control-inner-background: #f8fafc;");

         Region spacer = new Region();
         VBox.setVgrow(spacer, Priority.ALWAYS);

         Button btnComplete = new Button("✅ Hoàn Thành Bảo Trì");
         btnComplete.setPrefWidth(Double.MAX_VALUE);
         btnComplete.setPrefHeight(45);
         btnComplete.setStyle("-fx-background-color: " + COLOR_AVAILABLE + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
         btnComplete.setOnMouseEntered(e -> btnComplete.setStyle("-fx-background-color: " + COLOR_AVAILABLE + "DD; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
         btnComplete.setOnMouseExited(e -> btnComplete.setStyle("-fx-background-color: " + COLOR_AVAILABLE + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
         btnComplete.setOnAction(e -> {
             QuanLyPhieuDatTraPhongController controller = new QuanLyPhieuDatTraPhongController(
                     phieuDatPhongService, phongService, khachHangService,
                     hoaDonService, chiTietHoaDonService, dichVuService, currentUser
             );
             if (controller.completeMaintenance(phong.getMaPhong())) {
                 Window window = btnComplete.getScene().getWindow();
                 if (window instanceof Stage) ((Stage) window).close();
                 showDashboard();
             }
         });

         content.getChildren().addAll(title, subtitle, infoBox, noteLabel, noteArea, spacer, btnComplete);
         return content;
     }

     // ===== HỖ TRỢ =====
     private VBox createInfoBox(String[] labels, String[] values) {
         VBox box = new VBox(10);
         box.setPadding(new Insets(14));
         box.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e2e8f0; -fx-border-width: 1;");

         for (int i = 0; i < labels.length && i < values.length; i++) {
             HBox row = new HBox(10);
             row.setAlignment(Pos.CENTER_LEFT);

             Label label = new Label(labels[i] + ":");
             label.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 11));
             label.setTextFill(Color.web(COLOR_TEXT_MUTED));
             label.setPrefWidth(140);

             Label value = new Label(values[i]);
             value.setFont(Font.font("Segoe UI", 12));
             value.setTextFill(Color.web(COLOR_TEXT_MAIN));
             value.setWrapText(true);

             row.getChildren().addAll(label, value);
             box.getChildren().add(row);
         }

         return box;
     }

     private VBox createServiceList(List<HoaDonDTO> hoaDons) {
         VBox box = new VBox(8);
         box.setStyle("-fx-background-color: #f8fafc; -fx-padding: 10; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e2e8f0; -fx-border-width: 1;");

         if (hoaDons.isEmpty()) {
             Label empty = new Label("Chưa có dịch vụ nào được sử dụng.");
             empty.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");
             box.getChildren().add(empty);
         } else {
             for (HoaDonDTO hd : hoaDons) {
                 HBox row = new HBox(10);
                 row.setAlignment(Pos.CENTER_LEFT);
                 row.setPadding(new Insets(6));
                 row.setStyle("-fx-background-color: white; -fx-background-radius: 4;");

                 Label serviceName = new Label("• " + (hd.getTenPhong() != null ? hd.getTenPhong() : "Dịch vụ"));
                 serviceName.setFont(Font.font("Segoe UI", 11));
                 serviceName.setTextFill(Color.web(COLOR_TEXT_MAIN));

                 Region spacer = new Region();
                 HBox.setHgrow(spacer, Priority.ALWAYS);

                 Label price = new Label(String.format("%,.0f đ", hd.getTongTienDichVu()));
                 price.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
                 price.setTextFill(Color.web(COLOR_PRIMARY));

                 row.getChildren().addAll(serviceName, spacer, price);
                 box.getChildren().add(row);
             }
         }

         return box;
     }

     private Button createActionButton(String text, String colorHex) {
         Button btn = new Button(text);
         btn.setMaxWidth(Double.MAX_VALUE);
         btn.setPrefHeight(42);
         btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 7; -fx-cursor: hand;");
         btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + colorHex + "DD; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 7; -fx-cursor: hand;"));
         btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 7; -fx-cursor: hand;"));
         return btn;
     }

     private void showMessage(String title, String message) {
         Alert alert = new Alert(Alert.AlertType.INFORMATION);
         alert.setTitle(title);
         alert.setHeaderText(null);
         alert.setContentText(message);
          alert.show();
      }

      /**
       * Hiển thị dialog để  đặt phòng mới
       */
      private void showBookingDialog(PhongDTO phong, Stage parentStage) {
         try {
             Stage dialog = new Stage();
             dialog.setTitle("Đặt Phòng - " + phong.getMaPhong());
             dialog.setResizable(false);
             dialog.setWidth(500);
             dialog.setHeight(450);

             VBox mainContent = new VBox(15);
             mainContent.setPadding(new Insets(20));
             mainContent.setStyle("-fx-background-color: white;");

             Label title = new Label("Đặt Phòng Mới");
             title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
             title.setTextFill(Color.web(COLOR_TEXT_MAIN));

             // Chọn khách hàng
             Label lblKhach = new Label("Khách Hàng:");
             lblKhach.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
             ComboBox<String> cbKhachHang = new ComboBox<>();
             cbKhachHang.setMaxWidth(Double.MAX_VALUE);
             try {
                 List<KhachHangDTO> khachs = khachHangService.getAllKhachHang();
                 for (KhachHangDTO kh : khachs) {
                     cbKhachHang.getItems().add(kh.getMaKhachHang() + " - " + kh.getHoTen());
                 }
             } catch (Exception e) {
                 e.printStackTrace();
             }

             // Ngày nhận
             Label lblNhanPhong = new Label("Ngày Nhận Phòng:");
             lblNhanPhong.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
             DatePicker dpNhanPhong = new DatePicker(LocalDate.now());
             dpNhanPhong.setMaxWidth(Double.MAX_VALUE);

             // Ngày trả
             Label lblTraPhong = new Label("Ngày Trả Phòng:");
             lblTraPhong.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
             DatePicker dpTraPhong = new DatePicker(LocalDate.now().plusDays(1));
             dpTraPhong.setMaxWidth(Double.MAX_VALUE);

             Region spacer = new Region();
             VBox.setVgrow(spacer, Priority.ALWAYS);

             // Nút hành động
             HBox buttonBox = new HBox(12);
             buttonBox.setAlignment(Pos.CENTER_RIGHT);

             Button btnBook = new Button("✅ Đặt Phòng");
             btnBook.setStyle("-fx-background-color: " + COLOR_AVAILABLE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");
             btnBook.setOnMouseEntered(e -> btnBook.setStyle("-fx-background-color: " + COLOR_AVAILABLE + "DD; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;"));
             btnBook.setOnMouseExited(e -> btnBook.setStyle("-fx-background-color: " + COLOR_AVAILABLE + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;"));

             Button btnCancel = new Button("Hủy");
             btnCancel.setStyle("-fx-background-color: #cbd5e1; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");

             btnBook.setOnAction(e -> {
                 String selectedKhach = cbKhachHang.getValue();
                 if (selectedKhach == null || selectedKhach.isEmpty()) {
                     showMessage("Lỗi", "Vui lòng chọn khách hàng");
                     return;
                 }

                 String maKhach = selectedKhach.split(" - ")[0];
                 LocalDate ngayNhan = dpNhanPhong.getValue();
                 LocalDate ngayTra = dpTraPhong.getValue();

                 QuanLyPhieuDatTraPhongController controller = new QuanLyPhieuDatTraPhongController(
                         phieuDatPhongService, phongService, khachHangService,
                         hoaDonService, chiTietHoaDonService, dichVuService, currentUser
                 );

                 if (controller.processBooking(maKhach, phong.getMaPhong(), ngayNhan, ngayTra)) {
                     dialog.close();
                     showDashboard();
                 }
             });

             btnCancel.setOnAction(e -> dialog.close());

             buttonBox.getChildren().addAll(btnCancel, btnBook);

             mainContent.getChildren().addAll(title, lblKhach, cbKhachHang, lblNhanPhong, dpNhanPhong, lblTraPhong, dpTraPhong, spacer, buttonBox);

             Scene scene = new Scene(mainContent);
             dialog.setScene(scene);
             dialog.showAndWait();

         } catch (Exception ex) {
             ex.printStackTrace();
             showErrorBox("LỖI KHI MỞ DIALOG ĐẶT PHÒNG", ex);
         }
     }

     /**
       * Hiển thị dialog trả phòng & thanh toán cho phòng đang phục vụ
       */
      private void showCheckoutDialog(PhieuDatPhongDTO phieu, PhongDTO phong, KhachHangDTO khachHang, Stage parentStage) {
          try {
              Stage dialog = new Stage();
              dialog.setTitle("Trả Phòng & Thanh Toán - " + phong.getMaPhong());
              dialog.setResizable(false);
              dialog.setWidth(600);
              dialog.setHeight(700);
              dialog.initModality(Modality.APPLICATION_MODAL);
              dialog.initOwner(parentStage);

              ScrollPane scrollPane = new ScrollPane();
              VBox mainContent = new VBox(12);
              mainContent.setPadding(new Insets(20));
              mainContent.setStyle("-fx-background-color: white;");

              // ===== TIÊU ĐỀ =====
              Label title = new Label("💳 Trả Phòng & Thanh Toán");
              title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
              title.setTextFill(Color.web(COLOR_TEXT_MAIN));
              mainContent.getChildren().add(title);

              // ===== THÔNG TIN KHÁCH & PHÒNG =====
              VBox infoBox = new VBox(8);
              infoBox.setPadding(new Insets(12));
              infoBox.setStyle("-fx-background-color: #f0f7ff; -fx-background-radius: 8; -fx-border-color: #dbeafe; -fx-border-radius: 8;");

              Label lblKhach = new Label("Khách: " + khachHang.getHoTen() + " (" + khachHang.getSoDienThoai() + ")");
              lblKhach.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
              lblKhach.setTextFill(Color.web(COLOR_TEXT_MAIN));

              Label lblPhong = new Label("Phòng: " + phong.getTenPhong() + " | Giá: " + String.format("%,.0f đ/ngày", phong.getGiaPhong()));
              lblPhong.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
              lblPhong.setTextFill(Color.web(COLOR_TEXT_MAIN));

              Label lblCheckIn = new Label("Nhận phòng: " + phieu.getNgayNhan());
              lblCheckIn.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
              lblCheckIn.setTextFill(Color.web(COLOR_TEXT_MAIN));

              infoBox.getChildren().addAll(lblKhach, lblPhong, lblCheckIn);
              mainContent.getChildren().add(infoBox);

              // ===== NGÀY TRẢ PHÒNG =====
              Label lblCheckoutDateLabel = new Label("Ngày Trả Phòng:");
              lblCheckoutDateLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
              DatePicker dpCheckoutDate = new DatePicker(LocalDate.now());
              dpCheckoutDate.setMaxWidth(Double.MAX_VALUE);
              mainContent.getChildren().addAll(lblCheckoutDateLabel, dpCheckoutDate);

              // ===== DỊCH VỤ ĐÃ DÙNG =====
              Label lblServicesTitle = new Label("Dịch Vụ Đã Sử Dụng:");
              lblServicesTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
              mainContent.getChildren().add(lblServicesTitle);

              VBox servicesBox = new VBox(6);
              servicesBox.setStyle("-fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-padding: 8;");

              try {
                  List<HoaDonDTO> hoaDons = hoaDonService.getHoaDonByPhieuDat(phieu.getMaPhieu());
                  // FIX: removed unused totalServiceCost variable
                  if (hoaDons.isEmpty()) {
                      Label noService = new Label("Không có dịch vụ nào được sử dụng");
                      noService.setStyle("-fx-text-fill: #94a3b8;");
                      servicesBox.getChildren().add(noService);
                  } else {
                      for (HoaDonDTO hd : hoaDons) {
                          HBox serviceItem = new HBox(10);
                          serviceItem.setAlignment(Pos.CENTER_LEFT);

                          Label serviceName = new Label("• Dịch vụ");
                          serviceName.setFont(Font.font("Segoe UI", 11));
                          serviceName.setPrefWidth(300);

                          Label serviceCost = new Label(String.format("%,.0f đ", hd.getTongTienDichVu()));
                          serviceCost.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
                          serviceCost.setTextFill(Color.web(COLOR_PRIMARY));

                          HBox.setHgrow(serviceName, Priority.ALWAYS);
                          serviceItem.getChildren().addAll(serviceName, serviceCost);
                          servicesBox.getChildren().add(serviceItem);
                      }
                  }
              } catch (Exception e) {
                  Label errorLabel = new Label("Lỗi tải dịch vụ: " + e.getMessage());
                  errorLabel.setStyle("-fx-text-fill: #ef4444;");
                  servicesBox.getChildren().add(errorLabel);
              }

              ScrollPane servicesScroll = new ScrollPane(servicesBox);
              servicesScroll.setFitToWidth(true);
              servicesScroll.setPrefHeight(120);
              mainContent.getChildren().add(servicesScroll);

              // ===== TÍNH TOÁN CHI PHÍ =====
              Label lblCalculateTitle = new Label("Tính Toán Chi Phí:");
              lblCalculateTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
              mainContent.getChildren().add(lblCalculateTitle);

              GridPane calcGrid = new GridPane();
              calcGrid.setHgap(15);
              calcGrid.setVgap(10);
              calcGrid.setStyle("-fx-background-color: #f8fafc; -fx-padding: 10; -fx-background-radius: 6;");

              // Row 0: Room cost
              Label lblRoomCost = new Label("Tiền Phòng:");
              lblRoomCost.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 11));
              calcGrid.add(lblRoomCost, 0, 0);

              Label lblRoomCostValue = new Label("0 đ");
              lblRoomCostValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
              lblRoomCostValue.setTextFill(Color.web(COLOR_PRIMARY));
              calcGrid.add(lblRoomCostValue, 1, 0);

              // Row 1: Service cost
              Label lblServiceCost = new Label("Tiền Dịch Vụ:");
              lblServiceCost.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 11));
              calcGrid.add(lblServiceCost, 0, 1);

              Label lblServiceCostValue = new Label("0 đ");
              lblServiceCostValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
              lblServiceCostValue.setTextFill(Color.web(COLOR_PRIMARY));
              calcGrid.add(lblServiceCostValue, 1, 1);

              // Row 2: VAT
              Label lblVATLabel = new Label("VAT (%):");
              lblVATLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 11));
              calcGrid.add(lblVATLabel, 0, 2);

              Spinner<Integer> spinnerVAT = new Spinner<>(0, 100, 10, 1);
              spinnerVAT.setEditable(true);
              spinnerVAT.setPrefWidth(80);
              calcGrid.add(spinnerVAT, 1, 2);

              Label lblVATValue = new Label("0 đ");
              lblVATValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
              lblVATValue.setTextFill(Color.web(COLOR_PRIMARY));
              calcGrid.add(lblVATValue, 2, 2);

              // Row 3: Discount
              Label lblDiscountLabel = new Label("Chiết Khấu (%):");
              lblDiscountLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 11));
              calcGrid.add(lblDiscountLabel, 0, 3);

              Spinner<Integer> spinnerDiscount = new Spinner<>(0, 100, 0, 1);
              spinnerDiscount.setEditable(true);
              spinnerDiscount.setPrefWidth(80);
              calcGrid.add(spinnerDiscount, 1, 3);

              Label lblDiscountValue = new Label("0 đ");
              lblDiscountValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
              lblDiscountValue.setTextFill(Color.web(COLOR_DANGER));
              calcGrid.add(lblDiscountValue, 2, 3);

              // Row 4: Total
              Label lblTotalLabel = new Label("TỔNG CỘNG:");
              lblTotalLabel.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 13));
              lblTotalLabel.setTextFill(Color.web(COLOR_PRIMARY));
              calcGrid.add(lblTotalLabel, 0, 4);

              Label lblTotalValue = new Label("0 đ");
              lblTotalValue.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 16));
              lblTotalValue.setTextFill(Color.web(COLOR_DANGER));
              calcGrid.add(lblTotalValue, 1, 4);
              GridPane.setColumnSpan(lblTotalValue, 2);

              mainContent.getChildren().add(calcGrid);

              // ===== PHƯƠNG THỨC THANH TOÁN =====
              Label lblPaymentMethod = new Label("Phương Thức Thanh Toán:");
              lblPaymentMethod.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));

              ComboBox<String> cbPaymentMethod = new ComboBox<>();
              cbPaymentMethod.getItems().addAll("Tiền Mặt", "Thẻ Debit", "Thẻ Credit", "Chuyển Khoản", "Khác");
              cbPaymentMethod.setValue("Tiền Mặt");
              cbPaymentMethod.setMaxWidth(Double.MAX_VALUE);

              mainContent.getChildren().addAll(lblPaymentMethod, cbPaymentMethod);

              // ===== CẬP NHẬT TÍNH TOÁN KHI CHANGE INPUT =====
              Runnable updateCalculation = () -> {
                  LocalDate checkoutDate = dpCheckoutDate.getValue();
                  if (checkoutDate == null) checkoutDate = LocalDate.now();

                  long numDays = java.time.temporal.ChronoUnit.DAYS.between(phieu.getNgayNhan(), checkoutDate);
                  if (numDays <= 0) numDays = 1;

                  double roomCost = phong.getGiaPhong() * numDays;
                  lblRoomCostValue.setText(String.format("%,.0f đ", roomCost));

                  double serviceCost = 0;
                  try {
                      List<HoaDonDTO> hoaDons = hoaDonService.getHoaDonByPhieuDat(phieu.getMaPhieu());
                      for (HoaDonDTO hd : hoaDons) {
                          serviceCost += hd.getTongTienDichVu();
                      }
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
                  lblServiceCostValue.setText(String.format("%,.0f đ", serviceCost));

                  int vatPercent = spinnerVAT.getValue() != null ? spinnerVAT.getValue() : 0;
                  double vat = (roomCost + serviceCost) * (vatPercent / 100.0);
                  lblVATValue.setText(String.format("%,.0f đ", vat));

                  int discountPercent = spinnerDiscount.getValue() != null ? spinnerDiscount.getValue() : 0;
                  double discount = (roomCost + serviceCost) * (discountPercent / 100.0);
                  lblDiscountValue.setText(String.format("%,.0f đ", discount));

                  double total = roomCost + serviceCost + vat - discount;
                  lblTotalValue.setText(String.format("%,.0f đ", total));
              };

              // Add listeners để cập nhật khi thay đổi
              dpCheckoutDate.valueProperty().addListener((obs, oldVal, newVal) -> updateCalculation.run());
              spinnerVAT.valueProperty().addListener((obs, oldVal, newVal) -> updateCalculation.run());
              spinnerDiscount.valueProperty().addListener((obs, oldVal, newVal) -> updateCalculation.run());

              // Tính lần đầu
              updateCalculation.run();

              // ===== NÚT HÀNH ĐỘNG =====
              Region spacer = new Region();
              VBox.setVgrow(spacer, Priority.ALWAYS);

              HBox buttonBox = new HBox(12);
              buttonBox.setAlignment(Pos.CENTER_RIGHT);

              Button btnConfirm = new Button("✅ Xác Nhận Trả Phòng");
              btnConfirm.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 24; -fx-font-size: 13px; -fx-background-radius: 6;");
              btnConfirm.setCursor(Cursor.HAND);

              Button btnCancel = new Button("❌ Hủy");
              btnCancel.setStyle("-fx-background-color: #cbd5e1; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 24; -fx-font-size: 13px; -fx-background-radius: 6;");
              btnCancel.setCursor(Cursor.HAND);

              btnConfirm.setOnAction(e -> {
                  LocalDate checkoutDate = dpCheckoutDate.getValue();
                  if (checkoutDate == null) {
                      showMessage("Lỗi", "Vui lòng chọn ngày trả phòng");
                      return;
                  }

                  if (checkoutDate.isBefore(phieu.getNgayNhan())) {
                      showMessage("Lỗi", "Ngày trả phòng phải sau ngày nhận phòng");
                      return;
                  }

                  String paymentMethod = cbPaymentMethod.getValue();
                  if (paymentMethod == null || paymentMethod.isEmpty()) {
                      showMessage("Lỗi", "Vui lòng chọn phương thức thanh toán");
                      return;
                  }

                  // Xác nhận trước
                  Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                  confirm.setTitle("Xác Nhận Trả Phòng & Thanh Toán");
                  confirm.setHeaderText(null);
                  confirm.setContentText("Bạn có chắc chắn muốn hoàn tất trả phòng?\n\n" +
                          "Tổng tiền: " + lblTotalValue.getText() + "\n" +
                          "Phương thức: " + paymentMethod);

                  Optional<ButtonType> result = confirm.showAndWait();
                  if (result.isPresent() && result.get() == ButtonType.OK) {
                      // Tính toán các giá trị từ UI
                      int vatPercent = spinnerVAT.getValue() != null ? spinnerVAT.getValue() : 0;
                      int discountPercent = spinnerDiscount.getValue() != null ? spinnerDiscount.getValue() : 0;

                      // Gọi processCheckout từ service
                      QuanLyPhieuDatTraPhongController controller = new QuanLyPhieuDatTraPhongController(
                              phieuDatPhongService, phongService, khachHangService,
                              hoaDonService, chiTietHoaDonService, dichVuService, currentUser
                      );

                      if (controller.processCheckout(phieu.getMaPhieu(), vatPercent, discountPercent, paymentMethod)) {
                          dialog.close();
                          showDashboard();  // Refresh dashboard
                      }
                  }
              });

              btnCancel.setOnAction(e -> dialog.close());

              buttonBox.getChildren().addAll(btnCancel, btnConfirm);
              mainContent.getChildren().addAll(spacer, buttonBox);

              scrollPane.setContent(mainContent);
              scrollPane.setFitToWidth(true);

              Scene scene = new Scene(scrollPane);
              dialog.setScene(scene);
              dialog.showAndWait();

          } catch (Exception ex) {
              ex.printStackTrace();
              showErrorBox("LỖI KHI MỞ DIALOG TRẢ PHÒNG", ex);
          }
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

            // 1. Khởi tạo ĐÚNG 6 THAM SỐ
            QuanLyDatPhongController dpController = new QuanLyDatPhongController(
                    phongService, phieuDatPhongService, khachHangService, dichVuService, currentUser, this::showDashboard
            );

            // 2. Gọi hàm createQuanLyDatPhongView() (trả về BorderPane/Node, KHÔNG CÒN LÀ Scene nữa)
            contentArea.getChildren().add(dpController.createQuanLyDatPhongView());

        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorBox("LỖI KHI MỞ GIAO DIỆN ĐẶT PHÒNG", ex);
        }
    }

    private void loadQuanLyPhieuDatTraPhong() {
        try {
            contentArea.getChildren().clear();
            QuanLyPhieuDatTraPhongController controller = new QuanLyPhieuDatTraPhongController(
                    phieuDatPhongService, phongService, khachHangService,
                    hoaDonService, chiTietHoaDonService, dichVuService, currentUser
            );
            BorderPane view = controller.createMainView();
            view.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            contentArea.getChildren().setAll(view);
        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorBox("LỖI KHI MỞ GIAO DIỆN QUẢN LÝ PHIẾU ĐẶT & TRẢ PHÒNG", ex);
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
        loadQuanLyPhieuDatTraPhong();
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

     /**
      * Render rooms grouped by floor
      */
     private void renderRoomsByFloor(List<PhongDTO> rooms, LocalDate checkInDate, LocalDate checkOutDate) {
         try {
             if (roomContainer == null) {
                 return;
             }

             roomContainer.getChildren().clear();

             if (rooms == null || rooms.isEmpty()) {
                 Label emptyLabel = new Label("Không có phòng nào để hiển thị");
                 emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
                 roomContainer.getChildren().add(emptyLabel);
                 return;
             }

             // Group rooms by floor (using first character of room code as floor indicator)
             Map<String, List<PhongDTO>> roomsByFloor = new TreeMap<>();
             for (PhongDTO room : rooms) {
                 String floorKey = room.getMaPhong() != null && !room.getMaPhong().isEmpty()
                     ? room.getMaPhong().substring(0, 1)
                     : "0";
                 roomsByFloor.computeIfAbsent(floorKey, k -> new ArrayList<>()).add(room);
             }

             // Render each floor section
             for (String floor : roomsByFloor.keySet()) {
                 List<PhongDTO> floorRooms = roomsByFloor.get(floor);

                 // Floor header
                 Label floorLabel = new Label("📍 Tầng " + floor);
                 floorLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
                 floorLabel.setTextFill(Color.web(COLOR_TEXT_MAIN));
                 floorLabel.setStyle("-fx-text-fill: " + COLOR_PRIMARY + ";");
                 roomContainer.getChildren().add(floorLabel);

                 // Room grid for this floor
                 GridPane roomGrid = new GridPane();
                 roomGrid.setHgap(12);
                 roomGrid.setVgap(12);

                 int col = 0, row = 0;
                 for (PhongDTO room : floorRooms) {
                     VBox roomCard = createRoomCard(room, checkInDate, checkOutDate);
                     roomGrid.add(roomCard, col, row);

                     col++;
                     if (col >= 4) { // 4 rooms per row
                         col = 0;
                         row++;
                     }
                 }

                 roomContainer.getChildren().add(roomGrid);
             }
         } catch (Exception ex) {
             ex.printStackTrace();
         }
     }

     /**
      * Create a room card component
      */
     private VBox createRoomCard(PhongDTO room, LocalDate checkInDate, LocalDate checkOutDate) {
         VBox card = new VBox(8);
         card.setPadding(new Insets(12));
         card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #e2e8f0; -fx-border-width: 1;");
         card.setPrefWidth(200);
         card.setPrefHeight(180);

         // Add hover effect
         card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #cbd5e1; -fx-border-width: 1; -fx-cursor: hand;"));
         card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #e2e8f0; -fx-border-width: 1;"));
         card.setOnMouseClicked(e -> showRoomDetailModal(room, checkInDate, checkOutDate));
         card.setCursor(Cursor.HAND);

         // Room code
         Label lblMa = new Label(room.getMaPhong());
         lblMa.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
         lblMa.setTextFill(Color.web(COLOR_TEXT_MAIN));

         // Room type
         Label lblType = new Label(room.getMaLoaiPhong());
         lblType.setFont(Font.font("Segoe UI", 11));
         lblType.setTextFill(Color.web(COLOR_TEXT_MUTED));

         // Status badge
         String statusText = room.getTinhTrang();
         String statusColor = getStatusColor(statusText);
         Label lblStatus = new Label(getStatusEmoji(statusText) + " " + statusText);
         lblStatus.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 11));
         lblStatus.setStyle("-fx-background-color: " + statusColor + "20; -fx-padding: 4 8; -fx-background-radius: 4; -fx-text-fill: " + statusColor + ";");

         // Price
         Label lblPrice = new Label(String.format("%,.0f đ", room.getGiaPhong()));
         lblPrice.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
         lblPrice.setTextFill(Color.web(COLOR_PRIMARY));

         Region spacer = new Region();
         VBox.setVgrow(spacer, Priority.ALWAYS);

         card.getChildren().addAll(lblMa, lblType, lblStatus, spacer, lblPrice);
         return card;
     }

     /**
      * Get status color based on room status
      */
     private String getStatusColor(String status) {
         if (status == null) return COLOR_TEXT_MUTED;
         return switch (status) {
             case "Trống" -> COLOR_AVAILABLE;
             case "Đã Đặt" -> COLOR_BOOKED;
             case "Đang ở" -> COLOR_OCCUPIED;
             case "Bảo Trì" -> COLOR_MAINTENANCE;
             default -> COLOR_TEXT_MUTED;
         };
     }

     /**
      * Get status emoji
      */
     private String getStatusEmoji(String status) {
         if (status == null) return "❓";
         return switch (status) {
             case "Trống" -> "✅";
             case "Đã Đặt" -> "📅";
             case "Đang ở" -> "🔑";
             case "Bảo Trì" -> "🔧";
             default -> "❓";
         };
     }

    private void showGoiDichVuScreen() {
        try {
            contentArea.getChildren().clear();
            GoiDichVuController gd = new GoiDichVuController(dichVuService, phieuDatPhongService, chiTietHoaDonService);
            contentArea.getChildren().add(gd.createGoiDichVuView());
        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorBox("LỖI KHI MỞ GIAO DIỆN GỌI DỊCH VỤ", ex);
        }
    }

    private void loadTraPhong() {
        loadQuanLyPhieuDatTraPhong();
    }

    private void loadGoiDichVu() {
        try {
            contentArea.getChildren().clear();
            GoiDichVuController gd = new GoiDichVuController(dichVuService, phieuDatPhongService, chiTietHoaDonService);
            contentArea.getChildren().add(gd.createGoiDichVuView());
        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorBox("LỖI KHI MỞ GIAO DIỆN GỌI DỊCH VỤ", ex);
        }
    }

    private void loadThongKeDoanHThu() {
        try {
            contentArea.getChildren().clear();
            RevenueController rc = new RevenueController(hoaDonService);
            contentArea.getChildren().add(rc.createRevenueView());
        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorBox("LỖI KHI MỞ GIAO DIỆN THỐNG KÊ DOANH THU", ex);
        }
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

