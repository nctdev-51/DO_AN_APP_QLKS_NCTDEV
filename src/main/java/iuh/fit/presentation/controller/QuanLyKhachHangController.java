package iuh.fit.presentation.controller;

import iuh.fit.core.dto.KhachHangDTO;
import iuh.fit.core.service.IKhachHangService;
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

import java.time.LocalDate;
import java.util.List;

public class QuanLyKhachHangController {

    private TableView<KhachHangDTO> tableView;
    private TextField searchField; // Thêm thanh tìm kiếm
    private TextField maTextField, tenTextField, sdtTextField;
    private DatePicker datePickerNgaySinh;
    private ComboBox<String> loaiComboBox;

    private Button addButton, updateButton, deleteButton, clearButton;
    private IKhachHangService khachHangService;
    private ObservableList<KhachHangDTO> khachHangList;

    // --- BẢNG MÀU HIỆN ĐẠI ---
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_DANGER = "#ef4444";
    private final String COLOR_TEXT_MAIN = "#1e293b";
    private final String COLOR_BORDER = "#cbd5e1";

    public QuanLyKhachHangController(IKhachHangService khachHangService) {
        this.khachHangService = khachHangService;
    }

    public VBox createQuanLyKhachHangView() {
        VBox mainVBox = new VBox(20);
        mainVBox.setStyle("-fx-background-color: #f1f5f9; -fx-padding: 25;");

        // 1. Tiêu đề
        Label lblTitle = new Label("👥 QUẢN LÝ KHÁCH HÀNG");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 24));
        lblTitle.setTextFill(Color.web(COLOR_PRIMARY));

        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.color(0, 0, 0, 0.05));
        cardShadow.setRadius(10);
        cardShadow.setOffsetY(3);

        // 2. Khu vực Bảng (Table Card) & Thanh tìm kiếm
        VBox tableCard = new VBox(15);
        tableCard.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 20;");
        tableCard.setEffect(cardShadow);
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        // --- THANH CÔNG CỤ (Tiêu đề + Tìm kiếm) ---
        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Label lblTableTitle = new Label("Danh sách Khách Hàng");
        lblTableTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblTableTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // Đẩy thanh tìm kiếm sang góc phải

        // Khởi tạo thanh tìm kiếm
        searchField = new TextField();
        searchField.setPromptText("🔍 Tìm theo tên, SĐT hoặc Mã KH...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-padding: 8 15; -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: " + COLOR_BORDER + "; -fx-background-color: #f8fafc; -fx-font-size: 13px;");

        toolbar.getChildren().addAll(lblTableTitle, spacer, searchField);
        // ----------------------------------------

        tableView = new TableView<>();
        tableView.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 5;");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        TableColumn<KhachHangDTO, String> maCol = new TableColumn<>("Mã KH");
        maCol.setCellValueFactory(new PropertyValueFactory<>("maKhachHang"));
        maCol.setMaxWidth(1500);

        TableColumn<KhachHangDTO, String> tenCol = new TableColumn<>("Họ và Tên");
        tenCol.setCellValueFactory(new PropertyValueFactory<>("hoTen"));

        TableColumn<KhachHangDTO, String> sdtCol = new TableColumn<>("Số Điện Thoại");
        sdtCol.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));

        TableColumn<KhachHangDTO, LocalDate> ngayCol = new TableColumn<>("Ngày Sinh");
        ngayCol.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));

        TableColumn<KhachHangDTO, String> loaiCol = new TableColumn<>("Loại Khách Hàng");
        loaiCol.setCellValueFactory(new PropertyValueFactory<>("loaiKhachHang"));

        tableView.getColumns().addAll(maCol, tenCol, sdtCol, ngayCol, loaiCol);
        tableView.setOnMouseClicked(e -> handleTableRowClicked());

        tableCard.getChildren().addAll(toolbar, tableView);

        // 3. Khu vực Form
        VBox formCard = new VBox(15);
        formCard.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 20;");
        formCard.setEffect(cardShadow);

        Label lblFormTitle = new Label("📝 Thông tin chi tiết");
        lblFormTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblFormTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        String inputStyle = "-fx-padding: 10 12; -fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: " + COLOR_BORDER + "; -fx-background-color: #f8fafc; -fx-font-size: 13px;";

        maTextField = new TextField(); maTextField.setDisable(true); maTextField.setStyle(inputStyle);
        tenTextField = new TextField(); tenTextField.setPromptText("Nhập họ tên..."); tenTextField.setStyle(inputStyle);
        sdtTextField = new TextField(); sdtTextField.setPromptText("Nhập số điện thoại..."); sdtTextField.setStyle(inputStyle);
        datePickerNgaySinh = new DatePicker(); datePickerNgaySinh.setStyle(inputStyle); datePickerNgaySinh.setMaxWidth(Double.MAX_VALUE);

        loaiComboBox = new ComboBox<>();
        loaiComboBox.setItems(FXCollections.observableArrayList("KHACH_HOI_VIEN", "KHACH_THUONG_XUYEN", "KHACH_MOI", "KHACH_VANG_LAI"));
        loaiComboBox.setStyle(inputStyle); loaiComboBox.setMaxWidth(Double.MAX_VALUE);

        GridPane gridPane = new GridPane(); gridPane.setHgap(20); gridPane.setVgap(15);
        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        gridPane.getColumnConstraints().addAll(col1, col2);

        gridPane.add(createInputBox("Mã khách hàng:", maTextField), 0, 0);
        gridPane.add(createInputBox("Họ và tên:", tenTextField), 1, 0);
        gridPane.add(createInputBox("Số điện thoại:", sdtTextField), 0, 1);
        gridPane.add(createInputBox("Ngày sinh:", datePickerNgaySinh), 1, 1);
        gridPane.add(createInputBox("Loại khách hàng:", loaiComboBox), 0, 2);

        // 4. Nút bấm
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        clearButton = createButton("Làm Mới", "#64748b"); clearButton.setOnAction(e -> clearFields());
        addButton = createButton("✨ Thêm Mới", COLOR_SUCCESS); addButton.setOnAction(e -> handleThemKhachHang());
        updateButton = createButton("🔄 Cập Nhật", COLOR_PRIMARY); updateButton.setOnAction(e -> handleCapNhatKhachHang());
        deleteButton = createButton("🗑 Xóa", COLOR_DANGER); deleteButton.setOnAction(e -> handleXoaKhachHang());

        buttonBox.getChildren().addAll(clearButton, addButton, updateButton, deleteButton);
        formCard.getChildren().addAll(lblFormTitle, gridPane, new Separator(), buttonBox);

        mainVBox.getChildren().addAll(lblTitle, tableCard, formCard);

        // Load dữ liệu và nạp tính năng tìm kiếm
        loadKhachHangData();

        return mainVBox;
    }

    private VBox createInputBox(String labelText, Control inputControl) {
        VBox box = new VBox(5);
        Label lbl = new Label(labelText); lbl.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13)); lbl.setTextFill(Color.web(COLOR_TEXT_MAIN));
        box.getChildren().addAll(lbl, inputControl);
        return box;
    }

    private Button createButton(String text, String colorHex) {
        Button btn = new Button(text); btn.setCursor(Cursor.HAND);
        btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 5;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + colorHex + "dd; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 5;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 5;"));
        return btn;
    }

    private void loadKhachHangData() {
        try {
            // Lấy danh sách từ CSDL
            List<KhachHangDTO> list = khachHangService.getAllKhachHang();
            khachHangList = FXCollections.observableArrayList(list);

            // --- TÍNH NĂNG LỌC/TÌM KIẾM THÔNG MINH ---
            FilteredList<KhachHangDTO> filteredData = new FilteredList<>(khachHangList, b -> true);

            // Lắng nghe sự thay đổi của thanh tìm kiếm
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(khachHang -> {
                    // Nếu ô tìm kiếm trống, hiển thị tất cả
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    String lowerCaseFilter = newValue.toLowerCase();

                    // Tìm theo Tên
                    if (khachHang.getHoTen() != null && khachHang.getHoTen().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    // Tìm theo Số điện thoại
                    if (khachHang.getSoDienThoai() != null && khachHang.getSoDienThoai().contains(lowerCaseFilter)) {
                        return true;
                    }
                    // Tìm theo Mã KH
                    if (khachHang.getMaKhachHang() != null && khachHang.getMaKhachHang().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }

                    return false; // Không khớp tiêu chí nào thì ẩn đi
                });
            });

            // Bọc FilteredList vào SortedList để kết nối với TableView
            SortedList<KhachHangDTO> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tableView.comparatorProperty());

            // Gán dữ liệu đã được bọc bộ lọc vào Bảng
            tableView.setItems(sortedData);
            // ----------------------------------------

        } catch (Exception e) {
            showError("Lỗi tải dữ liệu khách hàng: " + e.getMessage());
        }
    }

    private void handleTableRowClicked() {
        KhachHangDTO selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            maTextField.setText(selected.getMaKhachHang());
            tenTextField.setText(selected.getHoTen());
            sdtTextField.setText(selected.getSoDienThoai());
            datePickerNgaySinh.setValue(selected.getNgaySinh());
            loaiComboBox.setValue(selected.getLoaiKhachHang());
        }
    }

    private void handleThemKhachHang() {
        try {
            KhachHangDTO dto = new KhachHangDTO();
            dto.setHoTen(tenTextField.getText()); dto.setSoDienThoai(sdtTextField.getText());
            dto.setNgaySinh(datePickerNgaySinh.getValue()); dto.setLoaiKhachHang(loaiComboBox.getValue());
            if (khachHangService.addKhachHang(dto) != null) {
                loadKhachHangData(); // Tải lại bảng để cập nhật cả thanh tìm kiếm
                clearFields();
                showSuccess("Thêm thành công!");
            }
        } catch (Exception e) { showError("Không thể thêm: " + e.getMessage()); }
    }

    private void handleCapNhatKhachHang() {
        try {
            if (maTextField.getText().isEmpty()) {
                showError("Vui lòng chọn khách hàng cần cập nhật."); return;
            }
            KhachHangDTO dto = new KhachHangDTO();
            dto.setMaKhachHang(maTextField.getText()); dto.setHoTen(tenTextField.getText());
            dto.setSoDienThoai(sdtTextField.getText()); dto.setNgaySinh(datePickerNgaySinh.getValue()); dto.setLoaiKhachHang(loaiComboBox.getValue());
            if (khachHangService.updateKhachHang(dto) != null) {
                loadKhachHangData();
                clearFields();
                showSuccess("Cập nhật thành công!");
            }
        } catch (Exception e) { showError("Không thể cập nhật: " + e.getMessage()); }
    }

    private void handleXoaKhachHang() {
        try {
            if (maTextField.getText().isEmpty()) {
                showError("Vui lòng chọn khách hàng cần xóa."); return;
            }
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc muốn xóa khách hàng này không?");
            confirmAlert.setHeaderText(null);
            if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                if (khachHangService.deleteKhachHang(maTextField.getText())) {
                    loadKhachHangData();
                    clearFields();
                    showSuccess("Xóa thành công.");
                }
            }
        } catch (Exception e) { showError("Không thể xóa: " + e.getMessage()); }
    }

    private void clearFields() {
        maTextField.clear(); tenTextField.clear(); sdtTextField.clear(); searchField.clear(); // Xóa luôn cả thanh tìm kiếm
        datePickerNgaySinh.setValue(null); loaiComboBox.setValue(null); tableView.getSelectionModel().clearSelection();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message); alert.setHeaderText("⚠️ Đã xảy ra lỗi"); alert.show();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message); alert.setHeaderText("✅ Thành công"); alert.show();
    }
}