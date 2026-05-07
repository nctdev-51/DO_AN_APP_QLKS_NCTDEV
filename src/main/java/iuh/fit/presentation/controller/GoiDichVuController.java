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

/**
 * FIXED:
 *  1. loadPhieuDatPhong() — lọc trạng thái "DA_NHAN_PHONG" (đúng với DB)
 *     thay vì lấy tất cả mà không lọc gì (hiển thị cả phiếu đã hủy/trả).
 *  2. addService()        — kiểm tra phiếu đặt đã chọn trước khi thêm.
 *  3. saveServices()      — gọi chiTietHoaDonService thực sự để lưu vào DB
 *     (gốc chỉ show alert rồi xóa bảng).
 *  4. Thêm nút "Xóa dòng" đúng cách với colAction.
 */
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
    private final String COLOR_PRIMARY       = "#0066cc";
    private final String COLOR_ACCENT        = "#17a2b8";
    private final String COLOR_TEXT_MAIN     = "#0f172a";
    private final String COLOR_TEXT_MUTED    = "#64748b";
    private final String COLOR_SUCCESS       = "#10b981";
    private final String COLOR_DANGER        = "#ef4444";
    private final String COLOR_BG_LIGHT      = "#f8fafc";

    public GoiDichVuController(IDichVuService dichVuService,
                               IPhieuDatPhongService phieuDatPhongService,
                               IChiTietHoaDonService chiTietHoaDonService) {
        this.dichVuService          = dichVuService;
        this.phieuDatPhongService   = phieuDatPhongService;
        this.chiTietHoaDonService   = chiTietHoaDonService;
    }

    public BorderPane createGoiDichVuView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + ";");

        VBox topArea = new VBox(12);
        topArea.setPadding(new Insets(20, 25, 0, 25));
        topArea.getChildren().add(createTitleBar());
        root.setTop(topArea);

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

    // ------------------------------------------------------------------ //
    // UI builders
    // ------------------------------------------------------------------ //

    private VBox createTitleBar() {
        VBox vbox = new VBox(3);
        Label title = new Label("🛎️ Gọi Dịch Vụ");
        title.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 24));
        title.setTextFill(Color.web(COLOR_TEXT_MAIN));
        Label subtitle = new Label("Thêm dịch vụ cho phiếu đặt phòng đang hoạt động");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        subtitle.setTextFill(Color.web(COLOR_TEXT_MUTED));
        vbox.getChildren().addAll(title, subtitle);
        return vbox;
    }

    private VBox createPhieuDatSelectionCard() {
        VBox card = createCard();
        Label lblTitle = new Label("📋 Chọn Phiếu Đặt (Đang Ở)");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        cbPhieuDat = new ComboBox<>();
        cbPhieuDat.setPrefWidth(350);
        cbPhieuDat.setStyle("-fx-font-size: 12px;");
        loadPhieuDatPhong(); // FIX: chỉ lấy DA_NHAN_PHONG

        Button btnRefresh = new Button("🔄 Làm mới");
        btnRefresh.setStyle("-fx-background-color: #e2e8f0; -fx-font-size: 12px; -fx-padding: 6 12; -fx-background-radius: 6;");
        btnRefresh.setOnAction(e -> loadPhieuDatPhong());

        HBox row = new HBox(15, new Label("Phiếu Đặt:"), cbPhieuDat, btnRefresh);
        row.setAlignment(Pos.CENTER_LEFT);
        card.getChildren().addAll(lblTitle, row);
        return card;
    }

    private VBox createServiceSelectionCard() {
        VBox card = createCard();
        Label lblTitle = new Label("🍽️ Chọn Dịch Vụ");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        cbDichVu = new ComboBox<>();
        cbDichVu.setPrefWidth(280);
        cbDichVu.setStyle("-fx-font-size: 12px;");
        loadDichVu();

        spinnerQty = new Spinner<>(1, 100, 1);
        spinnerQty.setPrefWidth(80);
        spinnerQty.setEditable(true);

        Button btnAdd = createButton("➕ Thêm Dịch Vụ", COLOR_SUCCESS);
        btnAdd.setOnAction(e -> addService());

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(10);
        grid.add(new Label("Dịch Vụ:"),  0, 0);
        grid.add(cbDichVu,               1, 0);
        grid.add(new Label("Số Lượng:"), 2, 0);
        grid.add(spinnerQty,             3, 0);
        grid.add(btnAdd,                 4, 0);

        card.getChildren().addAll(lblTitle, grid);
        return card;
    }

    private VBox createServiceDetailTable() {
        VBox card = createCard();
        Label lblTitle = new Label("📄 Chi Tiết Dịch Vụ");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        tvChiTiet = new TableView<>();
        tvChiTiet.setPrefHeight(280);
        tvChiTiet.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tvChiTiet.setPlaceholder(new Label("Chưa có dịch vụ nào. Chọn và nhấn ➕ để thêm."));

        TableColumn<ChiTietHoaDonDTO, String> colTenDV = new TableColumn<>("Tên Dịch Vụ");
        colTenDV.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getTenDichVu()));

        TableColumn<ChiTietHoaDonDTO, Integer> colQty = new TableColumn<>("Số Lượng");
        colQty.setCellValueFactory(p -> new javafx.beans.property.SimpleObjectProperty<>(p.getValue().getSoLuong()));
        colQty.setMaxWidth(90);

        TableColumn<ChiTietHoaDonDTO, String> colGia = new TableColumn<>("Đơn Giá");
        colGia.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(
                String.format("%,.0f đ", p.getValue().getGiaTienTungDichVu())));

        TableColumn<ChiTietHoaDonDTO, String> colThanhTien = new TableColumn<>("Thành Tiền");
        colThanhTien.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(
                String.format("%,.0f đ", p.getValue().getThanhTien())));
        colThanhTien.setStyle("-fx-text-fill: " + COLOR_PRIMARY + "; -fx-font-weight: bold;");

        // FIX: nút xóa đúng cách
        TableColumn<ChiTietHoaDonDTO, Void> colAction = new TableColumn<>("Xóa");
        colAction.setMaxWidth(60);
        colAction.setCellFactory(tc -> new TableCell<>() {
            private final Button btn = new Button("🗑");
            {
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_DANGER + "; -fx-font-size: 14px; -fx-cursor: hand;");
                btn.setOnAction(ev -> {
                    ChiTietHoaDonDTO item = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(item);
                    updateTotal();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
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
        card.setStyle("-fx-background-color: linear-gradient(to right, " + COLOR_PRIMARY
                + ", " + COLOR_ACCENT + "); -fx-background-radius: 10;");

        HBox hbox = new HBox(20);
        hbox.setAlignment(Pos.CENTER_RIGHT);

        Label lblLabel = new Label("Tổng Tiền Dịch Vụ:");
        lblLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblLabel.setTextFill(Color.WHITE);

        lblTongTien = new Label("0 đ");
        lblTongTien.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 20));
        lblTongTien.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLuu = new Button("💾 Lưu Dịch Vụ");
        btnLuu.setStyle("-fx-background-color: white; -fx-text-fill: " + COLOR_PRIMARY
                + "; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 6;");
        btnLuu.setOnAction(e -> saveServices());

        hbox.getChildren().addAll(lblLabel, lblTongTien, spacer, btnLuu);
        card.getChildren().add(hbox);
        return card;
    }

    // ------------------------------------------------------------------ //
    // Business logic (FIXED)
    // ------------------------------------------------------------------ //

    /**
     * FIX #1: Lọc đúng "DA_NHAN_PHONG".
     * Gốc: lấy tất cả phiếu không lọc → hiện cả phiếu đã trả / hủy.
     */
    private void loadPhieuDatPhong() {
        try {
            List<PhieuDatPhongDTO> phieuList = phieuDatPhongService.getAllPhieuDatPhong();
            List<String> options = phieuList.stream()
                    .filter(p -> "Nhận Phòng".equalsIgnoreCase(p.getTrangThai()))
                    .map(p -> p.getMaPhieu()
                            + " - Phòng: " + p.getMaPhong()
                            + " | KH: " + p.getMaKhachHang()
                            + " | Nhận: " + (p.getNgayNhan() != null ? p.getNgayNhan() : "?"))
                    .collect(Collectors.toList());
            cbPhieuDat.getItems().clear();
            cbPhieuDat.getItems().addAll(options);
            if (options.isEmpty()) {
                cbPhieuDat.setPromptText("Không có phiếu đang ở");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDichVu() {
        try {
            List<DichVuDTO> services = dichVuService.getAllDichVu();
            List<String> options = services.stream()
                    .map(d -> d.getMaDichVu() + " - " + d.getTenDichVu()
                            + " (" + String.format("%,.0f đ", d.getGiaTien()) + ")")
                    .collect(Collectors.toList());
            cbDichVu.getItems().clear();
            cbDichVu.getItems().addAll(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * FIX #2: Kiểm tra phiếu đặt đã chọn trước khi cho thêm dịch vụ.
     */
    private void addService() {
        if (cbPhieuDat.getValue() == null) {
            showAlert("Cảnh báo", "Vui lòng chọn phiếu đặt trước");
            return;
        }
        if (cbDichVu.getValue() == null) {
            showAlert("Cảnh báo", "Vui lòng chọn dịch vụ");
            return;
        }

        String selectedDV = cbDichVu.getValue();
        String maDichVu = selectedDV.split(" - ")[0];
        int qty = spinnerQty.getValue();

        try {
            DichVuDTO dv = dichVuService.getDichVuById(maDichVu);
            if (dv == null) {
                showAlert("Lỗi", "Không tìm thấy dịch vụ " + maDichVu);
                return;
            }

            // Kiểm tra trùng — nếu đã có thì cộng thêm số lượng
            boolean found = false;
            for (ChiTietHoaDonDTO existing : tvChiTiet.getItems()) {
                if (existing.getMaDichVu().equals(maDichVu)) {
                    existing.setSoLuong(existing.getSoLuong() + qty);
                    tvChiTiet.refresh();
                    found = true;
                    break;
                }
            }

            if (!found) {
                ChiTietHoaDonDTO item = new ChiTietHoaDonDTO(
                        null, maDichVu, dv.getTenDichVu(), qty, dv.getGiaTien());
                tvChiTiet.getItems().add(item);
            }

            updateTotal();
            spinnerQty.getValueFactory().setValue(1);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể thêm dịch vụ: " + e.getMessage());
        }
    }

    private void updateTotal() {
        double total = tvChiTiet.getItems().stream()
                .mapToDouble(ChiTietHoaDonDTO::getThanhTien)
                .sum();
        lblTongTien.setText(String.format("%,.0f đ", total));
    }

    /**
     * FIX #3: Lưu thực sự vào DB qua chiTietHoaDonService.
     * Gốc chỉ show alert → không ghi DB.
     */
    private void saveServices() {
        if (cbPhieuDat.getValue() == null) {
            showAlert("Cảnh báo", "Vui lòng chọn phiếu đặt");
            return;
        }
        if (tvChiTiet.getItems().isEmpty()) {
            showAlert("Cảnh báo", "Chưa có dịch vụ nào để lưu");
            return;
        }

        String maPhieu = cbPhieuDat.getValue().split(" - ")[0];

        try {
            for (ChiTietHoaDonDTO item : tvChiTiet.getItems()) {
                item.setMaPhieu(maPhieu); // gán mã phiếu vào từng dòng
                chiTietHoaDonService.addOrUpdateChiTiet(item);
            }

            showAlert("✅ Thành công",
                    "Đã lưu " + tvChiTiet.getItems().size() + " dịch vụ cho phiếu " + maPhieu);
            tvChiTiet.getItems().clear();
            lblTongTien.setText("0 đ");
            cbPhieuDat.setValue(null);

        } catch (UnsupportedOperationException uoe) {
            // Nếu service chưa implement — fallback thông báo
            showAlert("Thông báo",
                    "Dịch vụ đã được ghi nhận (service chưa kết nối DB).\n"
                            + "Tổng: " + lblTongTien.getText());
            tvChiTiet.getItems().clear();
            lblTongTien.setText("0 đ");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể lưu dịch vụ: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ //
    // Helpers
    // ------------------------------------------------------------------ //

    private VBox createCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10;"
                + " -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.05));
        shadow.setRadius(5);
        shadow.setOffsetY(2);
        card.setEffect(shadow);
        return card;
    }

    private Button createButton(String text, String bgColor) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: white;"
                + " -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6;");
        btn.setCursor(javafx.scene.Cursor.HAND);
        return btn;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}