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

public class TraPhongController {

    private final IPhieuDatPhongService phieuDatPhongService;
    private final IPhongService phongService;
    private final IKhachHangService khachHangService;
    private final IHoaDonService hoaDonService;
    private final IChiTietHoaDonService chiTietHoaDonService;
    private final IDichVuService dichVuService;

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

    // Colors
    private final String COLOR_PRIMARY = "#0066cc";
    private final String COLOR_ACCENT = "#17a2b8";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_WARNING = "#f59e0b";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_BG_LIGHT = "#f8fafc";

    public TraPhongController(IPhieuDatPhongService phieuDatPhongService, IPhongService phongService,
                              IKhachHangService khachHangService, IHoaDonService hoaDonService,
                              IChiTietHoaDonService chiTietHoaDonService, IDichVuService dichVuService) {
        this.phieuDatPhongService = phieuDatPhongService;
        this.phongService = phongService;
        this.khachHangService = khachHangService;
        this.hoaDonService = hoaDonService;
        this.chiTietHoaDonService = chiTietHoaDonService;
        this.dichVuService = dichVuService;
    }

    public BorderPane createTraPhongView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + ";");

        // Top: Title bar
        VBox topArea = new VBox(12);
        topArea.setPadding(new Insets(20, 25, 0, 25));
        topArea.getChildren().add(createTitleBar());
        root.setTop(topArea);

        // Center: Main content
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

    private VBox createTitleBar() {
        VBox vbox = new VBox(3);
        Label title = new Label("🏨 Trả Phòng & Thanh Toán");
        title.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 24));
        title.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label subtitle = new Label("Tính toán hóa đơn và xử lý thanh toán khi khách trả phòng");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        subtitle.setTextFill(Color.web(COLOR_TEXT_MUTED));

        vbox.getChildren().addAll(title, subtitle);
        return vbox;
    }

    private VBox createPhieuDatSelectionCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
        applyCardStyle(card);

        Label lblTitle = new Label("🔍 Chọn Phiếu Đặt Phòng");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        HBox selectionBox = new HBox(15);
        selectionBox.setAlignment(Pos.CENTER_LEFT);

        Label lblPhieu = new Label("Phiếu Đặt:");
        lblPhieu.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        cbPhieuDat = new ComboBox<>();
        cbPhieuDat.setPrefWidth(300);
        cbPhieuDat.setStyle("-fx-font-size: 12px;");
        loadPhieuDatPhong();

        Button btnLoad = new Button("🔄 Tải Thông Tin");
        btnLoad.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;");
        btnLoad.setOnAction(e -> loadPhieuInfo());

        selectionBox.getChildren().addAll(lblPhieu, cbPhieuDat, btnLoad);
        card.getChildren().addAll(lblTitle, selectionBox);
        return card;
    }

    private VBox createGuestInfoCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
        applyCardStyle(card);

        Label lblTitle = new Label("👤 Thông Tin Khách & Phòng");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(12);

        // Column 1
        Label lbl1 = new Label("Khách hàng:");
        lbl1.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
        lblKhachHang = createInfoLabel("Chưa chọn");

        Label lbl2 = new Label("Phòng:");
        lbl2.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
        lblPhong = createInfoLabel("Chưa chọn");

        // Column 2
        Label lbl3 = new Label("Nhận phòng:");
        lbl3.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
        lblNgayNhan = createInfoLabel("Chưa chọn");

        Label lbl4 = new Label("Trả phòng:");
        lbl4.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
        lblNgayTra = createInfoLabel("Chưa chọn");

        // Column 3
        Label lbl5 = new Label("Số ngày ở:");
        lbl5.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
        lblSoNgay = createInfoLabel("0 ngày");

        Label lbl6 = new Label("Giá phòng/ngày:");
        lbl6.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
        lblGiaPhong = createInfoLabel("0 đ");

        grid.add(lbl1, 0, 0);
        grid.add(lblKhachHang, 0, 1);
        grid.add(lbl2, 0, 2);
        grid.add(lblPhong, 0, 3);

        grid.add(lbl3, 1, 0);
        grid.add(lblNgayNhan, 1, 1);
        grid.add(lbl4, 1, 2);
        grid.add(lblNgayTra, 1, 3);

        grid.add(lbl5, 2, 0);
        grid.add(lblSoNgay, 2, 1);
        grid.add(lbl6, 2, 2);
        grid.add(lblGiaPhong, 2, 3);

        card.getChildren().addAll(lblTitle, grid);
        return card;
    }

    private VBox createServiceDetailTable() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
        applyCardStyle(card);

        Label lblTitle = new Label("🍽️ Chi Tiết Dịch Vụ Sử Dụng");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        tvChiTiet = new TableView<>();
        tvChiTiet.setPrefHeight(200);
        tvChiTiet.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ChiTietHoaDonDTO, String> colTenDV = new TableColumn<>("Tên Dịch Vụ");
        colTenDV.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getTenDichVu()));

        TableColumn<ChiTietHoaDonDTO, Integer> colQty = new TableColumn<>("Số Lượng");
        colQty.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getSoLuong()));

        TableColumn<ChiTietHoaDonDTO, Double> colGia = new TableColumn<>("Giá Đơn Vị");
        colGia.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getGiaTienTungDichVu()));

        TableColumn<ChiTietHoaDonDTO, Double> colThanhTien = new TableColumn<>("Thành Tiền");
        colThanhTien.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getThanhTien()));

        tvChiTiet.getColumns().addAll(colTenDV, colQty, colGia, colThanhTien);

        card.getChildren().addAll(lblTitle, tvChiTiet);
        return card;
    }

    private VBox createInvoiceCalculationCard() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: linear-gradient(to bottom, #f0f9ff, #e0f2fe); -fx-background-radius: 10; -fx-border-color: " + COLOR_PRIMARY + "; -fx-border-radius: 10; -fx-border-width: 2;");

        Label lblTitle = new Label("💰 Tính Toán Hóa Đơn");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 16));
        lblTitle.setTextFill(Color.web(COLOR_PRIMARY));

        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(12);

        // Row 1: Tổng tiền phòng
        Label lbl1 = new Label("Tổng tiền phòng:");
        lbl1.setStyle("-fx-font-weight: bold;");
        lblTongTienPhong = createInfoLabel("0 đ");
        lblTongTienPhong.setStyle("-fx-font-size: 13px; -fx-text-fill: #1e40af;");

        // Row 2: Tổng tiền dịch vụ
        Label lbl2 = new Label("Tổng tiền dịch vụ:");
        lbl2.setStyle("-fx-font-weight: bold;");
        lblTongTienDichVu = createInfoLabel("0 đ");
        lblTongTienDichVu.setStyle("-fx-font-size: 13px; -fx-text-fill: #16a34a;");

        // Row 3: VAT
        Label lbl3 = new Label("Thuế VAT (%):");
        lbl3.setStyle("-fx-font-weight: bold;");
        spinnerVAT = new Spinner<>(0.0, 50.0, 10.0, 1.0);
        spinnerVAT.setPrefWidth(80);

        lblThueVAT = createInfoLabel("0 đ");
        lblThueVAT.setStyle("-fx-font-size: 13px; -fx-text-fill: #ea580c;");

        // Row 4: Chiết khấu
        Label lbl4 = new Label("Chiết khấu (đ):");
        lbl4.setStyle("-fx-font-weight: bold;");
        spinnerChietKhau = new Spinner<>(0.0, 10000000.0, 0.0, 100000.0);
        spinnerChietKhau.setPrefWidth(120);

        // Row 5: Tổng tiền
        Label lblTotal = new Label("TỔNG TIỀN:");
        lblTotal.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 14));
        lblTotal.setStyle("-fx-text-fill: " + COLOR_PRIMARY + ";");

        lblTongTien = new Label("0 đ");
        lblTongTien.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 16));
        lblTongTien.setStyle("-fx-text-fill: " + COLOR_PRIMARY + "; -fx-padding: 0 0 0 10;");

        grid.add(lbl1, 0, 0);
        grid.add(lblTongTienPhong, 1, 0);
        grid.add(lbl2, 0, 1);
        grid.add(lblTongTienDichVu, 1, 1);
        grid.add(lbl3, 0, 2);
        grid.add(spinnerVAT, 1, 2);
        grid.add(lblThueVAT, 2, 2);
        grid.add(lbl4, 0, 3);
        grid.add(spinnerChietKhau, 1, 3);
        grid.add(lblTotal, 0, 4);
        grid.add(lblTongTien, 1, 4);

        Button btnCalc = new Button("🧮 Tính Toán");
        btnCalc.setStyle("-fx-background-color: white; -fx-text-fill: " + COLOR_PRIMARY + "; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6; -fx-border-color: " + COLOR_PRIMARY + "; -fx-border-radius: 6;");
        btnCalc.setOnAction(e -> calculateInvoice());

        HBox calcBox = new HBox(10);
        calcBox.setAlignment(Pos.CENTER_RIGHT);
        calcBox.getChildren().add(btnCalc);

        card.getChildren().addAll(lblTitle, grid, calcBox);
        return card;
    }

    private VBox createPaymentCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
        applyCardStyle(card);

        Label lblTitle = new Label("💳 Chọn Phương Thức Thanh Toán");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        HBox methodBox = new HBox(20);
        methodBox.setAlignment(Pos.CENTER_LEFT);

        Label lblMethod = new Label("Phương thức:");
        lblMethod.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        cbPhuongThucThanhToan = new ComboBox<>();
        cbPhuongThucThanhToan.getItems().addAll("Tiền mặt", "Chuyển khoản", "Thẻ Debit", "Thẻ Credit");
        cbPhuongThucThanhToan.setValue("Tiền mặt");
        cbPhuongThucThanhToan.setPrefWidth(200);

        methodBox.getChildren().addAll(lblMethod, cbPhuongThucThanhToan);

        HBox actionBox = new HBox(15);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnCancel = new Button("❌ Hủy");
        btnCancel.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");
        btnCancel.setOnAction(e -> showAlert("Thông báo", "Đã hủy trả phòng"));

        Button btnCheckout = new Button("✅ Xác Nhận Thanh Toán");
        btnCheckout.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6;");
        btnCheckout.setOnAction(e -> confirmCheckout());

        actionBox.getChildren().addAll(btnCancel, btnCheckout);

        card.getChildren().addAll(lblTitle, methodBox, actionBox);
        return card;
    }

    private Label createInfoLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + COLOR_TEXT_MAIN + ";");
        return lbl;
    }

    private void loadPhieuDatPhong() {
        try {
            List<PhieuDatPhongDTO> phieuList = phieuDatPhongService.getAllPhieuDatPhong();
            List<String> options = phieuList.stream()
                    .filter(p -> "Đang ở".equalsIgnoreCase(p.getTrangThai()) || "Checked-in".equalsIgnoreCase(p.getTrangThai()))
                    .map(p -> p.getMaPhieu() + " - Phòng: " + p.getMaPhong() + " (" + p.getMaKhachHang() + ")")
                    .collect(Collectors.toList());
            cbPhieuDat.getItems().clear();
            cbPhieuDat.getItems().addAll(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPhieuInfo() {
        if (cbPhieuDat.getValue() == null) {
            showAlert("Cảnh báo", "Vui lòng chọn phiếu đặt");
            return;
        }

        String selected = cbPhieuDat.getValue();
        String maPhieu = selected.split(" - ")[0];

        try {
            PhieuDatPhongDTO phieu = phieuDatPhongService.getPhieuDatPhongById(maPhieu);
            if (phieu == null) return;

            // Load guest info
            lblKhachHang.setText(phieu.getMaKhachHang());
            lblPhong.setText(phieu.getMaPhong());
            lblNgayNhan.setText(phieu.getNgayNhan() != null ? phieu.getNgayNhan().toString() : "N/A");
            lblNgayTra.setText(phieu.getNgayTra() != null ? phieu.getNgayTra().toString() : LocalDate.now().toString());

            // Calculate days
            LocalDate ngayNhan = phieu.getNgayNhan() != null ? phieu.getNgayNhan() : LocalDate.now();
            LocalDate ngayTra = phieu.getNgayTra() != null ? phieu.getNgayTra() : LocalDate.now();
            long soNgay = ChronoUnit.DAYS.between(ngayNhan, ngayTra);
            lblSoNgay.setText(soNgay + " ngày");

            // Load room price
            PhongDTO phong = phongService.getPhongById(phieu.getMaPhong());
            if (phong != null) {
                lblGiaPhong.setText(String.format("%,.0f đ", phong.getGiaPhong()));
                double tongTienPhong = soNgay * phong.getGiaPhong();
                lblTongTienPhong.setText(String.format("%,.0f đ", tongTienPhong));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateInvoice() {
        if (cbPhieuDat.getValue() == null) {
            showAlert("Cảnh báo", "Vui lòng chọn phiếu đặt");
            return;
        }

        try {
            double vat = spinnerVAT.getValue() / 100.0;
            double chietKhau = spinnerChietKhau.getValue();

            // For now, calculate based on room price only
            String tongTienPhongStr = lblTongTienPhong.getText().replace(".","").replace(",", "").replace(" đ", "");
            double tongTienPhong = Double.parseDouble(tongTienPhongStr);

            double thueVAT = tongTienPhong * vat;
            double tongTien = tongTienPhong + thueVAT - chietKhau;

            lblThueVAT.setText(String.format("%,.0f đ", thueVAT));
            lblTongTien.setText(String.format("%,.0f đ", tongTien));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confirmCheckout() {
        showAlert("Thông báo", "✅ Trả phòng thành công!\nPhòng sẵn sàng cho khách tiếp theo.");
        clearForm();
    }

    private void clearForm() {
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
        lblTongTien.setText("0 đ");
        tvChiTiet.getItems().clear();
    }

    private void applyCardStyle(VBox card) {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.05));
        shadow.setRadius(5);
        shadow.setOffsetY(2);
        card.setEffect(shadow);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}

