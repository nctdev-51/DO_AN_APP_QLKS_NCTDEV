package iuh.fit.presentation.controller;

import iuh.fit.core.dto.HoaDonDTO;
import iuh.fit.core.service.IHoaDonService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.List;

public class QuanLyHoaDonController {

    private final IHoaDonService hoaDonService;

    // UI Components
    private TableView<HoaDonDTO> tableHoaDon;
    private TextField txtSearch;
    private DatePicker dpFrom, dpTo;
    private Label lblTotalRevenue;

    // Định nghĩa bảng màu đồng bộ với MainController của Tú
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_BG = "#f8fafc";
    private final String COLOR_TEXT = "#0f172a";

    public QuanLyHoaDonController(IHoaDonService hoaDonService) {
        this.hoaDonService = hoaDonService;
    }

    public VBox createView() {
        VBox root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + COLOR_BG + ";");

        // 1. Tiêu đề
        VBox header = new VBox(5);
        Label lblTitle = new Label("QUẢN LÝ HÓA ĐƠN");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 28));
        lblTitle.setTextFill(Color.web(COLOR_TEXT));
        header.getChildren().addAll(lblTitle, new Label("Tra cứu hóa đơn và doanh thu dịch vụ."));

        // 2. Thanh công cụ (Lọc theo ngày & Tìm kiếm)
        HBox toolBar = createToolBar();

        // 3. Bảng hiển thị
        setupTable();
        VBox.setVgrow(tableHoaDon, Priority.ALWAYS);

        // 4. Footer hiển thị tổng doanh thu
        HBox footer = createFooter();

        root.getChildren().addAll(header, toolBar, tableHoaDon, footer);

        // Load dữ liệu mặc định (1 tháng gần nhất)
        refreshData();

        return root;
    }

    private HBox createToolBar() {
        HBox bar = new HBox(15);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(20));
        bar.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e2e8f0;");

        txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Nhập mã hóa đơn hoặc mã phiếu...");
        txtSearch.setPrefWidth(250);
        txtSearch.setStyle("-fx-padding: 8;");
        // Tú gõ phím đến đâu, hệ thống tìm đến đó
        txtSearch.textProperty().addListener((o, old, nw) -> {
            if (nw.isEmpty()) refreshData();
            else searchByCode(nw);
        });

        dpFrom = new DatePicker(LocalDate.now().minusMonths(1));
        dpTo = new DatePicker(LocalDate.now());

        Button btnFilter = new Button("Lọc Dữ Liệu");
        btnFilter.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        btnFilter.setOnAction(e -> refreshData());

        bar.getChildren().addAll(new Label("Tìm:"), txtSearch, new Label("Từ:"), dpFrom, new Label("Đến:"), dpTo, btnFilter);
        return bar;
    }

    private void setupTable() {
        tableHoaDon = new TableView<>();
        tableHoaDon.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Cột Mã Hóa Đơn
        TableColumn<HoaDonDTO, String> colMa = new TableColumn<>("Mã Hóa Đơn");
        colMa.setCellValueFactory(new PropertyValueFactory<>("maHoaDon"));

        // Cột Ngày Lập
        TableColumn<HoaDonDTO, String> colNgay = new TableColumn<>("Ngày Lập");
        // Lưu ý: "ngayLap" phải khớp đúng tên field/getter trong HoaDonDTO của ông
        colNgay.setCellValueFactory(new PropertyValueFactory<>("ngayLap"));

        // Cột Mã Phiếu Đặt
        TableColumn<HoaDonDTO, String> colPhieu = new TableColumn<>("Mã Phiếu Đặt");
        colPhieu.setCellValueFactory(new PropertyValueFactory<>("maPhieuDat"));

        // Cột Tổng Tiền (Xử lý định dạng tiền tệ)
        TableColumn<HoaDonDTO, Double> colTong = new TableColumn<>("Tổng Thanh Toán");
        colTong.setCellValueFactory(new PropertyValueFactory<>("tongTien"));
        colTong.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%,.0f đ", item));
                    setTextFill(Color.web(COLOR_PRIMARY));
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });

        tableHoaDon.getColumns().addAll(colMa, colNgay, colPhieu, colTong);
    }

    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(15));
        footer.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 8;");

        lblTotalRevenue = new Label("TỔNG DOANH THU: 0 đ");
        lblTotalRevenue.setTextFill(Color.WHITE);
        lblTotalRevenue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        footer.getChildren().add(lblTotalRevenue);
        return footer;
    }

    // --- LOGIC XỬ LÝ DỮ LIỆU TỪ SERVICE ---

    private void refreshData() {
        LocalDate start = dpFrom.getValue();
        LocalDate end = dpTo.getValue();

        Task<List<HoaDonDTO>> task = new Task<>() {
            @Override
            protected List<HoaDonDTO> call() {
                // Sử dụng hàm lọc theo khoảng ngày từ interface của ông
                return hoaDonService.getHoaDonByDateRange(start, end);
            }
        };

        task.setOnSucceeded(e -> {
            List<HoaDonDTO> result = task.getValue();
            tableHoaDon.setItems(FXCollections.observableArrayList(result));
            updateTotalRevenue(result);
        });

        new Thread(task).start();
    }

    private void searchByCode(String code) {
        // Tận dụng hàm tìm theo mã phiếu đặt trong interface
        List<HoaDonDTO> result = hoaDonService.getHoaDonByPhieuDat(code);
        // Nếu không thấy theo mã phiếu, thì ta lọc từ danh sách tất cả theo mã hóa đơn (giả lập)
        if (result.isEmpty()) {
            result = hoaDonService.getAllHoaDon().stream()
                    .filter(h -> h.getMaHoaDon().toLowerCase().contains(code.toLowerCase()))
                    .toList();
        }
        tableHoaDon.setItems(FXCollections.observableArrayList(result));
        updateTotalRevenue(result);
    }

    private void updateTotalRevenue(List<HoaDonDTO> list) {
        double total = list.stream().mapToDouble(HoaDonDTO::getTongTien).sum();
        lblTotalRevenue.setText("TỔNG DOANH THU KHOẢNG NÀY: " + String.format("%,.0f đ", total));
    }


}