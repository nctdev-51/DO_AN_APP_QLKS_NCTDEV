package iuh.fit.presentation.controller;

import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChonPhongController {

    private final IPhongService phongService;
    private final IKhachHangService khachHangService;
    private final IPhieuDatPhongService phieuDatPhongService;
    private final TaiKhoanDTO currentUser;
    private final Stage primaryStage;

    // UI Components
    private VBox roomContainer;
    private ListView<TempBooking> lvTempBookings;
    private ObservableList<TempBooking> tempBookingsList;
    private Set<String> currentSelectedRoomIds = new HashSet<>();

    // Filters
    private DatePicker dpIn, dpOut;
    private ComboBox<String> cbType;
    private Slider sliderPrice;
    private Label lblPriceValue;

    // Bảng màu thiết kế chuẩn (Khớp hệ thống)
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_PRIMARY_DARK = "#1e3a8a";
    private final String COLOR_BG_MAIN = "#f8fafc";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_BORDER = "#e2e8f0";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_SELECTED_BG = "#eff6ff";

    public ChonPhongController(IPhongService phongService, IKhachHangService khachHangService,
                               IPhieuDatPhongService phieuDatPhongService, TaiKhoanDTO currentUser, Stage primaryStage) {
        this.phongService = phongService;
        this.khachHangService = khachHangService;
        this.phieuDatPhongService = phieuDatPhongService;
        this.currentUser = currentUser;
        this.primaryStage = primaryStage;
        this.tempBookingsList = FXCollections.observableArrayList();
    }

    public HBox createView() {
        HBox root = new HBox(0);
        root.setStyle("-fx-background-color: " + COLOR_BG_MAIN + ";");

        // --- 1. SIDEBAR TRÁI: DANH SÁCH CHỜ ---
        VBox leftSidebar = createSidebar();

        // --- 2. KHU VỰC CHÍNH ---
        VBox mainArea = new VBox(25);
        mainArea.setPadding(new Insets(30));
        HBox.setHgrow(mainArea, Priority.ALWAYS);

        // Header khu vực chính
        VBox header = new VBox(5);
        Label lblTitle = new Label("CHỌN PHÒNG NHANH");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 32));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblSub = new Label("Tìm và chọn các phòng đang trống để đưa vào danh sách chờ lập phiếu.");
        lblSub.setFont(Font.font("Segoe UI", 15));
        lblSub.setTextFill(Color.web(COLOR_TEXT_MUTED));
        header.getChildren().addAll(lblTitle, lblSub);

        // Thanh bộ lọc gọn gàng
        VBox filterBar = createFilterBar();

        // Lưới phòng
        roomContainer = new VBox(25);
        roomContainer.setPadding(new Insets(10, 0, 10, 0));

        ScrollPane scrollPane = new ScrollPane(roomContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        mainArea.getChildren().addAll(header, filterBar, scrollPane);
        root.getChildren().addAll(leftSidebar, mainArea);

        loadRooms();
        return root;
    }

    // =========================================================================
    // SIDEBAR: DANH SÁCH PHIẾU TẠM
    // =========================================================================
    private VBox createSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.setPrefWidth(350);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setStyle("-fx-background-color: white; -fx-border-width: 0 1 0 0; -fx-border-color: " + COLOR_BORDER + ";");
        sidebar.setEffect(new DropShadow(15, Color.web("#000000", 0.05)));

        Label lblTitle = new Label("📑 DANH SÁCH CHỜ (" + tempBookingsList.size() + ")");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 18));
        lblTitle.setTextFill(Color.web(COLOR_PRIMARY_DARK));

        tempBookingsList.addListener((javafx.collections.ListChangeListener<TempBooking>) c ->
                lblTitle.setText("📑 DANH SÁCH CHỜ (" + tempBookingsList.size() + ")"));

        lvTempBookings = new ListView<>(tempBookingsList);
        lvTempBookings.setStyle("-fx-background-color: transparent; -fx-control-inner-background: white; -fx-border-color: transparent;");
        VBox.setVgrow(lvTempBookings, Priority.ALWAYS);
        lvTempBookings.setCellFactory(param -> new TempBookingCell());

        VBox btnBox = new VBox(12);

        Button btnProceed = new Button("XÁC NHẬN ĐẶT PHÒNG");
        btnProceed.setMaxWidth(Double.MAX_VALUE);
        btnProceed.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btnProceed.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-padding: 14; -fx-background-radius: 8;");
        btnProceed.setCursor(Cursor.HAND);
        btnProceed.setOnAction(e -> handleConfirmAll());

        Button btnClearAll = new Button("Xóa tất cả danh sách");
        btnClearAll.setMaxWidth(Double.MAX_VALUE);
        btnClearAll.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btnClearAll.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-border-color: #fecaca; -fx-border-radius: 8; -fx-padding: 10;");
        btnClearAll.setCursor(Cursor.HAND);
        btnClearAll.setOnAction(e -> {
            tempBookingsList.clear();
            currentSelectedRoomIds.clear();
            loadRooms();
        });

        btnBox.getChildren().addAll(btnProceed, btnClearAll);
        sidebar.getChildren().addAll(lblTitle, lvTempBookings, btnBox);

        return sidebar;
    }

    // =========================================================================
    // FILTER BAR: BỘ LỌC TÌM KIẾM CẢI TIẾN
    // =========================================================================
    private VBox createFilterBar() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + COLOR_BORDER + "; -fx-border-width: 1;");
        card.setEffect(new DropShadow(10, Color.web("#000000", 0.03)));

        String inputStyle = "-fx-font-size: 14px; -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #cbd5e1; -fx-background-color: #f8fafc; -fx-padding: 8 12;";

        HBox topRow = new HBox(15);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label lblDate = new Label("Thời gian thuê:");
        lblDate.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblDate.setTextFill(Color.web(COLOR_TEXT_MUTED));

        dpIn = new DatePicker(LocalDate.now()); dpIn.setStyle(inputStyle); dpIn.setPrefWidth(140);
        dpIn.setOnAction(e -> loadRooms());
        Label lblDash = new Label("-"); lblDash.setTextFill(Color.web(COLOR_TEXT_MUTED));
        dpOut = new DatePicker(LocalDate.now().plusDays(1)); dpOut.setStyle(inputStyle); dpOut.setPrefWidth(140);
        dpOut.setOnAction(e -> loadRooms());

        Label lblType = new Label("   |   Loại phòng:");
        lblType.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblType.setTextFill(Color.web(COLOR_TEXT_MUTED));

        cbType = new ComboBox<>(FXCollections.observableArrayList("Tất cả loại phòng", "Phòng Đơn", "Phòng Đôi", "Phòng Gia Đình", "Phòng VIP"));
        cbType.setValue("Tất cả loại phòng");
        cbType.setStyle(inputStyle);
        cbType.setPrefWidth(200);
        cbType.setOnAction(e -> loadRooms());

        Region spacer1 = new Region(); HBox.setHgrow(spacer1, Priority.ALWAYS);
        Button btnRefresh = new Button("🔄 Cập nhật");
        btnRefresh.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8;");
        btnRefresh.setCursor(Cursor.HAND);
        btnRefresh.setOnAction(e -> {
            cbType.setValue("Tất cả loại phòng");
            dpIn.setValue(LocalDate.now());
            dpOut.setValue(LocalDate.now().plusDays(1));
            sliderPrice.setValue(5000000);
            loadRooms();
        });

        topRow.getChildren().addAll(lblDate, dpIn, lblDash, dpOut, lblType, cbType, spacer1, btnRefresh);

        HBox botRow = new HBox(15);
        botRow.setAlignment(Pos.CENTER_LEFT);

        Label lblPriceTitle = new Label("Mức giá tối đa:");
        lblPriceTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblPriceTitle.setTextFill(Color.web(COLOR_TEXT_MUTED));

        sliderPrice = new Slider(0, 10000000, 5000000);
        sliderPrice.setPrefWidth(300);

        lblPriceValue = new Label("5.000.000 đ");
        lblPriceValue.setFont(Font.font("Segoe UI", FontWeight.BLACK, 15));
        lblPriceValue.setTextFill(Color.web(COLOR_PRIMARY));
        lblPriceValue.setPrefWidth(120);

        sliderPrice.valueProperty().addListener((obs, oldVal, newVal) -> {
            lblPriceValue.setText(String.format("%,.0f đ", newVal.doubleValue()));
        });
        sliderPrice.setOnMouseReleased(e -> loadRooms());

        Region spacer2 = new Region(); HBox.setHgrow(spacer2, Priority.ALWAYS);

        Button btnAddTemp = new Button("➕ Thêm Phòng Đã Chọn");
        btnAddTemp.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 25; -fx-background-radius: 8;");
        btnAddTemp.setCursor(Cursor.HAND);
        btnAddTemp.setOnAction(e -> handleAddToTemp());

        botRow.getChildren().addAll(lblPriceTitle, sliderPrice, lblPriceValue, spacer2, btnAddTemp);
        card.getChildren().addAll(topRow, botRow);

        return card;
    }

    // =========================================================================
    // LOAD & RENDER PHÒNG TRỐNG
    // =========================================================================
    private void loadRooms() {
        LocalDate in = dpIn.getValue();
        LocalDate out = dpOut.getValue();
        if (in == null || out == null || !out.isAfter(in)) return;

        double maxPrice = sliderPrice.getValue();
        String typeSel = cbType.getValue();

        Task<List<PhongDTO>> task = new Task<>() {
            @Override protected List<PhongDTO> call() {
                return phongService.findAvailableRooms(in, out, 0, maxPrice, null);
            }
        };

        task.setOnSucceeded(e -> {
            List<PhongDTO> rooms = task.getValue();
            if (rooms != null) {
                List<PhongDTO> filtered = rooms.stream()
                        .filter(r -> typeSel.equals("Tất cả loại phòng") || mapMaLoaiToTen(r.getMaLoaiPhong()).equals(typeSel))
                        .filter(r -> getNormalizedStatusKey(r.getTinhTrang()).equals("TRONG"))
                        .collect(Collectors.toList());
                renderRoomGrid(filtered);
            }
        });
        new Thread(task).start();
    }

    private void renderRoomGrid(List<PhongDTO> rooms) {
        Platform.runLater(() -> {
            roomContainer.getChildren().clear();

            if (rooms == null || rooms.isEmpty()) {
                Label lblEmpty = new Label("📭 Không có phòng trống nào phù hợp với bộ lọc.");
                lblEmpty.setFont(Font.font("Segoe UI", 16));
                lblEmpty.setTextFill(Color.web(COLOR_TEXT_MUTED));
                roomContainer.getChildren().add(lblEmpty);
                return;
            }

            Map<String, List<PhongDTO>> byFloor = new TreeMap<>();
            for (PhongDTO r : rooms) {
                String ma = r.getMaPhong();
                String floor = (ma != null && ma.length() > 0) ? ma.substring(0, 1) : "?";
                byFloor.computeIfAbsent(floor, k -> new ArrayList<>()).add(r);
            }

            for (String floor : byFloor.keySet()) {
                VBox floorBox = new VBox(15);

                // --- ÉP KIỂU INLINE CSS CHỮ TẦNG ĐỂ KHÔNG BỊ CSS CHUNG GHI ĐÈ ---
                Label lblFloor = new Label("📍 TẦNG " + floor);
                lblFloor.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: " + COLOR_PRIMARY + ";");

                GridPane grid = new GridPane();
                grid.setHgap(20); grid.setVgap(20);
                for (int i = 0; i < 5; i++) {
                    ColumnConstraints cc = new ColumnConstraints();
                    cc.setPercentWidth(20);
                    grid.getColumnConstraints().add(cc);
                }

                List<PhongDTO> floorRooms = byFloor.get(floor);
                for (int i = 0; i < floorRooms.size(); i++) {
                    if (i >= 10) break;
                    grid.add(createRoomCard(floorRooms.get(i)), i % 5, i / 5);
                }

                floorBox.getChildren().addAll(lblFloor, grid);
                roomContainer.getChildren().add(floorBox);
            }
        });
    }

    // --- THIẾT KẾ CARD PHÒNG BẢO VỆ CHỐNG LỖI MẤT CHỮ ---
    private VBox createRoomCard(PhongDTO r) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setAlignment(Pos.TOP_LEFT);
        card.setCursor(Cursor.HAND);

        // BẢO VỆ DỮ LIỆU: Cắt khoảng trắng, nếu rỗng gán chuỗi báo lỗi để chắc chắn hiển thị
        String rawMa = r.getMaPhong();
        String maPhong = (rawMa != null && !rawMa.trim().isEmpty()) ? rawMa.trim() : "Lỗi Mã";

        String rawLoai = r.getMaLoaiPhong();
        String tenLoai = (rawLoai != null && !rawLoai.trim().isEmpty()) ? mapMaLoaiToTen(rawLoai.trim()) : "Chưa rõ";

        double gia = r.getGiaPhong(); // Primitive mặc định là 0.0

        boolean isSelected = currentSelectedRoomIds.contains(maPhong);

        // THIẾT KẾ MỚI: Viền trái dày màu xanh ngọc (SUCCESS) giống trang Đặt Phòng
        String defaultStyle = "-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; " +
                "-fx-border-color: #e2e8f0 #e2e8f0 #e2e8f0 " + COLOR_SUCCESS + "; -fx-border-width: 1 1 1 6;";

        String selectedStyle = "-fx-background-color: " + COLOR_SELECTED_BG + "; -fx-background-radius: 12; -fx-border-radius: 12; " +
                "-fx-border-color: " + COLOR_PRIMARY + "; -fx-border-width: 2;";

        card.setStyle(isSelected ? selectedStyle : defaultStyle);
        DropShadow ds = new DropShadow(isSelected ? 15 : 8, Color.web("#000000", isSelected ? 0.1 : 0.04));
        card.setEffect(ds);

        // 1. Dòng Mã Phòng
        HBox topBox = new HBox();
        topBox.setAlignment(Pos.CENTER_LEFT);

        Label lblMa = new Label(maPhong);
        // Ép Style nội tuyến (Inline) để chống bị CSS ngoài ghi đè thành màu trắng
        lblMa.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: " + (isSelected ? COLOR_PRIMARY : COLOR_TEXT_MAIN) + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Label lblCheck = new Label(isSelected ? "✔️" : "");
        lblCheck.setStyle("-fx-font-size: 16px; -fx-text-fill: " + COLOR_PRIMARY + ";");

        topBox.getChildren().addAll(lblMa, sp, lblCheck);

        // 2. Dòng Loại Phòng
        Label lblType = new Label(tenLoai.toUpperCase());
        lblType.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_TEXT_MUTED + ";");

        // 3. Dòng Trạng thái (Cố định là TRỐNG)
        Label lblStatus = new Label("TRỐNG");
        lblStatus.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 10px; -fx-font-weight: 900; " +
                "-fx-background-color: " + COLOR_SUCCESS + "15; -fx-text-fill: " + COLOR_SUCCESS + "; -fx-padding: 4 10; -fx-background-radius: 6;");

        // 4. Dòng Giá
        Label lblPrice = new Label(String.format("%,.0f đ", gia));
        lblPrice.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + (isSelected ? COLOR_PRIMARY : COLOR_SUCCESS) + ";");

        card.getChildren().addAll(topBox, lblType, lblStatus, new Region(), lblPrice);
        VBox.setVgrow(card.getChildren().get(3), Priority.ALWAYS); // Đẩy giá xuống đáy

        // --- SỰ KIỆN CLICK CHỌN PHÒNG ---
        card.setOnMouseClicked(e -> {
            if (currentSelectedRoomIds.contains(maPhong)) {
                currentSelectedRoomIds.remove(maPhong);
                card.setStyle(defaultStyle);
                lblCheck.setText("");
                lblMa.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: " + COLOR_TEXT_MAIN + ";");
                lblPrice.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_SUCCESS + ";");
                ds.setRadius(8); ds.setColor(Color.web("#000000", 0.04));
            } else {
                currentSelectedRoomIds.add(maPhong);
                card.setStyle(selectedStyle);
                lblCheck.setText("✔️");
                lblMa.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: " + COLOR_PRIMARY + ";");
                lblPrice.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_PRIMARY + ";");
                ds.setRadius(15); ds.setColor(Color.web("#000000", 0.1));
            }
        });

        // Hiệu ứng Hover
        card.setOnMouseEntered(e -> {
            if (!currentSelectedRoomIds.contains(maPhong)) {
                card.setStyle(defaultStyle + "-fx-background-color: #f8fafc; -fx-translate-y: -3;");
            }
        });
        card.setOnMouseExited(e -> {
            if (!currentSelectedRoomIds.contains(maPhong)) {
                card.setStyle(defaultStyle);
            }
        });

        return card;
    }

    // =========================================================================
    // HỆ THỐNG MAPPING VÀ HELPER
    // =========================================================================

    private String getNormalizedStatusKey(String rawStatus) {
        if (rawStatus == null || rawStatus.trim().isEmpty()) return "BAO_TRI";
        String normalized = removeAccents(rawStatus).trim().toUpperCase();

        if (normalized.contains("BAO TRI") || normalized.contains("HONG")) return "BAO_TRI";
        if (normalized.contains("TRONG")) return "TRONG";
        if (normalized.contains("DAT")) return "DA_DAT";
        if (normalized.contains("DANG") || normalized.contains("SU DUNG") || normalized.equals("O")) return "DANG_O";

        return "BAO_TRI";
    }

    private String removeAccents(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(normalized)
                .replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }

    private String mapMaLoaiToTen(String maLoai) {
        if (maLoai == null) return "Tiêu Chuẩn";
        return switch (maLoai.toUpperCase()) {
            case "DON" -> "Phòng Đơn";
            case "DOI" -> "Phòng Đôi";
            case "GIADINH" -> "Phòng Gia Đình";
            case "VIP" -> "Phòng VIP";
            default -> "Phòng " + maLoai;
        };
    }

    private void handleAddToTemp() {
        if (currentSelectedRoomIds.isEmpty()) {
            showAlert("Chưa chọn phòng", "Vui lòng click chọn ít nhất một phòng trống trước khi thêm vào danh sách!");
            return;
        }

        LocalDate in = dpIn.getValue();
        LocalDate out = dpOut.getValue();

        List<String> rooms = new ArrayList<>(currentSelectedRoomIds);
        String id = "TẠM-" + (System.currentTimeMillis() % 10000);
        tempBookingsList.add(new TempBooking(id, in, out, rooms));

        currentSelectedRoomIds.clear();
        loadRooms(); // Render lại để xóa các viền xanh
    }

    private void handleConfirmAll() {
        if (tempBookingsList.isEmpty()) {
            showAlert("Danh sách trống", "Không có phiếu nào trong danh sách chờ để xác nhận.");
            return;
        }
        showAlert("Thành công", "Chuyển dữ liệu thành công. Vui lòng nhập thông tin khách hàng...");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // --- LỚP INNER CHO GIAO DIỆN DANH SÁCH CHỜ ---
    public static class TempBooking {
        String id; LocalDate from, to; List<String> roomIds;
        TempBooking(String id, LocalDate f, LocalDate t, List<String> r) { this.id = id; this.from = f; this.to = t; this.roomIds = r; }
    }

    private class TempBookingCell extends ListCell<TempBooking> {
        @Override protected void updateItem(TempBooking item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null); setText(null);
                setStyle("-fx-background-color: transparent;");
            } else {
                VBox box = new VBox(8);
                box.setPadding(new Insets(15));
                box.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 12; -fx-border-color: #e2e8f0; -fx-border-radius: 12;");

                HBox top = new HBox();
                Label lblId = new Label(item.id);
                lblId.setFont(Font.font("Segoe UI", FontWeight.BLACK, 15));
                lblId.setTextFill(Color.web(COLOR_PRIMARY));

                Button btnXoa = new Button("✖");
                btnXoa.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #ef4444; -fx-padding: 3 8; -fx-background-radius: 5; -fx-cursor: hand;");
                btnXoa.setOnAction(e -> tempBookingsList.remove(item));

                Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
                top.getChildren().addAll(lblId, sp, btnXoa);

                Label lblDate = new Label("🕒 " + item.from.format(DateTimeFormatter.ofPattern("dd/MM")) + " ⮕ " + item.to.format(DateTimeFormatter.ofPattern("dd/MM")));
                lblDate.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
                lblDate.setTextFill(Color.web(COLOR_TEXT_MAIN));

                Label lblRooms = new Label("🔑 Phòng: " + String.join(", ", item.roomIds));
                lblRooms.setFont(Font.font("Segoe UI", 13));
                lblRooms.setTextFill(Color.web(COLOR_TEXT_MUTED));

                box.getChildren().addAll(top, lblDate, lblRooms);
                setGraphic(box);
                setStyle("-fx-background-color: transparent; -fx-padding: 0 0 12 0;");
            }
        }
    }
}