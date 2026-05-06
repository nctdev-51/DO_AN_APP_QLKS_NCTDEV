package iuh.fit.presentation.controller;

import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.IKhachHangService;
import iuh.fit.core.service.INhanVienService;
import iuh.fit.core.service.IPhieuDatPhongService;
import iuh.fit.core.service.IPhongService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
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
import java.util.*;
import java.util.stream.Collectors;

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

    // --- CÁC MÃ MÀU HIỆN ĐẠI TÙY CHỈNH ---
    private final String COLOR_PRIMARY = "#2563eb";
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
        primaryStage.setTitle("Quản Lý Khách Sạn TATP - " + currentUser.getHoTenNhanVien());
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

        Label lblTitle = new Label("TATP HOTEL");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 22));
        lblTitle.setTextFill(Color.web(COLOR_PRIMARY));

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

        header.getChildren().addAll(lblTitle, spacer, rightControls);
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
        btnGoiDichVu.setOnAction(e -> { setActiveMenu(btnGoiDichVu, menuButtons); showPlaceholder("Giao diện Gọi Dịch Vụ đang phát triển..."); });
        btnQuanLyPhieu.setOnAction(e -> { setActiveMenu(btnQuanLyPhieu, menuButtons); showPlaceholder("Giao diện Quản Lý Phiếu đang phát triển..."); });
        btnDangXuat.setOnAction(e -> handleLogout());

        VBox footerInfo = new VBox(4);
        footerInfo.setAlignment(Pos.CENTER);
        footerInfo.setPadding(new Insets(20, 0, 0, 0));
        footerInfo.getChildren().addAll(
                new Label("SV: Nguyễn Chí Tâm") {{ setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;"); }},
                new Label("Lớp: DHKTPM19A") {{ setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;"); }},
                new Label("ĐH Công Nghiệp TP.HCM") {{ setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;"); }}
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // ĐÃ CHUYỂN VỊ TRÍ CỦA btnPhong TRONG DANH SÁCH HIỂN THỊ
        sidebar.getChildren().addAll(
                profileBox,
                lblMenuSection, btnTrangChu, btnDatPhong, btnGoiDichVu, btnQuanLyPhieu, btnPhong,
                lblListSection, btnKhachHang, btnNhanVien,
                spacer, btnDangXuat, footerInfo
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

    private void showDashboard() {
        try {
            contentArea.getChildren().clear();
            VBox dashboard = new VBox(20);
            dashboard.setPadding(new Insets(10));

            HBox filterBox = createFilterBox(dashboard);
            dashboard.getChildren().add(filterBox);

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-padding: 0;");
            scrollPane.setBorder(Border.EMPTY);

            roomContainer = new VBox(20);
            roomContainer.setPadding(new Insets(5, 5, 20, 5));
            scrollPane.setContent(roomContainer);
            VBox.setVgrow(scrollPane, Priority.ALWAYS);

            dashboard.getChildren().add(scrollPane);

            List<PhongDTO> allRooms = phongService.getAllPhong();
            renderRoomsByFloor(allRooms, null, null);

            contentArea.getChildren().add(dashboard);
        } catch (Exception ex) {
            showErrorBox("LỖI HIỂN THỊ TRANG CHỦ", ex);
        }
    }

    private HBox createFilterBox(VBox dashboard) {
        HBox filterBox = new HBox(15);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.setPadding(new Insets(15, 20, 15, 20));
        filterBox.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e2e8f0;");

        DropShadow shadow = new DropShadow(); shadow.setColor(Color.color(0, 0, 0, 0.03)); shadow.setRadius(5); shadow.setOffsetY(2);
        filterBox.setEffect(shadow);

        Label lblIn = new Label("Nhận phòng:"); lblIn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        DatePicker dpCheckIn = new DatePicker(LocalDate.now()); dpCheckIn.setStyle("-fx-font-size: 13px;");

        Label lblOut = new Label("Trả phòng:"); lblOut.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        DatePicker dpCheckOut = new DatePicker(LocalDate.now().plusDays(1)); dpCheckOut.setStyle("-fx-font-size: 13px;");

        Label lblGia = new Label("Mức giá:"); lblGia.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        Slider priceSlider = new Slider(0, 10_000_000, 10_000_000); priceSlider.setPrefWidth(120);
        Label lblPrice = new Label("≤ 10Tr VNĐ"); lblPrice.setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");

        priceSlider.valueProperty().addListener((obs, old, newVal) -> {
            double val = newVal.doubleValue();
            if(val >= 1000000) lblPrice.setText(String.format("≤ %.1fTr", val/1000000));
            else lblPrice.setText(String.format("≤ %,.0fđ", val));
        });

        Button btnGuest = new Button("👥 2 Khách");
        btnGuest.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-padding: 5 10; -fx-cursor: hand;");

        Button btnSearch = new Button("🔍 Tìm Kiếm");
        btnSearch.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 5; -fx-cursor: hand;");

        btnSearch.setOnAction(e -> {
            LocalDate in = dpCheckIn.getValue();
            LocalDate out = dpCheckOut.getValue();
            double maxPrice = priceSlider.getValue();
            List<PhongDTO> available = phongService.findAvailableRooms(in, out, 0, maxPrice);
            renderRoomsByFloor(available, in, out);
        });

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        filterBox.getChildren().addAll(lblIn, dpCheckIn, lblOut, dpCheckOut, lblGia, priceSlider, lblPrice, btnGuest, spacer, btnSearch);
        return filterBox;
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
            contentArea.getChildren().add(dpController.createQuanLyDatPhongScene().getRoot());
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
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        Label icon = new Label("🚧"); icon.setFont(Font.font(40));
        Label lbl = new Label(message); lbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 18)); lbl.setTextFill(Color.web("#64748b"));
        box.getChildren().addAll(icon, lbl);
        contentArea.getChildren().add(box);
    }

    private void handleLogout() {
        primaryStage.close();
        System.out.println("Đã đăng xuất hệ thống!");
    }
}