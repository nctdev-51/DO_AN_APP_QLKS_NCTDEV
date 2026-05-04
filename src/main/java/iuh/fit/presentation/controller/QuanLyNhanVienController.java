package iuh.fit.presentation.controller;

import iuh.fit.core.dto.NhanVienDTO;
import iuh.fit.core.entity.LoaiNhanVien;
import iuh.fit.core.service.INhanVienService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller: QuanLyNhanVienController (Presentation Layer - Employee Management Screen)
 * 
 * Tầng: PRESENTATION - JavaFX Controller Layer
 * Trách nhiệm: Quản lý giao diện quản lý nhân viên
 */
public class QuanLyNhanVienController {
    
    private static final Logger logger = Logger.getLogger(QuanLyNhanVienController.class.getName());
    
    // ============ FXML COMPONENTS ============
    @FXML
    private TableView<NhanVienDTO> tableNhanVien;
    @FXML
    private TableColumn<NhanVienDTO, String> colMa;
    @FXML
    private TableColumn<NhanVienDTO, String> colTen;
    @FXML
    private TableColumn<NhanVienDTO, String> colSDT;
    @FXML
    private TableColumn<NhanVienDTO, LocalDate> colNgaySinh;
    @FXML
    private TableColumn<NhanVienDTO, String> colCCCD;
    @FXML
    private TableColumn<NhanVienDTO, String> colLoai;
    @FXML
    private TableColumn<NhanVienDTO, Boolean> colTrangThai;
    
    @FXML
    private TextField txtMa, txtTen, txtSDT, txtCCCD, txtQueQuan;
    @FXML
    private DatePicker dpNgaySinh, dpNgayVaoLam;
    @FXML
    private ComboBox<String> cbLoai;
    @FXML
    private CheckBox chkTrangThai;
    @FXML
    private Label lblThongBao;
    
    @FXML
    private Button btnThem, btnCapNhat, btnXoa, btnLamMoi;
    
    // ============ SERVICES ============
    private INhanVienService nhanVienService;
    
    // ============ DATA ============
    private ObservableList<NhanVienDTO> nhanVienList;
    
    /**
     * Constructor - Inject Service
     */
    public QuanLyNhanVienController(INhanVienService nhanVienService) {
        this.nhanVienService = nhanVienService;
    }
    
    /**
     * Khởi tạo Scene từ FXML
     */
    public Scene createQuanLyNhanVienScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/QuanLyNhanVien.fxml"));
        loader.setController(this);
        
        Scene scene = new Scene(loader.load(), 1000, 700);
        initialize();
        
        return scene;
    }
    
    /**
     * Khởi tạo các FXML components
     */
    public void initialize() {
        // Setup TableView columns
        colMa.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        colTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colSDT.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        colNgaySinh.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        colCCCD.setCellValueFactory(new PropertyValueFactory<>("cccd"));
        colLoai.setCellValueFactory(new PropertyValueFactory<>("loaiNhanVien"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        
        // Setup ComboBox with employee types
        cbLoai.setItems(FXCollections.observableArrayList(
                "NHAN_VIEN_LE_TAN",
                "NHAN_VIEN_PHONG",
                "NHAN_VIEN_SAN_KHAU",
                "QUAN_LY",
                "GIAM_DOC"
        ));
        
        // Setup Button handlers
        btnThem.setOnAction(e -> handleThem());
        btnCapNhat.setOnAction(e -> handleCapNhat());
        btnXoa.setOnAction(e -> handleXoa());
        btnLamMoi.setOnAction(e -> handleLamMoi());
        
        // Setup TableView row click handler
        tableNhanVien.setOnMouseClicked(e -> handleTableRowClicked());
        
        // Load data
        loadNhanVienData();
    }
    
    /**
     * Tải dữ liệu nhân viên từ service
     */
    private void loadNhanVienData() {
        try {
            List<NhanVienDTO> list = nhanVienService.getAllNhanVien();
            nhanVienList = FXCollections.observableArrayList(list);
            tableNhanVien.setItems(nhanVienList);
            logger.info("✅ Tải " + list.size() + " nhân viên");
        } catch (Exception e) {
            logger.severe("❌ Lỗi tải dữ liệu: " + e.getMessage());
            showError("Lỗi tải dữ liệu nhân viên");
        }
    }
    
    /**
     * Xử lý khi click vào row trong table
     */
    private void handleTableRowClicked() {
        NhanVienDTO selected = tableNhanVien.getSelectionModel().getSelectedItem();
        if (selected != null) {
            txtMa.setText(selected.getMaNhanVien());
            txtTen.setText(selected.getHoTen());
            txtSDT.setText(selected.getSoDienThoai());
            txtCCCD.setText(selected.getCccd());
            dpNgaySinh.setValue(selected.getNgaySinh());
            dpNgayVaoLam.setValue(selected.getNgayVaoLam());
            cbLoai.setValue(selected.getLoaiNhanVien());
            txtQueQuan.setText(selected.getQueQuan());
            chkTrangThai.setSelected(selected.isTrangThai());
        }
    }
    
    /**
     * Xử lý thêm nhân viên
     */
    private void handleThem() {
        try {
            NhanVienDTO dto = new NhanVienDTO();
            dto.setHoTen(txtTen.getText());
            dto.setSoDienThoai(txtSDT.getText());
            dto.setCccd(txtCCCD.getText());
            dto.setNgaySinh(dpNgaySinh.getValue());
            dto.setNgayVaoLam(dpNgayVaoLam.getValue());
            dto.setLoaiNhanVien(cbLoai.getValue());
            dto.setQueQuan(txtQueQuan.getText());
            dto.setTrangThai(chkTrangThai.isSelected());
            
            NhanVienDTO created = nhanVienService.addNhanVien(dto);
            
            if (created != null) {
                logger.info("✅ Thêm nhân viên: " + created.getMaNhanVien());
                loadNhanVienData();
                clearFields();
                showSuccess("Thêm nhân viên thành công");
            }
        } catch (IllegalArgumentException e) {
            logger.warning("⚠️ " + e.getMessage());
            showError("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            logger.severe("❌ Lỗi thêm: " + e.getMessage());
            showError("Lỗi hệ thống: " + e.getMessage());
        }
    }
    
    /**
     * Xử lý cập nhật nhân viên
     */
    private void handleCapNhat() {
        try {
            if (txtMa.getText().isEmpty()) {
                showError("Vui lòng chọn nhân viên");
                return;
            }
            
            NhanVienDTO dto = new NhanVienDTO();
            dto.setMaNhanVien(txtMa.getText());
            dto.setHoTen(txtTen.getText());
            dto.setSoDienThoai(txtSDT.getText());
            dto.setCccd(txtCCCD.getText());
            dto.setNgaySinh(dpNgaySinh.getValue());
            dto.setNgayVaoLam(dpNgayVaoLam.getValue());
            dto.setLoaiNhanVien(cbLoai.getValue());
            dto.setQueQuan(txtQueQuan.getText());
            dto.setTrangThai(chkTrangThai.isSelected());
            
            NhanVienDTO updated = nhanVienService.updateNhanVien(dto);
            
            if (updated != null) {
                logger.info("✅ Cập nhật nhân viên: " + updated.getMaNhanVien());
                loadNhanVienData();
                clearFields();
                showSuccess("Cập nhật nhân viên thành công");
            }
        } catch (IllegalArgumentException e) {
            logger.warning("⚠️ " + e.getMessage());
            showError("Lỗi: " + e.getMessage());
        } catch (Exception e) {
            logger.severe("❌ Lỗi cập nhật: " + e.getMessage());
            showError("Lỗi hệ thống: " + e.getMessage());
        }
    }
    
    /**
     * Xử lý xóa nhân viên
     */
    private void handleXoa() {
        try {
            if (txtMa.getText().isEmpty()) {
                showError("Vui lòng chọn nhân viên");
                return;
            }
            
            boolean success = nhanVienService.deleteNhanVien(txtMa.getText());
            
            if (success) {
                logger.info("✅ Xóa nhân viên: " + txtMa.getText());
                loadNhanVienData();
                clearFields();
                showSuccess("Xóa nhân viên thành công");
            }
        } catch (Exception e) {
            logger.severe("❌ Lỗi xóa: " + e.getMessage());
            showError("Lỗi: " + e.getMessage());
        }
    }
    
    /**
     * Xử lý làm mới form
     */
    private void handleLamMoi() {
        clearFields();
        tableNhanVien.getSelectionModel().clearSelection();
        lblThongBao.setText("");
    }
    
    /**
     * Xóa các trường input
     */
    private void clearFields() {
        txtMa.clear();
        txtTen.clear();
        txtSDT.clear();
        txtCCCD.clear();
        txtQueQuan.clear();
        dpNgaySinh.setValue(null);
        dpNgayVaoLam.setValue(null);
        cbLoai.setValue(null);
        chkTrangThai.setSelected(false);
    }
    
    /**
     * Hiển thị lỗi
     */
    private void showError(String message) {
        lblThongBao.setText("❌ " + message);
        lblThongBao.setStyle("-fx-text-fill: red; -fx-font-size: 12;");
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Hiển thị thành công
     */
    private void showSuccess(String message) {
        lblThongBao.setText("✅ " + message);
        lblThongBao.setStyle("-fx-text-fill: green; -fx-font-size: 12;");
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

