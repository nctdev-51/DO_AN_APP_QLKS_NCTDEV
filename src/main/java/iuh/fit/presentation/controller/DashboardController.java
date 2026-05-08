package iuh.fit.presentation.controller;

import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.service.IHoaDonService;
import iuh.fit.core.service.IPhongService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.chart.*;
import javafx.application.Platform;

import java.util.List;
import java.util.function.Consumer;

public class DashboardController {

    private IPhongService phongService;
    private IHoaDonService hoaDonService; // Đã thêm HoaDonService
    private Consumer<String> navigationHandler;

    // Màu sắc
    private final String COLOR_BG = "#f8fafc";
    private final String COLOR_CARD_BG = "#ffffff";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";

    // Truyền thêm IHoaDonService vào Constructor
    public DashboardController(IPhongService phongService, IHoaDonService hoaDonService, Consumer<String> navigationHandler) {
        this.phongService = phongService;
        this.hoaDonService = hoaDonService;
        this.navigationHandler = navigationHandler;
    }

    public VBox createDashboardView() {
        VBox root = new VBox(30);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: " + COLOR_BG + ";");

        // 1. HÀNG THẺ THỐNG KÊ
        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER_LEFT);

        List<PhongDTO> allRooms = phongService.getAllPhong();
        long total = allRooms.size();
        long trong = allRooms.stream().filter(p -> "Trống".equalsIgnoreCase(p.getTinhTrang())).count();
        long dangO = allRooms.stream().filter(p -> "Đang ở".equalsIgnoreCase(p.getTinhTrang())).count();
        long daDat = allRooms.stream().filter(p -> "Đã Đặt".equalsIgnoreCase(p.getTinhTrang())).count();
        long baoTri = total - trong - dangO - daDat;

        VBox cardTotal = buildStatCard("Tổng số phòng", String.valueOf(total), "#2563eb", "🏨");
        VBox cardTrong = buildStatCard("Phòng trống", String.valueOf(trong), "#10b981", "🟢");
        VBox cardDangO = buildStatCard("Đang phục vụ", String.valueOf(dangO), "#f59e0b", "🔑");
        VBox cardDaDat = buildStatCard("Đã đặt trước", String.valueOf(daDat), "#ef4444", "📅");

        statsRow.getChildren().addAll(cardTotal, cardTrong, cardDangO, cardDaDat);
        for (var node : statsRow.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
            ((VBox) node).setMaxWidth(Double.MAX_VALUE);
        }

        // 2. HÀNG BIỂU ĐỒ TRÒN & THÔNG BÁO
        HBox middleRow = new HBox(30);
        middleRow.setAlignment(Pos.TOP_CENTER);

        VBox chartCard = new VBox(15);
        chartCard.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 12; -fx-padding: 20;");
        chartCard.setEffect(new DropShadow(5, Color.color(0,0,0,0.05)));
        chartCard.setPrefWidth(450);
        HBox.setHgrow(chartCard, Priority.ALWAYS);

        Label lblChartTitle = new Label("📊 Trạng thái phòng hiện tại");
        lblChartTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblChartTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        PieChart pieChart = new PieChart();
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(false);
        pieChart.setPrefSize(400, 280);

        PieChart.Data sliceTrong = new PieChart.Data("Trống (" + trong + ")", trong);
        PieChart.Data sliceDangO = new PieChart.Data("Đang ở (" + dangO + ")", dangO);
        PieChart.Data sliceDaDat = new PieChart.Data("Đã đặt (" + daDat + ")", daDat);
        PieChart.Data sliceBaoTri = new PieChart.Data("Bảo trì (" + baoTri + ")", baoTri);

        pieChart.getData().addAll(sliceTrong, sliceDangO, sliceDaDat, sliceBaoTri);

        Platform.runLater(() -> {
            sliceTrong.getNode().setStyle("-fx-pie-color: #10b981;");
            sliceDangO.getNode().setStyle("-fx-pie-color: #f59e0b;");
            sliceDaDat.getNode().setStyle("-fx-pie-color: #ef4444;");
            sliceBaoTri.getNode().setStyle("-fx-pie-color: #64748b;");
        });

        String conclusion = String.format("Hiện có %d phòng sẵn sàng phục vụ, %d phòng đang có khách.", trong, dangO);
        Label lblConclusion = new Label("💡 " + conclusion);
        lblConclusion.setWrapText(true);
        lblConclusion.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        lblConclusion.setTextFill(Color.web(COLOR_TEXT_MUTED));

        chartCard.getChildren().addAll(lblChartTitle, pieChart, lblConclusion);

        VBox notificationCard = new VBox(15);
        notificationCard.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 12; -fx-padding: 20;");
        notificationCard.setEffect(new DropShadow(5, Color.color(0,0,0,0.05)));
        HBox.setHgrow(notificationCard, Priority.ALWAYS);

        Label lblNotiTitle = new Label("🔔 Thông báo & Nhắc nhở");
        lblNotiTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblNotiTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        VBox notiList = new VBox(10);
        notiList.getChildren().addAll(
                createNotification("Phòng 101 sắp đến giờ trả (13:00)", "Đi tới phiếu đặt", "MANAGE_ORDERS"),
                createNotification("Khách hàng Nguyễn Văn A vừa đặt phòng 202", "Xem chi tiết", "ROOM_MAP"),
                createNotification("Cần dọn dẹp phòng 305", "Cập nhật trạng thái", "MANAGE_ROOMS"),
                createNotification("Doanh thu hôm nay đạt 12.500.000đ", "Xem báo cáo", "REPORT")
        );

        ScrollPane notiScroll = new ScrollPane(notiList);
        notiScroll.setFitToWidth(true);
        notiScroll.setStyle("-fx-background-color: transparent;");

        notificationCard.getChildren().addAll(lblNotiTitle, notiScroll);
        middleRow.getChildren().addAll(chartCard, notificationCard);

        // 3. HÀNG BIỂU ĐỒ DOANH THU (2 BIỂU ĐỒ)
        HBox revenueRow = new HBox(30);
        revenueRow.setAlignment(Pos.CENTER);

        // Biểu đồ cột: Doanh thu 7 ngày qua
        VBox barChartCard = new VBox(10);
        barChartCard.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 12; -fx-padding: 20;");
        barChartCard.setEffect(new DropShadow(5, Color.color(0,0,0,0.05)));
        HBox.setHgrow(barChartCard, Priority.ALWAYS);

        Label lblBarTitle = new Label("📈 Doanh thu 7 ngày gần nhất");
        lblBarTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblBarTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        CategoryAxis xAxisBar = new CategoryAxis();
        NumberAxis yAxisBar = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxisBar, yAxisBar);
        barChart.setLegendVisible(false);
        XYChart.Series<String, Number> seriesBar = new XYChart.Series<>();

        // Dữ liệu giả lập (Sau này bạn map với hoaDonService để lấy dữ liệu thực tế)
        seriesBar.getData().add(new XYChart.Data<>("Thứ 2", 1500000));
        seriesBar.getData().add(new XYChart.Data<>("Thứ 3", 2200000));
        seriesBar.getData().add(new XYChart.Data<>("Thứ 4", 1800000));
        seriesBar.getData().add(new XYChart.Data<>("Thứ 5", 3500000));
        seriesBar.getData().add(new XYChart.Data<>("Thứ 6", 4200000));
        seriesBar.getData().add(new XYChart.Data<>("Thứ 7", 6500000));
        seriesBar.getData().add(new XYChart.Data<>("CN", 5800000));
        barChart.getData().add(seriesBar);
        barChartCard.getChildren().addAll(lblBarTitle, barChart);

        // Biểu đồ đường: Tăng trưởng doanh thu
        VBox lineChartCard = new VBox(10);
        lineChartCard.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 12; -fx-padding: 20;");
        lineChartCard.setEffect(new DropShadow(5, Color.color(0,0,0,0.05)));
        HBox.setHgrow(lineChartCard, Priority.ALWAYS);

        Label lblLineTitle = new Label("📉 Xu hướng tăng trưởng trong tháng");
        lblLineTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblLineTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        CategoryAxis xAxisLine = new CategoryAxis();
        NumberAxis yAxisLine = new NumberAxis();
        LineChart<String, Number> lineChart = new LineChart<>(xAxisLine, yAxisLine);
        lineChart.setLegendVisible(false);
        XYChart.Series<String, Number> seriesLine = new XYChart.Series<>();

        // Dữ liệu giả lập
        seriesLine.getData().add(new XYChart.Data<>("Tuần 1", 12000000));
        seriesLine.getData().add(new XYChart.Data<>("Tuần 2", 15000000));
        seriesLine.getData().add(new XYChart.Data<>("Tuần 3", 14500000));
        seriesLine.getData().add(new XYChart.Data<>("Tuần 4", 21000000));
        lineChart.getData().add(seriesLine);
        lineChartCard.getChildren().addAll(lblLineTitle, lineChart);

        revenueRow.getChildren().addAll(barChartCard, lineChartCard);

        // 4. TRUY CẬP NHANH
        Label lblQuick = new Label("⚡ Truy cập nhanh");
        lblQuick.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblQuick.setTextFill(Color.web(COLOR_TEXT_MAIN));

        HBox quickActions = new HBox(20);
        quickActions.setAlignment(Pos.CENTER_LEFT);

        Button btnBook = createQuickActionButton("🏨 Đặt phòng mới", "#3b82f6", "BOOKING");
        Button btnMap = createQuickActionButton("🗺️ Sơ đồ phòng", "#10b981", "ROOM_MAP");
        Button btnManageOrders = createQuickActionButton("📋 Quản lý phiếu", "#f59e0b", "MANAGE_ORDERS");
        Button btnManageRooms = createQuickActionButton("🚪 Quản lý phòng", "#8b5cf6", "MANAGE_ROOMS");
        Button btnReport = createQuickActionButton("📊 Báo cáo doanh thu", "#ec4899", "REPORT");

        quickActions.getChildren().addAll(btnBook, btnMap, btnManageOrders, btnManageRooms, btnReport);

        root.getChildren().addAll(statsRow, middleRow, revenueRow, lblQuick, quickActions);

        return root;
    }

    private VBox buildStatCard(String title, String value, String color, String icon) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(18));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 14;");
        card.setEffect(new DropShadow(3, Color.color(0,0,0,0.15)));

        Label lblIcon = new Label(icon);
        lblIcon.setFont(Font.font(28));

        Label lblValue = new Label(value);
        lblValue.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 30));
        lblValue.setTextFill(Color.WHITE);

        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web("#e2e8f0"));

        card.getChildren().addAll(lblIcon, lblValue, lblTitle);
        return card;
    }

    private VBox createNotification(String message, String actionLabel, String targetScreen) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8;");

        Label lblMsg = new Label(message);
        lblMsg.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        lblMsg.setTextFill(Color.web(COLOR_TEXT_MAIN));
        lblMsg.setWrapText(true);

        Hyperlink link = new Hyperlink(actionLabel);
        link.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        link.setOnAction(e -> {
            if (navigationHandler != null) {
                navigationHandler.accept(targetScreen);
            }
        });

        box.getChildren().addAll(lblMsg, link);
        return box;
    }

    private Button createQuickActionButton(String text, String color, String targetScreen) {
        Button btn = new Button(text);
        btn.setCursor(Cursor.HAND);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 15 25; -fx-background-radius: 10;");
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle().replace(color, color + "dd")));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace(color + "dd", color)));
        btn.setOnAction(e -> {
            if (navigationHandler != null) {
                navigationHandler.accept(targetScreen);
            }
        });
        return btn;
    }
}