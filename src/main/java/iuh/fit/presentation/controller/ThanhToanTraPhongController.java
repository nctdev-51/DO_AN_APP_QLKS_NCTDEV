package iuh.fit.presentation.controller;

import iuh.fit.core.dto.*;
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

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ThanhToanTraPhongController {

    private final IPhieuDatPhongService phieuDatPhongService;
    private final IPhongService phongService;
    private final IHoaDonService hoaDonService;
    private final IKhachHangService khachHangService;
    private final IChiTietHoaDonService chiTietHoaDonService;
    private final IDichVuService dichVuService;
    private final TaiKhoanDTO currentUser;

    // UI Components - Trả Phòng
    private ComboBox<String> cbPhieuDatCheckout;
    private Label lblGuestNameCheckout, lblRoomCheckout, lblCheckInDateCheckout, lblCheckOutDateCheckout, lblNumDaysCheckout, lblRoomPriceCheckout;
    private Label lblRoomTotalCheckout, lblServiceTotalCheckout, lblSurchargeCheckout, lblVATCheckout, lblDiscountCheckout, lblGrandTotalCheckout;
    private TableView<ChiTietHoaDonDTO> tvServiceDetails;
    private Spinner<Double> spinnerVAT;
    private Spinner<Double> spinnerDiscount;

    // Biến lưu trữ tiền để tính toán Real-time
    private double currentRoomTotal = 0.0;
    private double currentServiceTotal = 0.0;
    private double currentSurchargeTotal = 0.0;

    // Bảng màu thiết kế chuẩn
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_PRIMARY_DARK = "#1e3a8a";
    private final String COLOR_BG_MAIN = "#f8fafc";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_BORDER = "#e2e8f0";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_DANGER = "#ef4444";

    private String preselectedMaPhieu = null;

    public ThanhToanTraPhongController(
            IPhieuDatPhongService phieuDatPhongService,
            IPhongService phongService,
            IKhachHangService khachHangService,
            IHoaDonService hoaDonService,
            IChiTietHoaDonService chiTietHoaDonService,
            IDichVuService dichVuService,
            TaiKhoanDTO currentUser,
            String preselectedMaPhieu) {

        this.phieuDatPhongService = phieuDatPhongService;
        this.phongService = phongService;
        this.hoaDonService = hoaDonService;
        this.khachHangService = khachHangService;
        this.chiTietHoaDonService = chiTietHoaDonService;
        this.dichVuService = dichVuService;
        this.currentUser = currentUser;
        this.preselectedMaPhieu = preselectedMaPhieu;
    }

    public BorderPane createMainView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_BG_MAIN + ";");

        // --- HEADER ---
        VBox header = new VBox(5);
        header.setPadding(new Insets(30, 30, 10, 30));

        Label lblTitle = new Label("THANH TOÁN & TRẢ PHÒNG");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 28));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblSubTitle = new Label("Kiểm tra thông tin lưu trú, dịch vụ, phụ thu và chốt hóa đơn.");
        lblSubTitle.setFont(Font.font("Segoe UI", 14));
        lblSubTitle.setTextFill(Color.web(COLOR_TEXT_MUTED));

        header.getChildren().addAll(lblTitle, lblSubTitle);
        root.setTop(header);

        // --- MAIN LAYOUT (GRIDPANE CHỐNG LẤN CỘT) ---
        GridPane contentArea = new GridPane();
        contentArea.setPadding(new Insets(20, 30, 30, 30));
        contentArea.setHgap(25);
        contentArea.setVgap(20);
        contentArea.setStyle("-fx-background-color: " + COLOR_BG_MAIN + ";");

        ColumnConstraints leftColConstraint = new ColumnConstraints();
        leftColConstraint.setPercentWidth(60);
        ColumnConstraints rightColConstraint = new ColumnConstraints();
        rightColConstraint.setPercentWidth(40);
        contentArea.getColumnConstraints().addAll(leftColConstraint, rightColConstraint);

        VBox leftCol = new VBox(20);
        leftCol.getChildren().addAll(createSelectionCard(), createGuestInfoCard(), createServiceDetailsCard());

        VBox rightCol = new VBox(20);
        rightCol.getChildren().addAll(createInvoiceCard(), createPaymentCard());

        contentArea.add(leftCol, 0, 0);
        contentArea.add(rightCol, 1, 0);

        ScrollPane scrollPane = new ScrollPane(contentArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + COLOR_BG_MAIN + "; -fx-control-inner-background: " + COLOR_BG_MAIN + "; -fx-border-color: transparent;");

        root.setCenter(scrollPane);

        // Tải danh sách
        loadPhieuDatForCheckout();

        return root;
    }

    // =========================================================================
    // CỘT TRÁI: THÔNG TIN CHI TIẾT
    // =========================================================================

    private VBox createSelectionCard() {
        VBox card = createCardBase();

        Label lblTitle = new Label("1. TÌM KIẾM PHIẾU ĐẶT ĐANG PHỤC VỤ");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 15));
        lblTitle.setTextFill(Color.web(COLOR_PRIMARY_DARK));

        HBox selection = new HBox(15);
        selection.setAlignment(Pos.CENTER_LEFT);

        cbPhieuDatCheckout = new ComboBox<>();
        cbPhieuDatCheckout.setPrefWidth(350);
        cbPhieuDatCheckout.setPromptText("Bấm để chọn phòng cần thanh toán...");
        cbPhieuDatCheckout.setStyle("-fx-font-size: 14px; -fx-background-color: white; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-padding: 4;");

        cbPhieuDatCheckout.setOnAction(e -> {
            if (cbPhieuDatCheckout.getValue() != null) {
                loadCheckoutInfo();
            }
        });

        Button btnLoad = new Button("🔄 Làm mới danh sách");
        btnLoad.setCursor(Cursor.HAND);
        btnLoad.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-font-size: 14px;");
        btnLoad.setOnAction(e -> loadPhieuDatForCheckout());

        selection.getChildren().addAll(cbPhieuDatCheckout, btnLoad);
        card.getChildren().addAll(lblTitle, selection);
        return card;
    }

    private VBox createGuestInfoCard() {
        VBox card = createCardBase();

        Label lblTitle = new Label("2. THÔNG TIN KHÁCH & LƯU TRÚ");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 15));
        lblTitle.setTextFill(Color.web(COLOR_PRIMARY_DARK));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(20);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(30);
        ColumnConstraints col3 = new ColumnConstraints(); col3.setPercentWidth(20);
        ColumnConstraints col4 = new ColumnConstraints(); col4.setPercentWidth(30);
        grid.getColumnConstraints().addAll(col1, col2, col3, col4);

        addGridLabel(grid, "Khách Hàng:", 0, 0);
        lblGuestNameCheckout = createInfoLabel("—");
        grid.add(lblGuestNameCheckout, 1, 0);

        addGridLabel(grid, "Ngày Nhận:", 2, 0);
        lblCheckInDateCheckout = createInfoLabel("—");
        grid.add(lblCheckInDateCheckout, 3, 0);

        addGridLabel(grid, "Mã Phòng:", 0, 1);
        lblRoomCheckout = createInfoLabel("—");
        grid.add(lblRoomCheckout, 1, 1);

        addGridLabel(grid, "Ngày Trả:", 2, 1);
        lblCheckOutDateCheckout = createInfoLabel("—");
        grid.add(lblCheckOutDateCheckout, 3, 1);

        addGridLabel(grid, "Tổng Số Ngày:", 0, 2);
        lblNumDaysCheckout = createInfoLabel("0 ngày");
        lblNumDaysCheckout.setTextFill(Color.web(COLOR_PRIMARY));
        grid.add(lblNumDaysCheckout, 1, 2);

        addGridLabel(grid, "Giá Phòng/Ngày:", 2, 2);
        lblRoomPriceCheckout = createInfoLabel("0 đ");
        grid.add(lblRoomPriceCheckout, 3, 2);

        card.getChildren().addAll(lblTitle, grid);
        return card;
    }

    private VBox createServiceDetailsCard() {
        VBox card = createCardBase();
        VBox.setVgrow(card, Priority.ALWAYS);

        Label lblTitle = new Label("3. CHI TIẾT DỊCH VỤ ĐÃ SỬ DỤNG");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 15));
        lblTitle.setTextFill(Color.web(COLOR_PRIMARY_DARK));

        tvServiceDetails = new TableView<>();
        tvServiceDetails.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tvServiceDetails.setStyle("-fx-border-color: #cbd5e1; -fx-border-radius: 8;");
        tvServiceDetails.setPrefHeight(250);
        tvServiceDetails.setMinHeight(150);
        VBox.setVgrow(tvServiceDetails, Priority.ALWAYS);

        TableColumn<ChiTietHoaDonDTO, String> colTen = new TableColumn<>("Mã Dịch Vụ");
        colTen.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getMaDichVu() != null ? param.getValue().getMaDichVu() : "—"));

        TableColumn<ChiTietHoaDonDTO, Integer> colQty = new TableColumn<>("Số Lượng");
        colQty.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getSoLuong()));
        colQty.setStyle("-fx-alignment: CENTER;");

        TableColumn<ChiTietHoaDonDTO, Double> colGia = new TableColumn<>("Đơn Giá");
        colGia.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getGiaTienTungDichVu()));
        colGia.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f đ", item));
            }
        });
        colGia.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<ChiTietHoaDonDTO, Double> colThanhTien = new TableColumn<>("Thành Tiền");
        colThanhTien.setCellValueFactory(param -> {
            double price = param.getValue().getGiaTienTungDichVu();
            double tt = param.getValue().getSoLuong() * price;
            return new javafx.beans.property.SimpleObjectProperty<>(tt);
        });
        colThanhTien.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f đ", item));
                setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: " + COLOR_PRIMARY + ";");
            }
        });

        tvServiceDetails.getColumns().addAll(colTen, colQty, colGia, colThanhTien);

        Label lblEmpty = new Label("Không có dịch vụ nào.");
        lblEmpty.setTextFill(Color.web(COLOR_TEXT_MUTED));
        tvServiceDetails.setPlaceholder(lblEmpty);

        card.getChildren().addAll(lblTitle, tvServiceDetails);

        return card;
    }

    // =========================================================================
    // CỘT PHẢI: HÓA ĐƠN & THANH TOÁN
    // =========================================================================

    private VBox createInvoiceCard() {
        VBox card = createCardBase();

        Label lblTitle = new Label("TỔNG KẾT HÓA ĐƠN");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 18));
        lblTitle.setTextFill(Color.web(COLOR_PRIMARY_DARK));

        VBox calcBox = new VBox(15);

        lblRoomTotalCheckout = addBillRow(calcBox, "Tiền phòng:", "0 đ");
        lblServiceTotalCheckout = addBillRow(calcBox, "Tiền dịch vụ:", "0 đ");

        lblSurchargeCheckout = addBillRow(calcBox, "Phụ thu (Sớm/Trễ):", "0 đ");
        lblSurchargeCheckout.setTextFill(Color.web(COLOR_DANGER));

        HBox vatRow = new HBox();
        vatRow.setAlignment(Pos.CENTER_LEFT);
        Label lblVatText = new Label("Thuế VAT (%):"); lblVatText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14)); lblVatText.setTextFill(Color.web(COLOR_TEXT_MUTED));
        spinnerVAT = new Spinner<>(0.0, 100.0, 10.0, 1.0);
        spinnerVAT.setPrefWidth(80); spinnerVAT.setStyle("-fx-base: white;");
        spinnerVAT.valueProperty().addListener((obs, oldV, newV) -> recalculateTotal());
        Region sp1 = new Region(); HBox.setHgrow(sp1, Priority.ALWAYS);
        lblVATCheckout = new Label("0 đ"); lblVATCheckout.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        vatRow.getChildren().addAll(lblVatText, new Label("  "), spinnerVAT, sp1, lblVATCheckout);
        calcBox.getChildren().add(vatRow);

        HBox discRow = new HBox();
        discRow.setAlignment(Pos.CENTER_LEFT);
        Label lblDiscText = new Label("Chiết khấu (%):"); lblDiscText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14)); lblDiscText.setTextFill(Color.web(COLOR_TEXT_MUTED));
        spinnerDiscount = new Spinner<>(0.0, 100.0, 0.0, 1.0);
        spinnerDiscount.setPrefWidth(80); spinnerDiscount.setStyle("-fx-base: white;");
        spinnerDiscount.valueProperty().addListener((obs, oldV, newV) -> recalculateTotal());
        Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
        lblDiscountCheckout = new Label("0 đ"); lblDiscountCheckout.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15)); lblDiscountCheckout.setTextFill(Color.web(COLOR_SUCCESS));
        discRow.getChildren().addAll(lblDiscText, new Label("  "), spinnerDiscount, sp2, lblDiscountCheckout);
        calcBox.getChildren().add(discRow);

        Separator sep = new Separator();
        sep.setStyle("-fx-padding: 10 0;");

        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER_LEFT);
        Label lblTotalTxt = new Label("TỔNG CỘNG:");
        lblTotalTxt.setFont(Font.font("Segoe UI", FontWeight.BLACK, 18));
        Region sp3 = new Region(); HBox.setHgrow(sp3, Priority.ALWAYS);
        lblGrandTotalCheckout = new Label("0 đ");
        lblGrandTotalCheckout.setFont(Font.font("Segoe UI", FontWeight.BLACK, 28));
        lblGrandTotalCheckout.setTextFill(Color.web(COLOR_DANGER));
        totalRow.getChildren().addAll(lblTotalTxt, sp3, lblGrandTotalCheckout);

        card.getChildren().addAll(lblTitle, calcBox, sep, totalRow);
        return card;
    }

    private VBox createPaymentCard() {
        VBox card = createCardBase();

        Button btnCheckout = new Button("XÁC NHẬN THANH TOÁN");
        btnCheckout.setMaxWidth(Double.MAX_VALUE);
        btnCheckout.setCursor(Cursor.HAND);
        btnCheckout.setStyle("-fx-background-color: linear-gradient(to right, " + COLOR_SUCCESS + ", #059669); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 15; -fx-background-radius: 10;");
        btnCheckout.setOnAction(e -> processCheckout());

        Button btnCancel = new Button("Xóa thông tin");
        btnCancel.setMaxWidth(Double.MAX_VALUE);
        btnCancel.setCursor(Cursor.HAND);
        btnCancel.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_DANGER + "; -fx-font-weight: bold; -fx-padding: 10; -fx-border-color: #fecaca; -fx-border-radius: 8;");
        btnCancel.setOnAction(e -> clearCheckoutForm());

        card.getChildren().addAll(btnCheckout, btnCancel);
        return card;
    }

    // =========================================================================
    // UI HELPERS & LOGIC
    // =========================================================================

    private VBox createCardBase() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 12;");
        card.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.05)));
        return card;
    }

    private void addGridLabel(GridPane grid, String text, int col, int row) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.web(COLOR_TEXT_MUTED));
        grid.add(lbl, col, row);
    }

    private Label createInfoLabel(String defaultText) {
        Label lbl = new Label(defaultText);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        lbl.setTextFill(Color.web(COLOR_TEXT_MAIN));
        lbl.setWrapText(true);
        return lbl;
    }

    private Label addBillRow(VBox parent, String title, String val) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label lblT = new Label(title); lblT.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14)); lblT.setTextFill(Color.web(COLOR_TEXT_MUTED));
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label lblV = new Label(val); lblV.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        row.getChildren().addAll(lblT, sp, lblV);
        parent.getChildren().add(row);
        return lblV;
    }

    // 👉 ĐÃ TÍCH HỢP HÀM NÂNG CẤP XỬ LÝ KHÁCH TRẢ TRỄ QUA NGÀY
    private double tinhPhuThuTraTre(LocalDateTime ngayTraDuKien, double giaPhongMotDem) {
        if (ngayTraDuKien == null) return 0;
        LocalDateTime bayGio = LocalDateTime.now();

        // 1. Nếu khách ở lỳ qua ngày hôm sau mới trả phòng
        if (bayGio.toLocalDate().isAfter(ngayTraDuKien.toLocalDate())) {
            // Tính số ngày trễ (phạt 100% tiền phòng cho mỗi ngày trễ)
            long soNgayTre = ChronoUnit.DAYS.between(ngayTraDuKien.toLocalDate(), bayGio.toLocalDate());
            double phatQuaNgay = soNgayTre * giaPhongMotDem;

            // Tính thêm số giờ lố của ngày cuối cùng khách bước ra khỏi cửa
            int gio = bayGio.getHour();
            if (gio >= 12 && gio < 15) phatQuaNgay += giaPhongMotDem * 0.3; // Trễ 12h-15h: +30%
            else if (gio >= 15 && gio < 18) phatQuaNgay += giaPhongMotDem * 0.5; // Trễ 15h-18h: +50%
            else if (gio >= 18) phatQuaNgay += giaPhongMotDem; // Sau 18h: +100%

            return phatQuaNgay;
        }
        // 2. Nếu trả phòng đúng ngày dự kiến nhưng trễ giờ (sau 12:00 trưa)
        else if (bayGio.toLocalDate().isEqual(ngayTraDuKien.toLocalDate())) {
            int gio = bayGio.getHour();
            if (gio >= 12 && gio < 15) return giaPhongMotDem * 0.3;
            if (gio >= 15 && gio < 18) return giaPhongMotDem * 0.5;
            if (gio >= 18) return giaPhongMotDem;
        }

        return 0; // Trả trước hoặc đúng 12h trưa thì không phạt
    }

    private void recalculateTotal() {
        double vatRate = spinnerVAT.getValue() != null ? spinnerVAT.getValue() : 0.0;
        double discRate = spinnerDiscount.getValue() != null ? spinnerDiscount.getValue() : 0.0;

        double baseTotal = currentRoomTotal + currentServiceTotal + currentSurchargeTotal;

        double vatAmt = baseTotal * (vatRate / 100.0);
        double discAmt = baseTotal * (discRate / 100.0);

        lblVATCheckout.setText(String.format("%,.0f đ", vatAmt));
        lblDiscountCheckout.setText(String.format("-%,.0f đ", discAmt));

        double grandTotal = baseTotal + vatAmt - discAmt;
        if(grandTotal < 0) grandTotal = 0;

        lblGrandTotalCheckout.setText(String.format("%,.0f đ", grandTotal));
    }

    private void loadPhieuDatForCheckout() {
        Task<List<String>> task = new Task<>() {
            @Override protected List<String> call() {
                try {
                    List<PhieuDatPhongDTO> allPhieu = phieuDatPhongService.getAllPhieuDatPhong();
                    if (allPhieu == null) return FXCollections.observableArrayList();

                    List<PhieuDatPhongDTO> activePhieu = allPhieu.stream()
                            .filter(p -> {
                                if (p.getTrangThai() == null) return false;
                                String st = p.getTrangThai().toUpperCase();
                                return st.contains("NHẬN PHÒNG") || st.contains("NHAN PHONG") || st.equals("DANG_O") || st.equals("DA_NHAN_PHONG");
                            }).collect(Collectors.toList());

                    Map<String, List<PhieuDatPhongDTO>> grouped = activePhieu.stream()
                            .collect(Collectors.groupingBy(p -> p.getMaPhieu().split("-")[0]));

                    List<String> displayList = new ArrayList<>();
                    for (Map.Entry<String, List<PhieuDatPhongDTO>> entry : grouped.entrySet()) {
                        List<PhieuDatPhongDTO> group = entry.getValue();
                        String maGoc = entry.getKey();
                        String tenKhach = group.get(0).getTenKhachHang() != null ? group.get(0).getTenKhachHang() : group.get(0).getMaKhachHang();

                        if (group.size() > 1) {
                            displayList.add(maGoc + " - " + group.size() + " phòng (Khách: " + tenKhach + ")");
                        } else {
                            displayList.add(maGoc + " - Phòng " + group.get(0).getMaPhong() + " (Khách: " + tenKhach + ")");
                        }
                    }
                    return displayList;
                } catch (Exception e) {
                    e.printStackTrace();
                    return FXCollections.observableArrayList();
                }
            }
        };

        task.setOnSucceeded(evt -> {
            cbPhieuDatCheckout.getItems().setAll(task.getValue());
            if (cbPhieuDatCheckout.getItems().isEmpty()) {
                cbPhieuDatCheckout.setPromptText("Không có phòng nào đang sử dụng!");
            } else {
                if (preselectedMaPhieu != null && !preselectedMaPhieu.isEmpty()) {
                    String maGocKiemTra = preselectedMaPhieu.split("-")[0];
                    for (String item : cbPhieuDatCheckout.getItems()) {
                        if (item.startsWith(maGocKiemTra)) {
                            cbPhieuDatCheckout.setValue(item);
                            loadCheckoutInfo();
                            break;
                        }
                    }
                } else {
                    cbPhieuDatCheckout.setPromptText("Bấm để chọn phòng cần thanh toán...");
                }
            }
        });
        new Thread(task).start();
    }

    private void loadCheckoutInfo() {
        String selected = cbPhieuDatCheckout.getValue();
        if (selected == null || selected.isEmpty() || selected.equals("Không có phòng nào đang sử dụng!")) {
            clearCheckoutForm();
            return;
        }

        String maPhieuGoc = selected.split(" - ")[0].trim();
        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<PhieuDatPhongDTO> allPhieu = phieuDatPhongService.getAllPhieuDatPhong();
                List<PhieuDatPhongDTO> groupPhieu = allPhieu.stream()
                        .filter(p -> p.getMaPhieu() != null && p.getMaPhieu().startsWith(maPhieuGoc))
                        .collect(Collectors.toList());

                if (groupPhieu.isEmpty()) throw new Exception("Không tìm thấy Phiếu Đặt Phòng trong CSDL.");

                PhieuDatPhongDTO firstPhieu = groupPhieu.get(0);
                KhachHangDTO khach = khachHangService.getKhachHangById(firstPhieu.getMaKhachHang());
                String tenKhach = (khach != null && khach.getHoTen() != null) ? khach.getHoTen() : "Khách vãng lai";

                String allRooms = groupPhieu.stream().map(PhieuDatPhongDTO::getMaPhong).collect(Collectors.joining(", "));

                List<ChiTietHoaDonDTO> allCT = null;
                try {
                    allCT = chiTietHoaDonService.getAllChiTietHoaDon();
                } catch (Exception ignored) { }

                List<ChiTietHoaDonDTO> dsDichVuAll = new ArrayList<>();
                if (allCT != null) {
                    for (PhieuDatPhongDTO p : groupPhieu) {
                        dsDichVuAll.addAll(allCT.stream()
                                .filter(ct -> ct.getMaPhieu() != null && ct.getMaPhieu().equals(p.getMaPhieu()))
                                .collect(Collectors.toList()));
                    }
                }
                final List<ChiTietHoaDonDTO> finalDichVu = dsDichVuAll;

                Platform.runLater(() -> {
                    lblGuestNameCheckout.setText(tenKhach);
                    lblRoomCheckout.setText(allRooms);

                    LocalDateTime in = firstPhieu.getNgayNhan();
                    LocalDateTime out = firstPhieu.getNgayTra() != null ? firstPhieu.getNgayTra() : LocalDateTime.now();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    lblCheckInDateCheckout.setText(in != null ? in.format(formatter) : "—");
                    lblCheckOutDateCheckout.setText(out.format(formatter));

                    currentRoomTotal = 0;
                    currentSurchargeTotal = 0;

                    if (in != null) {
                        long days = ChronoUnit.DAYS.between(in.toLocalDate(), out.toLocalDate());
                        if (days <= 0) days = 1;
                        lblNumDaysCheckout.setText(days + " ngày");

                        try {
                            for (PhieuDatPhongDTO p : groupPhieu) {
                                PhongDTO r = phongService.getPhongById(p.getMaPhong());
                                if (r != null) {
                                    double basePrice = r.getGiaPhong() * days;
                                    currentRoomTotal += basePrice;

                                    double tongTienPhieu = p.getTongTien() != null ? p.getTongTien() : basePrice;
                                    double phuThuSom = tongTienPhieu - basePrice;

                                    if (phuThuSom > 0) {
                                        currentSurchargeTotal += phuThuSom;
                                    }

                                    currentSurchargeTotal += tinhPhuThuTraTre(out, r.getGiaPhong());
                                }
                            }
                        } catch (Exception ignored) { }

                        lblRoomPriceCheckout.setText(groupPhieu.size() > 1 ? "(Nhiều phòng)" : String.format("%,.0f đ", currentRoomTotal/days));
                    }

                    lblRoomTotalCheckout.setText(String.format("%,.0f đ", currentRoomTotal));
                    lblSurchargeCheckout.setText(String.format("%,.0f đ", currentSurchargeTotal));

                    currentServiceTotal = 0;
                    tvServiceDetails.getItems().setAll(finalDichVu);
                    for (ChiTietHoaDonDTO ct : finalDichVu) {
                        double price = ct.getGiaTienTungDichVu();
                        int qty = ct.getSoLuong();
                        currentServiceTotal += (price * qty);
                    }
                    lblServiceTotalCheckout.setText(String.format("%,.0f đ", currentServiceTotal));

                    recalculateTotal();
                });

                return null;
            }
        };

        loadTask.setOnFailed(e -> {
            loadTask.getException().printStackTrace();
            showAlert("Lỗi", "Không thể tải thông tin chi tiết: " + loadTask.getException().getMessage());
            clearCheckoutForm();
        });

        new Thread(loadTask).start();
    }

    private void processCheckout() {
        String selected = cbPhieuDatCheckout.getValue();
        if (selected == null || selected.isEmpty()) {
            showAlert("Lỗi", "Vui lòng chọn Phiếu Đặt trước khi xác nhận thanh toán.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác Nhận Thanh Toán & Trả Phòng");
        confirm.setHeaderText(null);
        confirm.setContentText("Hành động này sẽ đóng hóa đơn và cập nhật phòng thành TRỐNG. Bạn có chắc chắn?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            executeCheckoutLogic(selected.split(" - ")[0].trim());
        }
    }

    private void executeCheckoutLogic(String maPhieuGoc) {
        try {
            List<PhieuDatPhongDTO> allPhieu = phieuDatPhongService.getAllPhieuDatPhong();
            List<PhieuDatPhongDTO> groupPhieu = allPhieu.stream()
                    .filter(p -> p.getMaPhieu() != null && p.getMaPhieu().startsWith(maPhieuGoc))
                    .collect(Collectors.toList());

            if (groupPhieu.isEmpty()) throw new Exception("Không tìm thấy mã phiếu.");

            PhieuDatPhongDTO firstPhieu = groupPhieu.get(0);
            String allRooms = groupPhieu.stream().map(PhieuDatPhongDTO::getMaPhong).collect(Collectors.joining(", "));

            // 1. Tính toán tổng tiền cuối cùng
            double vatRate = spinnerVAT.getValue() != null ? spinnerVAT.getValue() : 0;
            double discRate = spinnerDiscount.getValue() != null ? spinnerDiscount.getValue() : 0;
            double baseTotal = currentRoomTotal + currentServiceTotal + currentSurchargeTotal;
            double tienVAT = baseTotal * (vatRate / 100.0);
            double tienChietKhau = baseTotal * (discRate / 100.0);
            double tongTienCuoiCung = baseTotal + tienVAT - tienChietKhau;

            // 2. MỞ CỔNG THANH TOÁN (Pop-up QR & Tiền mặt)
            Stage currentStage = (Stage) cbPhieuDatCheckout.getScene().getWindow();
            ThanhToanController paymentCtrl = new ThanhToanController(tongTienCuoiCung);

            paymentCtrl.showThanhToanDialog(currentStage, phuongThuc -> {
                if (phuongThuc != null) { // Nếu bấm "Hoàn tất" (không phải "Hủy")
                    try {
                        // 3. Lập Hóa Đơn
                        HoaDonDTO hoaDon = new HoaDonDTO();
                        String maTuDong = "HD" + System.currentTimeMillis();
                        hoaDon.setMaHoaDon(maTuDong);
                        hoaDon.setMaKhachHang(firstPhieu.getMaKhachHang());
                        hoaDon.setMaNhanVien(currentUser != null ? currentUser.getTenDangNhap() : "ADMIN");
                        hoaDon.setNgayLap(LocalDate.now());
                        hoaDon.setTongTienPhong(currentRoomTotal + currentSurchargeTotal);
                        hoaDon.setTongTienDichVu(currentServiceTotal);
                        hoaDon.setThueVAT(tienVAT);
                        hoaDon.setChietKhau(tienChietKhau);
                        hoaDon.setTongTien(tongTienCuoiCung);
                        hoaDon.setTrangThaiThanhToan("Đã Thanh Toán (" + phuongThuc + ")");
                        hoaDon.setMaPhongDat(firstPhieu.getMaPhong());
                        hoaDon.setTenPhong(allRooms);

                        hoaDonService.addHoaDon(hoaDon);

                        // 4. Giải phóng toàn bộ Phòng và Phiếu trong đoàn
                        for (PhieuDatPhongDTO p : groupPhieu) {
                            p.setTrangThai("Trả Phòng");
                            p.setTienCoc(p.getTongTien()); // Cập nhật cọc full để biết đã thu đủ
                            phieuDatPhongService.updatePhieuDatPhong(p);

                            if (p.getMaPhong() != null) {
                                // 👉 Dùng hàm này để chỉ cập nhật đúng cột "Tình trạng", tránh đụng vào các cột khác
                                phongService.updatePhongTrangThai(p.getMaPhong(), "Trống");
                            }
                        }

                        showAlert("Thành Công", String.format("Đã thu tiền và trả phòng cho đoàn thành công!\nSố tiền: %,.0f đ\nPhương thức: %s", tongTienCuoiCung, phuongThuc));
                        clearCheckoutForm();
                        loadPhieuDatForCheckout();

                    } catch (Exception e) {
                        e.printStackTrace();
                        showAlert("Lỗi", "Quá trình lưu hóa đơn thất bại: " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể xử lý thanh toán: " + e.getMessage());
        }
    }

    private void clearCheckoutForm() {
        Platform.runLater(() -> {
            lblGuestNameCheckout.setText("—");
            lblRoomCheckout.setText("—");
            lblCheckInDateCheckout.setText("—");
            lblCheckOutDateCheckout.setText("—");
            lblNumDaysCheckout.setText("0 ngày");
            lblRoomPriceCheckout.setText("0 đ");
            tvServiceDetails.getItems().clear();

            currentRoomTotal = 0;
            currentServiceTotal = 0;
            currentSurchargeTotal = 0;
            lblSurchargeCheckout.setText("0 đ");

            spinnerVAT.getValueFactory().setValue(10.0);
            spinnerDiscount.getValueFactory().setValue(0.0);
            recalculateTotal();
        });
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}