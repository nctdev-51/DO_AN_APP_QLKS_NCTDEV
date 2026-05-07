package iuh.fit.presentation.controller;

import iuh.fit.core.dto.DichVuDTO;
import iuh.fit.core.service.IChiTietHoaDonService;
import iuh.fit.core.service.IDichVuService;
import iuh.fit.core.service.IPhieuDatPhongService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Optional;

public class QuanLyDichVuController {

    private IDichVuService dichVuService;
    private ObservableList<DichVuDTO> dichVuList;
    private TableView<DichVuDTO> table;

    // UI Components
    private TextField txtMaDV, txtTenDV, txtGiaTien, txtTimKiem;
    private TextArea txtMoTa;

    // Bảng màu chuẩn hiện đại
    private final String COLOR_BG = "#f1f5f9";
    private final String COLOR_CARD_BG = "#ffffff";
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_WARNING = "#f59e0b";
    private final String COLOR_DANGER = "#ef4444";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_BORDER = "#e2e8f0";

    public QuanLyDichVuController(IDichVuService dichVuService, IPhieuDatPhongService phieuDatPhongService, IChiTietHoaDonService chiTietHoaDonService) {
        this.dichVuService = dichVuService;
    }

    public VBox createView() {
        VBox rootPane = new VBox(20);
        rootPane.setStyle("-fx-background-color: " + COLOR_BG + "; -fx-padding: 30;");

        // --- 1. HEADER ---
        VBox headerBox = new VBox(5);
        Label lblTitle = new Label("QUẢN LÝ DỊCH VỤ");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 28));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));
        Label lblSubTitle = new Label("Thêm, sửa, xóa và theo dõi danh mục dịch vụ cung cấp cho khách hàng");
        lblSubTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        lblSubTitle.setTextFill(Color.web(COLOR_TEXT_MUTED));
        headerBox.getChildren().addAll(lblTitle, lblSubTitle);

        // --- 2. MAIN LAYOUT (Split màn hình: Trái = Form, Phải = Table) ---
        HBox mainContent = new HBox(25);
        VBox.setVgrow(mainContent, Priority.ALWAYS); // Chiếm toàn bộ chiều cao còn lại

        // Cột Trái: Form nhập liệu
        VBox leftColumn = createFormColumn();

        // Cột Phải: Bảng dữ liệu
        VBox rightColumn = createTableColumn();
        HBox.setHgrow(rightColumn, Priority.ALWAYS); // Bảng tự động dãn ra chiếm hết chỗ trống

        mainContent.getChildren().addAll(leftColumn, rightColumn);
        rootPane.getChildren().addAll(headerBox, mainContent);

        // Load dữ liệu
        loadTableData();

        return rootPane;
    }

    // ==========================================================
    // TẠO CỘT TRÁI (FORM NHẬP LIỆU)
    // ==========================================================
    private VBox createFormColumn() {
        VBox card = new VBox(20);
        card.setPrefWidth(380); // Cố định độ rộng cột form
        card.setMinWidth(380);
        card.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-padding: 25; -fx-background-radius: 12; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 12;");

        DropShadow shadow = new DropShadow(); shadow.setColor(Color.web("#000000", 0.04)); shadow.setRadius(10); shadow.setOffsetY(5);
        card.setEffect(shadow);

        // Tiêu đề form
        Label lblTitle = new Label("📝 Thông tin Dịch Vụ");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblTitle.setTextFill(Color.web(COLOR_PRIMARY));

        // Các ô nhập liệu
        String inputStyle = "-fx-padding: 10 12; -fx-background-radius: 6; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 6; -fx-font-size: 14px; -fx-background-color: #f8fafc;";

        txtMaDV = new TextField(); txtMaDV.setStyle(inputStyle); txtMaDV.setPromptText("VD: DV001");
        txtTenDV = new TextField(); txtTenDV.setStyle(inputStyle); txtTenDV.setPromptText("Tên dịch vụ");
        txtGiaTien = new TextField(); txtGiaTien.setStyle(inputStyle); txtGiaTien.setPromptText("Nhập giá tiền (VNĐ)...");

        txtMoTa = new TextArea();
        txtMoTa.setStyle(inputStyle);
        txtMoTa.setPromptText("Mô tả chi tiết về dịch vụ...");
        txtMoTa.setPrefRowCount(4); // Rộng hơn một chút
        txtMoTa.setWrapText(true);

        VBox boxMa = createInputGroup("Mã Dịch Vụ:", txtMaDV);
        VBox boxTen = createInputGroup("Tên Dịch Vụ:", txtTenDV);
        VBox boxGia = createInputGroup("Giá Tiền (VNĐ):", txtGiaTien);
        VBox boxMoTa = createInputGroup("Mô Tả:", txtMoTa);

        // Nút chức năng (Xếp thành Grid 2x2 cho đẹp)
        GridPane btnGrid = new GridPane();
        btnGrid.setHgap(10);
        btnGrid.setVgap(10);
        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        btnGrid.getColumnConstraints().addAll(col1, col2);

        Button btnThem = createActionButton("➕ Thêm Mới", COLOR_SUCCESS);
        Button btnSua = createActionButton("✏️ Cập Nhật", COLOR_WARNING);
        Button btnXoa = createActionButton("🗑️ Xóa", COLOR_DANGER);
        Button btnLamMoi = createActionButton("🔄 Làm Mới", "#94a3b8");

        btnThem.setOnAction(e -> handleAdd());
        btnSua.setOnAction(e -> handleUpdate());
        btnXoa.setOnAction(e -> handleDelete());
        btnLamMoi.setOnAction(e -> clearForm());

        btnGrid.add(btnThem, 0, 0);
        btnGrid.add(btnSua, 1, 0);
        btnGrid.add(btnXoa, 0, 1);
        btnGrid.add(btnLamMoi, 1, 1);

        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(lblTitle, boxMa, boxTen, boxGia, boxMoTa, spacer, new Separator(), btnGrid);
        return card;
    }

    // ==========================================================
    // TẠO CỘT PHẢI (BẢNG DỮ LIỆU)
    // ==========================================================
    private VBox createTableColumn() {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-padding: 25; -fx-background-radius: 12; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 12;");
        DropShadow shadow = new DropShadow(); shadow.setColor(Color.web("#000000", 0.04)); shadow.setRadius(10); shadow.setOffsetY(5);
        card.setEffect(shadow);

        // Thanh công cụ bảng (Tìm kiếm)
        HBox toolBox = new HBox(15);
        toolBox.setAlignment(Pos.CENTER_LEFT);

        Label lblSearch = new Label("🔍 Tìm kiếm dịch vụ:");
        lblSearch.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        lblSearch.setTextFill(Color.web(COLOR_TEXT_MAIN));

        txtTimKiem = new TextField();
        txtTimKiem.setPromptText("Nhập mã hoặc tên dịch vụ để lọc...");
        txtTimKiem.setPrefWidth(350);
        txtTimKiem.setStyle("-fx-padding: 10 15; -fx-background-radius: 20; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 20; -fx-font-size: 13px; -fx-background-color: #f8fafc;");
        txtTimKiem.textProperty().addListener((obs, oldV, newV) -> handleSearch(newV));

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        toolBox.getChildren().addAll(lblSearch, txtTimKiem, spacer);

        // Khởi tạo bảng
        table = new TableView<>();
        table.setStyle("-fx-font-size: 14px; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS); // Bảng tự dãn chiều dọc

        // Placeholder khi bảng trống
        Label placeholder = new Label("📭 Không có dữ liệu dịch vụ");
        placeholder.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        placeholder.setTextFill(Color.web(COLOR_TEXT_MUTED));
        table.setPlaceholder(placeholder);

        TableColumn<DichVuDTO, String> colMa = new TableColumn<>("Mã Dịch Vụ");
        colMa.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getMaDichVu()));
        colMa.setMaxWidth(120); colMa.setMinWidth(120);

        TableColumn<DichVuDTO, String> colTen = new TableColumn<>("Tên Dịch Vụ");
        colTen.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getTenDichVu()));

        TableColumn<DichVuDTO, Double> colGia = new TableColumn<>("Giá Tiền");
        colGia.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getGiaTien()));
        colGia.setMaxWidth(150); colGia.setMinWidth(150);
        colGia.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) setText(null);
                else {
                    setText(String.format("%,.0f đ", price));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: " + COLOR_PRIMARY + "; -fx-alignment: center-right; -fx-padding: 0 15 0 0;");
                }
            }
        });

        TableColumn<DichVuDTO, String> colMoTa = new TableColumn<>("Mô Tả");
        colMoTa.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getMoTa()));

        table.getColumns().addAll(colMa, colTen, colGia, colMoTa);

        // Sự kiện click chọn dòng trong bảng
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtMaDV.setText(newSel.getMaDichVu());
                txtMaDV.setDisable(true); // Khóa sửa mã
                txtTenDV.setText(newSel.getTenDichVu());
                txtGiaTien.setText(String.format("%.0f", newSel.getGiaTien()));
                txtMoTa.setText(newSel.getMoTa());
            }
        });

        card.getChildren().addAll(toolBox, table);
        return card;
    }

    // ==========================================================
    // XỬ LÝ LOGIC NGHIỆP VỤ
    // ==========================================================

    private void loadTableData() {
        try {
            List<DichVuDTO> list = dichVuService.getAllDichVu();
            dichVuList = FXCollections.observableArrayList(list);
            table.setItems(dichVuList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi kết nối", "Không thể tải dữ liệu dịch vụ!");
        }
    }

    private void clearForm() {
        txtMaDV.clear();
        txtMaDV.setDisable(false);
        txtTenDV.clear();
        txtGiaTien.clear();
        txtMoTa.clear();
        table.getSelectionModel().clearSelection();
    }

    private boolean validateInput() {
        if (txtMaDV.getText().trim().isEmpty() || txtTenDV.getText().trim().isEmpty() || txtGiaTien.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ Mã, Tên và Giá tiền!");
            return false;
        }
        if (!txtMaDV.getText().trim().matches("^DV\\d+$")) {
            showAlert(Alert.AlertType.WARNING, "Sai định dạng", "Mã dịch vụ phải có dạng DVxxx (VD: DV001)");
            return false;
        }
        try {
            double gia = Double.parseDouble(txtGiaTien.getText().trim());
            if (gia <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Sai định dạng", "Giá tiền phải là số hợp lệ lớn hơn 0!");
            return false;
        }
        return true;
    }

    private DichVuDTO getDtoFromForm() {
        DichVuDTO dto = new DichVuDTO();
        dto.setMaDichVu(txtMaDV.getText().trim());
        dto.setTenDichVu(txtTenDV.getText().trim());
        dto.setGiaTien(Double.parseDouble(txtGiaTien.getText().trim()));
        dto.setMoTa(txtMoTa.getText().trim());
        return dto;
    }

    private void handleAdd() {
        if (!validateInput()) return;
        try {
            DichVuDTO dto = getDtoFromForm();
            DichVuDTO saved = dichVuService.addDichVu(dto);
            if (saved != null) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm dịch vụ mới thành công!");
                loadTableData();
                clearForm();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Thêm thất bại. Có thể mã dịch vụ đã tồn tại!\nChi tiết: " + e.getMessage());
        }
    }

    private void handleUpdate() {
        if (table.getSelectionModel().getSelectedItem() == null) {
            showAlert(Alert.AlertType.WARNING, "Chú ý", "Vui lòng chọn một dịch vụ trong bảng bên phải để cập nhật!");
            return;
        }
        if (!validateInput()) return;
        try {
            DichVuDTO dto = getDtoFromForm();
            DichVuDTO updated = dichVuService.updateDichVu(dto);
            if (updated != null) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật dịch vụ thành công!");
                loadTableData();
                clearForm();
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Cập nhật thất bại!\nChi tiết: " + e.getMessage());
        }
    }

    private void handleDelete() {
        DichVuDTO selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Chú ý", "Vui lòng chọn một dịch vụ để xóa!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa dịch vụ:\n👉 " + selected.getTenDichVu() + " (" + selected.getMaDichVu() + ") ?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText(null);
        Optional<ButtonType> res = confirm.showAndWait();

        if (res.isPresent() && res.get() == ButtonType.YES) {
            try {
                boolean success = dichVuService.deleteDichVu(selected.getMaDichVu());
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa dịch vụ thành công!");
                    loadTableData();
                    clearForm();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa dịch vụ này!");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Database", "Dịch vụ này đang được sử dụng trong hệ thống, không thể xóa!");
            }
        }
    }

    private void handleSearch(String keyword) {
        if (dichVuList == null) return;
        String kw = keyword.toLowerCase();
        ObservableList<DichVuDTO> filtered = dichVuList.filtered(dv ->
                dv.getMaDichVu().toLowerCase().contains(kw) ||
                        dv.getTenDichVu().toLowerCase().contains(kw)
        );
        table.setItems(filtered);
    }

    // ==========================================================
    // UI HELPER
    // ==========================================================

    private VBox createInputGroup(String labelText, Control inputControl) {
        VBox box = new VBox(5);
        Label lbl = new Label(labelText);
        lbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        lbl.setTextFill(Color.web(COLOR_TEXT_MUTED));
        box.getChildren().addAll(lbl, inputControl);
        return box;
    }

    private Button createActionButton(String text, String colorHex) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE); // Cho phép nút dãn ngang hết cỡ trong Grid
        btn.setPrefHeight(40);
        btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 6;");
        btn.setCursor(Cursor.HAND);
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + colorHex + "DD; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 6;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 6;"));
        return btn;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}