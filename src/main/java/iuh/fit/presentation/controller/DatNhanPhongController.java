package iuh.fit.presentation.controller;

import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DatNhanPhongController {

    private final IPhongService phongService;
    private final IPhieuDatPhongService phieuDatPhongService;
    private final IKhachHangService khachHangService;
    private final IHoaDonService hoaDonService;
    private final IChiTietHoaDonService chiTietHoaDonService;
    private final IDichVuService dichVuService;
    private final TaiKhoanDTO currentUser;
    private final Stage primaryStage;

    private VBox roomContainer;
    private List<PhongDTO> allRoomsCache = new ArrayList<>();
    private Map<String, PhieuDatPhongDTO> activeBookingsMap = new HashMap<>();

    // Các Component của Bộ lọc
    private TextField txtSearch, txtGiaMax;
    private Slider sliderPrice;
    private ComboBox<String> cbLoai;
    private ComboBox<String> cbTrangThai;
    private DatePicker dpIn;
    private DatePicker dpOut;
    private Label lblResultCount;

    // --- BẢNG MÀU CHUẨN ĐỒNG BỘ DASHBOARD ---
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_BG_LIGHT = "#f8fafc";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_BORDER = "#e2e8f0";

    // Màu trạng thái phòng
    private final String COLOR_TRONG = "#10b981";    // Trống - Xanh lá
    private final String COLOR_DA_DAT = "#3b82f6";   // Đã Đặt - Xanh dương
    private final String COLOR_DANG_O = "#f59e0b";   // Đang ở - Cam
    private final String COLOR_BAO_TRI = "#64748b";  // Bảo Trì - Xám

    public DatNhanPhongController(IPhongService phongService, IPhieuDatPhongService phieuDatPhongService,
                                  IKhachHangService khachHangService, IHoaDonService hoaDonService,
                                  IChiTietHoaDonService chiTietHoaDonService, IDichVuService dichVuService,
                                  TaiKhoanDTO currentUser, Stage primaryStage) {
        this.phongService = phongService;
        this.phieuDatPhongService = phieuDatPhongService;
        this.khachHangService = khachHangService;
        this.hoaDonService = hoaDonService;
        this.chiTietHoaDonService = chiTietHoaDonService;
        this.dichVuService = dichVuService;
        this.currentUser = currentUser;
        this.primaryStage = primaryStage;
    }

    public VBox createView() {
        VBox rootPane = new VBox(25);
        rootPane.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + "; -fx-padding: 30;");

        // --- 1. HEADER ---
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.BOTTOM_LEFT);

        VBox titleBox = new VBox(5);
        Label lblTitle = new Label("SƠ ĐỒ PHÒNG TRỰC TUYẾN");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 28));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblSubTitle = new Label("Quản lý trạng thái phòng và thực hiện nghiệp vụ đặt/nhận nhanh.");
        lblSubTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        lblSubTitle.setTextFill(Color.web(COLOR_TEXT_MUTED));
        titleBox.getChildren().addAll(lblTitle, lblSubTitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        lblResultCount = new Label("Đang kết nối...");
        lblResultCount.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblResultCount.setStyle("-fx-background-color: white; -fx-padding: 10 25; -fx-background-radius: 30; " +
                "-fx-text-fill: " + COLOR_PRIMARY + "; -fx-border-color: " + COLOR_PRIMARY + "; -fx-border-width: 1;");

        headerBox.getChildren().addAll(titleBox, spacer, lblResultCount);

        // --- 2. BỘ LỌC TÌM KIẾM ---
        VBox filterBar = createFilterBar();

        // --- 3. LƯỚI DANH SÁCH PHÒNG ---
        roomContainer = new VBox(35);
        roomContainer.setPadding(new Insets(10, 0, 10, 0));

        ScrollPane scrollPane = new ScrollPane(roomContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        rootPane.getChildren().addAll(headerBox, filterBar, scrollPane);

        loadDataAsync();
        return rootPane;
    }

    private VBox createFilterBar() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + COLOR_BORDER + "; -fx-border-width: 1;");

        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000000", 0.04));
        ds.setRadius(10); ds.setOffsetY(4);
        card.setEffect(ds);

        String inputStyle = "-fx-font-size: 14px; -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #cbd5e1; -fx-background-color: #f8fafc; -fx-padding: 8 12; -fx-pref-height: 40px;";

        HBox row1 = new HBox(15);
        row1.setAlignment(Pos.CENTER_LEFT);

        txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm số phòng, tên phòng...");
        txtSearch.setStyle(inputStyle);
        HBox.setHgrow(txtSearch, Priority.ALWAYS);
        txtSearch.textProperty().addListener((obs, old, nw) -> filterRooms());

        cbLoai = new ComboBox<>();
        cbLoai.getItems().addAll("Tất cả loại phòng", "Phòng Đơn", "Phòng Đôi", "Phòng Gia Đình", "Phòng VIP");
        cbLoai.setValue("Tất cả loại phòng");
        cbLoai.setStyle(inputStyle);
        cbLoai.setPrefWidth(200);
        cbLoai.setOnAction(e -> filterRooms());

        cbTrangThai = new ComboBox<>();
        cbTrangThai.getItems().addAll("Tất cả trạng thái", "Trống", "Đã Đặt", "Đang ở", "Bảo Trì");
        cbTrangThai.setValue("Tất cả trạng thái");
        cbTrangThai.setStyle(inputStyle);
        cbTrangThai.setPrefWidth(180);
        cbTrangThai.setOnAction(e -> filterRooms());

        row1.getChildren().addAll(txtSearch, cbLoai, cbTrangThai);

        FlowPane row2 = new FlowPane();
        row2.setHgap(15);
        row2.setVgap(15);
        row2.setAlignment(Pos.CENTER_LEFT);

        Label lblGia = new Label("Giá tối đa:");
        lblGia.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblGia.setTextFill(Color.web(COLOR_TEXT_MUTED));

        sliderPrice = new Slider(0, 10000000, 10000000);
        sliderPrice.setPrefWidth(200);
        sliderPrice.setStyle("-fx-cursor: hand;");
        sliderPrice.setMajorTickUnit(500000);
        sliderPrice.setMinorTickCount(4);
        sliderPrice.setSnapToTicks(true);

        txtGiaMax = new TextField("10,000,000");
        txtGiaMax.setStyle(inputStyle + " -fx-font-weight: bold; -fx-text-fill: " + COLOR_PRIMARY + ";");
        txtGiaMax.setPrefWidth(130);
        txtGiaMax.setAlignment(Pos.CENTER_RIGHT);

        Label lblUnit = new Label("VNĐ");
        lblUnit.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblUnit.setTextFill(Color.web(COLOR_TEXT_MUTED));

        sliderPrice.valueProperty().addListener((obs, oldV, newV) -> {
            if (!txtGiaMax.isFocused()) {
                txtGiaMax.setText(String.format("%,.0f", newV.doubleValue()));
                filterRooms();
            }
        });

        Runnable updatePriceFromText = () -> {
            try {
                String cleanStr = txtGiaMax.getText().replaceAll("[^\\d]", "");
                if (cleanStr.isEmpty()) cleanStr = "0";

                double val = Double.parseDouble(cleanStr);
                if (val > 10000000) val = 10000000;

                sliderPrice.setValue(val);
                txtGiaMax.setText(String.format("%,.0f", val));
                filterRooms();
            } catch (Exception e) {
                txtGiaMax.setText(String.format("%,.0f", sliderPrice.getValue()));
            }
        };

        txtGiaMax.setOnAction(e -> updatePriceFromText.run());
        txtGiaMax.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) updatePriceFromText.run();
        });

        Label lblNgay = new Label("   |   Thời gian:");
        lblNgay.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblNgay.setTextFill(Color.web(COLOR_TEXT_MUTED));

        dpIn = new DatePicker(LocalDate.now());
        dpIn.setPromptText("Ngày nhận");
        dpIn.setStyle(inputStyle);
        dpIn.setPrefWidth(140);
        dpIn.setOnAction(e -> filterRooms());

        Label lblDash2 = new Label("-");
        lblDash2.setTextFill(Color.web(COLOR_TEXT_MUTED));

        dpOut = new DatePicker(LocalDate.now().plusDays(1));
        dpOut.setPromptText("Ngày trả");
        dpOut.setStyle(inputStyle);
        dpOut.setPrefWidth(140);
        dpOut.setOnAction(e -> filterRooms());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnClear = new Button("🔄 Làm mới");
        btnClear.setCursor(Cursor.HAND);
        btnClear.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 25; -fx-background-radius: 6; -fx-pref-height: 40px;");
        btnClear.setOnAction(e -> resetFilters());

        row2.getChildren().addAll(lblGia, sliderPrice, txtGiaMax, lblUnit, lblNgay, dpIn, lblDash2, dpOut, spacer, btnClear);

        card.getChildren().addAll(row1, row2);
        return card;
    }

    private void filterRooms() {
        String kw = txtSearch.getText() != null ? txtSearch.getText().toLowerCase().trim() : "";
        String loaiSel = cbLoai.getValue();
        String ttSel = cbTrangThai.getValue();

        final double finalMaxP = sliderPrice.getValue();

        List<PhongDTO> filtered = allRoomsCache.stream()
                .filter(p -> kw.isEmpty() || p.getMaPhong().toLowerCase().contains(kw) || p.getTenPhong().toLowerCase().contains(kw))
                .filter(p -> loaiSel.equals("Tất cả loại phòng") || mapMaLoaiToTen(p.getMaLoaiPhong()).equals(loaiSel))
                .filter(p -> matchStatus(p.getTinhTrang(), ttSel))
                .filter(p -> p.getGiaPhong() <= finalMaxP)
                .collect(Collectors.toList());

        renderRoomsGrid(filtered);
    }

    private void resetFilters() {
        txtSearch.clear();
        cbLoai.setValue("Tất cả loại phòng");
        cbTrangThai.setValue("Tất cả trạng thái");
        dpIn.setValue(LocalDate.now());
        dpOut.setValue(LocalDate.now().plusDays(1));
        sliderPrice.setValue(10000000);
        txtGiaMax.setText("10,000,000");
        filterRooms();
    }

    // =========================================================================
    // RENDER LƯỚI DANH SÁCH PHÒNG (5 CỘT MỖI TẦNG)
    // =========================================================================
    private void renderRoomsGrid(List<PhongDTO> rooms) {
        Platform.runLater(() -> {
            roomContainer.getChildren().clear();
            lblResultCount.setText("Kết quả: " + rooms.size() + " phòng");

            Map<String, List<PhongDTO>> byFloor = new TreeMap<>();
            rooms.forEach(r -> {
                String floor = (r.getMaPhong() != null && r.getMaPhong().length() > 0) ? r.getMaPhong().substring(0, 1) : "?";
                byFloor.computeIfAbsent(floor, k -> new ArrayList<>()).add(r);
            });

            for (String floor : byFloor.keySet()) {
                VBox floorBox = new VBox(15);
                Label lblFloor = new Label("📍 TẦNG " + floor);
                lblFloor.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
                lblFloor.setTextFill(Color.web(COLOR_PRIMARY));

                GridPane gridPane = new GridPane();
                gridPane.setHgap(20);
                gridPane.setVgap(20);

                for (int i = 0; i < 5; i++) {
                    ColumnConstraints col = new ColumnConstraints();
                    col.setPercentWidth(20);
                    gridPane.getColumnConstraints().add(col);
                }

                List<PhongDTO> floorRooms = byFloor.get(floor);
                for (int i = 0; i < floorRooms.size(); i++) {
                    PhieuDatPhongDTO activePhieu = activeBookingsMap.get(floorRooms.get(i).getMaPhong());
                    VBox card = createEnhancedRoomCard(floorRooms.get(i), activePhieu);
                    card.setMaxWidth(Double.MAX_VALUE);

                    int col = i % 5;
                    int row = i / 5;
                    gridPane.add(card, col, row);
                }

                floorBox.getChildren().addAll(lblFloor, gridPane);
                roomContainer.getChildren().add(floorBox);
            }
        });
    }

    // =========================================================================
    // THIẾT KẾ THẺ PHÒNG
    // =========================================================================
    private VBox createEnhancedRoomCard(PhongDTO room, PhieuDatPhongDTO phieu) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15, 15, 20, 15));
        card.setAlignment(Pos.TOP_LEFT);

        String statusKey = getNormalizedStatusKey(room.getTinhTrang());
        String color = getStatusColorByKey(statusKey);

        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; " +
                "-fx-border-color: #e2e8f0 #e2e8f0 #e2e8f0 " + color + "; -fx-border-width: 1 1 1 6;");

        DropShadow ds = new DropShadow(10, Color.web("#000000", 0.05));
        card.setEffect(ds);

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label lblMa = new Label(room.getMaPhong());
        lblMa.setFont(Font.font("Segoe UI", FontWeight.BLACK, 22));
        lblMa.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label lblIcon = new Label(getStatusEmojiByKey(statusKey));
        lblIcon.setFont(Font.font(20));
        top.getChildren().addAll(lblMa, sp, lblIcon);

        HBox mid = new HBox(10);
        mid.setAlignment(Pos.CENTER_LEFT);

        Label lblTenLoai = new Label(mapMaLoaiToTen(room.getMaLoaiPhong()).toUpperCase());
        lblTenLoai.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        lblTenLoai.setTextFill(Color.web(COLOR_TEXT_MUTED));

        Label lblStatus = new Label(mapStatusToVietnameseByKey(statusKey).toUpperCase());
        lblStatus.setFont(Font.font("Segoe UI", FontWeight.BLACK, 10));
        lblStatus.setStyle("-fx-background-color: " + color + "15; -fx-text-fill: " + color + "; -fx-padding: 4 8; -fx-background-radius: 6;");
        mid.getChildren().addAll(lblTenLoai, lblStatus);

        card.getChildren().addAll(top, mid, new Region());
        VBox.setVgrow(card.getChildren().get(2), Priority.ALWAYS);

        VBox bottomInfo = new VBox(5);
        if (statusKey.equals("DANG_O") && phieu != null) {
            Label lblKhach = new Label("👤 " + (phieu.getTenKhachHang() != null ? phieu.getTenKhachHang() : "Khách Vãng Lai"));
            lblKhach.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            lblKhach.setTextFill(Color.web(COLOR_TEXT_MAIN));

            String ngayTraStr = phieu.getNgayTra() != null ?
                    phieu.getNgayTra().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Chưa xác định";
            Label lblNgayTra = new Label("⏳ Ra: " + ngayTraStr);
            lblNgayTra.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
            lblNgayTra.setTextFill(Color.web(COLOR_DANG_O));

            bottomInfo.getChildren().addAll(lblKhach, lblNgayTra);
        } else {
            Label lblPrice = new Label(String.format("%,.0f đ", room.getGiaPhong()));
            lblPrice.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
            lblPrice.setTextFill(Color.web(COLOR_PRIMARY));
            bottomInfo.getChildren().add(lblPrice);
        }
        card.getChildren().add(bottomInfo);

        card.setCursor(Cursor.HAND);
        card.setOnMouseEntered(e -> {
            card.setStyle(card.getStyle() + "-fx-background-color: #f8fafc; -fx-translate-y: -5;");
            ds.setRadius(20);
        });
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; " +
                    "-fx-border-color: #e2e8f0 #e2e8f0 #e2e8f0 " + color + "; -fx-border-width: 1 1 1 6; -fx-translate-y: 0;");
            ds.setRadius(10);
        });

        card.setOnMouseClicked(e -> handleRoomClick(room));

        return card;
    }

    private void handleRoomClick(PhongDTO room) {
        try {
            ChiTietPhongController detail = new ChiTietPhongController(
                    room, dpIn.getValue(), dpOut.getValue(), currentUser,
                    phongService, phieuDatPhongService, khachHangService,
                    hoaDonService, chiTietHoaDonService, dichVuService,
                    this::loadDataAsync
            );
            detail.showDialog(primaryStage);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // =========================================================================
    // FIX CỐT LÕI: LOGIC CHUYỂN ĐỔI TRẠNG THÁI SIÊU AN TOÀN
    // =========================================================================
    private String getNormalizedStatusKey(String rawStatus) {
        if (rawStatus == null || rawStatus.trim().isEmpty()) return "BAO_TRI";
        String s = rawStatus.trim();

        // 1. ƯU TIÊN SO SÁNH CHÍNH XÁC (Tuyệt đối an toàn, không sợ lỗi Regex)
        if (s.equalsIgnoreCase("Trống") || s.equalsIgnoreCase("Trong")) return "TRONG";
        if (s.equalsIgnoreCase("Đã Đặt") || s.equalsIgnoreCase("Da Dat")) return "DA_DAT";
        if (s.equalsIgnoreCase("Đang ở") || s.equalsIgnoreCase("Dang o")) return "DANG_O";
        if (s.equalsIgnoreCase("Bảo Trì") || s.equalsIgnoreCase("Bao Tri")) return "BAO_TRI";

        // 2. PHƯƠNG ÁN DỰ PHÒNG (Fallback)
        String normalized = removeAccents(s).toUpperCase();
        if (normalized.contains("TRONG")) return "TRONG";
        if (normalized.contains("DAT")) return "DA_DAT";
        if (normalized.contains("DANG") || normalized.equals("O")) return "DANG_O";

        return "BAO_TRI";
    }

    private String getStatusColorByKey(String key) {
        return switch (key) {
            case "TRONG" -> COLOR_TRONG;
            case "DA_DAT" -> COLOR_DA_DAT;
            case "DANG_O" -> COLOR_DANG_O;
            default -> COLOR_BAO_TRI;
        };
    }

    private String getStatusEmojiByKey(String key) {
        return switch (key) {
            case "TRONG" -> "✨";
            case "DA_DAT" -> "📅";
            case "DANG_O" -> "🔑";
            default -> "🔧";
        };
    }

    private String mapStatusToVietnameseByKey(String key) {
        return switch (key) {
            case "TRONG" -> "Trống";
            case "DA_DAT" -> "Đã Đặt";
            case "DANG_O" -> "Đang ở";
            default -> "Bảo Trì";
        };
    }

    private boolean matchStatus(String dbStatus, String uiStatus) {
        if (uiStatus.equals("Tất cả trạng thái")) return true;
        String key = getNormalizedStatusKey(dbStatus);
        String mappedUI = mapStatusToVietnameseByKey(key);
        return mappedUI.equalsIgnoreCase(uiStatus);
    }

    private String mapMaLoaiToTen(String maLoai) {
        if (maLoai == null) return "PHÒNG TIÊU CHUẨN";
        return switch (maLoai.toUpperCase()) {
            case "DON" -> "Phòng Đơn";
            case "DOI" -> "Phòng Đôi";
            case "GIADINH" -> "Phòng Gia Đình";
            case "VIP" -> "Phòng VIP";
            default -> "Phòng " + maLoai;
        };
    }

    private void loadDataAsync() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                allRoomsCache = phongService.getAllPhong();

                List<PhieuDatPhongDTO> phieus = phieuDatPhongService.getAllPhieuDatPhong();
                activeBookingsMap.clear();
                if (phieus != null) {
                    for (PhieuDatPhongDTO p : phieus) {
                        if (p.getTrangThai() != null &&
                                (p.getTrangThai().contains("Nhận Phòng") ||
                                        p.getTrangThai().equals("DA_NHAN_PHONG") ||
                                        p.getTrangThai().equalsIgnoreCase("Chưa Nhận Phòng"))) {
                            activeBookingsMap.put(p.getMaPhong(), p);
                        }
                    }
                }
                return null;
            }
        };
        task.setOnSucceeded(evt -> filterRooms());
        new Thread(task).start();
    }

    private String removeAccents(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalized)
                .replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }
}