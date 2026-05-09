package iuh.fit.presentation.controller;

import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
    private ComboBox<String> cbSoNguoi; // 👉 Thêm biến chọn số lượng người

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
        VBox card = new VBox();
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + COLOR_BORDER + "; -fx-border-width: 1;");

        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000000", 0.04));
        ds.setRadius(10); ds.setOffsetY(4);
        card.setEffect(ds);

        String inputStyle = "-fx-font-size: 13px; -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #cbd5e1; -fx-background-color: #f8fafc; -fx-padding: 8 10; -fx-pref-height: 40px;";
        String labelStyle = "-fx-font-weight: bold; -fx-text-fill: " + COLOR_TEXT_MUTED + "; -fx-font-size: 12px;";

        // 👉 ĐÃ FIX: Sử dụng GridPane để chia cột đều đặn, không bị che
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);

        // Chia làm 4 cột cân bằng nhau
        for(int i=0; i<4; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(25);
            grid.getColumnConstraints().add(col);
        }

        // --- CỘT 1: TÌM KIẾM ---
        VBox boxSearch = new VBox(5);
        Label lblSearch = new Label("TỪ KHÓA:"); lblSearch.setStyle(labelStyle);
        txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Số phòng, tên...");
        txtSearch.setStyle(inputStyle);
        txtSearch.setMaxWidth(Double.MAX_VALUE);
        txtSearch.textProperty().addListener((obs, old, nw) -> filterRooms());
        boxSearch.getChildren().addAll(lblSearch, txtSearch);

        // --- CỘT 2: LOẠI PHÒNG ---
        VBox boxLoai = new VBox(5);
        Label lblLoai = new Label("LOẠI PHÒNG:"); lblLoai.setStyle(labelStyle);
        cbLoai = new ComboBox<>(FXCollections.observableArrayList("Tất cả loại phòng", "Phòng Đơn", "Phòng Đôi", "Phòng Gia Đình", "Phòng VIP"));
        cbLoai.setValue("Tất cả loại phòng");
        cbLoai.setStyle(inputStyle);
        cbLoai.setMaxWidth(Double.MAX_VALUE);
        cbLoai.setOnAction(e -> filterRooms());
        boxLoai.getChildren().addAll(lblLoai, cbLoai);

        // --- CỘT 3: TRẠNG THÁI ---
        VBox boxTrangThai = new VBox(5);
        Label lblTrangThai = new Label("TRẠNG THÁI:"); lblTrangThai.setStyle(labelStyle);
        cbTrangThai = new ComboBox<>(FXCollections.observableArrayList("Tất cả trạng thái", "Trống", "Đã Đặt", "Đang ở", "Bảo Trì"));
        cbTrangThai.setValue("Tất cả trạng thái");
        cbTrangThai.setStyle(inputStyle);
        cbTrangThai.setMaxWidth(Double.MAX_VALUE);
        cbTrangThai.setOnAction(e -> filterRooms());
        boxTrangThai.getChildren().addAll(lblTrangThai, cbTrangThai);

        // --- CỘT 4: SỨC CHỨA ---
        VBox boxSucChua = new VBox(5);
        Label lblSucChua = new Label("SỨC CHỨA:"); lblSucChua.setStyle(labelStyle);
        cbSoNguoi = new ComboBox<>(FXCollections.observableArrayList("Sức chứa bất kỳ", "1 Người", "2 Người", "3 Người", "4 Người trở lên"));
        cbSoNguoi.setValue("Sức chứa bất kỳ");
        cbSoNguoi.setStyle(inputStyle);
        cbSoNguoi.setMaxWidth(Double.MAX_VALUE);
        cbSoNguoi.setOnAction(e -> filterRooms());
        boxSucChua.getChildren().addAll(lblSucChua, cbSoNguoi);

        // --- HÀNG 2 - CỘT 1,2: NGÀY NHẬN TRẢ ---
        VBox boxNgay = new VBox(5);
        Label lblNgay = new Label("THỜI GIAN LƯU TRÚ:"); lblNgay.setStyle(labelStyle);
        HBox dateRow = new HBox(10);

        dpIn = new DatePicker(LocalDate.now());
        dpIn.setPromptText("Nhận");
        dpIn.setStyle(inputStyle);
        HBox.setHgrow(dpIn, Priority.ALWAYS);
        dpIn.setMaxWidth(Double.MAX_VALUE);

        Label lblDash = new Label("-");
        lblDash.setStyle("-fx-font-weight: bold; -fx-padding: 8 0 0 0;");

        dpOut = new DatePicker(LocalDate.now().plusDays(1));
        dpOut.setPromptText("Trả");
        dpOut.setStyle(inputStyle);
        HBox.setHgrow(dpOut, Priority.ALWAYS);
        dpOut.setMaxWidth(Double.MAX_VALUE);

        // 👉 ĐÃ FIX: VALIDATE NGÀY NHẬN
        dpIn.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                // 1. Ngày nhận không được lùi về trước hôm nay
                if (newDate.isBefore(LocalDate.now())) {
                    Platform.runLater(() -> dpIn.setValue(LocalDate.now()));
                }
                // 2. Nếu ngày nhận bị đẩy qua ngày trả, tự đẩy ngày trả lên +1 ngày
                else if (dpOut.getValue() != null && !newDate.isBefore(dpOut.getValue())) {
                    Platform.runLater(() -> dpOut.setValue(newDate.plusDays(1)));
                } else {
                    filterRooms(); // Hợp lệ thì gọi hàm lọc phòng
                }
            }
        });

        // 👉 ĐÃ FIX: VALIDATE NGÀY TRẢ
        dpOut.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                // Ngày trả phải LỚN HƠN ngày nhận ít nhất 1 ngày
                if (dpIn.getValue() != null && !newDate.isAfter(dpIn.getValue())) {
                    Platform.runLater(() -> dpOut.setValue(dpIn.getValue().plusDays(1)));
                } else {
                    filterRooms(); // Hợp lệ thì gọi hàm lọc phòng
                }
            }
        });

        dateRow.getChildren().addAll(dpIn, lblDash, dpOut);
        boxNgay.getChildren().addAll(lblNgay, dateRow);

        // Hàng 2 chiếm 2 cột
        GridPane.setColumnSpan(boxNgay, 2);

        // --- HÀNG 2 - CỘT 3: GIÁ TỐI ĐA ---
        VBox boxGia = new VBox(5);
        Label lblGia = new Label("GIÁ TỐI ĐA:"); lblGia.setStyle(labelStyle);
        HBox priceRow = new HBox(10);
        priceRow.setAlignment(Pos.CENTER_LEFT);
        sliderPrice = new Slider(0, 10000000, 10000000); sliderPrice.setPrefWidth(120); sliderPrice.setStyle("-fx-cursor: hand;");
        txtGiaMax = new TextField("10,000,000"); txtGiaMax.setStyle(inputStyle + " -fx-font-weight: bold; -fx-text-fill: " + COLOR_PRIMARY + "; -fx-padding: 2;"); txtGiaMax.setPrefWidth(90);
        priceRow.getChildren().addAll(sliderPrice, txtGiaMax);
        boxGia.getChildren().addAll(lblGia, priceRow);

        // Sync Slider & Text
        sliderPrice.valueProperty().addListener((obs, oldV, newV) -> {
            if (!txtGiaMax.isFocused()) { txtGiaMax.setText(String.format("%,.0f", newV.doubleValue())); filterRooms(); }
        });
        txtGiaMax.setOnAction(e -> updatePriceAction());
        txtGiaMax.focusedProperty().addListener((obs, oldV, newV) -> { if (!newV) updatePriceAction(); });

        // --- HÀNG 2 - CỘT 4: NÚT LÀM MỚI ---
        VBox boxBtn = new VBox(5);
        boxBtn.setAlignment(Pos.BOTTOM_RIGHT);
        Button btnClear = new Button("🔄 LÀM MỚI BỘ LỌC");
        btnClear.setCursor(Cursor.HAND);
        btnClear.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 6; -fx-pref-height: 40px;");
        btnClear.setMaxWidth(Double.MAX_VALUE);
        btnClear.setOnAction(e -> {
            resetFilters();
            cbSoNguoi.setValue("Sức chứa bất kỳ");
        });
        boxBtn.getChildren().addAll(new Label(" "), btnClear); // Thêm label rỗng để nút tụt xuống bằng với các field kia

        // Đưa các Box vào Grid
        grid.add(boxSearch, 0, 0);
        grid.add(boxLoai, 1, 0);
        grid.add(boxTrangThai, 2, 0);
        grid.add(boxSucChua, 3, 0);

        grid.add(boxNgay, 0, 1); // Chiếm cột 0 và 1
        grid.add(boxGia, 2, 1);
        grid.add(boxBtn, 3, 1);

        card.getChildren().add(grid);
        return card;
    }

    private void updatePriceAction() {
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
    }

    private void filterRooms() {
        String kw = txtSearch.getText() != null ? txtSearch.getText().toLowerCase().trim() : "";
        String loaiSel = cbLoai.getValue();
        String ttSel = cbTrangThai.getValue();

        // 1. LẤY SỐ NGƯỜI YÊU CẦU
        String soNguoiSel = cbSoNguoi.getValue() != null ? cbSoNguoi.getValue() : "Sức chứa bất kỳ";
        int tempCap = 0; // Biến tạm để tính toán
        if (soNguoiSel.contains("1")) tempCap = 1;
        else if (soNguoiSel.contains("2")) tempCap = 2;
        else if (soNguoiSel.contains("3")) tempCap = 3;
        else if (soNguoiSel.contains("4")) tempCap = 4;

        final int finalReqCapacity = tempCap;
        final double finalMaxP = sliderPrice.getValue();

        // 👉 2. GỌI SERVICE ĐỂ LẤY DANH SÁCH PHÒNG TRỐNG THỰC SỰ THEO THỜI GIAN
        List<String> listMaPhongTrong = new ArrayList<>();
        try {
            LocalDate inDate = dpIn.getValue() != null ? dpIn.getValue() : LocalDate.now();
            LocalDate outDate = dpOut.getValue() != null ? dpOut.getValue() : LocalDate.now().plusDays(1);

            // Gọi Database: Tìm các phòng KHÔNG bị trùng lịch trong khoảng thời gian này
            // (Đảm bảo phongService.findAvailableRooms của Tú đã xử lý truy vấn SQL chuẩn)
            List<PhongDTO> dsPhongTrong = phongService.findAvailableRooms(inDate, outDate, 0, Double.MAX_VALUE, null);

            // Chỉ lấy danh sách Mã Phòng để lát nữa đối chiếu
            listMaPhongTrong = dsPhongTrong.stream().map(PhongDTO::getMaPhong).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback: Nếu lỗi kết nối, tạm lấy tất cả để không bị trắng màn hình
            listMaPhongTrong = allRoomsCache.stream().map(PhongDTO::getMaPhong).collect(Collectors.toList());
        }

        final List<String> finalAvailableRooms = listMaPhongTrong;

        // 👉 3. KẾT HỢP LỌC BỀ MẶT (UI) VÀ LỌC THỜI GIAN (DATABASE)
        List<PhongDTO> filtered = allRoomsCache.stream()
                // Lọc theo Từ khóa (Tên, Mã)
                .filter(p -> kw.isEmpty() || p.getMaPhong().toLowerCase().contains(kw) || p.getTenPhong().toLowerCase().contains(kw))
                // Lọc theo Loại phòng
                .filter(p -> loaiSel.equals("Tất cả loại phòng") || mapMaLoaiToTen(p.getMaLoaiPhong()).equals(loaiSel))
                // Lọc theo Giá tối đa
                .filter(p -> p.getGiaPhong() <= finalMaxP)
                // Lọc theo Sức chứa
                .filter(p -> finalReqCapacity == 0 || getSucChuaTuLoaiPhong(p.getMaLoaiPhong()) >= finalReqCapacity)
                // 🔥 ĐIỀU KIỆN SỐNG CÒN: Phòng phải có mặt trong danh sách không kẹt lịch từ Database
                .filter(p -> finalAvailableRooms.contains(p.getMaPhong()))
                .collect(Collectors.toList());

        // ... (Phần trên của hàm filterRooms giữ nguyên)

        // Lọc trạng thái (Áp dụng cuối cùng nếu Lễ tân có nhu cầu nhìn các phòng cụ thể)
        if (!"Tất cả trạng thái".equals(ttSel)) {
            filtered = filtered.stream()
                    .filter(p -> matchStatus(p.getTinhTrang(), ttSel))
                    .collect(Collectors.toList());
        }

        // 👉 ĐÃ FIX: NGHIỆP VỤ "XUYÊN KHÔNG" (TIME TRAVEL FIX)
        // Nếu Lễ tân đang tìm phòng cho TƯƠNG LAI (Ngày nhận > Hôm nay)
        // Thì tất cả những phòng lọt qua được bộ lọc SQL đều mặc định là "TRỐNG" trong tương lai đó.
        boolean isFutureSearch = dpIn.getValue() != null && dpIn.getValue().isAfter(LocalDate.now());

        if (isFutureSearch) {
            for (PhongDTO p : filtered) {
                // Ép trạng thái trên giao diện thành Trống để hiện nút Đặt Phòng
                // (Việc này chỉ thay đổi hiển thị UI, không làm thay đổi DB hôm nay)
                p.setTinhTrang("Trống");
            }
        }

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

    // 👉 HÀM HỖ TRỢ: Lấy sức chứa dựa trên Mã Loại Phòng
    private int getSucChuaTuLoaiPhong(String maLoai) {
        if (maLoai == null) return 2; // Mặc định
        return switch (maLoai.toUpperCase()) {
            case "DON" -> 1;
            case "DOI", "VIP" -> 2;
            case "GIADINH" -> 4;
            default -> 2;
        };
    }
}