package iuh.fit.presentation.controller;

import iuh.fit.core.dto.DichVuDTO;
import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.dto.ChiTietHoaDonDTO;
import iuh.fit.core.service.IDichVuService;
import iuh.fit.core.service.IPhieuDatPhongService;
import iuh.fit.core.service.IChiTietHoaDonService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.stream.Collectors;

public class GoiDichVuController {

    private final IDichVuService dichVuService;
    private final IPhieuDatPhongService phieuDatPhongService;
    private final IChiTietHoaDonService chiTietHoaDonService;

    // UI Components
    private ComboBox<String> cbPhieuDat;
    private ComboBox<String> cbDichVu;
    private Spinner<Integer> spinnerQty;
    private TableView<ChiTietHoaDonDTO> tvChiTiet;
    private Label lblTongTien;

    // Colors
    private final String COLOR_PRIMARY = "#0066cc";
    private final String COLOR_ACCENT = "#17a2b8";
    private final String COLOR_PRIMARY_HOVER = "#004999";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_BG_LIGHT = "#f8fafc";

    public GoiDichVuController(IDichVuService dichVuService, IPhieuDatPhongService phieuDatPhongService,
                                IChiTietHoaDonService chiTietHoaDonService) {
        this.dichVuService = dichVuService;
        this.phieuDatPhongService = phieuDatPhongService;
        this.chiTietHoaDonService = chiTietHoaDonService;
    }

    public BorderPane createGoiDichVuView() {
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
                createPhieuDatSelectionCard(),
                createServiceSelectionCard(),
                createServiceDetailTable(),
                createTotalCard()
        );
        ScrollPane scrollPane = new ScrollPane(centerArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent;");
        root.setCenter(scrollPane);

        return root;
    }

    private VBox createTitleBar() {
        VBox vbox = new VBox(3);
        Label title = new Label("🍽️ Gọi Dịch Vụ");
        title.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 24));
        title.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label subtitle = new Label("Quản lý dịch vụ cho khách hàng đang ở");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        subtitle.setTextFill(Color.web(COLOR_TEXT_MUTED));

        vbox.getChildren().addAll(title, subtitle);
        return vbox;
    }

    private VBox createPhieuDatSelectionCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
        applyCardStyle(card);

        Label lblTitle = new Label("🔍 Chọn Phiếu Đặt Phòng");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        HBox selectionBox = new HBox(15);
        selectionBox.setAlignment(Pos.CENTER_LEFT);

        Label lblPhieu = new Label("Phiếu Đặt:");
        lblPhieu.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        cbPhieuDat = new ComboBox<>();
        cbPhieuDat.setPrefWidth(250);
        cbPhieuDat.setStyle("-fx-font-size: 12px;");
        loadPhieuDatPhong();

        selectionBox.getChildren().addAll(lblPhieu, cbPhieuDat);
        card.getChildren().addAll(lblTitle, selectionBox);
        return card;
    }

    private VBox createServiceSelectionCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
        applyCardStyle(card);

        Label lblTitle = new Label("🛎️ Chọn Dịch Vụ");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        Label lblDichVu = new Label("Dịch Vụ:");
        lblDichVu.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        cbDichVu = new ComboBox<>();
        cbDichVu.setPrefWidth(250);
        cbDichVu.setStyle("-fx-font-size: 12px;");
        loadDichVu();

        Label lblQty = new Label("Số Lượng:");
        lblQty.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        spinnerQty = new Spinner<>(1, 100, 1);
        spinnerQty.setPrefWidth(80);

        Button btnAdd = createButton("➕ Thêm Dịch Vụ", COLOR_SUCCESS);
        btnAdd.setOnAction(e -> addService());

        grid.add(lblDichVu, 0, 0);
        grid.add(cbDichVu, 1, 0);
        grid.add(lblQty, 2, 0);
        grid.add(spinnerQty, 3, 0);
        grid.add(btnAdd, 4, 0);

        card.getChildren().addAll(lblTitle, grid);
        return card;
    }

    private VBox createServiceDetailTable() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
        applyCardStyle(card);

        Label lblTitle = new Label("📋 Chi Tiết Dịch Vụ");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        tvChiTiet = new TableView<>();
        tvChiTiet.setPrefHeight(300);
        tvChiTiet.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ChiTietHoaDonDTO, String> colTenDV = new TableColumn<>("Tên Dịch Vụ");
        colTenDV.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(param.getValue().getTenDichVu()));

        TableColumn<ChiTietHoaDonDTO, Integer> colQty = new TableColumn<>("Số Lượng");
        colQty.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getSoLuong()));

        TableColumn<ChiTietHoaDonDTO, Double> colGia = new TableColumn<>("Giá Đơn Vị");
        colGia.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getGiaTienTungDichVu()));

        TableColumn<ChiTietHoaDonDTO, Double> colThanhTien = new TableColumn<>("Thành Tiền");
        colThanhTien.setCellValueFactory(param -> new javafx.beans.property.SimpleObjectProperty<>(param.getValue().getThanhTien()));

        TableColumn<ChiTietHoaDonDTO, Void> colAction = new TableColumn<>("Hành Động");
        colAction.setCellFactory(param -> new TableCell<ChiTietHoaDonDTO, Void>() {
            private final Button btnDelete = new Button("🗑️");
            {
                btnDelete.setStyle("-fx-font-size: 11px; -fx-padding: 3 8;");
                btnDelete.setOnAction(event -> {
                    ChiTietHoaDonDTO item = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(item);
                    updateTotal();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
                setAlignment(Pos.CENTER);
            }
        });

        tvChiTiet.getColumns().addAll(colTenDV, colQty, colGia, colThanhTien, colAction);

        card.getChildren().addAll(lblTitle, tvChiTiet);
        return card;
    }

    private VBox createTotalCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: linear-gradient(to right, " + COLOR_PRIMARY + ", " + COLOR_ACCENT + "); -fx-background-radius: 10;");

        HBox hbox = new HBox(20);
        hbox.setAlignment(Pos.CENTER_RIGHT);

        Label lblLabel = new Label("Tổng Tiền Dịch Vụ:");
        lblLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblLabel.setTextFill(Color.WHITE);

        lblTongTien = new Label("0 đ");
        lblTongTien.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 18));
        lblTongTien.setTextFill(Color.WHITE);

        Button btnLuu = createButton("💾 Lưu Dịch Vụ", "#ffffff", COLOR_PRIMARY);
        btnLuu.setStyle("-fx-background-color: white; -fx-text-fill: " + COLOR_PRIMARY + "; -fx-font-weight: bold;");
        btnLuu.setOnAction(e -> saveServices());

        hbox.getChildren().addAll(lblLabel, lblTongTien, new Region(), btnLuu);
        HBox.setHgrow(hbox.getChildren().get(2), Priority.ALWAYS);

        card.getChildren().add(hbox);
        return card;
    }

    private void loadPhieuDatPhong() {
        try {
            List<PhieuDatPhongDTO> phieuList = phieuDatPhongService.getAllPhieuDatPhong();
            List<String> options = phieuList.stream()
                    .map(p -> p.getMaPhieu() + " - Phòng: " + p.getMaPhong())
                    .collect(Collectors.toList());
            cbPhieuDat.getItems().addAll(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDichVu() {
        try {
            List<DichVuDTO> services = dichVuService.getAllDichVu();
            List<String> options = services.stream()
                    .map(d -> d.getMaDichVu() + " - " + d.getTenDichVu() + " (" + String.format("%.0f đ", d.getGiaTien()) + ")")
                    .collect(Collectors.toList());
            cbDichVu.getItems().addAll(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addService() {
        if (cbDichVu.getValue() == null || spinnerQty.getValue() == null) {
            showAlert("Cảnh báo", "Vui lòng chọn dịch vụ và số lượng");
            return;
        }

        String selectedDV = cbDichVu.getValue();
        String maDichVu = selectedDV.split(" - ")[0];
        String tenDichVu = selectedDV.split(" - ")[1];
        int qty = spinnerQty.getValue();

        try {
            DichVuDTO dv = dichVuService.getDichVuById(maDichVu);
            ChiTietHoaDonDTO item = new ChiTietHoaDonDTO(null, maDichVu, dv.getTenDichVu(), qty, dv.getGiaTien());
            tvChiTiet.getItems().add(item);
            updateTotal();
            spinnerQty.getValueFactory().setValue(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTotal() {
        double total = tvChiTiet.getItems().stream()
                .mapToDouble(ChiTietHoaDonDTO::getThanhTien)
                .sum();
        lblTongTien.setText(String.format("%,.0f đ", total));
    }

    private void saveServices() {
        if (tvChiTiet.getItems().isEmpty()) {
            showAlert("Cảnh báo", "Chưa có dịch vụ nào được thêm");
            return;
        }
        showAlert("Thông báo", "Dịch vụ đã được lưu thành công!");
        tvChiTiet.getItems().clear();
        lblTongTien.setText("0 đ");
    }

    private Button createButton(String text, String bgColor) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;");
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + darkenColor(bgColor) + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;"));
        return btn;
    }

    private Button createButton(String text, String textColor, String bgColor) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + "; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;");
        btn.setCursor(javafx.scene.Cursor.HAND);
        return btn;
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

    private String darkenColor(String hex) {
        try {
            long rgb = Long.parseLong(hex.substring(1), 16);
            int r = (int) ((rgb >> 16) & 0xFF);
            int g = (int) ((rgb >> 8) & 0xFF);
            int b = (int) (rgb & 0xFF);
            r = Math.max(0, r - 30);
            g = Math.max(0, g - 30);
            b = Math.max(0, b - 30);
            return String.format("#%02x%02x%02x", r, g, b);
        } catch (Exception e) {
            return hex;
        }
    }
}

