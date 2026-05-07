package iuh.fit.presentation.controller.dialog;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import iuh.fit.core.dto.KhachHangDTO;

/**
 * ThemKhachHangDialogController: Dialog thêm/sửa khách hàng
 */
public class ThemKhachHangDialogController {

    @FXML
    private TextField tfMaKhach;
    @FXML
    private TextField tfTenKhach;
    @FXML
    private TextField tfSoDienThoai;
    @FXML
    private DatePicker dpNgaySinh;
    @FXML
    private ComboBox<String> cbLoaiKhach;

    private KhachHangDTO khachHangDTO;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        setupComboBoxes();
    }

    /**
     * Setup ComboBoxes
     */
    private void setupComboBoxes() {
        ObservableList<String> loaiKhachList = FXCollections.observableArrayList(
                "KHACH_HOI_VIEN", "KHACH_THUONG_XUYEN",
                "KHACH_VANG_LAI", "KHACH_MOI"
        );
        cbLoaiKhach.setItems(loaiKhachList);
    }

    /**
     * Set mode Edit khi sửa khách hàng
     */
    public void setEditMode(KhachHangDTO khachHang) {
        this.khachHangDTO = khachHang;
        this.isEditMode = true;

        tfMaKhach.setText(khachHang.getMaKhachHang());
        tfMaKhach.setDisable(true);
        tfTenKhach.setText(khachHang.getHoTen());
        tfSoDienThoai.setText(khachHang.getSoDienThoai());
        dpNgaySinh.setValue(khachHang.getNgaySinh());
        cbLoaiKhach.setValue(khachHang.getLoaiKhachHang());
    }

    /**
     * Get dữ liệu từ form
     */
    public KhachHangDTO getKhachHangData() {
        KhachHangDTO dto = new KhachHangDTO();
        dto.setMaKhachHang(tfMaKhach.getText());
        dto.setHoTen(tfTenKhach.getText());
        dto.setSoDienThoai(tfSoDienThoai.getText());
        dto.setNgaySinh(dpNgaySinh.getValue());
        dto.setLoaiKhachHang(cbLoaiKhach.getValue());
        return dto;
    }

    /**
     * Validate input
     */
    public boolean validateInput() {
        if (tfTenKhach.getText().trim().isEmpty()) {
            showError("Tên khách hàng không được để trống!");
            return false;
        }
        if (tfSoDienThoai.getText().trim().isEmpty()) {
            showError("Số điện thoại không được để trống!");
            return false;
        }
        if (!tfSoDienThoai.getText().matches("\\d{10}")) {
            showError("Số điện thoại phải có 10 chữ số!");
            return false;
        }
        if (dpNgaySinh.getValue() == null) {
            showError("Vui lòng chọn ngày sinh!");
            return false;
        }
        if (cbLoaiKhach.getValue() == null) {
            showError("Vui lòng chọn loại khách!");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

