package iuh.fit.presentation.controller;

import iuh.fit.core.dto.PhongDTO;
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
import javafx.scene.chart.PieChart;
import javafx.application.Platform;

import java.util.List;
import java.util.function.Consumer;

public class DashboardController {

    private IPhongService phongService;
    private Consumer<String> navigationHandler; // Hàm điều hướng từ MainController

    // Màu sắc
    private final String COLOR_BG = "#f8fafc";
    private final String COLOR_CARD_BG = "#ffffff";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";

    public DashboardController(IPhongService phongService, Consumer<String> navigationHandler) {
        this.phongService = phongService;
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
        // Mỗi thẻ sẽ tự động dãn đều nhờ HBox.setHgrow
        for (var node : statsRow.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
            ((VBox) node).setMaxWidth(Double.MAX_VALUE);
        }

        // 2. HÀNG BIỂU ĐỒ & THÔNG BÁO
        HBox middleRow = new HBox(30);
        middleRow.setAlignment(Pos.TOP_CENTER);

        // --- Biểu đồ tròn ---
        // --- Biểu đồ tròn ---
        VBox chartCard = new VBox(15);
        chartCard.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 12; -fx-padding: 20;");
        chartCard.setEffect(new DropShadow(5, Color.color(0,0,0,0.05)));
        chartCard.setPrefWidth(450);

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

// Đặt màu cho các lát cắt SAU KHI CHART ĐÃ ĐƯỢC HIỂN THỊ
        Platform.runLater(() -> {
            sliceTrong.getNode().setStyle("-fx-pie-color: #10b981;");
            sliceDangO.getNode().setStyle("-fx-pie-color: #f59e0b;");
            sliceDaDat.getNode().setStyle("-fx-pie-color: #ef4444;");
            sliceBaoTri.getNode().setStyle("-fx-pie-color: #64748b;");
        });

// Kết luận nhỏ dưới biểu đồ
        String conclusion = String.format("Hiện có %d phòng sẵn sàng phục vụ, %d phòng đang có khách.",
                trong, dangO);
        Label lblConclusion = new Label("💡 " + conclusion);
        lblConclusion.setWrapText(true);
        lblConclusion.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        lblConclusion.setTextFill(Color.web(COLOR_TEXT_MUTED));

        chartCard.getChildren().addAll(lblChartTitle, pieChart, lblConclusion);

        // --- Cột thông báo ---
        VBox notificationCard = new VBox(15);
        notificationCard.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 12; -fx-padding: 20;");
        notificationCard.setEffect(new DropShadow(5, Color.color(0,0,0,0.05)));
        HBox.setHgrow(notificationCard, Priority.ALWAYS);

        Label lblNotiTitle = new Label("🔔 Thông báo & Nhắc nhở");
        lblNotiTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblNotiTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        VBox notiList = new VBox(10);
        // Thêm các thông báo giả lập (có thể lấy từ service thực tế)
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

        // 3. TRUY CẬP NHANH
        Label lblQuick = new Label("⚡ Truy cập nhanh");
        lblQuick.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblQuick.setTextFill(Color.web(COLOR_TEXT_MAIN));

        HBox quickActions = new HBox(20);
        quickActions.setAlignment(Pos.CENTER_LEFT);

        Button btnBook = createQuickActionButton("🏨 Đặt phòng mới", "#3b82f6", "BOOKING");
        Button btnMap = createQuickActionButton("🗺️ Sơ đồ phòng", "#10b981", "ROOM_MAP");
        Button btnManageOrders = createQuickActionButton("📋 Quản lý phiếu", "#f59e0b", "MANAGE_ORDERS");
        Button btnManageRooms = createQuickActionButton("🚪 Quản lý phòng", "#8b5cf6", "MANAGE_ROOMS");

        quickActions.getChildren().addAll(btnBook, btnMap, btnManageOrders, btnManageRooms);

        root.getChildren().addAll(statsRow, middleRow, lblQuick, quickActions);

        return root;
    }

    // --- Hàm tạo thẻ thống kê ---
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

    // --- Hàm tạo một dòng thông báo click được ---
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

    // --- Nút truy cập nhanh ---
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