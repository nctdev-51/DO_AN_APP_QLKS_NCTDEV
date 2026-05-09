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
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
    private Label lblRoomTotalCheckout, lblServiceTotalCheckout, lblVATCheckout, lblDiscountCheckout, lblGrandTotalCheckout;
    private TableView<ChiTietHoaDonDTO> tvServiceDetails;
    private ComboBox<String> cbPaymentMethod;
    private Spinner<Double> spinnerVAT;
    private Spinner<Double> spinnerDiscount;

    // Biến lưu trữ tiền để tính toán Real-time
    private double currentRoomTotal = 0.0;
    private double currentServiceTotal = 0.0;


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
            TaiKhoanDTO currentUser, // 👉 ĐÃ THÊM DẤU PHẨY
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

        Label lblSubTitle = new Label("Kiểm tra thông tin lưu trú, dịch vụ và chốt hóa đơn trước khi khách rời đi.");
        lblSubTitle.setFont(Font.font("Segoe UI", 14));
        lblSubTitle.setTextFill(Color.web(COLOR_TEXT_MUTED));

        header.getChildren().addAll(lblTitle, lblSubTitle);
        root.setTop(header);

        // --- MAIN LAYOUT (2 CỘT) ---
        HBox contentArea = new HBox(25);
        contentArea.setPadding(new Insets(20, 30, 30, 30));

        VBox leftCol = new VBox(20);
        HBox.setHgrow(leftCol, Priority.ALWAYS);
        leftCol.getChildren().addAll(createSelectionCard(), createGuestInfoCard(), createServiceDetailsCard());

        VBox rightCol = new VBox(20);
        rightCol.setPrefWidth(420);
        rightCol.getChildren().addAll(createInvoiceCard(), createPaymentCard());

        contentArea.getChildren().addAll(leftCol, rightCol);
        root.setCenter(contentArea);

        // Bắt đầu quá trình tải danh sách phiếu đặt phòng (bất đồng bộ)
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
        cbPhieuDatCheckout.setStyle("-fx-font-size: 14px; -fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-padding: 4;");

        // CẬP NHẬT GIAO DIỆN KHI CHỌN KHÁC (Tự động Tải Dữ Liệu)
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
        grid.setHgap(40); grid.setVgap(15);

        addGridLabel(grid, "Khách Hàng:", 0, 0);
        lblGuestNameCheckout = createInfoLabel("—");
        grid.add(lblGuestNameCheckout, 1, 0);

        addGridLabel(grid, "Mã Phòng:", 0, 1);
        lblRoomCheckout = createInfoLabel("—");
        grid.add(lblRoomCheckout, 1, 1);

        addGridLabel(grid, "Ngày Nhận:", 2, 0);
        lblCheckInDateCheckout = createInfoLabel("—");
        grid.add(lblCheckInDateCheckout, 3, 0);

        addGridLabel(grid, "Ngày Trả:", 2, 1);
        lblCheckOutDateCheckout = createInfoLabel("—");
        grid.add(lblCheckOutDateCheckout, 3, 1);

        addGridLabel(grid, "Tổng Số Ngày:", 4, 0);
        lblNumDaysCheckout = createInfoLabel("0 ngày");
        lblNumDaysCheckout.setTextFill(Color.web(COLOR_PRIMARY));
        grid.add(lblNumDaysCheckout, 5, 0);

        addGridLabel(grid, "Giá Phòng/Ngày:", 4, 1);
        lblRoomPriceCheckout = createInfoLabel("0 đ");
        grid.add(lblRoomPriceCheckout, 5, 1);

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
        // FIX: Nếu không có getThanhTien() thì tự tính Thành tiền = SL * Đơn Giá
        colThanhTien.setCellValueFactory(param -> {
            // Xóa luôn check null cho getGiaTienTungDichVu()
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

        // VAT Spinner
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

        // Discount Spinner
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

        // Grand Total
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

        Label lblMethod = new Label("Phương thức thanh toán:");
        lblMethod.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        cbPaymentMethod = new ComboBox<>(FXCollections.observableArrayList("Tiền Mặt", "Thẻ Tín Dụng / Ghi Nợ", "Chuyển Khoản Ngân Hàng", "Ví Điện Tử"));
        cbPaymentMethod.setValue("Tiền Mặt");
        cbPaymentMethod.setMaxWidth(Double.MAX_VALUE);
        cbPaymentMethod.setStyle("-fx-font-size: 14px; -fx-background-color: #f8fafc; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-padding: 5;");

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

        card.getChildren().addAll(lblMethod, cbPaymentMethod, new Region(), btnCheckout, btnCancel);
        return card;
    }

    // =========================================================================
    // UI HELPERS & LOGIC (ĐÃ FIX CHECK NULL)
    // =========================================================================

    private VBox createCardBase() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 15;");
        card.setEffect(new DropShadow(15, Color.web("#000000", 0.04)));
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

    private void recalculateTotal() {
        double vatRate = spinnerVAT.getValue() != null ? spinnerVAT.getValue() : 0.0;
        double discRate = spinnerDiscount.getValue() != null ? spinnerDiscount.getValue() : 0.0;

        double baseTotal = currentRoomTotal + currentServiceTotal;
        double vatAmt = baseTotal * (vatRate / 100.0);
        double discAmt = baseTotal * (discRate / 100.0);

        lblVATCheckout.setText(String.format("%,.0f đ", vatAmt));
        lblDiscountCheckout.setText(String.format("-%,.0f đ", discAmt));

        double grandTotal = baseTotal + vatAmt - discAmt;
        if(grandTotal < 0) grandTotal = 0;

        lblGrandTotalCheckout.setText(String.format("%,.0f đ", grandTotal));
    }

    // 1. TẢI DANH SÁCH PHIẾU ĐẶT ĐANG "NHẬN PHÒNG" VÀO COMBOBOX
    private void loadPhieuDatForCheckout() {
        Task<List<String>> task = new Task<>() {
            @Override protected List<String> call() {
                try {
                    List<PhieuDatPhongDTO> allPhieu = phieuDatPhongService.getAllPhieuDatPhong();
                    if (allPhieu == null) return FXCollections.observableArrayList();

                    return allPhieu.stream()
                            .filter(p -> {
                                if (p.getTrangThai() == null) return false;
                                String st = p.getTrangThai().toUpperCase();
                                return st.contains("NHẬN PHÒNG") || st.contains("NHAN PHONG") || st.equals("DANG_O") || st.equals("DA_NHAN_PHONG");
                            })
                            .map(p -> p.getMaPhieu() + " - Phòng " + p.getMaPhong() + " (Khách: " + (p.getTenKhachHang() != null ? p.getTenKhachHang() : p.getMaKhachHang()) + ")")
                            .collect(Collectors.toList());
                } catch (Exception e) {
                    e.printStackTrace();
                    return FXCollections.observableArrayList();
                }
            }
        };
        // 3. Tìm đến hàm loadPhieuDatForCheckout() và sửa lại đoạn task.setOnSucceeded
        task.setOnSucceeded(evt -> {
            cbPhieuDatCheckout.getItems().setAll(task.getValue());
            if (cbPhieuDatCheckout.getItems().isEmpty()) {
                cbPhieuDatCheckout.setPromptText("Không có phòng nào đang sử dụng!");
            } else {
                // 👉 TỰ ĐỘNG TÌM VÀ CHỌN PHIẾU NẾU ĐƯỢC TRUYỀN TỪ MÀN HÌNH QUẢN LÝ SANG
                if (preselectedMaPhieu != null && !preselectedMaPhieu.isEmpty()) {
                    for (String item : cbPhieuDatCheckout.getItems()) {
                        if (item.contains(preselectedMaPhieu)) {
                            cbPhieuDatCheckout.setValue(item);
                            // SetValue sẽ tự động kích hoạt hàm loadCheckoutInfo() luôn
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

    // 2. KHI CHỌN PHÒNG THÌ HIỂN THỊ THÔNG TIN CHI TIẾT RA MÀN HÌNH
    private void loadCheckoutInfo() {
        String selected = cbPhieuDatCheckout.getValue();
        if (selected == null || selected.isEmpty() || selected.equals("Không có phòng nào đang sử dụng!")) {
            clearCheckoutForm();
            return;
        }

        String maPhieu = selected.split(" - ")[0].trim();
        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                PhieuDatPhongDTO phieu = phieuDatPhongService.getPhieuDatPhongById(maPhieu);
                if (phieu == null) throw new Exception("Không tìm thấy Phiếu Đặt Phòng trong CSDL.");

                // 1. Info Khách Hàng
                KhachHangDTO khach = khachHangService.getKhachHangById(phieu.getMaKhachHang());
                String tenKhach = (khach != null && khach.getHoTen() != null) ? khach.getHoTen() : "Khách vãng lai";

                // 2. Info Phòng
                PhongDTO room = null;
                if (phieu.getMaPhong() != null) {
                    room = phongService.getPhongById(phieu.getMaPhong());
                }
                final PhongDTO finalRoom = room;

                // 3. Info Dịch vụ
                List<ChiTietHoaDonDTO> dsDichVu = null;
                try {
                    List<ChiTietHoaDonDTO> allCT = chiTietHoaDonService.getAllChiTietHoaDon();
                    if (allCT != null) {
                        dsDichVu = allCT.stream()
                                .filter(ct -> ct.getMaPhieu() != null && ct.getMaPhieu().equals(maPhieu))
                                .collect(Collectors.toList());
                    }
                } catch (Exception ignored) {}
                final List<ChiTietHoaDonDTO> finalDichVu = dsDichVu != null ? dsDichVu : FXCollections.observableArrayList();

                // Cập nhật giao diện trên UI Thread
                Platform.runLater(() -> {
                    lblGuestNameCheckout.setText(tenKhach);
                    lblRoomCheckout.setText(phieu.getMaPhong() != null ? phieu.getMaPhong() : "—");

                    // 👉 ĐÃ FIX: Sử dụng LocalDateTime thay vì LocalDate
                    LocalDateTime in = phieu.getNgayNhan();
                    LocalDateTime out = phieu.getNgayTra() != null ? phieu.getNgayTra() : LocalDateTime.now();

                    // Cập nhật giao diện với format có cả giờ và phút
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    lblCheckInDateCheckout.setText(in != null ? in.format(formatter) : "—");
                    lblCheckOutDateCheckout.setText(out.format(formatter));

                    // Tính tiền phòng
                    currentRoomTotal = 0;
                    if (in != null) {
                        // 👉 ĐÃ FIX: Ép về LocalDate để đếm số ngày lưu trú chính xác
                        long days = ChronoUnit.DAYS.between(in.toLocalDate(), out.toLocalDate());
                        if (days <= 0) days = 1;
                        lblNumDaysCheckout.setText(days + " ngày");

                        if (finalRoom != null) {
                            lblRoomPriceCheckout.setText(String.format("%,.0f đ", finalRoom.getGiaPhong()));
                            currentRoomTotal = finalRoom.getGiaPhong() * days;
                        }
                    }
                    lblRoomTotalCheckout.setText(String.format("%,.0f đ", currentRoomTotal));

                    // Tính tiền dịch vụ
                    currentServiceTotal = 0;
                    tvServiceDetails.getItems().setAll(finalDichVu);
                    for (ChiTietHoaDonDTO ct : finalDichVu) {
                        // Xóa phần check null
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

    private void executeCheckoutLogic(String maPhieu) {
        try {
            PhieuDatPhongDTO phieu = phieuDatPhongService.getPhieuDatPhongById(maPhieu);
            if (phieu == null) throw new Exception("Không tìm thấy mã phiếu.");

            // Lấy Phòng
            PhongDTO phong = null;
            if (phieu.getMaPhong() != null) phong = phongService.getPhongById(phieu.getMaPhong());

            // Tính tổng
            double vatRate = spinnerVAT.getValue() != null ? spinnerVAT.getValue() : 0;
            double discRate = spinnerDiscount.getValue() != null ? spinnerDiscount.getValue() : 0;

            double baseTotal = currentRoomTotal + currentServiceTotal;
            double tienVAT = baseTotal * (vatRate / 100.0);
            double tienChietKhau = baseTotal * (discRate / 100.0);
            double tongTien = baseTotal + tienVAT - tienChietKhau;

            // Lập hóa đơn
            HoaDonDTO hoaDon = new HoaDonDTO();
            hoaDon.setMaKhachHang(phieu.getMaKhachHang());
            hoaDon.setMaNhanVien(currentUser != null ? currentUser.getTenDangNhap() : "ADMIN");
            hoaDon.setNgayLap(LocalDate.now());
            hoaDon.setTongTienPhong(currentRoomTotal);
            hoaDon.setTongTienDichVu(currentServiceTotal);
            hoaDon.setThueVAT(tienVAT);
            hoaDon.setChietKhau(tienChietKhau);
            hoaDon.setTongTien(tongTien);
            hoaDon.setTrangThaiThanhToan("Đã Thanh Toán");
            hoaDon.setMaPhongDat(phieu.getMaPhong());
            hoaDon.setTenPhong(phong != null ? phong.getTenPhong() : "Unknown");

            hoaDonService.addHoaDon(hoaDon);

            // Cập nhật Phiếu
            phieu.setTrangThai("Trả Phòng");
            phieuDatPhongService.updatePhieuDatPhong(phieu);

            // Cập nhật Phòng
            if (phong != null) {
                phong.setTinhTrang("Trống");
                phongService.updatePhong(phong);
            }

            showAlert("Thành Công", String.format("Thanh toán hoàn tất!\nThu về: %,.0f đ\nPhương thức: %s", tongTien, cbPaymentMethod.getValue()));
            clearCheckoutForm();
            loadPhieuDatForCheckout();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Quá trình thanh toán thất bại: " + e.getMessage());
        }
    }

    private void clearCheckoutForm() {
        Platform.runLater(() -> {
            // Không set cbPhieuDatCheckout.setValue(null) ở đây nếu nó được gọi từ sự kiện onAction của chính ComboBox
            lblGuestNameCheckout.setText("—");
            lblRoomCheckout.setText("—");
            lblCheckInDateCheckout.setText("—");
            lblCheckOutDateCheckout.setText("—");
            lblNumDaysCheckout.setText("0 ngày");
            lblRoomPriceCheckout.setText("0 đ");
            tvServiceDetails.getItems().clear();

            currentRoomTotal = 0; currentServiceTotal = 0;
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

    // Các hàm này nếu không cần nữa có thể xóa, hoặc giữ lại làm Interface
    public boolean processCheckIn(String maPhieu) { return false; }
    public boolean processBooking(String maKhachHang, String maPhong, LocalDate ngayNhan, LocalDate ngayTra) { return false; }
    public boolean cancelBooking(String maPhieu) { return false; }
    public boolean completeMaintenance(String maPhong) { return false; }
}