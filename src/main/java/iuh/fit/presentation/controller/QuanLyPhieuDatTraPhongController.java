package iuh.fit.presentation.controller;

import iuh.fit.core.dto.*;
import iuh.fit.core.service.*;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller tổng hợp cho Quản lý Phiếu Đặt và Trả Phòng & Thanh Toán
 * Cung cấp 2 tab chính:
 * - Tab 1: Quản lý Phiếu Đặt Phòng
 * - Tab 2: Trả Phòng & Thanh Toán
 */
public class QuanLyPhieuDatTraPhongController {

    private static final Logger logger = LoggerFactory.getLogger(QuanLyPhieuDatTraPhongController.class);

    private final IPhieuDatPhongService phieuDatPhongService;
    private final IPhongService phongService;
    private final IHoaDonService hoaDonService;
    private final TaiKhoanDTO currentUser;

    // UI Components - Quản lý Phiếu Đặt
    private TableView<PhieuDatPhongDTO> tvPhieuDat;
    private TextField txtSearchPhieu;
    private ComboBox<String> cbTrangThaiPhieu;
    private DatePicker dpFromDate;
    private DatePicker dpToDate;
    private Label lblTotalPhieu;
    private Label lblWaitingPhieu;
    private Label lblConfirmedPhieu;

    // UI Components - Trả Phòng
    private ComboBox<String> cbPhieuDatCheckout;
    private Label lblGuestNameCheckout;
    private Label lblRoomCheckout;
    private Label lblCheckInDateCheckout;
    private Label lblCheckOutDateCheckout;
    private Label lblNumDaysCheckout;
    private Label lblRoomPriceCheckout;
    private Label lblRoomTotalCheckout;
    private Label lblServiceTotalCheckout;
    private Label lblVATCheckout;
    private Label lblDiscountCheckout;
    private Label lblGrandTotalCheckout;
    private TableView<ChiTietHoaDonDTO> tvServiceDetails;
    private ComboBox<String> cbPaymentMethod;
    private Spinner<Double> spinnerVAT;
    private Spinner<Double> spinnerDiscount;

    // Colors
    private final String COLOR_PRIMARY = "#0066cc";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_DANGER = "#ef4444";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b"; // FIX: thêm hằng bị thiếu
    private final String COLOR_BG_LIGHT = "#f8fafc";
    private final String COLOR_CARD_BG = "white";
    private final String COLOR_BORDER = "#e2e8f0";

    // FIX: constructor mở rộng khớp với cách gọi từ MainController (7 tham số)
    public QuanLyPhieuDatTraPhongController(IPhieuDatPhongService phieuDatPhongService,
                                            IPhongService phongService,
                                            IKhachHangService khachHangService,
                                            IHoaDonService hoaDonService,
                                            IChiTietHoaDonService chiTietHoaDonService,
                                            IDichVuService dichVuService,
                                            TaiKhoanDTO currentUser) {
        this.phieuDatPhongService = phieuDatPhongService;
        this.phongService = phongService;
        this.hoaDonService = hoaDonService;
        this.currentUser = currentUser;
        // khachHangService, chiTietHoaDonService, dichVuService hiện chưa dùng trong class này
    }

    /**
     * Tạo view chính với TabPane chứa 2 tab
     */
    public BorderPane createMainView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + ";");

        // Header
        VBox header = createHeader();
        root.setTop(header);

        // Tab Pane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-font-size: 13px;");

        // Tab 1: Quản lý Phiếu Đặt
        Tab tabPhieuDat = new Tab("📋 Quản lý Phiếu Đặt", createQuanLyPhieuDatPane());
        tabPhieuDat.setStyle("-fx-padding: 0;");

        // Tab 2: Trả Phòng & Thanh Toán
        Tab tabCheckout = new Tab("💳 Trả Phòng & Thanh Toán", createTraPhongThanhToanPane());
        tabCheckout.setStyle("-fx-padding: 0;");

        tabPane.getTabs().addAll(tabPhieuDat, tabCheckout);

        root.setCenter(tabPane);
        return root;
    }

    /**
     * Tạo header với tiêu đề
     */
    private VBox createHeader() {
        VBox header = new VBox(8);
        header.setPadding(new Insets(20, 25, 15, 25));
        header.setStyle("-fx-background-color: white; -fx-border-width: 0 0 1 0; -fx-border-color: " + COLOR_BORDER + ";");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.05));
        shadow.setRadius(5);
        header.setEffect(shadow);

        Label lblTitle = new Label("🏢 Quản Lý Phiếu Đặt & Trả Phòng");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 24));
        lblTitle.setTextFill(Color.web(COLOR_PRIMARY));

        Label lblSubtitle = new Label("Quản lý booking, theo dõi phiếu đặt và xử lý thanh toán khi khách trả phòng");
        lblSubtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        lblSubtitle.setTextFill(Color.web(COLOR_TEXT_MUTED));

        header.getChildren().addAll(lblTitle, lblSubtitle);
        return header;
    }

    /**
     * Tạo pane cho Quản lý Phiếu Đặt
     */
    private VBox createQuanLyPhieuDatPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20, 25, 25, 25));
        pane.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + ";");

        // Filter Bar
        HBox filterBar = createFilterPhieuBar();
        pane.getChildren().add(filterBar);

        // Stat Cards
        HBox statCards = createStatCardsPhieu();
        pane.getChildren().add(statCards);

        // Table
        VBox tableCard = createPhieuDatTable();
        VBox.setVgrow(tableCard, Priority.ALWAYS);
        pane.getChildren().add(tableCard);

        // Load data
        loadPhieuDatData();

        return pane;
    }

    /**
     * Tạo filter bar cho Phiếu Đặt
     */
    private HBox createFilterPhieuBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(14, 16, 14, 16));
        bar.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 10; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 10; -fx-border-width: 1;");

        // Search
        Label lblSearch = new Label("🔍 Tìm kiếm:");
        lblSearch.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        txtSearchPhieu = new TextField();
        txtSearchPhieu.setPromptText("Mã phiếu, tên khách, số phòng...");
        txtSearchPhieu.setPrefWidth(250);

        // Status Filter
        Label lblStatus = new Label("Trạng thái:");
        lblStatus.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        cbTrangThaiPhieu = new ComboBox<>();
        cbTrangThaiPhieu.getItems().addAll("Tất cả", "Đặt Phòng", "Nhận Phòng", "Trả Phòng", "Hủy");
        cbTrangThaiPhieu.setValue("Tất cả");
        cbTrangThaiPhieu.setPrefWidth(150);

        // Date Range
        Label lblFrom = new Label("Từ ngày:");
        lblFrom.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        dpFromDate = new DatePicker(LocalDate.now().minusMonths(1));
        dpFromDate.setPrefWidth(130);

        Label lblTo = new Label("Đến ngày:");
        lblTo.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        dpToDate = new DatePicker(LocalDate.now());
        dpToDate.setPrefWidth(130);

        // Search Button
        Button btnSearch = new Button("🔎 Tìm");
        btnSearch.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;");
        btnSearch.setOnAction(e -> loadPhieuDatData());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bar.getChildren().addAll(
                lblSearch, txtSearchPhieu,
                lblStatus, cbTrangThaiPhieu,
                lblFrom, dpFromDate,
                lblTo, dpToDate,
                spacer, btnSearch
        );

        return bar;
    }

    /**
     * Tạo stat cards cho Phiếu Đặt
     */
    private HBox createStatCardsPhieu() {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox c1 = createStatCard("Tổng Phiếu", "0", "#3b82f6", "#1d4ed8");
        VBox c2 = createStatCard("Đang Chờ", "0", "#f59e0b", "#b45309");
        VBox c3 = createStatCard("Đã Xác Nhận", "0", "#10b981", "#047857");

        lblTotalPhieu = (Label) c1.getChildren().get(1);
        lblWaitingPhieu = (Label) c2.getChildren().get(1);
        lblConfirmedPhieu = (Label) c3.getChildren().get(1);

        HBox.setHgrow(c1, Priority.ALWAYS);
        HBox.setHgrow(c2, Priority.ALWAYS);
        HBox.setHgrow(c3, Priority.ALWAYS);

        row.getChildren().addAll(c1, c2, c3);
        return row;
    }

    /**
     * Tạo table cho Phiếu Đặt
     */
    private VBox createPhieuDatTable() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 10; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 10; -fx-border-width: 1;");

        Label lblTable = new Label("Danh Sách Phiếu Đặt");
        lblTable.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTable.setTextFill(Color.web(COLOR_TEXT_MAIN));

        tvPhieuDat = new TableView<>();
        tvPhieuDat.setColumnResizePolicy(param -> true);

        TableColumn<PhieuDatPhongDTO, String> colMa = new TableColumn<>("Mã Phiếu"); // FIX: removed misplaced @SuppressWarnings
        colMa.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getMaPhieu()));

        TableColumn<PhieuDatPhongDTO, String> colKhach = new TableColumn<>("Khách Hàng");
        colKhach.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(
                param.getValue().getMaKhachHang() != null ? param.getValue().getMaKhachHang() : "N/A"
        ));

        TableColumn<PhieuDatPhongDTO, String> colPhong = new TableColumn<>("Phòng");
        colPhong.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(
                param.getValue().getMaPhong() != null ? param.getValue().getMaPhong() : "N/A"
        ));

        TableColumn<PhieuDatPhongDTO, LocalDate> colNgayNhan = new TableColumn<>("Ngày Nhận");
        colNgayNhan.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getNgayNhan()));

        TableColumn<PhieuDatPhongDTO, LocalDate> colNgayTra = new TableColumn<>("Ngày Trả");
        colNgayTra.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getNgayTra()));

        TableColumn<PhieuDatPhongDTO, String> colTrangThai = new TableColumn<>("Trạng Thái");
        colTrangThai.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(
                param.getValue().getTrangThai() != null ? param.getValue().getTrangThai() : "N/A"
        ));

        tvPhieuDat.getColumns().addAll(colMa, colKhach, colPhong, colNgayNhan, colNgayTra, colTrangThai);

        card.getChildren().addAll(lblTable, tvPhieuDat);
        VBox.setVgrow(tvPhieuDat, Priority.ALWAYS);
        return card;
    }

    /**
     * Tạo pane cho Trả Phòng & Thanh Toán
     */
    private VBox createTraPhongThanhToanPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(20, 25, 25, 25));
        pane.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + ";");

        ScrollPane scrollPane = new ScrollPane(createTraPhongContent());
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent;");
        pane.getChildren().add(scrollPane);

        return pane;
    }

    /**
     * Tạo nội dung cho Trả Phòng
     */
    private VBox createTraPhongContent() {
        VBox content = new VBox(15);

        // Selection Card
        VBox selectionCard = createPhieuSelectionCard();
        content.getChildren().add(selectionCard);

        // Guest Info Card
        VBox guestCard = createGuestInfoCard();
        content.getChildren().add(guestCard);

        // Service Details
        VBox serviceCard = createServiceDetailsCard();
        content.getChildren().add(serviceCard);

        // Invoice Summary
        VBox invoiceCard = createInvoiceSummaryCard();
        content.getChildren().add(invoiceCard);

        // Payment Card
        VBox paymentCard = createPaymentCard();
        content.getChildren().add(paymentCard);

        return content;
    }

    /**
     * Tạo card chọn Phiếu Đặt để trả phòng
     */
    private VBox createPhieuSelectionCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 10; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 10;");

        Label lblTitle = new Label("🔍 Chọn Phiếu Đặt để Trả Phòng");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        HBox selection = new HBox(15);
        selection.setAlignment(Pos.CENTER_LEFT);

        Label lblPhieu = new Label("Phiếu Đặt:");
        lblPhieu.setStyle("-fx-font-weight: bold;");
        cbPhieuDatCheckout = new ComboBox<>();
        cbPhieuDatCheckout.setPrefWidth(350);
        cbPhieuDatCheckout.setStyle("-fx-font-size: 13px;");
        loadPhieuDatForCheckout();

        Button btnLoad = new Button("📥 Tải Thông Tin");
        btnLoad.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;");
        btnLoad.setOnAction(e -> loadCheckoutInfo());

        selection.getChildren().addAll(lblPhieu, cbPhieuDatCheckout, btnLoad);
        card.getChildren().addAll(lblTitle, selection);

        return card;
    }

    /**
     * Tạo card hiển thị thông tin khách & phòng
     */
    private VBox createGuestInfoCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 10; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 10;");

        Label lblTitle = new Label("👤 Thông Tin Khách & Phòng");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(12);

        // Column 1
        addGridLabel(grid, "Khách Hàng:", 0, 0);
        lblGuestNameCheckout = createInfoLabel("—");
        grid.add(lblGuestNameCheckout, 1, 0);

        addGridLabel(grid, "Phòng:", 0, 1);
        lblRoomCheckout = createInfoLabel("—");
        grid.add(lblRoomCheckout, 1, 1);

        // Column 2
        addGridLabel(grid, "Ngày Nhận:", 1, 0);
        lblCheckInDateCheckout = createInfoLabel("—");
        grid.add(lblCheckInDateCheckout, 2, 0);

        addGridLabel(grid, "Ngày Trả:", 1, 1);
        lblCheckOutDateCheckout = createInfoLabel("—");
        grid.add(lblCheckOutDateCheckout, 2, 1);

        // Column 3
        addGridLabel(grid, "Số Ngày:", 2, 0);
        lblNumDaysCheckout = createInfoLabel("0");
        grid.add(lblNumDaysCheckout, 3, 0);

        addGridLabel(grid, "Giá Phòng/Ngày:", 2, 1);
        lblRoomPriceCheckout = createInfoLabel("0 đ");
        grid.add(lblRoomPriceCheckout, 3, 1);

        card.getChildren().addAll(lblTitle, grid);
        return card;
    }

    /**
     * Tạo card chi tiết dịch vụ
     */
    private VBox createServiceDetailsCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 10; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 10;");

        Label lblTitle = new Label("🍽️ Chi Tiết Dịch Vụ");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        tvServiceDetails = new TableView<>();
        tvServiceDetails.setPrefHeight(200);
        tvServiceDetails.setColumnResizePolicy(param -> true);

        TableColumn<ChiTietHoaDonDTO, String> colTen = new TableColumn<>("Tên Dịch Vụ");
        colTen.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(
                param.getValue().getTenDichVu() != null ? param.getValue().getTenDichVu() : "—"
        ));

        TableColumn<ChiTietHoaDonDTO, Integer> colQty = new TableColumn<>("Số Lượng");
        colQty.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getSoLuong()));

        TableColumn<ChiTietHoaDonDTO, Double> colGia = new TableColumn<>("Giá Đơn Vị");
        colGia.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getGiaTienTungDichVu()));
        colGia.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : String.format("%,.0f", item) + " đ");
            }
        });

        TableColumn<ChiTietHoaDonDTO, Double> colThanhTien = new TableColumn<>("Thành Tiền");
        colThanhTien.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getThanhTien()));
        colThanhTien.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : String.format("%,.0f", item) + " đ");
            }
        });

        // FIX: tránh unchecked raw-type array
        tvServiceDetails.getColumns().addAll(colTen, colQty, colGia, colThanhTien);
        card.getChildren().addAll(lblTitle, tvServiceDetails);
        VBox.setVgrow(tvServiceDetails, Priority.ALWAYS);

        return card;
    }

    /**
     * Tạo card tóm tắt hóa đơn
     */
    private VBox createInvoiceSummaryCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 10; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 10;");

        Label lblTitle = new Label("💰 Tính Toán Hóa Đơn");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(10);

        // Row 1
        addGridLabel(grid, "Tiền Phòng:", 0, 0);
        lblRoomTotalCheckout = createAmountLabel("0 đ");
        grid.add(lblRoomTotalCheckout, 1, 0);

        addGridLabel(grid, "Tiền Dịch Vụ:", 0, 1);
        lblServiceTotalCheckout = createAmountLabel("0 đ");
        grid.add(lblServiceTotalCheckout, 1, 1);

        // Row 2
        addGridLabel(grid, "VAT (%):", 1, 0);
        spinnerVAT = new Spinner<>(0, 100, 10, 1);
        grid.add(spinnerVAT, 2, 0);

        lblVATCheckout = createAmountLabel("0 đ");
        grid.add(lblVATCheckout, 3, 0);

        addGridLabel(grid, "Chiết Khấu (%):", 1, 1);
        spinnerDiscount = new Spinner<>(0, 100, 0, 1);
        grid.add(spinnerDiscount, 2, 1);

        lblDiscountCheckout = createAmountLabel("0 đ");
        grid.add(lblDiscountCheckout, 3, 1);

        // Row 3 - Total
        Label lblGrandTotal = new Label("TỔNG CỘNG:");
        lblGrandTotal.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 14));
        lblGrandTotal.setTextFill(Color.web(COLOR_PRIMARY));
        grid.add(lblGrandTotal, 0, 2);

        lblGrandTotalCheckout = new Label("0 đ");
        lblGrandTotalCheckout.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 18));
        lblGrandTotalCheckout.setTextFill(Color.web(COLOR_DANGER));
        grid.add(lblGrandTotalCheckout, 1, 2);

        card.getChildren().addAll(lblTitle, grid);
        return card;
    }

    /**
     * Tạo card thanh toán
     */
    private VBox createPaymentCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 10; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 10;");

        Label lblTitle = new Label("💳 Xử Lý Thanh Toán");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        HBox paymentBox = new HBox(15);
        paymentBox.setAlignment(Pos.CENTER_LEFT);

        Label lblMethod = new Label("Phương Thức:");
        lblMethod.setStyle("-fx-font-weight: bold;");
        cbPaymentMethod = new ComboBox<>();
        cbPaymentMethod.getItems().addAll("Tiền Mặt", "Thẻ Debit", "Thẻ Credit", "Chuyển Khoản", "Khác");
        cbPaymentMethod.setValue("Tiền Mặt");
        cbPaymentMethod.setPrefWidth(200);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnCheckout = new Button("✅ Hoàn Tất Trả Phòng");
        btnCheckout.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-font-size: 13px; -fx-background-radius: 6;");
        btnCheckout.setOnAction(e -> processCheckout());

        Button btnCancel = new Button("❌ Hủy");
        btnCancel.setStyle("-fx-background-color: " + COLOR_DANGER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-font-size: 13px; -fx-background-radius: 6;");
        btnCancel.setOnAction(e -> clearCheckoutForm());

        paymentBox.getChildren().addAll(lblMethod, cbPaymentMethod, spacer, btnCheckout, btnCancel);
        card.getChildren().addAll(lblTitle, paymentBox);

        return card;
    }

    // ============================================================
    // Helper Methods
    // ============================================================

    /**
     * Tạo stat card
     */
    private VBox createStatCard(String label, String value, String colorStart, String colorEnd) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(12, 14, 12, 14));
        card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, " + colorStart + ", " + colorEnd + ");" +
                        "-fx-background-radius: 10; -fx-border-radius: 10;"
        );

        Label lbl = new Label(label);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        lbl.setTextFill(Color.web("#ffffff", 0.85));

        Label val = new Label(value);
        val.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 24));
        val.setTextFill(Color.WHITE);

        card.getChildren().addAll(lbl, val);
        return card;
    }

    /**
     * Tạo label thông tin
     */
    private Label createInfoLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 12));
        lbl.setTextFill(Color.web(COLOR_TEXT_MAIN));
        return lbl;
    }

    /**
     * Tạo label hiển thị số tiền
     */
    private Label createAmountLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lbl.setTextFill(Color.web(COLOR_PRIMARY));
        return lbl;
    }

    /**
     * Thêm label vào grid
     */
    private void addGridLabel(GridPane grid, String text, int col, int row) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        grid.add(lbl, col, row);
    }

    // ============================================================
    // Data Loading Methods
    // ============================================================

    /**
     * Tải dữ liệu Phiếu Đặt
     */
    private void loadPhieuDatData() {
        Task<List<PhieuDatPhongDTO>> task = new Task<>() {
            @Override
            protected List<PhieuDatPhongDTO> call() {
                try {
                    List<PhieuDatPhongDTO> all = phieuDatPhongService.getAllPhieuDatPhong();
                    String keyword = txtSearchPhieu.getText() != null ? txtSearchPhieu.getText().trim().toLowerCase() : "";
                    String status = cbTrangThaiPhieu.getValue();
                    LocalDate from = dpFromDate.getValue();
                    LocalDate to = dpToDate.getValue();

                    return all.stream()
                            .filter(p -> keyword.isEmpty() ||
                                    (p.getMaPhieu() != null && p.getMaPhieu().toLowerCase().contains(keyword)) ||
                                    (p.getMaKhachHang() != null && p.getMaKhachHang().toLowerCase().contains(keyword)))
                            .filter(p -> "Tất cả".equals(status) || (p.getTrangThai() != null && p.getTrangThai().equals(status)))
                            .filter(p -> p.getNgayNhan() != null && !p.getNgayNhan().isBefore(from) && !p.getNgayNhan().isAfter(to))
                            .collect(Collectors.toList());
                } catch (Exception e) {
                    logger.error("Lỗi khi tải phiếu đặt phòng", e);
                    return new java.util.ArrayList<>();
                }
            }
        };

        task.setOnSucceeded(evt -> {
            List<PhieuDatPhongDTO> data = task.getValue();
            tvPhieuDat.getItems().setAll(data);
            updateStatPhieu(data);
        });

        task.setOnFailed(evt -> {
            System.err.println("Lỗi tải phiếu đặt");
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    /**
     * Cập nhật stat cards
     */
    private void updateStatPhieu(List<PhieuDatPhongDTO> data) {
        lblTotalPhieu.setText(String.valueOf(data.size()));
        long waiting = data.stream().filter(p -> "Đặt Phòng".equals(p.getTrangThai())).count();
        long confirmed = data.stream().filter(p -> "Nhận Phòng".equals(p.getTrangThai())).count();
        lblWaitingPhieu.setText(String.valueOf(waiting));
        lblConfirmedPhieu.setText(String.valueOf(confirmed));
    }

    /**
     * Tải danh sách Phiếu Đặt cho checkout
     */
    private void loadPhieuDatForCheckout() {
        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() {
                try {
                    return phieuDatPhongService.getAllPhieuDatPhong().stream()
                            .filter(p -> "Nhận Phòng".equals((p.getTrangThai())))
                            .map(p -> p.getMaPhieu() + " - " + (p.getMaKhachHang() != null ? p.getMaKhachHang() : "N/A"))
                            .collect(Collectors.toList());
                } catch (Exception e) {
                    return new java.util.ArrayList<>();
                }
            }
        };

        task.setOnSucceeded(evt -> {
            List<String> items = task.getValue();
            cbPhieuDatCheckout.getItems().setAll(items);
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    /**
     * Tải thông tin checkout
     */
    private void loadCheckoutInfo() {
        String selected = cbPhieuDatCheckout.getValue();
        if (selected == null || selected.isEmpty()) {
            showAlert("Thông báo", "Vui lòng chọn phiếu đặt");
            return;
        }

        String maPhieu = selected.split(" - ")[0];
        try {
            PhieuDatPhongDTO phieu = phieuDatPhongService.getPhieuDatPhongById(maPhieu);
            if (phieu != null) {
                // Update guest info - using ma instead of ten
                lblGuestNameCheckout.setText(phieu.getMaKhachHang() != null ? phieu.getMaKhachHang() : "—");
                lblRoomCheckout.setText(phieu.getMaPhong() != null ? phieu.getMaPhong() : "—");
                lblCheckInDateCheckout.setText(phieu.getNgayNhan() != null ? phieu.getNgayNhan().toString() : "—");
                lblCheckOutDateCheckout.setText(phieu.getNgayTra() != null ? phieu.getNgayTra().toString() : "—");

                if (phieu.getNgayNhan() != null && phieu.getNgayTra() != null) {
                    long days = ChronoUnit.DAYS.between(phieu.getNgayNhan(), phieu.getNgayTra());
                    lblNumDaysCheckout.setText(String.valueOf(days));

                    // Get room price
                    if (phieu.getMaPhong() != null) {
                        PhongDTO room = phongService.getPhongById(phieu.getMaPhong());
                        if (room != null) {
                            double price = room.getGiaPhong();
                            lblRoomPriceCheckout.setText(String.format("%,.0f đ", price));
                            double roomTotal = price * days;
                            lblRoomTotalCheckout.setText(String.format("%,.0f đ", roomTotal));
                        }
                    }
                }

                // Load service details (placeholder)
                tvServiceDetails.getItems().clear();
            }
        } catch (Exception e) {
            logger.error("Lỗi khi tải thông tin phiếu đặt", e);
            showAlert("Lỗi", "Không thể tải thông tin phiếu đặt");
        }
    }

     /**
      * Xử lý thanh toán & trả phòng
      */
     private void processCheckout() {
         String selected = cbPhieuDatCheckout.getValue();
         if (selected == null || selected.isEmpty()) {
             showAlert("Lỗi", "Vui lòng chọn phiếu đặt");
             return;
         }

         // Validate and process payment
         Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
         confirm.setTitle("Xác Nhận Trả Phòng");
         confirm.setHeaderText(null);
         confirm.setContentText("Bạn có chắc chắn muốn hoàn tất trả phòng và thanh toán?");

         Optional<ButtonType> result = confirm.showAndWait();
         if (result.isPresent() && result.get() == ButtonType.OK) {
             processCheckoutReal();
         }
     }

     /**
      * Xử lý trả phòng thực tế
      */
     private void processCheckoutReal() {
         try {
             String selected = cbPhieuDatCheckout.getValue();
             String maPhieu = selected.split(" - ")[0];
             PhieuDatPhongDTO phieu = phieuDatPhongService.getPhieuDatPhongById(maPhieu);

             if (phieu == null) {
                 showAlert("Lỗi", "Không tìm thấy phiếu đặt");
                 return;
             }

             // Tính toán hóa đơn
             PhongDTO phong = phongService.getPhongById(phieu.getMaPhong());
             if (phong == null) {
                 showAlert("Lỗi", "Không tìm thấy phòng");
                 return;
             }

             // Tính số ngày
             long numDays = ChronoUnit.DAYS.between(phieu.getNgayNhan(), phieu.getNgayTra());
             if (numDays <= 0) numDays = 1;

             // Tính tiền phòng
             double giaPhong = phong.getGiaPhong();
             double tongTienPhong = giaPhong * numDays;

             // Lấy dịch vụ dari database
             List<HoaDonDTO> hoaDons = hoaDonService.getHoaDonByPhieuDat(maPhieu);
             double tongTienDichVu = 0;
             for (HoaDonDTO hd : hoaDons) {
                 tongTienDichVu += hd.getTongTienDichVu();
             }

             // Lấy VAT và chiết khấu từ UI
             double vat = spinnerVAT.getValue() != null ? spinnerVAT.getValue() : 0;
             double chietKhau = spinnerDiscount.getValue() != null ? spinnerDiscount.getValue() : 0;

             // Tính tổng tiền
             double tienVAT = tongTienPhong * (vat / 100);
             double tongTien = tongTienPhong + tongTienDichVu + tienVAT - chietKhau;

             // Tạo hoá đơn mới
             HoaDonDTO hoaDon = new HoaDonDTO();
             hoaDon.setMaKhachHang(phieu.getMaKhachHang());
             hoaDon.setMaNhanVien(currentUser.getTaiKhoan());
             hoaDon.setNgayLap(LocalDate.now());
             hoaDon.setTongTienPhong(tongTienPhong);
             hoaDon.setTongTienDichVu(tongTienDichVu);
             hoaDon.setThueVAT(tienVAT);
             hoaDon.setChietKhau(chietKhau);
             hoaDon.setTongTien(tongTien);
             hoaDon.setTrangThaiThanhToan("Đã Thanh Toán");
             hoaDon.setMaPhongDat(phieu.getMaPhong());
             hoaDon.setTenPhong(phong.getTenPhong());

             // Lưu hoá đơn
             hoaDonService.addHoaDon(hoaDon);

             // Cập nhật trạng thái phiếu đặt
             phieu.setTrangThai("Trả Phòng");
             phieuDatPhongService.updatePhieuDatPhong(phieu);

             // Cập nhật trạng thái phòng về "Trống"
             phong.setTinhTrang("Trống");
             phongService.updatePhong(phong);

             showAlert("Thành Công", String.format("Trả phòng thành công!\nTổng tiền: %,.0f đ", tongTien));
             clearCheckoutForm();
             loadPhieuDatForCheckout();

         } catch (Exception e) {
             e.printStackTrace();
             showAlert("Lỗi", "Lỗi khi xử lý trả phòng: " + e.getMessage());
         }
     }

     /**
      * Xử lý nhận phòng (Check-in) từ phiếu đã đặt
      */
     public boolean processCheckIn(String maPhieu) {
         try {
             PhieuDatPhongDTO phieu = phieuDatPhongService.getPhieuDatPhongById(maPhieu);
             if (phieu == null) {
                 showAlert("Lỗi", "Không tìm thấy phiếu đặt");
                 return false;
             }

             // Cập nhật trạng thái phiếu đặt
             phieu.setTrangThai("Nhận Phòng");
             phieuDatPhongService.updatePhieuDatPhong(phieu);

             // Cập nhật trạng thái phòng
             PhongDTO phong = phongService.getPhongById(phieu.getMaPhong());
             if (phong != null) {
                 phong.setTinhTrang("Đang ở");
                 phongService.updatePhong(phong);
             }

             showAlert("Thành Công", "Khách đã nhận phòng thành công!");
             return true;

         } catch (Exception e) {
             e.printStackTrace();
             showAlert("Lỗi", "Lỗi khi xử lý nhận phòng: " + e.getMessage());
             return false;
         }
     }

     /**
      * Xử lý đặt phòng mới (booking)
      */
     public boolean processBooking(String maKhachHang, String maPhong, LocalDate ngayNhan, LocalDate ngayTra) {
         try {
             if (maKhachHang == null || maKhachHang.isEmpty() || maPhong == null) {
                 showAlert("Lỗi", "Vui lòng nhập đầy đủ thông tin");
                 return false;
             }

             // Kiểm tra phòng có sẵn không
             PhongDTO phong = phongService.getPhongById(maPhong);
             if (phong == null) {
                 showAlert("Lỗi", "Phòng không tồn tại");
                 return false;
             }

             if (!phong.getTinhTrang().equalsIgnoreCase("Trống")) {
                 showAlert("Lỗi", "Phòng không có sẵn");
                 return false;
             }

             // Tính tổng tiền
             long numDays = ChronoUnit.DAYS.between(ngayNhan, ngayTra);
             if (numDays <= 0) numDays = 1;
             double tongTien = phong.getGiaPhong() * numDays;

             // Tạo phiếu đặt phòng mới
             PhieuDatPhongDTO phieu = new PhieuDatPhongDTO();
             phieu.setMaKhachHang(maKhachHang);
             phieu.setMaPhong(maPhong);
             phieu.setNgayDat(LocalDate.now());
             phieu.setNgayNhan(ngayNhan);
             phieu.setNgayTra(ngayTra);
             phieu.setTongTien(tongTien);
             phieu.setTrangThai("Đặt Phòng");
             phieu.setMaNhanVien(currentUser.getTaiKhoan());

             // Lưu phiếu đặt phòng
             PhieuDatPhongDTO result = phieuDatPhongService.addPhieuDatPhong(phieu);
             if (result == null) {
                 showAlert("Lỗi", "Không thể tạo phiếu đặt phòng");
                 return false;
             }

             // Cập nhật trạng thái phòng
             phong.setTinhTrang("Đã Đặt");
             phongService.updatePhong(phong);

             showAlert("Thành Công", String.format("Đặt phòng thành công!\nMã phiếu: %s\nTổng tiền: %,.0f đ", result.getMaPhieu(), tongTien));
             return true;

         } catch (Exception e) {
             e.printStackTrace();
             showAlert("Lỗi", "Lỗi khi đặt phòng: " + e.getMessage());
             return false;
         }
     }

     /**
      * Xử lý hủy phiếu đặt phòng
      */
     public boolean cancelBooking(String maPhieu) {
         try {
             PhieuDatPhongDTO phieu = phieuDatPhongService.getPhieuDatPhongById(maPhieu);
             if (phieu == null) {
                 showAlert("Lỗi", "Không tìm thấy phiếu đặt");
                 return false;
             }

             // Xác nhận hủy
             Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
             confirm.setTitle("Xác Nhận Hủy");
             confirm.setHeaderText(null);
             confirm.setContentText("Bạn có chắc chắn muốn hủy phiếu đặt phòng?");

             Optional<ButtonType> result = confirm.showAndWait();
             if (result.isPresent() && result.get() == ButtonType.OK) {
                 // Cập nhật trạng thái
                 phieu.setTrangThai("Hủy");
                 phieuDatPhongService.updatePhieuDatPhong(phieu);

                 // Cập nhật trạng thái phòng
                 PhongDTO phong = phongService.getPhongById(phieu.getMaPhong());
                 if (phong != null) {
                     phong.setTinhTrang("Trống");
                     phongService.updatePhong(phong);
                 }

                 showAlert("Thành Công", "Phiếu đặt phòng đã được hủy");
                 return true;
             }
             return false;

         } catch (Exception e) {
             e.printStackTrace();
             showAlert("Lỗi", "Lỗi khi hủy phiếu đặt: " + e.getMessage());
             return false;
         }
     }

     /**
      * Xử lý hoàn thành bảo trì
      */
     public boolean completeMaintenance(String maPhong) {
         try {
             PhongDTO phong = phongService.getPhongById(maPhong);
             if (phong == null) {
                 showAlert("Lỗi", "Không tìm thấy phòng");
                 return false;
             }

             // Xác nhận hoàn thành
             Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
             confirm.setTitle("Xác Nhận");
             confirm.setHeaderText(null);
             confirm.setContentText("Bạn có chắc chắn phòng đã hoàn thành bảo trì?");

             Optional<ButtonType> result = confirm.showAndWait();
             if (result.isPresent() && result.get() == ButtonType.OK) {
                 // Cập nhật trạng thái phòng về Trống
                 phong.setTinhTrang("Trống");
                 phongService.updatePhong(phong);

                 showAlert("Thành Công", "Phòng đã hoàn thành bảo trì");
                 return true;
             }
             return false;

         } catch (Exception e) {
             e.printStackTrace();
             showAlert("Lỗi", "Lỗi khi hoàn thành bảo trì: " + e.getMessage());
             return false;
         }
     }

      /**
       * Xử lý trả phòng và thanh toán (Checkout)
       */
      public boolean processCheckout(String maPhieu, double thueVAT, double chietKhau, String hinhThucThanhToan) {
          try {
              // 1. Kiểm tra phiếu đặt
              PhieuDatPhongDTO phieu = phieuDatPhongService.getPhieuDatPhongById(maPhieu);
              if (phieu == null) {
                  showAlert("Lỗi", "Không tìm thấy phiếu đặt");
                  return false;
              }

              // 2. Kiểm tra trạng thái phiếu (phải là "Nhận Phòng")
              if (!phieu.getTrangThai().equalsIgnoreCase("Nhận Phòng")) {
                  showAlert("Lỗi", "Phiếu đặt không ở trạng thái 'Nhận Phòng'");
                  return false;
              }

              // 3. Tính hóa đơn
              HoaDonDTO hoaDon = hoaDonService.calculateInvoiceAtCheckout(maPhieu, thueVAT, chietKhau);
              if (hoaDon == null) {
                  showAlert("Lỗi", "Không thể tính toán hóa đơn");
                  return false;
              }

              // 4. Lưu hóa đơn
              HoaDonDTO savedHoaDon = hoaDonService.addHoaDon(hoaDon);
              if (savedHoaDon == null) {
                  showAlert("Lỗi", "Không thể lưu hóa đơn");
                  return false;
              }

              // 5. Cập nhật trạng thái phiếu từ "Nhận Phòng" → "Trả Phòng"
              phieu.setTrangThai("Trả Phòng");
              phieuDatPhongService.updatePhieuDatPhong(phieu);

              // 6. Cập nhật trạng thái phòng từ "Đang ở" → "Trống"
              PhongDTO phong = phongService.getPhongById(phieu.getMaPhong());
              if (phong != null) {
                  phong.setTinhTrang("Trống");
                  phongService.updatePhong(phong);
              }

              // 7. Xác nhận thành công
              showAlert("Thành Công", String.format(
                      "Trả phòng thành công!\n\nMã hóa đơn: %s\n" +
                      "Tiền phòng: %,.0f đ\n" +
                      "Tiền VAT: %,.0f đ\n" +
                      "Chiết khấu: %,.0f đ\n" +
                      "Tổng tiền: %,.0f đ\n\n" +
                      "Hình thức thanh toán: %s",
                      savedHoaDon.getMaHoaDon(),
                      hoaDon.getTongTienPhong(),
                      hoaDon.getThueVAT(),
                      hoaDon.getChietKhau(),
                      hoaDon.getTongTien(),
                      hinhThucThanhToan
              ));

              return true;

          } catch (Exception e) {
              e.printStackTrace();
              showAlert("Lỗi", "Lỗi khi trả phòng: " + e.getMessage());
              return false;
          }
      }

    /**
     * Xóa form checkout
     */
    private void clearCheckoutForm() {
        cbPhieuDatCheckout.setValue(null);
        lblGuestNameCheckout.setText("—");
        lblRoomCheckout.setText("—");
        lblCheckInDateCheckout.setText("—");
        lblCheckOutDateCheckout.setText("—");
        lblNumDaysCheckout.setText("0");
        lblRoomPriceCheckout.setText("—");
        lblRoomTotalCheckout.setText("0 đ");
        lblServiceTotalCheckout.setText("0 đ");
        lblVATCheckout.setText("0 đ");
        lblDiscountCheckout.setText("0 đ");
        lblGrandTotalCheckout.setText("0 đ");
        tvServiceDetails.getItems().clear();
        cbPaymentMethod.setValue("Tiền Mặt");
    }

    /**
     * Hiển thị dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
