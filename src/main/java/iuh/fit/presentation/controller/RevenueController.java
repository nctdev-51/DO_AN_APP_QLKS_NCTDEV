package iuh.fit.presentation.controller;

import iuh.fit.core.dto.HoaDonDTO;
import iuh.fit.core.service.IHoaDonService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class RevenueController {

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

    // Colors
    private final String COLOR_PRIMARY = "#0066cc";
    private final String COLOR_ACCENT = "#17a2b8";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_BG_LIGHT = "#f8fafc";
    private final String COLOR_SUCCESS = "#10b981";

    public RevenueController(IHoaDonService hoaDonService) {
        this.hoaDonService = hoaDonService;
    }

    public BorderPane createRevenueView() {
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
                createFilterBar(),
                createStatCardsRow(),
                createRevenueChartCard(),
                createInvoiceTableCard()
        );
        ScrollPane scrollPane = new ScrollPane(centerArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent;");
        root.setCenter(scrollPane);

        // Load initial data
        loadRevenueData();

        return root;
    }

    private VBox createTitleBar() {
        VBox vbox = new VBox(3);
        Label title = new Label("💰 Thống Kê Doanh Thu");
        title.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 24));
        title.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label subtitle = new Label("Xem báo cáo doanh thu từ các hóa đơn khách sạn");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        subtitle.setTextFill(Color.web(COLOR_TEXT_MUTED));

        vbox.getChildren().addAll(title, subtitle);
        return vbox;
    }

    private HBox createFilterBar() {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
        applyCardStyle((VBox) new VBox());

        Label lblStart = new Label("Từ ngày:");
        lblStart.setStyle("-fx-font-weight: bold;");
        dpStartDate = new DatePicker(LocalDate.now().minusMonths(1));
        dpStartDate.setStyle("-fx-font-size: 12px;");

        Label lblEnd = new Label("Đến ngày:");
        lblEnd.setStyle("-fx-font-weight: bold;");
        dpEndDate = new DatePicker(LocalDate.now());
        dpEndDate.setStyle("-fx-font-size: 12px;");

        Button btnSearch = new Button("🔍 Tìm Kiếm");
        btnSearch.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;");
        btnSearch.setOnAction(e -> loadRevenueData());

        Button btnExport = new Button("📥 Xuất Excel");
        btnExport.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;");
        btnExport.setOnAction(e -> showAlert("Thông báo", "Chức năng xuất Excel sẽ được bổ sung sau"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(lblStart, dpStartDate, lblEnd, dpEndDate, btnSearch, spacer, btnExport);
        return card;
    }

    private HBox createStatCardsRow() {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox c1 = makeStatCard("Tổng Doanh Thu", "0 đ", "#3b82f6", "#1d4ed8");
        VBox c2 = makeStatCard("Số Hóa Đơn", "0", "#10b981", "#047857");
        VBox c3 = makeStatCard("Hóa Đơn Trung Bình", "0 đ", "#f59e0b", "#b45309");
        VBox c4 = makeStatCard("Doanh Thu Dịch Vụ", "0 đ", "#8b5cf6", "#6d28d9");

        lblTotalRevenue = (Label) c1.getChildren().get(1);
        lblTotalInvoices = (Label) c2.getChildren().get(1);
        lblAverageInvoice = (Label) c3.getChildren().get(1);
        lblServiceRevenue = (Label) c4.getChildren().get(1);

        HBox.setHgrow(c1, Priority.ALWAYS);
        HBox.setHgrow(c2, Priority.ALWAYS);
        HBox.setHgrow(c3, Priority.ALWAYS);
        HBox.setHgrow(c4, Priority.ALWAYS);

        row.getChildren().addAll(c1, c2, c3, c4);
        return row;
    }

    private VBox createRevenueChartCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
        applyCardStyle(card);

        Label lblTitle = new Label("📊 Biểu Đồ Doanh Thu Theo Ngày");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Doanh Thu (₫)");

        chartRevenue = new BarChart<>(xAxis, yAxis);
        chartRevenue.setTitle("Doanh Thu Theo Ngày");
        chartRevenue.setPrefHeight(350);

        card.getChildren().addAll(lblTitle, chartRevenue);
        VBox.setVgrow(chartRevenue, Priority.ALWAYS);
        return card;
    }

    private VBox createInvoiceTableCard() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
        applyCardStyle(card);

        Label lblTitle = new Label("📋 Danh Sách Hóa Đơn");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        tvHoaDon = new TableView<>();
        tvHoaDon.setPrefHeight(300);
        tvHoaDon.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<HoaDonDTO, String> colMa = new TableColumn<>("Mã HĐ");
        colMa.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getMaHoaDon()));

        TableColumn<HoaDonDTO, LocalDate> colNgay = new TableColumn<>("Ngày Lập");
        colNgay.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getNgayLap()));

        TableColumn<HoaDonDTO, Double> colTienPhong = new TableColumn<>("Tiền Phòng");
        colTienPhong.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getTongTienPhong()));
        colTienPhong.setCellFactory(col -> new TableCell<HoaDonDTO, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : String.format("%,.0f", item) + " đ");
            }
        });

        TableColumn<HoaDonDTO, Double> colTienDV = new TableColumn<>("Tiền DV");
        colTienDV.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getTongTienDichVu()));
        colTienDV.setCellFactory(col -> new TableCell<HoaDonDTO, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : String.format("%,.0f", item) + " đ");
            }
        });

        TableColumn<HoaDonDTO, Double> colTong = new TableColumn<>("Tổng Tiền");
        colTong.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getTongTien()));
        colTong.setCellFactory(col -> new TableCell<HoaDonDTO, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : String.format("%,.0f", item) + " đ");
                setStyle("-fx-text-fill: #0066cc; -fx-font-weight: bold;");
            }
        });

        TableColumn<HoaDonDTO, String> colStatus = new TableColumn<>("Trạng Thái");
        colStatus.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getTrangThaiThanhToan()));

        tvHoaDon.getColumns().addAll(colMa, colNgay, colTienPhong, colTienDV, colTong, colStatus);

        card.getChildren().addAll(lblTitle, tvHoaDon);
        VBox.setVgrow(tvHoaDon, Priority.ALWAYS);
        return card;
    }

    private VBox makeStatCard(String label, String value, String colorStart, String colorEnd) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, " + colorStart + ", " + colorEnd + "); -fx-background-radius: 10; -fx-border-radius: 10;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.06));
        shadow.setRadius(8);
        shadow.setOffsetY(3);
        card.setEffect(shadow);

        Label lbl = new Label(label);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        lbl.setTextFill(Color.web("#ffffff", 0.85));

        Label val = new Label(value);
        val.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 24));
        val.setTextFill(Color.WHITE);

        card.getChildren().addAll(lbl, val);
        return card;
    }

    private void loadRevenueData() {
        try {
            LocalDate startDate = dpStartDate.getValue() != null ? dpStartDate.getValue() : LocalDate.now().minusMonths(1);
            LocalDate endDate = dpEndDate.getValue() != null ? dpEndDate.getValue() : LocalDate.now();

            // Get invoices
            List<HoaDonDTO> hoaDons = hoaDonService.getHoaDonByDateRange(startDate, endDate);

            // Update stat cards
            double totalRevenue = hoaDons.stream().mapToDouble(HoaDonDTO::getTongTien).sum();
            int totalInvoices = hoaDons.size();
            double avgInvoice = totalInvoices > 0 ? totalRevenue / totalInvoices : 0;
            double serviceRevenue = hoaDonService.getTotalServiceRevenueByDateRange(startDate, endDate);

            lblTotalRevenue.setText(String.format("%,.0f đ", totalRevenue));
            lblTotalInvoices.setText(String.valueOf(totalInvoices));
            lblAverageInvoice.setText(String.format("%,.0f đ", avgInvoice));
            lblServiceRevenue.setText(String.format("%,.0f đ", serviceRevenue));

            // Update table
            tvHoaDon.getItems().clear();
            tvHoaDon.getItems().addAll(hoaDons);

            // Update chart
            updateRevenueChart(hoaDons);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRevenueChart(List<HoaDonDTO> hoaDons) {
        chartRevenue.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh Thu");

        // Group by date
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

