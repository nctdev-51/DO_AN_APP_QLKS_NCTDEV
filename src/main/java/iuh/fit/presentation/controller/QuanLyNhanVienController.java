package iuh.fit.presentation.controller;

import iuh.fit.core.dto.NhanVienDTO;
import iuh.fit.core.service.INhanVienService;
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
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class QuanLyNhanVienController {
    private static final Logger logger = Logger.getLogger(QuanLyNhanVienController.class.getName());

    private INhanVienService nhanVienService;
    private ObservableList<NhanVienDTO> nhanVienList;

    // UI Components
    private TableView<NhanVienDTO> table;
    private TextField searchField;
    private TextField txtMa, txtHoTen, txtSDT, txtCCCD, txtQueQuan;
    private DatePicker dpNgaySinh, dpNgayVaoLam;
    private ComboBox<String> cbLoaiNhanVien;
    private CheckBox chkTrangThai;
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

    public QuanLyNhanVienController(INhanVienService nhanVienService) {
        this.nhanVienService = nhanVienService;
    }

    public VBox createQuanLyNhanVienView() {
        VBox mainVBox = new VBox(15);
        mainVBox.setStyle("-fx-background-color: " + COLOR_BG + "; -fx-padding: 15 20 15 20;");

        // 1. HEADER CHÍNH
        VBox headerBox = new VBox(2);
        Label lblTitle = new Label("QUẢN LÝ NHÂN VIÊN");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 24));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Label lblSubTitle = new Label("Quản lý hồ sơ, chức vụ và trạng thái làm việc của nhân sự");
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

        Label lblTableTitle = new Label("Danh sách Nhân Sự");
        lblTableTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblTableTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField = new TextField();
        searchField.setPromptText("🔍 Tìm theo tên, CCCD hoặc SĐT...");
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

        TableColumn<NhanVienDTO, String> colMa = new TableColumn<>("Mã NV"); colMa.setCellValueFactory(new PropertyValueFactory<>("maNhanVien")); colMa.setMaxWidth(80);
        TableColumn<NhanVienDTO, String> colTen = new TableColumn<>("Họ và Tên"); colTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        TableColumn<NhanVienDTO, String> colSDT = new TableColumn<>("SĐT"); colSDT.setCellValueFactory(new PropertyValueFactory<>("soDienThoai")); colSDT.setMaxWidth(120);
        TableColumn<NhanVienDTO, String> colCCCD = new TableColumn<>("CCCD"); colCCCD.setCellValueFactory(new PropertyValueFactory<>("cccd")); colCCCD.setMaxWidth(130);
        TableColumn<NhanVienDTO, String> colLoai = new TableColumn<>("Chức Vụ"); colLoai.setCellValueFactory(new PropertyValueFactory<>("loaiNhanVien"));

        TableColumn<NhanVienDTO, Boolean> colTrangThai = new TableColumn<>("Trạng Thái");
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTrangThai.setMaxWidth(120);
        colTrangThai.setCellFactory(column -> new TableCell<NhanVienDTO, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item ? "✅ Đang làm" : "❌ Đã nghỉ");
                    setTextFill(item ? Color.web(COLOR_SUCCESS) : Color.web(COLOR_DANGER));
                    setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
                }
            }
        });

        table.getColumns().addAll(colMa, colTen, colSDT, colCCCD, colLoai, colTrangThai);
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> { if (newVal != null) hienThiLenForm(newVal); });

        tableCard.getChildren().addAll(toolbar, table);

        // =================================================================================
        // 3. CARD FORM NHẬP LIỆU
        // =================================================================================
        VBox formCard = new VBox(10);
        formCard.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 10; -fx-padding: 15;");
        formCard.setEffect(softShadow);

        Label lblFormTitle = new Label("📝 Hồ sơ Nhân sự");
        lblFormTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblFormTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        String inputStyle = "-fx-padding: 8 12; -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: " + COLOR_BORDER + "; -fx-background-color: #f8fafc; -fx-font-size: 13px; -fx-text-fill: " + COLOR_TEXT_MAIN + ";";

        txtMa = new TextField(); txtMa.setDisable(true); txtMa.setStyle(inputStyle);
        txtHoTen = new TextField(); txtHoTen.setPromptText("Ví dụ: Nguyễn Văn A"); txtHoTen.setStyle(inputStyle);
        txtSDT = new TextField(); txtSDT.setPromptText("Ví dụ: 0901234567"); txtSDT.setStyle(inputStyle);
        txtCCCD = new TextField(); txtCCCD.setPromptText("Ví dụ: 079200012345"); txtCCCD.setStyle(inputStyle);
        txtQueQuan = new TextField(); txtQueQuan.setPromptText("Ví dụ: TP.HCM"); txtQueQuan.setStyle(inputStyle);

        dpNgaySinh = new DatePicker(); dpNgaySinh.setStyle(inputStyle); dpNgaySinh.setMaxWidth(Double.MAX_VALUE);
        dpNgayVaoLam = new DatePicker(); dpNgayVaoLam.setStyle(inputStyle); dpNgayVaoLam.setMaxWidth(Double.MAX_VALUE);

        cbLoaiNhanVien = new ComboBox<>();
        cbLoaiNhanVien.setItems(FXCollections.observableArrayList("NHAN_VIEN_LE_TAN", "NHAN_VIEN_PHONG", "NHAN_VIEN_SAN_KHAU", "NHAN_VIEN_QUAN_LY", "GIAM_DOC"));
        cbLoaiNhanVien.setStyle(inputStyle); cbLoaiNhanVien.setMaxWidth(Double.MAX_VALUE);

        chkTrangThai = new CheckBox("Đang làm việc");
        chkTrangThai.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        chkTrangThai.setTextFill(Color.web(COLOR_SUCCESS));
        chkTrangThai.setCursor(Cursor.HAND);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(10);
        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(50);
        gridPane.getColumnConstraints().addAll(col1, col2);

        gridPane.add(createInputBox("Mã nhân viên", txtMa), 0, 0);
        gridPane.add(createInputBox("Họ và tên", txtHoTen), 1, 0);
        gridPane.add(createInputBox("Căn cước công dân", txtCCCD), 0, 1);
        gridPane.add(createInputBox("Ngày sinh", dpNgaySinh), 1, 1);
        gridPane.add(createInputBox("Số điện thoại", txtSDT), 0, 2);
        gridPane.add(createInputBox("Quê quán", txtQueQuan), 1, 2);
        gridPane.add(createInputBox("Chức vụ", cbLoaiNhanVien), 0, 3);

        HBox rightBottomBox = new HBox(15);
        rightBottomBox.setAlignment(Pos.CENTER_LEFT);
        VBox dateBox = createInputBox("Ngày vào làm", dpNgayVaoLam);
        HBox.setHgrow(dateBox, Priority.ALWAYS);
        rightBottomBox.getChildren().addAll(dateBox, chkTrangThai);

        gridPane.add(rightBottomBox, 1, 3);

        // =================================================================================
        // 4. KHU VỰC NÚT BẤM (HÀNH ĐỘNG)
        // =================================================================================
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(5, 0, 0, 0));

        btnLamMoi = createButton("Làm Mới", COLOR_TEXT_MUTED, "#475569"); btnLamMoi.setOnAction(e -> lamMoiForm());
        btnThem = createButton("✨ Thêm Mới", COLOR_SUCCESS, COLOR_SUCCESS_HOVER); btnThem.setOnAction(e -> themNhanVien());
        btnCapNhat = createButton("🔄 Cập Nhật", COLOR_PRIMARY, COLOR_PRIMARY_HOVER); btnCapNhat.setOnAction(e -> capNhatNhanVien());
        btnXoa = createButton("🗑 Xóa", COLOR_DANGER, COLOR_DANGER_HOVER); btnXoa.setOnAction(e -> xoaNhanVien());

        buttonBox.getChildren().addAll(btnLamMoi, btnThem, btnCapNhat, btnXoa);

        formCard.getChildren().addAll(lblFormTitle, gridPane, buttonBox);

        mainVBox.getChildren().addAll(headerBox, tableCard, formCard);

        loadNhanVienData();
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

    // =================================================================================
    // HÀM LOẠI BỎ DẤU TIẾNG VIỆT (TÌM KIẾM TƯƠNG ĐỐI)
    // =================================================================================
    private String removeAccents(String str) {
        if (str == null) return "";
        try {
            String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("").replace('đ','d').replace('Đ','D');
        } catch (Exception e) {
            return str;
        }
    }

    private void loadNhanVienData() {
        try {
            List<NhanVienDTO> list = nhanVienService.getAllNhanVien();
            nhanVienList = FXCollections.observableArrayList(list);

            FilteredList<NhanVienDTO> filteredData = new FilteredList<>(nhanVienList, b -> true);

            // Lắng nghe thay đổi tìm kiếm tương đối (Không phân biệt dấu, không phân biệt hoa thường)
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(nv -> {
                    if (newValue == null || newValue.isEmpty()) return true;

                    // Từ khóa tìm kiếm đã bị chuyển thành viết thường và không dấu
                    String filter = removeAccents(newValue.toLowerCase().trim());

                    // Dữ liệu từ DB cũng được chuyển thành viết thường và không dấu để so sánh
                    String ten = removeAccents(nv.getHoTen() != null ? nv.getHoTen().toLowerCase() : "");
                    String cccd = removeAccents(nv.getCccd() != null ? nv.getCccd().toLowerCase() : "");
                    String sdt = removeAccents(nv.getSoDienThoai() != null ? nv.getSoDienThoai().toLowerCase() : "");
                    String ma = removeAccents(nv.getMaNhanVien() != null ? nv.getMaNhanVien().toLowerCase() : "");

                    if (ten.contains(filter)) return true;
                    if (cccd.contains(filter)) return true;
                    if (sdt.contains(filter)) return true;
                    if (ma.contains(filter)) return true;

                    return false;
                });
            });

            SortedList<NhanVienDTO> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(table.comparatorProperty());
            table.setItems(sortedData);

        } catch (Exception e) {
            logger.severe("Lỗi tải dữ liệu nhân viên: " + e.getMessage());
            showError("Lỗi tải dữ liệu nhân viên: " + e.getMessage());
        }
    }

    private void hienThiLenForm(NhanVienDTO nv) {
        txtMa.setText(nv.getMaNhanVien());
        txtHoTen.setText(nv.getHoTen());
        txtSDT.setText(nv.getSoDienThoai());
        dpNgaySinh.setValue(nv.getNgaySinh());
        txtCCCD.setText(nv.getCccd());
        cbLoaiNhanVien.setValue(nv.getLoaiNhanVien());
        dpNgayVaoLam.setValue(nv.getNgayVaoLam());
        txtQueQuan.setText(nv.getQueQuan());
        chkTrangThai.setSelected(nv.isTrangThai());
    }

    private void clearForm() {
        txtMa.clear(); txtHoTen.clear(); txtSDT.clear(); txtCCCD.clear(); txtQueQuan.clear(); searchField.clear();
        dpNgaySinh.setValue(null); dpNgayVaoLam.setValue(null); cbLoaiNhanVien.setValue(null); chkTrangThai.setSelected(false);
        table.getSelectionModel().clearSelection();
    }

    private boolean validateForm() {
        if (txtHoTen.getText().trim().isEmpty() || txtSDT.getText().trim().isEmpty() || txtCCCD.getText().trim().isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin bắt buộc!"); return false;
        }
        if (!txtSDT.getText().matches("\\d{10}")) {
            showError("Số điện thoại phải có 10 chữ số!"); return false;
        }
        return true;
    }

    private NhanVienDTO getFormData() {
        NhanVienDTO dto = new NhanVienDTO();
        dto.setMaNhanVien(txtMa.getText().trim());
        dto.setHoTen(txtHoTen.getText().trim());
        dto.setSoDienThoai(txtSDT.getText().trim());
        dto.setNgaySinh(dpNgaySinh.getValue());
        dto.setCccd(txtCCCD.getText().trim());
        dto.setLoaiNhanVien(cbLoaiNhanVien.getValue());
        dto.setNgayVaoLam(dpNgayVaoLam.getValue());
        dto.setQueQuan(txtQueQuan.getText().trim());
        dto.setTrangThai(chkTrangThai.isSelected());
        return dto;
    }

    private void themNhanVien() {
        if (!validateForm()) return;
        try {
            if (nhanVienService.addNhanVien(getFormData()) != null) {
                showSuccess("Thêm nhân viên thành công!");
                loadNhanVienData();
                clearForm();
            }
        } catch (Exception e) { showError("Không thể thêm: " + e.getMessage()); }
    }

    private void capNhatNhanVien() {
        if (txtMa.getText().isEmpty()) { showError("Vui lòng chọn nhân viên cần cập nhật từ bảng!"); return; }
        if (!validateForm()) return;
        try {
            if (nhanVienService.updateNhanVien(getFormData()) != null) {
                showSuccess("Cập nhật thành công!");
                loadNhanVienData();
                clearForm();
            }
        } catch (Exception e) { showError("Không thể cập nhật: " + e.getMessage()); }
    }

    private void xoaNhanVien() {
        if (txtMa.getText().isEmpty()) { showError("Vui lòng chọn nhân viên cần xóa từ bảng!"); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Hành động này không thể hoàn tác. Bạn có chắc muốn xóa?");
        confirm.setTitle("Xác nhận xóa"); confirm.setHeaderText("Xóa nhân sự " + txtHoTen.getText());
        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                if (nhanVienService.deleteNhanVien(txtMa.getText())) {
                    showSuccess("Đã xóa nhân viên khỏi hệ thống.");
                    loadNhanVienData();
                    clearForm();
                }
            } catch (Exception e) { showError("Lỗi khi xóa: " + e.getMessage()); }
        }
    }

    private void lamMoiForm() { clearForm(); loadNhanVienData(); }

    private void showError(String message) { Alert a = new Alert(Alert.AlertType.ERROR, message); a.setTitle("Lỗi Thao Tác"); a.setHeaderText("⚠️ Đã xảy ra lỗi"); a.show(); }
    private void showSuccess(String message) { Alert a = new Alert(Alert.AlertType.INFORMATION, message); a.setTitle("Thành Công"); a.setHeaderText("✅ Hoàn tất"); a.show(); }
}