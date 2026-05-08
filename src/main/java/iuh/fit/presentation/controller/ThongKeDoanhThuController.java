package iuh.fit.presentation.controller;

import iuh.fit.core.dto.HoaDonDTO;
import iuh.fit.core.service.IHoaDonService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

public class ThongKeDoanhThuController {

    private final IHoaDonService hoaDonService;

    // UI Components
    private DatePicker dpStartDate;
    private DatePicker dpEndDate;
    private Label lblTotalRevenue;
    private Label lblTotalInvoices;
    private Label lblAverageInvoice;
    private Label lblServiceRevenue;
    private TableView<HoaDonDTO> tvHoaDon;
    private BarChart<String, Number> chartRevenue;

    // Bảng màu thiết kế chuẩn (Khớp hệ thống)
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_PRIMARY_DARK = "#1e3a8a";
    private final String COLOR_BG_MAIN = "#f8fafc";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_BORDER = "#e2e8f0";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_HOVER = "#eff6ff";

    public ThongKeDoanhThuController(IHoaDonService hoaDonService) {
        this.hoaDonService = hoaDonService;
    }

    public BorderPane createRevenueView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_BG_MAIN + ";");

        // --- HEADER ---
        VBox topArea = new VBox(8);
        topArea.setPadding(new Insets(30, 30, 10, 30));

        Label lblTitle = new Label("THỐNG KÊ DOANH THU");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 32));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblSubTitle = new Label("Phân tích luồng doanh thu, hiệu suất phòng và dịch vụ theo thời gian thực.");
        lblSubTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 15));
        lblSubTitle.setTextFill(Color.web(COLOR_TEXT_MUTED));

        topArea.getChildren().addAll(lblTitle, lblSubTitle);
        root.setTop(topArea);

        // --- MAIN CONTENT ---
        VBox centerArea = new VBox(25);
        centerArea.setPadding(new Insets(20, 30, 30, 30));

        centerArea.getChildren().addAll(
                createFilterBar(),
                createStatCardsRow(),
                createRevenueChartCard(),
                createInvoiceTableCard()
        );

        ScrollPane scrollPane = new ScrollPane(centerArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-border-color: transparent;");
        root.setCenter(scrollPane);

        // Tải dữ liệu ban đầu
        loadRevenueData();

        return root;
    }

    // =========================================================================
    // KHU VỰC BỘ LỌC TÌM KIẾM (ĐÃ NÂNG CẤP QUICK FILTERS)
    // =========================================================================
    private VBox createFilterBar() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 12; -fx-border-width: 1;");
        card.setEffect(new DropShadow(10, Color.web("#000000", 0.03)));

        // Dòng trên: Chọn ngày thủ công & Nút tìm kiếm
        HBox topFilter = new HBox(15);
        topFilter.setAlignment(Pos.CENTER_LEFT);

        String inputStyle = "-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cbd5e1; -fx-background-color: #f8fafc; -fx-padding: 6 10;";

        Label lblStart = new Label("Từ ngày:");
        lblStart.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblStart.setTextFill(Color.web(COLOR_TEXT_MUTED));

        // 👉 MẶC ĐỊNH LÀ THỜI GIAN HIỆN TẠI (HÔM NAY)
        dpStartDate = new DatePicker(LocalDate.now());
        dpStartDate.setStyle(inputStyle);
        dpStartDate.setPrefWidth(160);

        Label lblEnd = new Label("Đến:");
        lblEnd.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblEnd.setTextFill(Color.web(COLOR_TEXT_MUTED));

        dpEndDate = new DatePicker(LocalDate.now());
        dpEndDate.setStyle(inputStyle);
        dpEndDate.setPrefWidth(160);

        Button btnSearch = new Button("🔍 Cập Nhật");
        btnSearch.setCursor(Cursor.HAND);
        btnSearch.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 8;");
        btnSearch.setOnAction(e -> loadRevenueData());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnExport = new Button("📥 Xuất Báo Cáo");
        btnExport.setCursor(Cursor.HAND);
        btnExport.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 8;");
        btnExport.setOnAction(e -> showAlert("Thông báo", "Chức năng xuất File PDF/Excel sẽ được bổ sung sau."));

        topFilter.getChildren().addAll(lblStart, dpStartDate, lblEnd, dpEndDate, btnSearch, spacer, btnExport);

        // Dòng dưới: Chọn nhanh (Hôm nay, Tuần này, Tháng này)
        HBox quickFilters = new HBox(10);
        quickFilters.setAlignment(Pos.CENTER_LEFT);

        Label lblQuick = new Label("Chọn nhanh:");
        lblQuick.setFont(Font.font("Segoe UI", 13));
        lblQuick.setTextFill(Color.web(COLOR_TEXT_MUTED));

        quickFilters.getChildren().addAll(
                lblQuick,
                createQuickBtn("Hôm nay", LocalDate.now(), LocalDate.now()),
                createQuickBtn("Tuần này", LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)), LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))),
                createQuickBtn("Tháng này", LocalDate.now().withDayOfMonth(1), LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())),
                createQuickBtn("Tháng trước", LocalDate.now().minusMonths(1).withDayOfMonth(1), LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()))
        );

        card.getChildren().addAll(topFilter, new Separator(), quickFilters);
        return card;
    }

    private Button createQuickBtn(String text, LocalDate start, LocalDate end) {
        Button btn = new Button(text);
        btn.setCursor(Cursor.HAND);
        btn.setStyle("-fx-background-color: transparent; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 20; -fx-background-radius: 20; -fx-text-fill: " + COLOR_TEXT_MUTED + "; -fx-padding: 5 15;");

        // Hiệu ứng hover
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + COLOR_HOVER + "; -fx-border-color: " + COLOR_PRIMARY + "; -fx-border-radius: 20; -fx-background-radius: 20; -fx-text-fill: " + COLOR_PRIMARY + "; -fx-padding: 5 15;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 20; -fx-background-radius: 20; -fx-text-fill: " + COLOR_TEXT_MUTED + "; -fx-padding: 5 15;"));

        btn.setOnAction(e -> {
            dpStartDate.setValue(start);
            dpEndDate.setValue(end);
            loadRevenueData(); // Auto search
        });
        return btn;
    }

    // =========================================================================
    // KHU VỰC THẺ THỐNG KÊ (ĐÃ TINH CHỈNH ĐỔ BÓNG VÀ FONT)
    // =========================================================================
    private HBox createStatCardsRow() {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox c1 = makeStatCard("TỔNG DOANH THU", "0 đ", "#3b82f6", "#1e40af", "💰");
        VBox c2 = makeStatCard("SỐ HÓA ĐƠN CHỐT", "0", "#10b981", "#065f46", "🧾");
        VBox c3 = makeStatCard("TRUNG BÌNH / BILL", "0 đ", "#f59e0b", "#92400e", "📊");
        VBox c4 = makeStatCard("DOANH THU DỊCH VỤ", "0 đ", "#8b5cf6", "#4c1d95", "🍱");

        lblTotalRevenue = (Label) ((HBox)c1.getChildren().get(1)).getChildren().get(0);
        lblTotalInvoices = (Label) ((HBox)c2.getChildren().get(1)).getChildren().get(0);
        lblAverageInvoice = (Label) ((HBox)c3.getChildren().get(1)).getChildren().get(0);
        lblServiceRevenue = (Label) ((HBox)c4.getChildren().get(1)).getChildren().get(0);

        HBox.setHgrow(c1, Priority.ALWAYS);
        HBox.setHgrow(c2, Priority.ALWAYS);
        HBox.setHgrow(c3, Priority.ALWAYS);
        HBox.setHgrow(c4, Priority.ALWAYS);

        row.getChildren().addAll(c1, c2, c3, c4);
        return row;
    }

    private VBox makeStatCard(String label, String value, String colorStart, String colorEnd, String icon) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(22));
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, " + colorStart + ", " + colorEnd + "); -fx-background-radius: 12;");

        card.setEffect(new DropShadow(15, Color.web(colorStart, 0.4)));

        Label lbl = new Label(label);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.web("#ffffff", 0.8));

        HBox valueBox = new HBox();
        valueBox.setAlignment(Pos.BOTTOM_LEFT);

        Label val = new Label(value);
        val.setFont(Font.font("Segoe UI", FontWeight.BLACK, 28));
        val.setTextFill(Color.WHITE);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label ico = new Label(icon);
        ico.setFont(Font.font("Segoe UI", 28));
        ico.setOpacity(0.5);

        valueBox.getChildren().addAll(val, sp, ico);
        card.getChildren().addAll(lbl, valueBox);
        return card;
    }

    // =========================================================================
    // BIỂU ĐỒ DOANH THU
    // =========================================================================
    private VBox createRevenueChartCard() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 12; -fx-border-width: 1;");
        card.setEffect(new DropShadow(10, Color.web("#000000", 0.03)));

        Label lblTitle = new Label("📈 BIỂU ĐỒ DOANH THU THEO NGÀY");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 16));
        lblTitle.setTextFill(Color.web(COLOR_PRIMARY_DARK));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Doanh Thu (VNĐ)");

        chartRevenue = new BarChart<>(xAxis, yAxis);
        chartRevenue.setLegendVisible(false);
        chartRevenue.setAnimated(true); // Thêm hiệu ứng chạy mượt mà
        chartRevenue.setPrefHeight(380);

        card.getChildren().addAll(lblTitle, chartRevenue);
        VBox.setVgrow(chartRevenue, Priority.ALWAYS);
        return card;
    }

    // =========================================================================
    // BẢNG DANH SÁCH HÓA ĐƠN
    // =========================================================================
    private VBox createInvoiceTableCard() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 12; -fx-border-width: 1;");
        card.setEffect(new DropShadow(10, Color.web("#000000", 0.03)));

        Label lblTitle = new Label("📋 CHI TIẾT DANH SÁCH HÓA ĐƠN");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 16));
        lblTitle.setTextFill(Color.web(COLOR_PRIMARY_DARK));

        tvHoaDon = new TableView<>();
        tvHoaDon.setPrefHeight(350);
        tvHoaDon.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tvHoaDon.setStyle("-fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 8; -fx-font-size: 14px;");

        // Hiện thông báo trống khi không có dữ liệu
        Label emptyLabel = new Label("📭 Không có dữ liệu doanh thu trong khoảng thời gian này.");
        emptyLabel.setTextFill(Color.web(COLOR_TEXT_MUTED));
        emptyLabel.setFont(Font.font("Segoe UI", 14));
        tvHoaDon.setPlaceholder(emptyLabel);

        TableColumn<HoaDonDTO, String> colMa = new TableColumn<>("Mã HĐ");
        colMa.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getMaHoaDon()));
        colMa.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");

        TableColumn<HoaDonDTO, LocalDate> colNgay = new TableColumn<>("Ngày Lập");
        colNgay.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getNgayLap()));
        colNgay.setStyle("-fx-alignment: CENTER;");

        TableColumn<HoaDonDTO, Double> colTienPhong = new TableColumn<>("Tiền Phòng");
        colTienPhong.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getTongTienPhong()));
        colTienPhong.setCellFactory(col -> new TableCell<HoaDonDTO, Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f đ", item));
            }
        });
        colTienPhong.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<HoaDonDTO, Double> colTienDV = new TableColumn<>("Tiền Dịch Vụ");
        colTienDV.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getTongTienDichVu()));
        colTienDV.setCellFactory(col -> new TableCell<HoaDonDTO, Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f đ", item));
            }
        });
        colTienDV.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<HoaDonDTO, Double> colTong = new TableColumn<>("Tổng Thu");
        colTong.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getTongTien()));
        colTong.setCellFactory(col -> new TableCell<HoaDonDTO, Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f đ", item));
                setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: " + COLOR_PRIMARY + ";");
            }
        });

        tvHoaDon.getColumns().addAll(colMa, colNgay, colTienPhong, colTienDV, colTong);

        card.getChildren().addAll(lblTitle, tvHoaDon);
        VBox.setVgrow(tvHoaDon, Priority.ALWAYS);
        return card;
    }

    // =========================================================================
    // LOGIC DỮ LIỆU
    // =========================================================================
    private void loadRevenueData() {
        try {
            LocalDate startDate = dpStartDate.getValue() != null ? dpStartDate.getValue() : LocalDate.now();
            LocalDate endDate = dpEndDate.getValue() != null ? dpEndDate.getValue() : LocalDate.now();

            // Nếu ngày kết thúc nhỏ hơn ngày bắt đầu, tự động fix lại
            if (endDate.isBefore(startDate)) {
                endDate = startDate;
                dpEndDate.setValue(endDate);
            }

            List<HoaDonDTO> hoaDons = hoaDonService.getHoaDonByDateRange(startDate, endDate);

            double totalRevenue = hoaDons.stream().mapToDouble(HoaDonDTO::getTongTien).sum();
            int totalInvoices = hoaDons.size();
            double avgInvoice = totalInvoices > 0 ? totalRevenue / totalInvoices : 0;

            // Nếu bạn chưa viết hàm getTotalServiceRevenueByDateRange trong DB thì tạm tính bằng Java như sau:
            // THAY BẰNG DÒNG NÀY (Bỏ check null vì double thì không bao giờ null)
            double serviceRevenue = hoaDons.stream().mapToDouble(HoaDonDTO::getTongTienDichVu).sum();

            lblTotalRevenue.setText(String.format("%,.0f đ", totalRevenue));
            lblTotalInvoices.setText(String.valueOf(totalInvoices));
            lblAverageInvoice.setText(String.format("%,.0f đ", avgInvoice));
            lblServiceRevenue.setText(String.format("%,.0f đ", serviceRevenue));

            tvHoaDon.getItems().clear();
            tvHoaDon.getItems().addAll(hoaDons);

            updateRevenueChart(hoaDons);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRevenueChart(List<HoaDonDTO> hoaDons) {
        chartRevenue.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh Thu");

        // Gom nhóm hóa đơn theo ngày và tính tổng
        Map<LocalDate, Double> dailyRevenue = new TreeMap<>(
                hoaDons.stream()
                        .collect(Collectors.groupingBy(
                                HoaDonDTO::getNgayLap,
                                Collectors.summingDouble(HoaDonDTO::getTongTien)
                        ))
        );

        dailyRevenue.forEach((date, revenue) -> {
            series.getData().add(new XYChart.Data<>(date.toString(), revenue));
        });

        chartRevenue.getData().add(series);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}