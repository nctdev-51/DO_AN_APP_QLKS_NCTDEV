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

import java.time.LocalDate;
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

    public ThongKeDoanhThuController(IHoaDonService hoaDonService) {
        this.hoaDonService = hoaDonService;
    }

    public BorderPane createRevenueView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_BG_MAIN + ";");

        // --- HEADER ---
        VBox topArea = new VBox(5);
        topArea.setPadding(new Insets(30, 30, 10, 30));

        Label lblTitle = new Label("THỐNG KÊ DOANH THU");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 32));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblSubTitle = new Label("Xem báo cáo chi tiết, phân tích doanh thu từ các hóa đơn phòng và dịch vụ.");
        lblSubTitle.setFont(Font.font("Segoe UI", 15));
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
    // KHU VỰC BỘ LỌC TÌM KIẾM
    // =========================================================================
    private HBox createFilterBar() {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 12; -fx-border-width: 1;");
        card.setEffect(new DropShadow(10, Color.web("#000000", 0.03)));

        String inputStyle = "-fx-font-size: 14px; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #cbd5e1; -fx-background-color: #f8fafc; -fx-padding: 6 12;";

        Label lblStart = new Label("Khoảng thời gian từ:");
        lblStart.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblStart.setTextFill(Color.web(COLOR_TEXT_MUTED));

        dpStartDate = new DatePicker(LocalDate.now().minusMonths(1));
        dpStartDate.setStyle(inputStyle);
        dpStartDate.setPrefWidth(150);

        Label lblEnd = new Label("Đến:");
        lblEnd.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblEnd.setTextFill(Color.web(COLOR_TEXT_MUTED));

        dpEndDate = new DatePicker(LocalDate.now());
        dpEndDate.setStyle(inputStyle);
        dpEndDate.setPrefWidth(150);

        Button btnSearch = new Button("Cập Nhật Dữ Liệu");
        btnSearch.setCursor(Cursor.HAND);
        btnSearch.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 9 20; -fx-background-radius: 8;");
        btnSearch.setOnAction(e -> loadRevenueData());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnExport = new Button("Xuất Excel");
        btnExport.setCursor(Cursor.HAND);
        btnExport.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 9 20; -fx-background-radius: 8;");
        btnExport.setOnAction(e -> showAlert("Thông báo", "Chức năng xuất Excel sẽ được bổ sung sau."));

        card.getChildren().addAll(lblStart, dpStartDate, lblEnd, dpEndDate, btnSearch, spacer, btnExport);
        return card;
    }

    // =========================================================================
    // KHU VỰC THẺ THỐNG KÊ (STAT CARDS)
    // =========================================================================
    private HBox createStatCardsRow() {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox c1 = makeStatCard("TỔNG DOANH THU", "0 đ", "#3b82f6", "#1d4ed8");
        VBox c2 = makeStatCard("SỐ HÓA ĐƠN", "0", "#10b981", "#047857");
        VBox c3 = makeStatCard("HÓA ĐƠN TRUNG BÌNH", "0 đ", "#f59e0b", "#b45309");
        VBox c4 = makeStatCard("DOANH THU DỊCH VỤ", "0 đ", "#8b5cf6", "#6d28d9");

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

    private VBox makeStatCard(String label, String value, String colorStart, String colorEnd) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(20, 25, 20, 25));
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, " + colorStart + ", " + colorEnd + "); -fx-background-radius: 12; -fx-border-radius: 12;");

        DropShadow shadow = new DropShadow(15, Color.web("#000000", 0.08));
        card.setEffect(shadow);

        Label lbl = new Label(label);
        lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lbl.setTextFill(Color.web("#ffffff", 0.9));

        Label val = new Label(value);
        val.setFont(Font.font("Segoe UI", FontWeight.BLACK, 32));
        val.setTextFill(Color.WHITE);

        card.getChildren().addAll(lbl, val);
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

        Label lblTitle = new Label("BIỂU ĐỒ DOANH THU THEO NGÀY");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 16));
        lblTitle.setTextFill(Color.web(COLOR_PRIMARY_DARK));

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Doanh Thu (VNĐ)");

        chartRevenue = new BarChart<>(xAxis, yAxis);
        chartRevenue.setLegendVisible(false); // Ẩn legend cho gọn vì chỉ có 1 series
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

        Label lblTitle = new Label("CHI TIẾT DANH SÁCH HÓA ĐƠN");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 16));
        lblTitle.setTextFill(Color.web(COLOR_PRIMARY_DARK));

        tvHoaDon = new TableView<>();
        tvHoaDon.setPrefHeight(350);
        tvHoaDon.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tvHoaDon.setStyle("-fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 8;");

        TableColumn<HoaDonDTO, String> colMa = new TableColumn<>("Mã HĐ");
        colMa.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getMaHoaDon()));
        colMa.setStyle("-fx-alignment: CENTER;");

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
        colTienPhong.setStyle("-fx-alignment: CENTER-RIGHT;"); // Căn phải tiền tệ

        TableColumn<HoaDonDTO, Double> colTienDV = new TableColumn<>("Tiền DV");
        colTienDV.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getTongTienDichVu()));
        colTienDV.setCellFactory(col -> new TableCell<HoaDonDTO, Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f đ", item));
            }
        });
        colTienDV.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<HoaDonDTO, Double> colTong = new TableColumn<>("Tổng Tiền");
        colTong.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getTongTien()));
        colTong.setCellFactory(col -> new TableCell<HoaDonDTO, Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%,.0f đ", item));
                setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-weight: bold; -fx-text-fill: " + COLOR_PRIMARY + ";");
            }
        });

        TableColumn<HoaDonDTO, String> colStatus = new TableColumn<>("Trạng Thái");
        colStatus.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getTrangThaiThanhToan()));
        colStatus.setCellFactory(col -> new TableCell<HoaDonDTO, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setGraphic(null);
                } else {
                    Label lbl = new Label(item.toUpperCase());
                    lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
                    if (item.contains("Thanh Toán") || item.contains("Thành Công")) {
                        lbl.setStyle("-fx-background-color: #10b98122; -fx-text-fill: #10b981; -fx-padding: 4 10; -fx-background-radius: 6;");
                    } else {
                        lbl.setStyle("-fx-background-color: #f59e0b22; -fx-text-fill: #f59e0b; -fx-padding: 4 10; -fx-background-radius: 6;");
                    }
                    setGraphic(lbl);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        tvHoaDon.getColumns().addAll(colMa, colNgay, colTienPhong, colTienDV, colTong, colStatus);

        card.getChildren().addAll(lblTitle, tvHoaDon);
        VBox.setVgrow(tvHoaDon, Priority.ALWAYS);
        return card;
    }

    // =========================================================================
    // LOGIC DỮ LIỆU
    // =========================================================================
    private void loadRevenueData() {
        try {
            LocalDate startDate = dpStartDate.getValue() != null ? dpStartDate.getValue() : LocalDate.now().minusMonths(1);
            LocalDate endDate = dpEndDate.getValue() != null ? dpEndDate.getValue() : LocalDate.now();

            List<HoaDonDTO> hoaDons = hoaDonService.getHoaDonByDateRange(startDate, endDate);

            double totalRevenue = hoaDons.stream().mapToDouble(HoaDonDTO::getTongTien).sum();
            int totalInvoices = hoaDons.size();
            double avgInvoice = totalInvoices > 0 ? totalRevenue / totalInvoices : 0;
            double serviceRevenue = hoaDonService.getTotalServiceRevenueByDateRange(startDate, endDate);

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