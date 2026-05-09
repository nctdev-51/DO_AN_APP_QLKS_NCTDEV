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

import java.io.Serializable;
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
    private List<PhongDTO> currentAvailableRoomsCache = new ArrayList<>(); // Lưu tạm để filter nhanh

    // Filters
    private DatePicker dpIn, dpOut;
    private ComboBox<String> cbType;
    private ComboBox<String> cbSoNguoi;
    private Slider sliderPrice;
    private Label lblPriceValue;
    private Label lblTotalHotelCapacity;
    private Label lblCurrentSelectedCapacity;
    private TextField txtSearch;

    // Bảng màu thiết kế chuẩn
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

        // 1. Tải dữ liệu cũ lên ngay khi khởi tạo
        loadDataFromFile();

        // 2. Lắng nghe thay đổi: Bất cứ khi nào thêm hoặc xóa, hệ thống sẽ tự động lưu vào file
        this.tempBookingsList.addListener((javafx.collections.ListChangeListener<TempBooking>) c -> {
            saveDataToFile();
        });
    }

    public HBox createView() {
        HBox root = new HBox(0);
        root.setStyle("-fx-background-color: " + COLOR_BG_MAIN + ";");

        VBox leftSidebar = createSidebar();
        VBox mainArea = new VBox(25);
        mainArea.setPadding(new Insets(30));
        HBox.setHgrow(mainArea, Priority.ALWAYS);

        VBox header = new VBox(5);
        Label lblTitle = new Label("CHỌN PHÒNG NHANH");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 28));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));
        Label lblSub = new Label("Tìm và chọn các phòng trống thực sự trong khoảng thời gian đã chọn.");
        lblSub.setFont(Font.font("Segoe UI", 14));
        lblSub.setTextFill(Color.web(COLOR_TEXT_MUTED));
        header.getChildren().addAll(lblTitle, lblSub);

        VBox filterBar = createFilterBar();

        roomContainer = new VBox(25);
        roomContainer.setPadding(new Insets(10, 0, 10, 0));
        ScrollPane scrollPane = new ScrollPane(roomContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        mainArea.getChildren().addAll(header, filterBar, scrollPane);
        root.getChildren().addAll(leftSidebar, mainArea);

        loadRooms();
        setupDateValidation();
        return root;
    }

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

        // --- BỘ CHỈ SỐ SỨC CHỨA ---
        VBox capacityBox = new VBox(8);
        capacityBox.setPadding(new Insets(15));
        capacityBox.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 10; -fx-border-color: #cbd5e1; -fx-border-radius: 10;");

        lblTotalHotelCapacity = new Label("🏠 KS còn trống: 0 chỗ");
        lblTotalHotelCapacity.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblTotalHotelCapacity.setTextFill(Color.web(COLOR_TEXT_MAIN));

        lblCurrentSelectedCapacity = new Label("👥 Đã chọn cho: 0 người");
        lblCurrentSelectedCapacity.setFont(Font.font("Segoe UI", FontWeight.BLACK, 14));
        lblCurrentSelectedCapacity.setTextFill(Color.web(COLOR_PRIMARY));

        capacityBox.getChildren().addAll(lblTotalHotelCapacity, lblCurrentSelectedCapacity);

        VBox btnBox = new VBox(12);
        Button btnProceed = new Button("XÁC NHẬN ĐẶT PHÒNG");
        btnProceed.setMaxWidth(Double.MAX_VALUE);
        btnProceed.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btnProceed.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-padding: 14; -fx-background-radius: 8; -fx-cursor: hand;");
        btnProceed.setOnAction(e -> handleConfirmAll());

        Button btnClearAll = new Button("Xóa tất cả danh sách");
        btnClearAll.setMaxWidth(Double.MAX_VALUE);
        btnClearAll.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-border-color: #fecaca; -fx-border-radius: 8; -fx-padding: 10; -fx-cursor: hand;");
        btnClearAll.setOnAction(e -> {
            tempBookingsList.clear();
            currentSelectedRoomIds.clear();
            loadRooms();
        });

        btnBox.getChildren().addAll(capacityBox, btnProceed, btnClearAll);
        sidebar.getChildren().addAll(lblTitle, lvTempBookings, btnBox);

        return sidebar;
    }

    private VBox createFilterBar() {
        VBox card = new VBox();
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + COLOR_BORDER + "; -fx-border-width: 1;");
        card.setEffect(new DropShadow(10, Color.web("#000000", 0.03)));

        String inputStyle = "-fx-font-size: 13px; -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #cbd5e1; -fx-background-color: #f8fafc; -fx-padding: 8 10; -fx-pref-height: 38px;";
        String labelStyle = "-fx-font-weight: bold; -fx-text-fill: " + COLOR_TEXT_MUTED + "; -fx-font-size: 12px;";

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(15);
        ColumnConstraints c0 = new ColumnConstraints(); c0.setPercentWidth(28);
        ColumnConstraints c1 = new ColumnConstraints(); c1.setPercentWidth(22);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setPercentWidth(25);
        ColumnConstraints c3 = new ColumnConstraints(); c3.setPercentWidth(25);
        grid.getColumnConstraints().addAll(c0, c1, c2, c3);

        VBox bSearch = new VBox(5); bSearch.getChildren().addAll(new Label("TỪ KHÓA:"), txtSearch = new TextField());
        ((Label)bSearch.getChildren().get(0)).setStyle(labelStyle);
        txtSearch.setPromptText("🔍 Nhập số phòng hoặc tên..."); txtSearch.setStyle(inputStyle);
        txtSearch.textProperty().addListener((o, old, nw) -> applyFilters());

        VBox bLoai = new VBox(5); bLoai.getChildren().addAll(new Label("LOẠI PHÒNG:"), cbType = new ComboBox<>(FXCollections.observableArrayList("Tất cả loại phòng", "Phòng Đơn", "Phòng Đôi", "Phòng Gia Đình", "Phòng VIP")));
        ((Label)bLoai.getChildren().get(0)).setStyle(labelStyle);
        cbType.setValue("Tất cả loại phòng"); cbType.setStyle(inputStyle); cbType.setMaxWidth(Double.MAX_VALUE);
        cbType.setOnAction(e -> applyFilters());

        VBox bSucChua = new VBox(5); bSucChua.getChildren().addAll(new Label("SỨC CHỨA:"), cbSoNguoi = new ComboBox<>(FXCollections.observableArrayList("Sức chứa bất kỳ", "1 Người", "2 Người", "3 Người", "4 Người trở lên")));
        ((Label)bSucChua.getChildren().get(0)).setStyle(labelStyle);
        cbSoNguoi.setValue("Sức chứa bất kỳ"); cbSoNguoi.setStyle(inputStyle); cbSoNguoi.setMaxWidth(Double.MAX_VALUE);
        cbSoNguoi.setOnAction(e -> applyFilters());

        VBox bAdd = new VBox(5); bAdd.setAlignment(Pos.BOTTOM_CENTER);
        Button btnAdd = new Button("➕ THÊM ĐÃ CHỌN");
        btnAdd.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-pref-height: 38px; -fx-cursor: hand;");
        btnAdd.setMaxWidth(Double.MAX_VALUE); btnAdd.setOnAction(e -> handleAddToTemp());
        bAdd.getChildren().add(btnAdd);

        VBox bNgay = new VBox(5); bNgay.getChildren().addAll(new Label("THỜI GIAN THUÊ:"), new HBox(8, dpIn = new DatePicker(LocalDate.now()), new Label("-"), dpOut = new DatePicker(LocalDate.now().plusDays(1))));
        ((Label)bNgay.getChildren().get(0)).setStyle(labelStyle);
        dpIn.setStyle(inputStyle); dpOut.setStyle(inputStyle);
        dpIn.valueProperty().addListener((o, v, n) -> loadRooms());
        dpOut.valueProperty().addListener((o, v, n) -> loadRooms());
        GridPane.setColumnSpan(bNgay, 2);

        VBox bGia = new VBox(5); bGia.getChildren().addAll(new Label("GIÁ TỐI ĐA:"), new HBox(10, sliderPrice = new Slider(0, 10000000, 5000000), lblPriceValue = new Label("5.000.000 đ")));
        ((Label)bGia.getChildren().get(0)).setStyle(labelStyle);
        lblPriceValue.setStyle("-fx-font-weight: bold; -fx-text-fill: " + COLOR_PRIMARY);
        sliderPrice.valueProperty().addListener((obs, old, nw) -> {
            lblPriceValue.setText(String.format("%,.0f đ", nw.doubleValue()));
            applyFilters();
        });

        VBox bRes = new VBox(5); bRes.setAlignment(Pos.BOTTOM_RIGHT);
        Button btnRes = new Button("🔄 LÀM MỚI");
        btnRes.setStyle("-fx-background-color: #e2e8f0; -fx-font-weight: bold; -fx-background-radius: 6; -fx-pref-height: 38px;");
        btnRes.setMaxWidth(Double.MAX_VALUE);
        btnRes.setOnAction(e -> {
            txtSearch.clear(); cbType.setValue("Tất cả loại phòng"); cbSoNguoi.setValue("Sức chứa bất kỳ");
            dpIn.setValue(LocalDate.now()); dpOut.setValue(LocalDate.now().plusDays(1));
            sliderPrice.setValue(10000000); loadRooms();
        });
        bRes.getChildren().add(btnRes);

        grid.add(bSearch, 0, 0); grid.add(bLoai, 1, 0); grid.add(bSucChua, 2, 0); grid.add(bAdd, 3, 0);
        grid.add(bNgay, 0, 1); grid.add(bGia, 2, 1); grid.add(bRes, 3, 1);

        card.getChildren().add(grid);
        return card;
    }

    // 👉 Hàm Load từ DB (Chỉ gọi khi đổi ngày)
    private void loadRooms() {
        LocalDate in = dpIn.getValue(); LocalDate out = dpOut.getValue();
        if (in == null || out == null || !out.isAfter(in)) return;

        Task<List<PhongDTO>> task = new Task<>() {
            @Override protected List<PhongDTO> call() {
                return phongService.findAvailableRooms(in, out, 0, Double.MAX_VALUE, null);
            }
        };

        task.setOnSucceeded(e -> {
            currentAvailableRoomsCache = task.getValue();
            applyFilters(); // Sau khi load DB xong thì áp dụng filter UI
        });
        new Thread(task).start();
    }

    // 👉 Hàm Lọc từ bộ nhớ (Gọi khi gõ phím, chọn combo, kéo slider)
    // =========================================================================
    // 👉 FIX LỖI 2: CHỈ ẨN PHÒNG NẾU NGÀY CHỌN TRÙNG VỚI NGÀY TRONG WAITLIST
    // =========================================================================
    private void applyFilters() {
        if (currentAvailableRoomsCache == null) return;

        LocalDate curIn = dpIn.getValue();
        LocalDate curOut = dpOut.getValue();

        // Chỉ lấy ID các phòng đang nằm trong Waitlist MÀ CÓ NGÀY GIAO THOA với ngày đang lọc
        Set<String> conflictRoomIds = new HashSet<>();
        for (TempBooking tb : tempBookingsList) {
            // Logic Overlap: (Đợt.Nhan < Lọc.Tra) AND (Đợt.Tra > Lọc.Nhan)
            boolean isOverlap = tb.from.isBefore(curOut) && tb.to.isAfter(curIn);
            if (isOverlap) {
                conflictRoomIds.addAll(tb.roomIds);
            }
        }

        final double maxP = sliderPrice.getValue();
        final String typeS = cbType.getValue();
        final String kw = txtSearch.getText() != null ? txtSearch.getText().toLowerCase().trim() : "";

        List<PhongDTO> filtered = currentAvailableRoomsCache.stream()
                .filter(r -> !conflictRoomIds.contains(r.getMaPhong())) // Chỉ ẩn nếu bị trùng lịch trong Waitlist
                .filter(r -> kw.isEmpty() || r.getMaPhong().toLowerCase().contains(kw) || (r.getTenPhong() != null && r.getTenPhong().toLowerCase().contains(kw)))
                .filter(r -> typeS.equals("Tất cả loại phòng") || mapMaLoaiToTen(r.getMaLoaiPhong()).equals(typeS))
                .filter(r -> r.getGiaPhong() <= maxP)
                .collect(Collectors.toList());

        renderRoomGrid(filtered);
        updateCapacityStatistics(currentAvailableRoomsCache);
    }

    private void renderRoomGrid(List<PhongDTO> rooms) {
        Platform.runLater(() -> {
            roomContainer.getChildren().clear();
            if (rooms.isEmpty()) {
                roomContainer.getChildren().add(new Label("📭 Không có phòng nào phù hợp."));
                return;
            }
            Map<String, List<PhongDTO>> byFloor = new TreeMap<>();
            for (PhongDTO r : rooms) {
                String floor = (r.getMaPhong() != null) ? r.getMaPhong().substring(0, 1) : "?";
                byFloor.computeIfAbsent(floor, k -> new ArrayList<>()).add(r);
            }
            for (String floor : byFloor.keySet()) {
                VBox fBox = new VBox(15);
                Label lblF = new Label("📍 TẦNG " + floor); lblF.setStyle("-fx-font-weight: 900; -fx-font-size: 18; -fx-text-fill: " + COLOR_PRIMARY);
                FlowPane flow = new FlowPane(20, 20);
                for (PhongDTO room : byFloor.get(floor)) { flow.getChildren().add(createRoomCard(room)); }
                fBox.getChildren().addAll(lblF, flow);
                roomContainer.getChildren().add(fBox);
            }
        });
    }

    private VBox createRoomCard(PhongDTO r) {
        String ma = r.getMaPhong();
        boolean isS = currentSelectedRoomIds.contains(ma);
        VBox card = new VBox(10); card.setPadding(new Insets(18)); card.setPrefWidth(220); card.setCursor(Cursor.HAND);

        String borderCol = isS ? COLOR_PRIMARY : COLOR_SUCCESS;
        card.setStyle("-fx-background-color: " + (isS ? COLOR_SELECTED_BG : "white") + "; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-width: " + (isS?2:1) + " 1 1 6; -fx-border-color: #e2e8f0 #e2e8f0 #e2e8f0 " + borderCol + ";");

        Label lMa = new Label(ma); lMa.setStyle("-fx-font-size: 22; -fx-font-weight: 900; -fx-text-fill: " + (isS?COLOR_PRIMARY:COLOR_TEXT_MAIN));
        Label lLoai = new Label(mapMaLoaiToTen(r.getMaLoaiPhong()).toUpperCase()); lLoai.setStyle("-fx-font-size: 11; -fx-text-fill: " + COLOR_TEXT_MUTED);
        Label lP = new Label(String.format("%,.0f đ", r.getGiaPhong())); lP.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: " + (isS?COLOR_PRIMARY:COLOR_SUCCESS));

        HBox top = new HBox(lMa, new Region(), new Label(isS ? "✔️" : "")); HBox.setHgrow(top.getChildren().get(1), Priority.ALWAYS);
        card.getChildren().addAll(top, lLoai, new Region(), lP); VBox.setVgrow(card.getChildren().get(2), Priority.ALWAYS);

        card.setOnMouseClicked(e -> {
            if (currentSelectedRoomIds.contains(ma)) currentSelectedRoomIds.remove(ma);
            else currentSelectedRoomIds.add(ma);
            applyFilters(); // Vẽ lại tích xanh và cập nhật số người
        });
        return card;
    }

    private void updateCapacityStatistics(List<PhongDTO> availableRooms) {
        int totalSlots = availableRooms.stream().mapToInt(r -> getSucChuaTuLoaiPhong(r.getMaLoaiPhong())).sum();
        int selectedSlots = 0;
        for (PhongDTO r : availableRooms) {
            if (currentSelectedRoomIds.contains(r.getMaPhong())) {
                selectedSlots += getSucChuaTuLoaiPhong(r.getMaLoaiPhong());
            }
        }

        final int finalTotal = totalSlots;
        final int finalSelected = selectedSlots;

        Platform.runLater(() -> {
            lblTotalHotelCapacity.setText("🏠 KS còn trống: " + finalTotal + " chỗ");
            lblCurrentSelectedCapacity.setText("👥 Đã chọn cho: " + finalSelected + " người");

            // Cảnh báo nếu khách đoàn đi quá đông mà KS không đủ chỗ
            String reqStr = cbSoNguoi.getValue();
            int required = 0;
            if (reqStr.contains("1")) required = 1; else if (reqStr.contains("2")) required = 2; else if (reqStr.contains("3")) required = 3; else if (reqStr.contains("4")) required = 4;

            if (finalTotal < required && required > 0) {
                lblTotalHotelCapacity.setText("⚠️ KHÔNG ĐỦ CHỖ (Thiếu " + (required - finalTotal) + ")");
                lblTotalHotelCapacity.setTextFill(Color.RED);
            } else {
                lblTotalHotelCapacity.setTextFill(Color.web(COLOR_TEXT_MAIN));
            }
        });
    }

    private int getSucChuaTuLoaiPhong(String maLoai) {
        if (maLoai == null) return 2;
        return switch (maLoai.toUpperCase()) {
            case "DON" -> 1; case "DOI", "VIP" -> 2; case "GIADINH" -> 4; default -> 2;
        };
    }

    private String mapMaLoaiToTen(String maLoai) {
        if (maLoai == null) return "Tiêu Chuẩn";
        return switch (maLoai.toUpperCase()) {
            case "DON" -> "Phòng Đơn"; case "DOI" -> "Phòng Đôi"; case "GIADINH" -> "Phòng Gia Đình"; case "VIP" -> "Phòng VIP"; default -> "Phòng " + maLoai;
        };
    }

    private void handleAddToTemp() {
        if (currentSelectedRoomIds.isEmpty()) {
            showAlert("Thông báo", "Vui lòng chọn ít nhất một phòng trước khi thêm!");
            return;
        }

        // Lấy ngày hiện tại từ bộ lọc
        LocalDate in = dpIn.getValue();
        LocalDate out = dpOut.getValue();

        // 👉 QUAN TRỌNG: Phải tạo một List mới để lưu, không dùng tham chiếu trực tiếp
        List<String> selectedRoomsCopy = new ArrayList<>(currentSelectedRoomIds);
        String batchId = "ĐỢT-" + (System.currentTimeMillis() % 10000);

        // Thêm vào danh sách chờ (ObservableList sẽ tự cập nhật ListView)
        tempBookingsList.add(new TempBooking(batchId, in, out, selectedRoomsCopy));

        // Xóa dấu tích ở các phòng vừa chọn để chọn đợt tiếp theo
        currentSelectedRoomIds.clear();
        applyFilters(); // Vẽ lại lưới phòng

        System.out.println("✅ Đã lưu đợt phòng vào danh sách chờ.");
    }

    // =========================================================================
    // 👉 FIX LỖI 1: CHỈ THANH TOÁN PHIẾU ĐANG ĐƯỢC CHỌN (KHÔNG GOM PHIẾU)
    // =========================================================================
    private void handleConfirmAll() {
        // Lấy phiếu cụ thể mà Tú đang nhấn chọn (Select) trong ListView
        TempBooking selectedBatch = lvTempBookings.getSelectionModel().getSelectedItem();

        if (selectedBatch == null) {
            showAlert("Thông báo", "Vui lòng click chọn 1 đợt trong danh sách chờ để tạo phiếu!");
            return;
        }

        try {
            iuh.fit.core.dto.NhanVienDTO nv = new iuh.fit.core.dto.NhanVienDTO();
            if (currentUser != null) nv.setMaNhanVien(currentUser.getMaNhanVien());
            else nv.setMaNhanVien("ADMIN");

            // Mở form Đặt phòng CHỈ VỚI thông tin của đợt đang chọn
            DatPhongController ctrl = new DatPhongController(
                    selectedBatch.roomIds,
                    nv,
                    selectedBatch.from,
                    selectedBatch.to,
                    khachHangService,
                    phongService,
                    phieuDatPhongService,
                    null,
                    () -> {
                        Platform.runLater(() -> {
                            tempBookingsList.remove(selectedBatch); // Xóa đợt đã thanh toán thành công
                            applyFilters(); // Cập nhật lại lưới phòng
                            showAlert("Thành công", "Đã lập phiếu đặt phòng thành công!");
                        });
                    }
            );
            ctrl.showDialog(primaryStage);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showAlert(String t, String c) { Alert a = new Alert(Alert.AlertType.INFORMATION, c); a.setTitle(t); a.setHeaderText(null); a.showAndWait(); }

    private void setupDateValidation() {
        dpIn.setDayCellFactory(p -> new DateCell() { @Override public void updateItem(LocalDate d, boolean e) { super.updateItem(d, e); setDisable(e || d.isBefore(LocalDate.now())); } });
        dpOut.setDayCellFactory(p -> new DateCell() { @Override public void updateItem(LocalDate d, boolean e) { super.updateItem(d, e); setDisable(e || d.isBefore(dpIn.getValue().plusDays(1))); } });
    }

    // Thêm import này ở đầu file


    public static class TempBooking implements Serializable {
        private static final long serialVersionUID = 1L; // Đảm bảo tính tương thích dữ liệu
        String id;
        LocalDate from, to;
        List<String> roomIds;

        TempBooking(String id, LocalDate f, LocalDate t, List<String> r) {
            this.id = id; this.from = f; this.to = t; this.roomIds = r;
        }
    }

    private class TempBookingCell extends ListCell<TempBooking> {
        private final VBox container = new VBox(5);
        private final Label lblId = new Label();
        private final Label lblInfo = new Label();
        private final Label lblRooms = new Label();
        private final Button btnXoa = new Button("✖");
        private final HBox header = new HBox();

        public TempBookingCell() {
            container.setPadding(new Insets(12));
            lblId.setStyle("-fx-font-weight: bold; -fx-text-fill: " + COLOR_PRIMARY + "; -fx-font-size: 14px;");
            lblInfo.setStyle("-fx-text-fill: " + COLOR_TEXT_MAIN + "; -fx-font-size: 12px;");
            lblRooms.setStyle("-fx-text-fill: " + COLOR_TEXT_MUTED + "; -fx-font-size: 12px;");
            lblRooms.setWrapText(true);
            btnXoa.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #ef4444; -fx-cursor: hand; -fx-background-radius: 5;");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            header.getChildren().addAll(lblId, spacer, btnXoa);
            header.setAlignment(Pos.CENTER_LEFT);
            container.getChildren().addAll(header, lblInfo, lblRooms);

            this.setOnMouseClicked(e -> {
                if (getItem() != null && !e.getTarget().equals(btnXoa)) {
                    dpIn.setValue(getItem().from);
                    dpOut.setValue(getItem().to);
                    loadRooms();
                }
            });
        }

        @Override
        protected void updateItem(TempBooking item, boolean empty) {
            super.updateItem(item, empty);
            setStyle("-fx-background-color: transparent; -fx-padding: 0 0 10 0;");

            if (empty || item == null) {
                setGraphic(null);
            } else {
                lblId.setText(item.id);
                lblInfo.setText("🕒 " + item.from.format(DateTimeFormatter.ofPattern("dd/MM")) +
                        " ⮕ " + item.to.format(DateTimeFormatter.ofPattern("dd/MM")));
                lblRooms.setText("🔑 " + String.join(", ", item.roomIds));
                btnXoa.setOnAction(e -> {
                    tempBookingsList.remove(item);
                    applyFilters(); // 👉 Cần gọi lại để các phòng vừa xóa hiện lại lên lưới
                });

                // 👉 XỬ LÝ TÔ ĐẬM KHUNG KHI ĐƯỢC CHỌN
                if (isSelected()) {
                    container.setStyle("-fx-background-color: #eff6ff; -fx-border-color: " + COLOR_PRIMARY +
                            "; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8;");
                } else {
                    container.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; " +
                            "-fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");
                }

                setGraphic(container);
            }
        }
    }

    private final String DATA_FILE = "waitlist_data.dat";

    private void saveDataToFile() {
        try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(DATA_FILE))) {
            // Chuyển ObservableList thành ArrayList thường để lưu
            oos.writeObject(new ArrayList<>(tempBookingsList));
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu danh sách chờ: " + e.getMessage());
        }
    }

    private void loadDataFromFile() {
        java.io.File file = new java.io.File(DATA_FILE);
        if (!file.exists()) return;

        try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.FileInputStream(DATA_FILE))) {
            List<TempBooking> data = (List<TempBooking>) ois.readObject();
            if (data != null) {
                tempBookingsList.setAll(data);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải danh sách chờ: " + e.getMessage());
        }
    }
}