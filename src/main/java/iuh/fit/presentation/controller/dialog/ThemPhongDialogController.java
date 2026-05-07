package iuh.fit.presentation.controller.dialog;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.service.impl.LoaiPhongServiceImpl;
import iuh.fit.core.service.ILoaiPhongService;
import iuh.fit.infrastructure.persistence.LoaiPhongRepositoryImpl;

/**
 * ThemPhongDialogController: Dialog thêm/sửa phòng
 */
public class ThemPhongDialogController {
    
    @FXML private TextField tfMaPhong;
    @FXML private TextField tfTenPhong;
    @FXML private ComboBox<String> cbLoaiPhong;
    @FXML private TextField tfGiaPhong;
    @FXML private ComboBox<String> cbTinhTrang;
    
    private ILoaiPhongService loaiPhongService;
    private PhongDTO phongDTO;
    private boolean isEditMode = false;
    
    @FXML
    public void initialize() {
        loaiPhongService = new LoaiPhongServiceImpl(new LoaiPhongRepositoryImpl());
        setupComboBoxes();
    }
    
    /**
     * Setup ComboBoxes
     */
    private void setupComboBoxes() {
        // Loại phòng
        ObservableList<String> loaiPhongList = FXCollections.observableArrayList(
            "Phòng đơn", "Phòng đôi", "Phòng gia đình", "Phòng VIP"
        );
        cbLoaiPhong.setItems(loaiPhongList);
        
        // Tình trạng
        ObservableList<String> tinhTrangList = FXCollections.observableArrayList(
            "Trống", "Đã Đặt", "Bảo Trì"
        );
        cbTinhTrang.setItems(tinhTrangList);
    }
    
    /**
     * Set mode Edit khi sửa phòng
     */
    public void setEditMode(PhongDTO phong) {
        this.phongDTO = phong;
        this.isEditMode = true;
        
        tfMaPhong.setText(phong.getMaPhong());
        tfMaPhong.setDisable(true);
        tfTenPhong.setText(phong.getTenPhong());
        cbLoaiPhong.setValue(phong.getTenLoaiPhong());
        tfGiaPhong.setText(String.valueOf(phong.getGiaPhong()));
        cbTinhTrang.setValue(phong.getTinhTrang());
    }
    
    /**
     * Get dữ liệu từ form
     */
    public PhongDTO getPhongData() {
        PhongDTO dto = new PhongDTO();
        dto.setMaPhong(tfMaPhong.getText());
        dto.setTenPhong(tfTenPhong.getText());
        dto.setTenLoaiPhong(cbLoaiPhong.getValue());
        try {
            dto.setGiaPhong(Double.parseDouble(tfGiaPhong.getText()));
        } catch (NumberFormatException e) {
            dto.setGiaPhong(0);
        }
        dto.setTinhTrang(cbTinhTrang.getValue());
        return dto;
    }
    
    /**
     * Validate input
     */
    public boolean validateInput() {
        if (tfMaPhong.getText().trim().isEmpty()) {
            showError("Mã phòng không được để trống!");
            return false;
        }
        if (tfTenPhong.getText().trim().isEmpty()) {
            showError("Tên phòng không được để trống!");
            return false;
        }
        if (cbLoaiPhong.getValue() == null) {
            showError("Vui lòng chọn loại phòng!");
            return false;
        }
        if (tfGiaPhong.getText().trim().isEmpty()) {
            showError("Giá phòng không được để trống!");
            return false;
        }
        try {
            Double.parseDouble(tfGiaPhong.getText());
        } catch (NumberFormatException e) {
            showError("Giá phòng phải là số!");
            return false;
        }
        if (cbTinhTrang.getValue() == null) {
            showError("Vui lòng chọn tình trạng!");
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

