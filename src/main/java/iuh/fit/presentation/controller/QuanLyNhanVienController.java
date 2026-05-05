package iuh.fit.presentation.controller;

import iuh.fit.core.dto.NhanVienDTO;
import iuh.fit.core.service.INhanVienService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class QuanLyNhanVienController {
    private static final Logger logger = Logger.getLogger(QuanLyNhanVienController.class.getName());

    private INhanVienService nhanVienService;
    private ObservableList<NhanVienDTO> nhanVienList;

    // UI Components
    private TableView<NhanVienDTO> table;
    private TextField txtMa, txtHoTen, txtSDT, txtCCCD, txtQueQuan;
    private DatePicker dpNgaySinh, dpNgayVaoLam;
    private ComboBox<String> cbLoaiNhanVien;
    private CheckBox chkTrangThai;
    private Label lblThongBao;
    private Button btnThem, btnCapNhat, btnXoa, btnLamMoi;

    public QuanLyNhanVienController(INhanVienService nhanVienService) {
        this.nhanVienService = nhanVienService;
    }

    public Scene createQuanLyNhanVienScene() {
        // ========== TABLE ==========
        table = new TableView<>();
        table.setPrefHeight(400);

        TableColumn<NhanVienDTO, String> colMa = new TableColumn<>("Mã NV");
        colMa.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        colMa.setPrefWidth(100);

        TableColumn<NhanVienDTO, String> colTen = new TableColumn<>("Họ tên");
        colTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colTen.setPrefWidth(180);

        TableColumn<NhanVienDTO, String> colSDT = new TableColumn<>("SĐT");
        colSDT.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        colSDT.setPrefWidth(120);

        TableColumn<NhanVienDTO, LocalDate> colNgaySinh = new TableColumn<>("Ngày sinh");
        colNgaySinh.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        colNgaySinh.setPrefWidth(120);

        TableColumn<NhanVienDTO, String> colCCCD = new TableColumn<>("CCCD");
        colCCCD.setCellValueFactory(new PropertyValueFactory<>("cccd"));
        colCCCD.setPrefWidth(120);

        TableColumn<NhanVienDTO, String> colLoai = new TableColumn<>("Loại NV");
        colLoai.setCellValueFactory(new PropertyValueFactory<>("loaiNhanVien"));
        colLoai.setPrefWidth(150);

        TableColumn<NhanVienDTO, Boolean> colTrangThai = new TableColumn<>("Trạng thái");
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTrangThai.setPrefWidth(100);

        table.getColumns().addAll(colMa, colTen, colSDT, colNgaySinh, colCCCD, colLoai, colTrangThai);

        // ========== FORM ==========
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(15));
        form.setStyle("-fx-border-color: gray; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");

        int row = 0;

        form.add(new Label("Mã NV:"), 0, row);
        txtMa = new TextField();
        txtMa.setDisable(true);
        form.add(txtMa, 1, row);
        row++;

        form.add(new Label("Họ tên:"), 0, row);
        txtHoTen = new TextField();
        form.add(txtHoTen, 1, row);
        form.add(new Label("Số điện thoại:"), 2, row);
        txtSDT = new TextField();
        form.add(txtSDT, 3, row);
        row++;

        form.add(new Label("Ngày sinh:"), 0, row);
        dpNgaySinh = new DatePicker();
        form.add(dpNgaySinh, 1, row);
        form.add(new Label("CCCD:"), 2, row);
        txtCCCD = new TextField();
        form.add(txtCCCD, 3, row);
        row++;

        form.add(new Label("Loại NV:"), 0, row);
        cbLoaiNhanVien = new ComboBox<>();
        cbLoaiNhanVien.setItems(FXCollections.observableArrayList(
                "NHAN_VIEN_LE_TAN", "NHAN_VIEN_PHONG", "NHAN_VIEN_SAN_KHAU", "NHAN_VIEN_QUAN_LY", "GIAM_DOC"
        ));
        form.add(cbLoaiNhanVien, 1, row);
        form.add(new Label("Ngày vào làm:"), 2, row);
        dpNgayVaoLam = new DatePicker();
        form.add(dpNgayVaoLam, 3, row);
        row++;

        form.add(new Label("Quê quán:"), 0, row);
        txtQueQuan = new TextField();
        form.add(txtQueQuan, 1, row);
        chkTrangThai = new CheckBox("Đang làm việc");
        form.add(chkTrangThai, 3, row);
        row++;

        lblThongBao = new Label();
        lblThongBao.setFont(Font.font("System", FontWeight.BOLD, 12));
        form.add(lblThongBao, 0, row, 4, 1);
        row++;

        // Buttons
        btnThem = new Button("Thêm");
        btnCapNhat = new Button("Cập nhật");
        btnXoa = new Button("Xóa");
        btnLamMoi = new Button("Làm mới");
        HBox buttonBox = new HBox(10, btnThem, btnCapNhat, btnXoa, btnLamMoi);
        buttonBox.setAlignment(Pos.CENTER);
        form.add(buttonBox, 0, row, 4, 1);

        // ========== EVENTS ==========
        setupEvents();

        // ========== MAIN LAYOUT ==========
        VBox root = new VBox(10, new Label("QUẢN LÝ NHÂN VIÊN"), table, form);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        // Load initial data
        loadNhanVienData();

        return new Scene(root, 1000, 750);
    }

    private void setupEvents() {
        // Click trên bảng -> đổ dữ liệu lên form
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                hienThiLenForm(newVal);
            }
        });

        btnThem.setOnAction(e -> themNhanVien());
        btnCapNhat.setOnAction(e -> capNhatNhanVien());
        btnXoa.setOnAction(e -> xoaNhanVien());
        btnLamMoi.setOnAction(e -> lamMoiForm());
    }

    private void loadNhanVienData() {
        try {
            List<NhanVienDTO> list = nhanVienService.getAllNhanVien();
            nhanVienList = FXCollections.observableArrayList(list);
            table.setItems(nhanVienList);
        } catch (Exception e) {
            logger.severe("Lỗi tải dữ liệu nhân viên: " + e.getMessage());
            showError("Lỗi tải dữ liệu nhân viên!");
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
        txtMa.clear();
        txtHoTen.clear();
        txtSDT.clear();
        dpNgaySinh.setValue(null);
        txtCCCD.clear();
        cbLoaiNhanVien.setValue(null);
        dpNgayVaoLam.setValue(null);
        txtQueQuan.clear();
        chkTrangThai.setSelected(false);
        table.getSelectionModel().clearSelection();
        lblThongBao.setText("");
    }

    private boolean validateForm() {
        if (txtHoTen.getText().trim().isEmpty()) {
            showError("Họ tên không được để trống!");
            return false;
        }
        if (!txtSDT.getText().matches("\\d{10}")) {
            showError("Số điện thoại phải có 10 chữ số!");
            return false;
        }
        if (txtCCCD.getText().trim().isEmpty()) {
            showError("CCCD không được để trống!");
            return false;
        }
        if (cbLoaiNhanVien.getValue() == null) {
            showError("Vui lòng chọn loại nhân viên!");
            return false;
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
            NhanVienDTO dto = getFormData();
            NhanVienDTO created = nhanVienService.addNhanVien(dto);
            if (created != null) {
                showSuccess("Thêm nhân viên thành công!");
                loadNhanVienData();
                clearForm();
            } else {
                showError("Thêm thất bại (có thể trùng số điện thoại)");
            }
        } catch (IllegalArgumentException e) {
            showError("Lỗi: " + e.getMessage());
        }
    }

    private void capNhatNhanVien() {
        if (txtMa.getText().isEmpty()) {
            showError("Vui lòng chọn nhân viên cần cập nhật!");
            return;
        }
        if (!validateForm()) return;
        try {
            NhanVienDTO dto = getFormData();
            NhanVienDTO updated = nhanVienService.updateNhanVien(dto);
            if (updated != null) {
                showSuccess("Cập nhật thành công!");
                loadNhanVienData();
                clearForm();
            } else {
                showError("Cập nhật thất bại!");
            }
        } catch (IllegalArgumentException e) {
            showError("Lỗi: " + e.getMessage());
        }
    }

    private void xoaNhanVien() {
        if (txtMa.getText().isEmpty()) {
            showError("Vui lòng chọn nhân viên cần xóa!");
            return;
        }
        boolean success = nhanVienService.deleteNhanVien(txtMa.getText());
        if (success) {
            showSuccess("Xóa thành công!");
            loadNhanVienData();
            clearForm();
        } else {
            showError("Xóa thất bại!");
        }
    }

    private void lamMoiForm() {
        clearForm();
        loadNhanVienData();
    }

    private void showError(String message) {
        lblThongBao.setText("❌ " + message);
        lblThongBao.setStyle("-fx-text-fill: red;");
    }

    private void showSuccess(String message) {
        lblThongBao.setText("✅ " + message);
        lblThongBao.setStyle("-fx-text-fill: green;");
    }
}