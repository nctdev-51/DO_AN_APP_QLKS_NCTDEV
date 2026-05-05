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
import javafx.scene.Scene;
import javafx.scene.control.*;
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
        rootLayout.setStyle("-fx-background-color: #f4f6f9;");

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
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setStyle("-fx-background-color: #34495e; -fx-border-width: 0 0 2px 0; -fx-border-color: #e74c3c;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label lblTitle = new Label("KHÁCH SẠN TATP");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        lblTitle.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        lblClock = new Label();
        lblClock.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblClock.setTextFill(Color.web("#f1c40f"));
        startClock();

        Button btnCaLamViec = new Button("🔄 Nhận / Giao Ca");
        btnCaLamViec.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold;");
        btnCaLamViec.setOnAction(e -> showPlaceholder("Module Giao Ca đang phát triển..."));

        HBox rightControls = new HBox(20);
        rightControls.setAlignment(Pos.CENTER_RIGHT);
        rightControls.getChildren().addAll(btnCaLamViec, lblClock);

        header.getChildren().addAll(lblTitle, spacer, rightControls);
        return header;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(5);
        sidebar.setPadding(new Insets(20, 10, 20, 10));
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #2c3e50;");

        VBox profileBox = new VBox(5);
        profileBox.setAlignment(Pos.CENTER);
        profileBox.setPadding(new Insets(0, 0, 20, 0));

        Label lblAvatar = new Label("👤");
        lblAvatar.setFont(Font.font(40));
        lblAvatar.setTextFill(Color.LIGHTGRAY);

        Label lblUser = new Label(currentUser.getHoTenNhanVien());
        lblUser.setTextFill(Color.WHITE);
        lblUser.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        Label lblRole = new Label("Vai trò: " + (currentUser.getTaiKhoan().equals("admin") ? "Quản Lý" : "Lễ Tân"));
        lblRole.setTextFill(Color.web("#bdc3c7"));

        profileBox.getChildren().addAll(lblAvatar, lblUser, lblRole);

        Label lblMenuSection = new Label("NGHIỆP VỤ");
        lblMenuSection.setTextFill(Color.GRAY);
        lblMenuSection.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lblMenuSection.setPadding(new Insets(10, 0, 5, 10));

        Button btnTrangChu = createMenuButton("🏠 Trang Chủ");
        Button btnDatPhong = createMenuButton("🏨 Đặt & Nhận Phòng");
        Button btnGoiDichVu = createMenuButton("🍽️ Gọi Dịch Vụ");
        Button btnQuanLyPhieu = createMenuButton("📋 Quản lý Phiếu Đặt");

        Label lblListSection = new Label("DANH MỤC");
        lblListSection.setTextFill(Color.GRAY);
        lblListSection.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lblListSection.setPadding(new Insets(15, 0, 5, 10));

        Button btnKhachHang = createMenuButton("👥 Khách Hàng");
        Button btnNhanVien = createMenuButton("👨‍💼 Nhân Viên");

        Button btnDangXuat = createMenuButton("🚪 Đăng xuất");
        btnDangXuat.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px;");

        btnTrangChu.setOnAction(e -> showDashboard());
        btnDatPhong.setOnAction(e -> loadQuanLyDatPhong());
        btnKhachHang.setOnAction(e -> loadQuanLyKhachHang());
        btnNhanVien.setOnAction(e -> loadQuanLyNhanVien());
        btnDangXuat.setOnAction(e -> handleLogout());

        btnGoiDichVu.setOnAction(e -> showPlaceholder("Giao diện Gọi Dịch Vụ đang phát triển..."));
        btnQuanLyPhieu.setOnAction(e -> showPlaceholder("Giao diện Quản Lý Phiếu đang phát triển..."));

        VBox footerInfo = new VBox(3);
        footerInfo.setAlignment(Pos.CENTER);
        footerInfo.setPadding(new Insets(20, 0, 0, 0));
        footerInfo.getChildren().addAll(
                new Label("SV: Nguyễn Chí Tâm") {{ setTextFill(Color.LIGHTGRAY); }},
                new Label("Lớp: DHKTPM19A") {{ setTextFill(Color.LIGHTGRAY); }},
                new Label("Trường: ĐH Công Nghiệp TP.HCM") {{ setTextFill(Color.GRAY); setFont(Font.font(10)); }}
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(
                profileBox,
                lblMenuSection, btnTrangChu, btnDatPhong, btnGoiDichVu, btnQuanLyPhieu,
                lblListSection, btnKhachHang, btnNhanVien,
                spacer,
                btnDangXuat, footerInfo
        );

        return sidebar;
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12px 20px;");

        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains("#c0392b")) {
                btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12px 20px; -fx-border-width: 0 0 0 4px; -fx-border-color: #3498db;");
            }
        });
        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().contains("#c0392b")) {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 12px 20px;");
            }
        });
        return btn;
    }

    private void startClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss  |  dd/MM/yyyy");
        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            lblClock.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void showDashboard() {
        contentArea.getChildren().clear();
        VBox dashboard = new VBox(10);
        dashboard.setPadding(new Insets(15));
        dashboard.setStyle("-fx-background-color: white;");

        HBox filterBox = createFilterBox(dashboard);
        dashboard.getChildren().add(filterBox);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        roomContainer = new VBox(10);
        roomContainer.setPadding(new Insets(10));
        scrollPane.setContent(roomContainer);
        dashboard.getChildren().add(scrollPane);

        List<PhongDTO> allRooms = phongService.getAllPhong();
        renderRoomsByFloor(allRooms, null, null);

        contentArea.getChildren().add(dashboard);
    }

    private HBox createFilterBox(VBox dashboard) {
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.setPadding(new Insets(10));
        filterBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-radius: 5; -fx-background-radius: 5;");

        DatePicker dpCheckIn = new DatePicker(LocalDate.now());
        DatePicker dpCheckOut = new DatePicker(LocalDate.now().plusDays(1));

        Slider priceSlider = new Slider(0, 10_000_000, 10_000_000);
        priceSlider.setPrefWidth(150);
        Label lblPrice = new Label("≤ 10,000,000đ");
        priceSlider.valueProperty().addListener((obs, old, newVal) ->
                lblPrice.setText(String.format("≤ %,.0fđ", newVal.doubleValue()))
        );

        Button btnGuest = new Button("2 khách");

        Button btnSearch = new Button("🔍 Tìm");
        btnSearch.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        btnSearch.setOnAction(e -> {
            LocalDate in = dpCheckIn.getValue();
            LocalDate out = dpCheckOut.getValue();
            double maxPrice = priceSlider.getValue();
            String guestText = btnGuest.getText().replaceAll("[^0-9]", "");
            int guests = guestText.isEmpty() ? 2 : Integer.parseInt(guestText);

            List<PhongDTO> available = phongService.findAvailableRooms(in, out, 0, maxPrice);
            renderRoomsByFloor(available, in, out);
        });

        filterBox.getChildren().addAll(
                new Label("Ngày nhận:"), dpCheckIn,
                new Label("Ngày trả:"), dpCheckOut,
                new Label("Giá:"), priceSlider, lblPrice,
                btnGuest, btnSearch
        );
        return filterBox;
    }

    private void renderRoomsByFloor(List<PhongDTO> rooms, LocalDate in, LocalDate out) {
        roomContainer.getChildren().clear();
        if (rooms.isEmpty()) {
            roomContainer.getChildren().add(new Label("Không có phòng trống phù hợp."));
            return;
        }

        Map<String, List<PhongDTO>> byFloor = new LinkedHashMap<>();
        for (PhongDTO p : rooms) {
            String ma = p.getMaPhong();
            String floor = "Tầng " + (ma.length() > 1 ? ma.substring(1, 2) : "0");
            byFloor.computeIfAbsent(floor, k -> new ArrayList<>()).add(p);
        }

        List<String> sortedFloors = byFloor.keySet().stream()
                .sorted(Comparator.comparingInt(s -> Integer.parseInt(s.replace("Tầng ", ""))))
                .collect(Collectors.toList());

        for (String floor : sortedFloors) {
            Label lblFloor = new Label(floor);
            lblFloor.setFont(Font.font("System", FontWeight.BOLD, 16));
            lblFloor.setPadding(new Insets(5, 0, 5, 0));
            roomContainer.getChildren().add(lblFloor);

            TilePane tilePane = new TilePane();
            tilePane.setHgap(10);
            tilePane.setVgap(10);
            tilePane.setPadding(new Insets(5));

            for (PhongDTO p : byFloor.get(floor)) {
                VBox card = createRoomCard(p, in, out);
                tilePane.getChildren().add(card);
            }
            roomContainer.getChildren().add(tilePane);
        }
    }

    private VBox createRoomCard(PhongDTO p, LocalDate in, LocalDate out) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(8));
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(100, 90);

        String trangThai = p.getTinhTrang() != null ? p.getTinhTrang() : "Không xác định";
        Color bg;
        switch (trangThai) {
            case "Trống" -> bg = Color.GREEN;
            case "Đã Đặt" -> bg = Color.RED;
            case "Đang ở" -> bg = Color.ORANGE;
            default -> bg = Color.GRAY;
        }
        card.setStyle("-fx-background-color: " + toHex(bg) + "; -fx-background-radius: 5; -fx-cursor: hand;");

        Label lblMa = new Label(p.getMaPhong());
        lblMa.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblMa.setTextFill(Color.WHITE);
        Label lblLoai = new Label(p.getMaLoaiPhong());
        lblLoai.setTextFill(Color.WHITE);
        Label lblGia = new Label(String.format("%,.0fđ", p.getGiaPhong()));
        lblGia.setTextFill(Color.WHITE);
        card.getChildren().addAll(lblMa, lblLoai, lblGia);

        card.setOnMouseClicked(e -> {
            if ("Trống".equals(trangThai)) {
                contentArea.getChildren().clear();
                QuanLyDatPhongController dpController = new QuanLyDatPhongController(
                        phongService, phieuDatPhongService, khachHangService, currentUser
                );
                contentArea.getChildren().add(dpController.createQuanLyDatPhongScene().getRoot());
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Phòng " + p.getMaPhong() + " đang " + trangThai);
                alert.show();
            }
        });
        return card;
    }

    private String toHex(Color c) {
        return String.format("#%02X%02X%02X",
                (int) (c.getRed() * 255),
                (int) (c.getGreen() * 255),
                (int) (c.getBlue() * 255));
    }

    private void loadQuanLyKhachHang() {
        contentArea.getChildren().clear();
        QuanLyKhachHangController khController = new QuanLyKhachHangController(khachHangService);
        contentArea.getChildren().add(khController.createQuanLyKhachHangScene().getRoot());
    }

    private void loadQuanLyNhanVien() {
        contentArea.getChildren().clear();
        QuanLyNhanVienController nvController = new QuanLyNhanVienController(nhanVienService);
        Scene scene = nvController.createQuanLyNhanVienScene();
        contentArea.getChildren().add(scene.getRoot());
    }

    private void loadQuanLyDatPhong() {
        contentArea.getChildren().clear();
        QuanLyDatPhongController dpController = new QuanLyDatPhongController(
                phongService, phieuDatPhongService, khachHangService, currentUser
        );
        contentArea.getChildren().add(dpController.createQuanLyDatPhongScene().getRoot());
    }

    private void showPlaceholder(String message) {
        contentArea.getChildren().clear();
        Label lbl = new Label(message);
        lbl.setFont(Font.font("Segoe UI", 16));
        contentArea.getChildren().add(lbl);
    }

    private void handleLogout() {
        primaryStage.close();
        System.out.println("Đã đăng xuất hệ thống!");
    }
}