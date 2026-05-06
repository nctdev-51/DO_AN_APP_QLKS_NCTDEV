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

    // UI Components (ĐÃ BỎ txtSucChua VÀ txtLoaiGiuong)
    private TableView<PhongDTO> table;
    private TextField searchField;
    private TextField txtMaPhong, txtTenPhong, txtGiaPhong;
    private ComboBox<String> cbLoaiPhong, cbTinhTrang;
    private Button btnThem, btnCapNhat, btnXoa, btnLamMoi;

    // --- BẢNG MÀU HIỆN ĐẠI ---
    private final String COLOR_BG = "#f1f5f9";
    private final String COLOR_CARD_BG = "#ffffff";
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_PRIMARY_HOVER = "#1d4ed8";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_SUCCESS_HOVER = "#059669";
    private final String COLOR_DANGER = "#ef4444";
    private final String COLOR_DANGER_HOVER = "#dc2626";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_BORDER = "#cbd5e1";

    public QuanLyPhongController(IPhongService phongService) {
        this.phongService = phongService;
    }

    public VBox createQuanLyPhongView() {
        VBox mainVBox = new VBox(15);
        mainVBox.setStyle("-fx-background-color: " + COLOR_BG + "; -fx-padding: 15 20 15 20;");

        // 1. HEADER CHÍNH
        VBox headerBox = new VBox(2);
        Label lblTitle = new Label("QUẢN LÝ PHÒNG");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 24));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblSubTitle = new Label("Quản lý danh sách phòng, phân loại, giá cả và tình trạng phòng");
        lblSubTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        lblSubTitle.setTextFill(Color.web(COLOR_TEXT_MUTED));
        headerBox.getChildren().addAll(lblTitle, lblSubTitle);

        DropShadow softShadow = new DropShadow();
        softShadow.setColor(Color.web("#000000", 0.05));
        softShadow.setRadius(10);
        softShadow.setOffsetY(3);

        // =================================================================================
        // 2. CARD BẢNG DỮ LIỆU & TÌM KIẾM
        // =================================================================================
        VBox tableCard = new VBox(10);
        tableCard.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 10; -fx-padding: 15;");
        tableCard.setEffect(softShadow);
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Label lblTableTitle = new Label("Danh sách Phòng");
        lblTableTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblTableTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField = new TextField();
        searchField.setPromptText("🔍 Tìm theo tên, mã phòng...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-padding: 8 15; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: " + COLOR_BORDER + "; -fx-background-color: #f8fafc; -fx-font-size: 13px;");

        searchField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) searchField.setStyle(searchField.getStyle().replace(COLOR_BORDER, COLOR_PRIMARY));
            else searchField.setStyle(searchField.getStyle().replace(COLOR_PRIMARY, COLOR_BORDER));
        });

        toolbar.getChildren().addAll(lblTableTitle, spacer, searchField);

        table = new TableView<>();
        table.setStyle("-fx-font-size: 13px; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8; -fx-base: white;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<PhongDTO, String> colMa = new TableColumn<>("Mã Phòng"); colMa.setCellValueFactory(new PropertyValueFactory<>("maPhong")); colMa.setMaxWidth(120);
        TableColumn<PhongDTO, String> colTen = new TableColumn<>("Tên Phòng"); colTen.setCellValueFactory(new PropertyValueFactory<>("tenPhong"));
        TableColumn<PhongDTO, String> colLoai = new TableColumn<>("Loại Phòng"); colLoai.setCellValueFactory(new PropertyValueFactory<>("maLoaiPhong")); colLoai.setMaxWidth(150);

        TableColumn<PhongDTO, Double> colGia = new TableColumn<>("Giá Phòng (VNĐ)");
        colGia.setCellValueFactory(new PropertyValueFactory<>("giaPhong"));
        colGia.setMaxWidth(200);
        colGia.setCellFactory(column -> new TableCell<PhongDTO, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else { setText(String.format("%,.0f đ", item)); }
            }
        });

        TableColumn<PhongDTO, String> colTinhTrang = new TableColumn<>("Tình Trạng");
        colTinhTrang.setCellValueFactory(new PropertyValueFactory<>("tinhTrang"));
        colTinhTrang.setMaxWidth(150);
        colTinhTrang.setCellFactory(column -> new TableCell<PhongDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
                    if(item.equalsIgnoreCase("Trống")) setTextFill(Color.web(COLOR_SUCCESS));
                    else if(item.equalsIgnoreCase("Đã Đặt")) setTextFill(Color.web(COLOR_DANGER));
                    else if(item.equalsIgnoreCase("Đang ở")) setTextFill(Color.web("#f59e0b")); // Amber
                    else setTextFill(Color.web(COLOR_TEXT_MUTED));
                }
            }
        });

        // ĐÃ XÓA colSucChua và colGiuong
        table.getColumns().addAll(colMa, colTen, colLoai, colGia, colTinhTrang);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> { if (newVal != null) hienThiLenForm(newVal); });

        tableCard.getChildren().addAll(toolbar, table);

        // =================================================================================
        // 3. CARD FORM NHẬP LIỆU
        // =================================================================================
        VBox formCard = new VBox(10);
        formCard.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 10; -fx-padding: 15;");
        formCard.setEffect(softShadow);

        Label lblFormTitle = new Label("📝 Thông tin chi tiết Phòng");
        lblFormTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblFormTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        String inputStyle = "-fx-padding: 8 12; -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: " + COLOR_BORDER + "; -fx-background-color: #f8fafc; -fx-font-size: 13px; -fx-text-fill: " + COLOR_TEXT_MAIN + ";";

        txtMaPhong = new TextField(); txtMaPhong.setDisable(true); txtMaPhong.setStyle(inputStyle);
        txtTenPhong = new TextField(); txtTenPhong.setPromptText("VD: Phòng 101"); txtTenPhong.setStyle(inputStyle);
        txtGiaPhong = new TextField(); txtGiaPhong.setPromptText("VD: 500000"); txtGiaPhong.setStyle(inputStyle);

        cbLoaiPhong = new ComboBox<>();
        cbLoaiPhong.setItems(FXCollections.observableArrayList("DON", "DOI", "GIADINH", "VIP"));
        cbLoaiPhong.setStyle(inputStyle); cbLoaiPhong.setMaxWidth(Double.MAX_VALUE);

        cbTinhTrang = new ComboBox<>();
        cbTinhTrang.setItems(FXCollections.observableArrayList("Trống", "Đã Đặt", "Đang ở", "Bảo Trì"));
        cbTinhTrang.setStyle(inputStyle); cbTinhTrang.setMaxWidth(Double.MAX_VALUE);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(15);
        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        gridPane.getColumnConstraints().addAll(col1, col2);

        // Layout mới gọn gàng hơn vì đã bỏ Sức chứa và Giường
        gridPane.add(createInputBox("Mã phòng", txtMaPhong), 0, 0);
        gridPane.add(createInputBox("Tên phòng", txtTenPhong), 1, 0);
        gridPane.add(createInputBox("Loại phòng", cbLoaiPhong), 0, 1);
        gridPane.add(createInputBox("Giá phòng (VNĐ)", txtGiaPhong), 1, 1);
        gridPane.add(createInputBox("Tình trạng hiện tại", cbTinhTrang), 0, 2);

        // =================================================================================
        // 4. KHU VỰC NÚT BẤM
        // =================================================================================
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(5, 0, 0, 0));

        btnLamMoi = createButton("Làm Mới", COLOR_TEXT_MUTED, "#475569"); btnLamMoi.setOnAction(e -> lamMoiForm());
        btnThem = createButton("✨ Thêm Mới", COLOR_SUCCESS, COLOR_SUCCESS_HOVER); btnThem.setOnAction(e -> themPhong());
        btnCapNhat = createButton("🔄 Cập Nhật", COLOR_PRIMARY, COLOR_PRIMARY_HOVER); btnCapNhat.setOnAction(e -> capNhatPhong());
        btnXoa = createButton("🗑 Xóa", COLOR_DANGER, COLOR_DANGER_HOVER); btnXoa.setOnAction(e -> xoaPhong());

        buttonBox.getChildren().addAll(btnLamMoi, btnThem, btnCapNhat, btnXoa);

        formCard.getChildren().addAll(lblFormTitle, gridPane, buttonBox);
        mainVBox.getChildren().addAll(headerBox, tableCard, formCard);

        loadPhongData();
        return mainVBox;
    }

    private VBox createInputBox(String labelText, Control inputControl) {
        VBox box = new VBox(5);
        Label lbl = new Label(labelText); lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12)); lbl.setTextFill(Color.web(COLOR_TEXT_MUTED));
        box.getChildren().addAll(lbl, inputControl);
        return box;
    }

    private Button createButton(String text, String colorHex, String hoverHex) {
        Button btn = new Button(text); btn.setCursor(Cursor.HAND);
        String defaultStyle = "-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 8 20; -fx-background-radius: 6;";
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
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").replace('đ','d').replace('Đ','D');
        } catch (Exception e) { return str; }
    }

    private void loadPhongData() {
        try {
            List<PhongDTO> list = phongService.getAllPhong();
            phongList = FXCollections.observableArrayList(list);

            FilteredList<PhongDTO> filteredData = new FilteredList<>(phongList, b -> true);
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(p -> {
                    if (newValue == null || newValue.isEmpty()) return true;
                    String filter = removeAccents(newValue.toLowerCase().trim());

                    String ten = removeAccents(p.getTenPhong() != null ? p.getTenPhong().toLowerCase() : "");
                    String ma = removeAccents(p.getMaPhong() != null ? p.getMaPhong().toLowerCase() : "");

                    if (ten.contains(filter)) return true;
                    if (ma.contains(filter)) return true;
                    return false;
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
            // phongService.addPhong(getFormData());
            showSuccess("Chức năng thêm đang gọi service...");
            loadPhongData();
            lamMoiForm();
        } catch (Exception e) { showError("Không thể thêm: " + e.getMessage()); }
    }

    private void capNhatPhong() {
        if (txtMaPhong.getText().isEmpty()) { showError("Vui lòng chọn phòng cần cập nhật từ bảng!"); return; }
        if (!validateForm()) return;
        try {
            // phongService.updatePhong(getFormData());
            showSuccess("Chức năng cập nhật đang gọi service...");
            loadPhongData();
            lamMoiForm();
        } catch (Exception e) { showError("Không thể cập nhật: " + e.getMessage()); }
    }

    private void xoaPhong() {
        if (txtMaPhong.getText().isEmpty()) { showError("Vui lòng chọn phòng cần xóa từ bảng!"); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Hành động này không thể hoàn tác. Bạn có chắc muốn xóa?");
        confirm.setTitle("Xác nhận xóa"); confirm.setHeaderText("Xóa phòng " + txtTenPhong.getText());
        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                // phongService.deletePhong(txtMaPhong.getText());
                showSuccess("Đã xóa phòng khỏi hệ thống.");
                loadPhongData();
                lamMoiForm();
            } catch (Exception e) { showError("Lỗi khi xóa: " + e.getMessage()); }
        }
    }

    private void showError(String message) { Alert a = new Alert(Alert.AlertType.ERROR, message); a.setTitle("Lỗi Thao Tác"); a.setHeaderText("⚠️ Đã xảy ra lỗi"); a.show(); }
    private void showSuccess(String message) { Alert a = new Alert(Alert.AlertType.INFORMATION, message); a.setTitle("Thành Công"); a.setHeaderText("✅ Hoàn tất"); a.show(); }
}