package iuh.fit.presentation.controller;

import iuh.fit.core.dto.HoaDonDTO;
import iuh.fit.core.service.IHoaDonService;
import iuh.fit.core.service.IKhachHangService;
import iuh.fit.presentation.utils.BillPrinter;
import javafx.application.Platform;
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

import java.time.LocalDate;
import java.util.List;

public class QuanLyHoaDonController {

    private final IHoaDonService hoaDonService;

    // UI Components
    private TextField txtSearch;
    private DatePicker dpFrom, dpTo;
    private FlowPane cardContainer;
    private Label lblStatTotalAmount, lblStatTotalCount, lblStatToday;

    // Bảng màu thiết kế chuẩn
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_BG = "#f8fafc";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_DANGER = "#ef4444";

    public QuanLyHoaDonController(IHoaDonService hoaDonService, IKhachHangService khachHangService) {
        this.hoaDonService = hoaDonService;
    }

    public VBox createView() {
        VBox root = new VBox(25);
        root.setPadding(new Insets(30));
        // 👉 ÉP MÀU NỀN CỨNG ĐỂ CHỮ KHÔNG BỊ TÀNG HÌNH
        root.setStyle("-fx-background-color: " + COLOR_BG + ";");

        // 1. HEADER
        VBox header = new VBox(5);
        Label lblTitle = new Label("LỊCH SỬ HÓA ĐƠN");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 28));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));
        Label lblSub = new Label("Tra cứu giao dịch và in lại hóa đơn.");
        lblSub.setTextFill(Color.web(COLOR_TEXT_MUTED));
        header.getChildren().addAll(lblTitle, lblSub);

        // 2. THẺ THỐNG KÊ
        HBox statsRow = createStatsRow();

        // 3. THANH CÔNG CỤ
        HBox toolBar = createToolBar();

        // 4. KHU VỰC HIỂN THỊ THẺ
        cardContainer = new FlowPane(20, 20);
        cardContainer.setPadding(new Insets(10));
        cardContainer.setStyle("-fx-background-color: " + COLOR_BG + ";"); // Ép màu nền

        ScrollPane scrollPane = new ScrollPane(cardContainer);
        scrollPane.setFitToWidth(true);
        // 👉 FIX LỖI GIAO DIỆN: Ép màu nền cho ScrollPane
        scrollPane.setStyle("-fx-background: " + COLOR_BG + "; -fx-background-color: " + COLOR_BG + "; -fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(header, statsRow, toolBar, scrollPane);

        refreshData();
        return root;
    }

    private HBox createStatsRow() {
        HBox row = new HBox(20);
        lblStatTotalAmount = new Label("0 đ");
        lblStatTotalCount = new Label("0");
        lblStatToday = new Label("0 đ");

        row.getChildren().addAll(
                createMetricCard("TỔNG DOANH THU", lblStatTotalAmount, COLOR_PRIMARY),
                createMetricCard("SỐ GIAO DỊCH", lblStatTotalCount, "#6366f1"),
                createMetricCard("HÔM NAY", lblStatToday, COLOR_SUCCESS)
        );
        return row;
    }

    private VBox createMetricCard(String title, Label valLabel, String color) {
        VBox card = new VBox(8);
        card.setPrefWidth(300);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #e2e8f0;");

        Label lblT = new Label(title);
        lblT.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lblT.setTextFill(Color.web(COLOR_TEXT_MUTED));

        valLabel.setFont(Font.font("Segoe UI", FontWeight.BLACK, 22));
        valLabel.setTextFill(Color.web(color));

        card.getChildren().addAll(lblT, valLabel);
        return card;
    }

    private HBox createToolBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(15, 20, 15, 20));
        bar.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #e2e8f0;");

        txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm mã HD hoặc mã phòng...");
        txtSearch.setPrefWidth(250);
        txtSearch.textProperty().addListener((o, old, nw) -> searchByCode(nw));

        dpFrom = new DatePicker(LocalDate.now().minusDays(7));
        dpTo = new DatePicker(LocalDate.now());

        Button btnFilter = new Button("Lọc dữ liệu");
        btnFilter.setCursor(Cursor.HAND);
        btnFilter.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 8;");
        btnFilter.setOnAction(e -> refreshData());

        Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS);
        bar.getChildren().addAll(txtSearch, r, new Label("Từ:"), dpFrom, new Label("Đến:"), dpTo, btnFilter);
        return bar;
    }

    private void renderInvoiceCards(List<HoaDonDTO> list) {
        Platform.runLater(() -> {
            cardContainer.getChildren().clear();
            if (list == null || list.isEmpty()) {
                cardContainer.getChildren().add(new Label("📭 Không có dữ liệu hóa đơn nào."));
                return;
            }
            for (HoaDonDTO hd : list) {
                cardContainer.getChildren().add(createInvoiceCard(hd));
            }
        });
    }

    private VBox createInvoiceCard(HoaDonDTO hd) {
        VBox card = new VBox(12);
        card.setPrefWidth(320);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #e2e8f0; -fx-border-width: 1.5;");

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 15; -fx-border-color: " + COLOR_PRIMARY + "; -fx-border-width: 1.5;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #e2e8f0; -fx-border-width: 1.5;"));

        // Mã hóa đơn (Rút gọn hiển thị nếu quá dài)
        String displayMa = hd.getMaHoaDon();
        if (displayMa.length() > 15) displayMa = displayMa.substring(0, 15) + "...";

        Label lblMa = new Label(displayMa);
        lblMa.setFont(Font.font("Segoe UI", FontWeight.BLACK, 16));
        lblMa.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblDate = new Label("📅 " + (hd.getNgayLap() != null ? hd.getNgayLap().toString() : "N/A"));
        lblDate.setTextFill(Color.web(COLOR_TEXT_MUTED));

        // Thông tin phòng
        Label lblPhong = new Label("🏨 Phòng: " + (hd.getMaPhongDat() != null ? hd.getMaPhongDat() : "Đoàn"));
        lblPhong.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

        Label lblMoney = new Label(String.format("%,.0f đ", hd.getTongTien()));
        lblMoney.setFont(Font.font("Segoe UI", FontWeight.BLACK, 20));
        lblMoney.setTextFill(Color.web(COLOR_PRIMARY));

        Button btnPrint = new Button("🖨️ In lại hóa đơn");
        btnPrint.setMaxWidth(Double.MAX_VALUE);
        btnPrint.setCursor(Cursor.HAND);
        btnPrint.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: " + COLOR_PRIMARY + "; -fx-font-weight: bold; -fx-padding: 8; -fx-background-radius: 8;");
        btnPrint.setOnAction(e -> BillPrinter.printInvoice(hd, "In lại"));

        card.getChildren().addAll(lblMa, lblDate, new Separator(), lblPhong, lblMoney, btnPrint);
        // 👉 FIX LỖI TÀNG HÌNH: Dùng Color.rgb cho DropShadow
        card.setEffect(new DropShadow(8, Color.rgb(0,0,0,0.08)));

        return card;
    }

    private void refreshData() {
        LocalDate start = dpFrom.getValue();
        LocalDate end = dpTo.getValue();

        Task<List<HoaDonDTO>> task = new Task<>() {
            @Override
            protected List<HoaDonDTO> call() {
                return hoaDonService.getHoaDonByDateRange(start, end);
            }
        };

        task.setOnSucceeded(e -> {
            List<HoaDonDTO> result = task.getValue();
            renderInvoiceCards(result);
            updateStats(result);
        });

        new Thread(task).start();
    }

    private void searchByCode(String code) {
        if (code == null || code.isEmpty()) {
            refreshData();
            return;
        }

        Task<List<HoaDonDTO>> task = new Task<>() {
            @Override
            protected List<HoaDonDTO> call() {
                return hoaDonService.getAllHoaDon().stream()
                        .filter(h -> h.getMaHoaDon().toLowerCase().contains(code.toLowerCase()) ||
                                (h.getMaPhongDat() != null && h.getMaPhongDat().toLowerCase().contains(code.toLowerCase())))
                        .toList();
            }
        };

        task.setOnSucceeded(e -> renderInvoiceCards(task.getValue()));
        new Thread(task).start();
    }

    private void updateStats(List<HoaDonDTO> list) {
        if (list == null) return;
        double total = list.stream().mapToDouble(HoaDonDTO::getTongTien).sum();
        double todayTotal = list.stream()
                .filter(h -> h.getNgayLap() != null && h.getNgayLap().equals(LocalDate.now()))
                .mapToDouble(HoaDonDTO::getTongTien).sum();

        lblStatTotalAmount.setText(String.format("%,.0f đ", total));
        lblStatTotalCount.setText(String.valueOf(list.size()));
        lblStatToday.setText(String.format("%,.0f đ", todayTotal));
    }
}

