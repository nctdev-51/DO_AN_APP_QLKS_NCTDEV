package iuh.fit.presentation.controller;

import iuh.fit.core.dto.*;
import iuh.fit.core.service.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FIXED:
 *  1. loadPhieuDatPhong() — lọc trang thái "DA_NHAN_PHONG" (đúng với DB)
 *     thay vì "Đang ở" / "Checked-in" (không bao giờ có trong DB).
 *  2. loadPhieuInfo()     — hiện thêm tên KH, tên phòng nếu DTO hỗ trợ.
 *  3. calculateInvoice()  — tính cả tiền dịch vụ từ DB (ChiTietPhieuDatPhong).
 *  4. confirmCheckout()   — lưu hóa đơn thực sự qua hoaDonService,
 *                           cập nhật trạng thái phiếu → DA_TRA_PHONG,
 *                           cập nhật phòng → Trống.
 */
public class TraPhongController {

    private final ITraPhongService traPhongService;
    private final IPhongService phongService;

    // UI Components
    private ComboBox<String> cbPhieuDat;
    private Label lblKhachHang;
    private Label lblPhong;
    private Label lblNgayNhan;
    private Label lblNgayTra;
    private Label lblSoNgay;
    private Label lblGiaPhong;
    private Label lblTongTienPhong;
    private Label lblTongTienDichVu;
    private Label lblThueVAT;
    private Label lblChietKhau;
    private Label lblTongTien;
    private TableView<ChiTietHoaDonDTO> tvChiTiet;
    private ComboBox<String> cbPhuongThucThanhToan;
    private Spinner<Double> spinnerVAT;
    private Spinner<Double> spinnerChietKhau;

    // State
    private PhieuDatPhongDTO currentPhieu = null;
    private HoaDonDTO cachedHoaDon = null;

    // Colors
    private final String COLOR_PRIMARY = "#0066cc";
    private final String COLOR_ACCENT  = "#17a2b8";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_BG_LIGHT = "#f8fafc";

    public TraPhongController(ITraPhongService traPhongService,
                              IPhongService phongService) {
        this.traPhongService = traPhongService;
        this.phongService = phongService;
    }

    public BorderPane createTraPhongView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + ";");

        VBox topArea = new VBox(12);
        topArea.setPadding(new Insets(20, 25, 0, 25));
        topArea.getChildren().add(createTitleBar());
        root.setTop(topArea);

        VBox centerArea = new VBox(15);
        centerArea.setPadding(new Insets(20, 25, 25, 25));
        centerArea.getChildren().addAll(
                createPhieuDatSelectionCard(),
                createGuestInfoCard(),
                createServiceDetailTable(),
                createInvoiceCalculationCard(),
                createPaymentCard()
        );
        ScrollPane scrollPane = new ScrollPane(centerArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent;");
        root.setCenter(scrollPane);

        return root;
    }

    // ------------------------------------------------------------------ //
    // UI builders (không đổi về layout)
    // ------------------------------------------------------------------ //

    private VBox createTitleBar() {
        VBox vbox = new VBox(3);
        Label title = new Label("💳 Trả Phòng & Thanh Toán");
        title.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 24));
        title.setTextFill(Color.web(COLOR_TEXT_MAIN));
        Label subtitle = new Label("Tính toán hóa đơn và xử lý thanh toán khi khách trả phòng");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        subtitle.setTextFill(Color.web(COLOR_TEXT_MUTED));
        vbox.getChildren().addAll(title, subtitle);
        return vbox;
    }

    private VBox createPhieuDatSelectionCard() {
        VBox card = createCard();
        Label lblTitle = new Label("📋 Chọn Phiếu Đang Ở");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        cbPhieuDat = new ComboBox<>();
        cbPhieuDat.setPrefWidth(350);
        cbPhieuDat.setStyle("-fx-font-size: 12px;");
        loadPhieuDatPhong(); // FIX: chỉ lấy DA_NHAN_PHONG

        Button btnLoad = new Button("📂 Tải Thông Tin");
        btnLoad.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;");
        btnLoad.setOnAction(e -> loadPhieuInfo());

        HBox row = new HBox(15, new Label("Phiếu Đặt:"), cbPhieuDat, btnLoad);
        row.setAlignment(Pos.CENTER_LEFT);
        card.getChildren().addAll(lblTitle, row);
        return card;
    }

    private VBox createGuestInfoCard() {
        VBox card = createCard();
        Label lblTitle = new Label("👤 Thông Tin Khách & Phòng");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        GridPane grid = new GridPane();
        grid.setHgap(30); grid.setVgap(12);

        lblKhachHang = createInfoLabel("Chưa chọn");
        lblPhong     = createInfoLabel("Chưa chọn");
        lblNgayNhan  = createInfoLabel("Chưa chọn");
        lblNgayTra   = createInfoLabel("Chưa chọn");
        lblSoNgay    = createInfoLabel("0 ngày");
        lblGiaPhong  = createInfoLabel("0 đ");

        addGridRow(grid, "Khách hàng:", lblKhachHang, 0);
        addGridRow(grid, "Phòng:",      lblPhong,     1);
        addGridRow(grid, "Nhận phòng:", lblNgayNhan,  2);
        addGridRow(grid, "Trả phòng:",  lblNgayTra,   3);
        addGridRow(grid, "Số ngày ở:",  lblSoNgay,    4);
        addGridRow(grid, "Giá/ngày:",   lblGiaPhong,  5);

        card.getChildren().addAll(lblTitle, grid);
        return card;
    }

    private VBox createServiceDetailTable() {
        VBox card = createCard();
        Label lblTitle = new Label("🧾 Chi Tiết Dịch Vụ Đã Dùng");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        tvChiTiet = new TableView<>();
        tvChiTiet.setPrefHeight(220);
        tvChiTiet.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tvChiTiet.setPlaceholder(new Label("Chưa có dịch vụ hoặc chưa chọn phiếu."));

        TableColumn<ChiTietHoaDonDTO, String> colTen = new TableColumn<>("Tên Dịch Vụ");
        colTen.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getTenDichVu()));

        TableColumn<ChiTietHoaDonDTO, Integer> colQty = new TableColumn<>("Số Lượng");
        colQty.setCellValueFactory(p -> new javafx.beans.property.SimpleObjectProperty<>(p.getValue().getSoLuong()));

        TableColumn<ChiTietHoaDonDTO, String> colDon = new TableColumn<>("Đơn Giá");
        colDon.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(
                String.format("%,.0f đ", p.getValue().getGiaTienTungDichVu())));

        TableColumn<ChiTietHoaDonDTO, String> colTT = new TableColumn<>("Thành Tiền");
        colTT.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(
                String.format("%,.0f đ", p.getValue().getThanhTien())));

        tvChiTiet.getColumns().addAll(colTen, colQty, colDon, colTT);
        card.getChildren().addAll(lblTitle, tvChiTiet);
        return card;
    }

    private VBox createInvoiceCalculationCard() {
        VBox card = createCard();
        Label lblTitle = new Label("🔢 Tính Toán Hóa Đơn");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(12);

        lblTongTienPhong  = createAmountLabel("0 đ");
        lblTongTienDichVu = createAmountLabel("0 đ");
        lblThueVAT        = createAmountLabel("0 đ");
        lblChietKhau      = createAmountLabel("0 đ");
        lblTongTien       = createAmountLabel("0 đ");
        lblTongTien.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 16));
        lblTongTien.setStyle("-fx-text-fill: " + COLOR_PRIMARY + ";");

        spinnerVAT = new Spinner<>(0.0, 30.0, 8.0, 1.0);
        spinnerVAT.setPrefWidth(90);
        spinnerVAT.setEditable(true);

        spinnerChietKhau = new Spinner<>(0.0, 10000000.0, 0.0, 100000.0);
        spinnerChietKhau.setPrefWidth(120);
        spinnerChietKhau.setEditable(true);

        addGridRow(grid, "Tiền phòng:",      lblTongTienPhong,  0);
        addGridRow(grid, "Tiền dịch vụ:",    lblTongTienDichVu, 1);
        grid.add(new Label("VAT (%):"),              0, 2);
        grid.add(spinnerVAT,                         1, 2);
        grid.add(lblThueVAT,                         2, 2);
        grid.add(new Label("Chiết khấu (đ):"),       0, 3);
        grid.add(spinnerChietKhau,                   1, 3);
        grid.add(new Label("TỔNG TIỀN:"),            0, 4);
        grid.add(lblTongTien,                        1, 4);

        Button btnCalc = new Button("⚙ Tính Toán");
        btnCalc.setStyle("-fx-background-color: white; -fx-text-fill: " + COLOR_PRIMARY
                + "; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;"
                + " -fx-border-color: " + COLOR_PRIMARY + "; -fx-border-radius: 6;");
        btnCalc.setOnAction(e -> calculateInvoice());

        HBox calcRow = new HBox(btnCalc);
        calcRow.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(lblTitle, grid, calcRow);
        return card;
    }

    private VBox createPaymentCard() {
        VBox card = createCard();
        Label lblTitle = new Label("💰 Phương Thức Thanh Toán");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        cbPhuongThucThanhToan = new ComboBox<>();
        cbPhuongThucThanhToan.getItems().addAll("Tiền mặt", "Chuyển khoản", "Thẻ Debit", "Thẻ Credit");
        cbPhuongThucThanhToan.setValue("Tiền mặt");
        cbPhuongThucThanhToan.setPrefWidth(200);

        HBox methodRow = new HBox(15, new Label("Phương thức:"), cbPhuongThucThanhToan);
        methodRow.setAlignment(Pos.CENTER_LEFT);

        Button btnCancel = new Button("❌ Hủy");
        btnCancel.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");
        btnCancel.setOnAction(e -> clearForm());

        Button btnCheckout = new Button("✅ Xác Nhận Thanh Toán");
        btnCheckout.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");
        btnCheckout.setOnAction(e -> confirmCheckout());

        HBox actionRow = new HBox(15, btnCancel, btnCheckout);
        actionRow.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(lblTitle, methodRow, actionRow);
        return card;
    }

    // ------------------------------------------------------------------ //
    // Business logic (FIXED)
    // ------------------------------------------------------------------ //

    /**
     * Load phiếu đang ở từ Service (chỉ lọc dữ liệu)
     */
    private void loadPhieuDatPhong() {
        try {
            List<PhieuDatPhongDTO> phieuList = traPhongService.getPhieuDangO();
            List<String> options = phieuList.stream()
                    .map(p -> p.getMaPhieu()
                            + " - Phòng: " + p.getMaPhong()
                            + " (" + p.getMaKhachHang() + ")")
                    .collect(Collectors.toList());
            cbPhieuDat.getItems().clear();
            cbPhieuDat.getItems().addAll(options);
            if (options.isEmpty()) {
                cbPhieuDat.setPromptText("Không có phiếu đang ở");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải phiếu: " + e.getMessage());
        }
    }

    /**
     * Load thông tin phiếu từ Service
     */
    private void loadPhieuInfo() {
        if (cbPhieuDat.getValue() == null) {
            showAlert("Cảnh báo", "Vui lòng chọn phiếu đặt");
            return;
        }

        String maPhieu = cbPhieuDat.getValue().split(" - ")[0];

        try {
            // Lấy chi tiết phiếu từ Service
            PhieuDatPhongDTO phieu = traPhongService.loadPhieuDetail(maPhieu);
            currentPhieu = phieu;

            // Hiển thị thông tin khách
            String tenKH = (phieu.getTenKhachHang() != null && !phieu.getTenKhachHang().isEmpty())
                    ? phieu.getTenKhachHang() : phieu.getMaKhachHang();
            lblKhachHang.setText(tenKH);

            String tenPhong = (phieu.getTenPhong() != null && !phieu.getTenPhong().isEmpty())
                    ? phieu.getTenPhong() + " (" + phieu.getMaPhong() + ")"
                    : phieu.getMaPhong();
            lblPhong.setText(tenPhong);

            // 👉 ĐÃ SỬA: Đổi sang LocalDateTime và LocalDateTime.now()
            java.time.LocalDateTime ngayNhan = phieu.getNgayNhan() != null ? phieu.getNgayNhan() : java.time.LocalDateTime.now();
            java.time.LocalDateTime ngayTra = phieu.getNgayTra() != null ? phieu.getNgayTra() : java.time.LocalDateTime.now();
            // Định dạng lại cho đẹp (Ví dụ: 08/05/2026 14:00)
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            lblNgayNhan.setText(ngayNhan.format(formatter));
            lblNgayTra.setText(ngayTra.format(formatter));

            // 👉 SỬA TÍNH NGÀY: Ép về LocalDate để tính số ngày lưu trú cho chuẩn xác
            long soNgay = ChronoUnit.DAYS.between(ngayNhan.toLocalDate(), ngayTra.toLocalDate());
            if (soNgay < 1) soNgay = 1;
            lblSoNgay.setText(soNgay + " ngày");

            // Tải giá phòng
            PhongDTO phong = phongService.getPhongById(phieu.getMaPhong());
            double giaPhong = phong != null ? phong.getGiaPhong() : 0;
            lblGiaPhong.setText(String.format("%,.0f đ", giaPhong));

            // Tải dịch vụ từ Service (chỉ cần call 1 method)
            tvChiTiet.getItems().clear();
            List<ChiTietHoaDonDTO> dichVus = traPhongService.getChiTietDichVu(maPhieu);
            if (dichVus != null && !dichVus.isEmpty()) {
                tvChiTiet.getItems().addAll(dichVus);
            }

            // Tự tính ngay sau khi tải
            calculateInvoice();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải thông tin phiếu: " + e.getMessage());
        }
    }

    /**
     * Tính hóa đơn từ Service
     */
    private void calculateInvoice() {
        if (currentPhieu == null) {
            showAlert("Cảnh báo", "Vui lòng tải phiếu trước");
            return;
        }

        try {
            double vatPercent = spinnerVAT.getValue() / 100.0;
            double chietKhau = spinnerChietKhau.getValue();

            // Gọi Service để tính toán
            cachedHoaDon = traPhongService.calculateCheckoutInvoice(
                    currentPhieu.getMaPhieu(),
                    vatPercent,
                    chietKhau
            );

            // Cập nhật UI từ kết quả
            lblTongTienPhong.setText(String.format("%,.0f đ", cachedHoaDon.getTongTienPhong()));
            lblTongTienDichVu.setText(String.format("%,.0f đ", cachedHoaDon.getTongTienDichVu()));
            lblThueVAT.setText(String.format("%,.0f đ", cachedHoaDon.getThueVAT()));
            lblChietKhau.setText(String.format("%,.0f đ", chietKhau));
            lblTongTien.setText(String.format("%,.0f đ", cachedHoaDon.getTongTien()));

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Lỗi tính toán: " + e.getMessage());
        }
    }

    /**
     * Xác nhận trả phòng từ Service
     */
    private void confirmCheckout() {
        if (currentPhieu == null || cachedHoaDon == null) {
            showAlert("Cảnh báo", "Vui lòng tải và tính toán hóa đơn trước");
            return;
        }

        try {
            // Gọi Service để xác nhận
            traPhongService.confirmCheckout(
                    currentPhieu.getMaPhieu(),
                    cachedHoaDon,
                    cbPhuongThucThanhToan.getValue()
            );

            showAlert("✅ Thành công",
                    String.format("Trả phòng thành công!\nTổng tiền: %,.0f đ", cachedHoaDon.getTongTien()));

            clearForm();
            loadPhieuDatPhong(); // Làm mới ComboBox

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể xử lý trả phòng: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ //
    // Helpers
    // ------------------------------------------------------------------ //

    private void clearForm() {
        currentPhieu = null;
        cachedHoaDon = null;
        cbPhieuDat.setValue(null);
        lblKhachHang.setText("Chưa chọn");
        lblPhong.setText("Chưa chọn");
        lblNgayNhan.setText("Chưa chọn");
        lblNgayTra.setText("Chưa chọn");
        lblSoNgay.setText("0 ngày");
        lblGiaPhong.setText("0 đ");
        lblTongTienPhong.setText("0 đ");
        lblTongTienDichVu.setText("0 đ");
        lblThueVAT.setText("0 đ");
        lblChietKhau.setText("0 đ");
        lblTongTien.setText("0 đ");
        tvChiTiet.getItems().clear();
    }

    private VBox createCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10;"
                + " -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.05));
        shadow.setRadius(5);
        shadow.setOffsetY(2);
        card.setEffect(shadow);
        return card;
    }

    private Label createInfoLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + COLOR_TEXT_MAIN + ";");
        return lbl;
    }

    private Label createAmountLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 13px; -fx-text-fill: " + COLOR_PRIMARY + "; -fx-font-weight: bold;");
        return lbl;
    }

    /** Thêm 1 hàng (label + value) vào GridPane */
    private void addGridRow(GridPane grid, String labelText, Label value, int row) {
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
        grid.add(lbl,   0, row);
        grid.add(value, 1, row);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}