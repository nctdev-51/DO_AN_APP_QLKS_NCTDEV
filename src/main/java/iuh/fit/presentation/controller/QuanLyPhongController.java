package iuh.fit.presentation.controller;

import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.service.IPhongService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.text.Normalizer;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class QuanLyPhongController {
    private static final Logger logger = Logger.getLogger(QuanLyPhongController.class.getName());

    private IPhongService phongService;
    private ObservableList<PhongDTO> phongList;

    // UI Components
    private TableView<PhongDTO> table;
    private TextField searchField;
    private TextField txtMaPhong, txtTenPhong, txtGiaPhong;
    private ComboBox<String> cbLoaiPhong, cbTinhTrang;
    private Button btnThem, btnCapNhat, btnXoa, btnLamMoi;

    // Labels cho Thống kê
    private Label lblTongPhongNum, lblTrongNum, lblDangONum;

    // --- BẢNG MÀU HIỆN ĐẠI TƯƠNG PHẢN CAO ---
    private final String COLOR_BG = "#f1f5f9";
    private final String COLOR_CARD_BG = "#ffffff";
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_WARNING = "#f59e0b";
    private final String COLOR_DANGER = "#ef4444";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_BORDER = "#e2e8f0";

    public QuanLyPhongController(IPhongService phongService) {
        this.phongService = phongService;
    }

    public VBox createQuanLyPhongView() {
        VBox rootBox = new VBox(20);
        rootBox.setStyle("-fx-background-color: " + COLOR_BG + "; -fx-padding: 25 30 25 30;");

        // Bóng đổ siêu mượt cho Card
        DropShadow softShadow = new DropShadow();
        softShadow.setColor(Color.web("#000000", 0.04));
        softShadow.setRadius(15);
        softShadow.setOffsetY(8);

        // =================================================================================
        // 1. KHU VỰC THỐNG KÊ (DASHBOARD STATS)
        // =================================================================================
        HBox statsContainer = new HBox(20);
        statsContainer.setAlignment(Pos.CENTER_LEFT);

        // Thẻ Tổng số phòng (Màu Xanh lam)
        VBox cardTong = createStatCard("🏨 TỔNG SỐ PHÒNG", "0", "-fx-background-color: linear-gradient(to right bottom, #3b82f6, #2563eb);", softShadow);
        lblTongPhongNum = (Label) cardTong.getChildren().get(1);

        // Thẻ Phòng Trống (Màu Xanh lá)
        VBox cardTrong = createStatCard("✨ PHÒNG TRỐNG", "0", "-fx-background-color: linear-gradient(to right bottom, #10b981, #059669);", softShadow);
        lblTrongNum = (Label) cardTrong.getChildren().get(1);

        // Thẻ Đang sử dụng (Màu Cam/Đỏ)
        VBox cardDangO = createStatCard("🔑 ĐANG PHỤC VỤ", "0", "-fx-background-color: linear-gradient(to right bottom, #f59e0b, #d97706);", softShadow);
        lblDangONum = (Label) cardDangO.getChildren().get(1);

        statsContainer.getChildren().addAll(cardTong, cardTrong, cardDangO);

        // =================================================================================
        // 2. KHU VỰC FORM NHẬP LIỆU (NẰM NGANG TINH GỌN)
        // =================================================================================
        VBox formCard = new VBox(15);
        formCard.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 12; -fx-padding: 20 25;");
        formCard.setEffect(softShadow);

        Label lblFormTitle = new Label("Thông Tin & Điều Khiển");
        lblFormTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblFormTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        String inputStyle = "-fx-padding: 9 12; -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: " + COLOR_BORDER + "; -fx-background-color: #f8fafc; -fx-font-size: 13px; -fx-text-fill: " + COLOR_TEXT_MAIN + ";";

        txtMaPhong = new TextField(); txtMaPhong.setDisable(true); txtMaPhong.setStyle(inputStyle);
        txtTenPhong = new TextField(); txtTenPhong.setPromptText("VD: Phòng 101"); txtTenPhong.setStyle(inputStyle);
        txtGiaPhong = new TextField(); txtGiaPhong.setPromptText("VD: 500000"); txtGiaPhong.setStyle(inputStyle);

        cbLoaiPhong = new ComboBox<>(); cbLoaiPhong.setItems(FXCollections.observableArrayList("DON", "DOI", "GIADINH", "VIP"));
        cbLoaiPhong.setStyle(inputStyle); cbLoaiPhong.setMaxWidth(Double.MAX_VALUE);

        cbTinhTrang = new ComboBox<>(); cbTinhTrang.setItems(FXCollections.observableArrayList("Trống", "Đã Đặt", "Đang ở", "Bảo Trì"));
        cbTinhTrang.setStyle(inputStyle); cbTinhTrang.setMaxWidth(Double.MAX_VALUE);

        // Sắp xếp Grid nằm ngang (5 cột)
        GridPane gridPane = new GridPane();
        gridPane.setHgap(20); gridPane.setVgap(15);
        for(int i=0; i<5; i++) { ColumnConstraints col = new ColumnConstraints(); col.setPercentWidth(20); gridPane.getColumnConstraints().add(col); }

        gridPane.add(createInputBox("Mã phòng", txtMaPhong), 0, 0);
        gridPane.add(createInputBox("Tên phòng", txtTenPhong), 1, 0);
        gridPane.add(createInputBox("Loại phòng", cbLoaiPhong), 2, 0);
        gridPane.add(createInputBox("Giá phòng (VNĐ)", txtGiaPhong), 3, 0);
        gridPane.add(createInputBox("Tình trạng", cbTinhTrang), 4, 0);

        // Thanh Nút Bấm
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        btnLamMoi = createButton("Làm Mới", COLOR_TEXT_MUTED, "#475569"); btnLamMoi.setOnAction(e -> lamMoiForm());
        btnThem = createButton("✨ Thêm Mới", COLOR_SUCCESS, "#059669"); btnThem.setOnAction(e -> themPhong());
        btnCapNhat = createButton("🔄 Cập Nhật", COLOR_PRIMARY, "#1d4ed8"); btnCapNhat.setOnAction(e -> capNhatPhong());
        btnXoa = createButton("🗑 Xóa", COLOR_DANGER, "#dc2626"); btnXoa.setOnAction(e -> xoaPhong());

        buttonBox.getChildren().addAll(btnLamMoi, btnThem, btnCapNhat, btnXoa);

        formCard.getChildren().addAll(lblFormTitle, gridPane, new Separator(), buttonBox);

        // =================================================================================
        // 3. KHU VỰC BẢNG DỮ LIỆU & TÌM KIẾM
        // =================================================================================
        VBox tableCard = new VBox(15);
        tableCard.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 12; -fx-padding: 20 25;");
        tableCard.setEffect(softShadow);
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        Label lblTableTitle = new Label("Danh sách Phòng");
        lblTableTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblTableTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField = new TextField();
        searchField.setPromptText("🔍 Tìm theo tên hoặc mã phòng...");
        searchField.setPrefWidth(350);
        searchField.setStyle("-fx-padding: 9 15; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: " + COLOR_BORDER + "; -fx-background-color: #f8fafc; -fx-font-size: 13px;");
        searchField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) searchField.setStyle(searchField.getStyle().replace(COLOR_BORDER, COLOR_PRIMARY));
            else searchField.setStyle(searchField.getStyle().replace(COLOR_PRIMARY, COLOR_BORDER));
        });

        toolbar.getChildren().addAll(lblTableTitle, spacer, searchField);

        table = new TableView<>();
        table.setStyle("-fx-font-size: 14px; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-base: white;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<PhongDTO, String> colMa = new TableColumn<>("Mã Phòng"); colMa.setCellValueFactory(new PropertyValueFactory<>("maPhong")); colMa.setMaxWidth(120);
        TableColumn<PhongDTO, String> colTen = new TableColumn<>("Tên Phòng"); colTen.setCellValueFactory(new PropertyValueFactory<>("tenPhong"));
        TableColumn<PhongDTO, String> colLoai = new TableColumn<>("Loại Phòng"); colLoai.setCellValueFactory(new PropertyValueFactory<>("maLoaiPhong")); colLoai.setMaxWidth(180);

        TableColumn<PhongDTO, Double> colGia = new TableColumn<>("Giá Phòng (VNĐ)");
        colGia.setCellValueFactory(new PropertyValueFactory<>("giaPhong"));
        colGia.setMaxWidth(220);
        colGia.setCellFactory(column -> new TableCell<PhongDTO, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else {
                    setText(String.format("%,.0f đ", item));
                    setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                    setTextFill(Color.web(COLOR_PRIMARY));
                }
            }
        });

        TableColumn<PhongDTO, String> colTinhTrang = new TableColumn<>("Tình Trạng");
        colTinhTrang.setCellValueFactory(new PropertyValueFactory<>("tinhTrang"));
        colTinhTrang.setMaxWidth(200);
        colTinhTrang.setCellFactory(column -> new TableCell<PhongDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                    if(item.equalsIgnoreCase("Trống")) setTextFill(Color.web(COLOR_SUCCESS));
                    else if(item.equalsIgnoreCase("Đã Đặt")) setTextFill(Color.web(COLOR_DANGER));
                    else if(item.equalsIgnoreCase("Đang ở")) setTextFill(Color.web(COLOR_WARNING));
                    else setTextFill(Color.web(COLOR_TEXT_MUTED));

                    setStyle("-fx-background-color: " + (item.equalsIgnoreCase("Trống") ? "#d1fae5" : item.equalsIgnoreCase("Đã Đặt") ? "#fee2e2" : item.equalsIgnoreCase("Đang ở") ? "#fef3c7" : "transparent") + ";");
                }
            }
        });

        table.getColumns().addAll(colMa, colTen, colLoai, colGia, colTinhTrang);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> { if (newVal != null) hienThiLenForm(newVal); });

        tableCard.getChildren().addAll(toolbar, table);

        // Gộp tất cả
        rootBox.getChildren().addAll(statsContainer, formCard, tableCard);

        loadPhongData();
        return rootBox;
    }

    // Hàm tạo Thẻ Thống Kê
    private VBox createStatCard(String title, String value, String bgStyle, DropShadow shadow) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(bgStyle + " -fx-background-radius: 12; -fx-padding: 20 25;");
        card.setEffect(shadow);
        HBox.setHgrow(card, Priority.ALWAYS); // Ép 3 thẻ dãn đều nhau

        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblTitle.setTextFill(Color.web("#ffffff", 0.9));

        Label lblValue = new Label(value);
        lblValue.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 32));
        lblValue.setTextFill(Color.WHITE);

        card.getChildren().addAll(lblTitle, lblValue);
        return card;
    }

    private VBox createInputBox(String labelText, Control inputControl) {
        VBox box = new VBox(6);
        Label lbl = new Label(labelText); lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12)); lbl.setTextFill(Color.web(COLOR_TEXT_MUTED));
        inputControl.setMaxWidth(Double.MAX_VALUE);
        box.getChildren().addAll(lbl, inputControl);
        return box;
    }

    private Button createButton(String text, String colorHex, String hoverHex) {
        Button btn = new Button(text); btn.setCursor(Cursor.HAND);
        String defaultStyle = "-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 9 20; -fx-background-radius: 6;";
        btn.setStyle(defaultStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(defaultStyle.replace(colorHex, hoverHex)));
        btn.setOnMouseExited(e -> btn.setStyle(defaultStyle));
        btn.setOnMousePressed(e -> btn.setTranslateY(2)); btn.setOnMouseReleased(e -> btn.setTranslateY(0));
        return btn;
    }

    private String removeAccents(String str) {
        if (str == null) return "";
        try {
            String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
            return Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(temp).replaceAll("").replace('đ','d').replace('Đ','D');
        } catch (Exception e) { return str; }
    }

    private void loadPhongData() {
        try {
            List<PhongDTO> list = phongService.getAllPhong();
            phongList = FXCollections.observableArrayList(list);

            // Tự động cập nhật số liệu lên các Thẻ Thống Kê
            long total = list.size();
            long trong = list.stream().filter(p -> "Trống".equalsIgnoreCase(p.getTinhTrang())).count();
            long dangPhucVu = list.stream().filter(p -> "Đang ở".equalsIgnoreCase(p.getTinhTrang()) || "Đã Đặt".equalsIgnoreCase(p.getTinhTrang())).count();

            lblTongPhongNum.setText(String.format("%02d", total));
            lblTrongNum.setText(String.format("%02d", trong));
            lblDangONum.setText(String.format("%02d", dangPhucVu));

            FilteredList<PhongDTO> filteredData = new FilteredList<>(phongList, b -> true);
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(p -> {
                    if (newValue == null || newValue.isEmpty()) return true;
                    String filter = removeAccents(newValue.toLowerCase().trim());
                    String ten = removeAccents(p.getTenPhong() != null ? p.getTenPhong().toLowerCase() : "");
                    String ma = removeAccents(p.getMaPhong() != null ? p.getMaPhong().toLowerCase() : "");
                    return ten.contains(filter) || ma.contains(filter);
                });
            });

            SortedList<PhongDTO> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(table.comparatorProperty());
            table.setItems(sortedData);

        } catch (Exception e) {
            showError("Lỗi tải dữ liệu phòng: " + e.getMessage());
        }
    }

    private void hienThiLenForm(PhongDTO p) {
        txtMaPhong.setText(p.getMaPhong());
        txtTenPhong.setText(p.getTenPhong());
        cbLoaiPhong.setValue(p.getMaLoaiPhong());
        txtGiaPhong.setText(String.valueOf(p.getGiaPhong()));
        cbTinhTrang.setValue(p.getTinhTrang());
    }

    private void lamMoiForm() {
        txtMaPhong.clear(); txtTenPhong.clear(); txtGiaPhong.clear(); searchField.clear();
        cbLoaiPhong.setValue(null); cbTinhTrang.setValue(null);
        table.getSelectionModel().clearSelection();
    }

    private boolean validateForm() {
        if (txtTenPhong.getText().trim().isEmpty() || txtGiaPhong.getText().trim().isEmpty()) {
            showError("Vui lòng nhập đầy đủ tên và giá phòng!"); return false;
        }
        try { Double.parseDouble(txtGiaPhong.getText().trim()); } catch (Exception e) { showError("Giá phòng phải là số hợp lệ!"); return false; }
        return true;
    }

    private PhongDTO getFormData() {
        PhongDTO dto = new PhongDTO();
        dto.setMaPhong(txtMaPhong.getText().trim());
        dto.setTenPhong(txtTenPhong.getText().trim());
        dto.setMaLoaiPhong(cbLoaiPhong.getValue());
        dto.setGiaPhong(Double.parseDouble(txtGiaPhong.getText().trim()));
        dto.setTinhTrang(cbTinhTrang.getValue());
        return dto;
    }

    private void themPhong() {
        if (!validateForm()) return;
        try {
            PhongDTO dto = getFormData();
            // Đặt mã phòng tự động nếu cần (dùng timestamp hoặc logic riêng)
            if (dto.getMaPhong() == null || dto.getMaPhong().isEmpty()) {
                dto.setMaPhong("P" + java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            }
            PhongDTO created = phongService.addPhong(dto);
            if (created != null) {
                showSuccess("Thêm phòng thành công!");
                loadPhongData();
                lamMoiForm();
            } else {
                showError("Thêm phòng thất bại (có thể trùng mã)");
            }
        } catch (Exception e) {
            showError("Lỗi khi thêm: " + e.getMessage());
        }
    }

    private void capNhatPhong() {
        if (txtMaPhong.getText().isEmpty()) {
            showError("Vui lòng chọn phòng cần cập nhật!");
            return;
        }
        if (!validateForm()) return;
        try {
            PhongDTO updated = phongService.updatePhong(getFormData());
            if (updated != null) {
                showSuccess("Cập nhật phòng thành công!");
                loadPhongData();
                lamMoiForm();
            } else {
                showError("Cập nhật thất bại (có thể không tìm thấy phòng)");
            }
        } catch (Exception e) {
            showError("Lỗi khi cập nhật: " + e.getMessage());
        }
    }

    private void xoaPhong() {
        if (txtMaPhong.getText().isEmpty()) {
            showError("Vui lòng chọn phòng cần xóa!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa phòng " + txtTenPhong.getText() + " không?");
        confirm.setHeaderText(null);
        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                if (phongService.deletePhong(txtMaPhong.getText())) {
                    showSuccess("Xóa phòng thành công!");
                    loadPhongData();
                    lamMoiForm();
                } else {
                    showError("Xóa phòng thất bại (có thể do ràng buộc dữ liệu)");
                }
            } catch (Exception e) {
                showError("Lỗi khi xóa: " + e.getMessage());
            }
        }
    }

    private void showError(String message) { Alert a = new Alert(Alert.AlertType.ERROR, message); a.setTitle("Lỗi Thao Tác"); a.setHeaderText("⚠️ Đã xảy ra lỗi"); a.show(); }
    private void showSuccess(String message) { Alert a = new Alert(Alert.AlertType.INFORMATION, message); a.setTitle("Thành Công"); a.setHeaderText("✅ Hoàn tất"); a.show(); }
}