package iuh.fit.presentation.controller;

import iuh.fit.core.dto.KhachHangDTO;
import iuh.fit.core.service.IKhachHangService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Controller: QuanLyKhachHangController (Presentation Layer - Customer Management Screen)
 * 
 * Tầng: PRESENTATION - JavaFX Controller Layer
 * Trách nhiệm: Quản lý giao diện quản lý khách hàng
 * 
 * Chức năng:
 * - Hiển thị danh sách khách hàng trong TableView
 * - Thêm khách hàng mới
 * - Cập nhật thông tin khách hàng
 * - Xóa khách hàng
 */
public class QuanLyKhachHangController {
    
    private static final Logger logger = Logger.getLogger(QuanLyKhachHangController.class.getName());
    
    // ============ UI COMPONENTS ============
    private TableView<KhachHangDTO> tableView;
    private TextField maTextField, tenTextField, sdtTextField;
    private DatePicker datePickerNgaySinh;
    private ComboBox<String> loaiComboBox;
    private Button addButton, updateButton, deleteButton;
    
    // ============ SERVICES ============
    private IKhachHangService khachHangService;
    
    // ============ DATA ============
    private ObservableList<KhachHangDTO> khachHangList;
    
    public QuanLyKhachHangController(IKhachHangService khachHangService) {
        this.khachHangService = khachHangService;
    }
    
    /**
     * Khởi tạo scene quản lý khách hàng
     */
    public Scene createQuanLyKhachHangScene() {
        // ============ CREATE TABLE ============
        tableView = new TableView<>();
        
        TableColumn<KhachHangDTO, String> maCol = new TableColumn<>("Mã KH");
        maCol.setCellValueFactory(new PropertyValueFactory<>("maKhachHang"));
        
        TableColumn<KhachHangDTO, String> tenCol = new TableColumn<>("Tên");
        tenCol.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        
        TableColumn<KhachHangDTO, String> sdtCol = new TableColumn<>("SĐT");
        sdtCol.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        
        TableColumn<KhachHangDTO, LocalDate> ngayCol = new TableColumn<>("Ngày sinh");
        ngayCol.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        
        TableColumn<KhachHangDTO, String> loaiCol = new TableColumn<>("Loại");
        loaiCol.setCellValueFactory(new PropertyValueFactory<>("loaiKhachHang"));
        
        tableView.getColumns().addAll(maCol, tenCol, sdtCol, ngayCol, loaiCol);
        tableView.setPrefHeight(300);
        
        // ============ CREATE INPUT FORM ============
        Label maLabel = new Label("Mã KH:");
        maTextField = new TextField();
        maTextField.setDisable(true); // Auto-generated
        
        Label tenLabel = new Label("Tên:");
        tenTextField = new TextField();
        tenTextField.setPromptText("Nhập tên khách hàng");
        
        Label sdtLabel = new Label("SĐT:");
        sdtTextField = new TextField();
        sdtTextField.setPromptText("Nhập số điện thoại (10 số)");
        
        Label ngayLabel = new Label("Ngày sinh:");
        datePickerNgaySinh = new DatePicker();
        
        Label loaiLabel = new Label("Loại:");
        loaiComboBox = new ComboBox<>();
        loaiComboBox.setItems(FXCollections.observableArrayList(
                "KHACH_HOI_VIEN",
                "KHACH_THUONG_XUYÊN",
                "KHACH_MOI"
        ));
        
        // ============ BUTTONS ============
        addButton = new Button("Thêm");
        addButton.setPrefWidth(80);
        addButton.setOnAction(e -> handleThemKhachHang());
        
        updateButton = new Button("Cập nhật");
        updateButton.setPrefWidth(80);
        updateButton.setOnAction(e -> handleCapNhatKhachHang());
        
        deleteButton = new Button("Xóa");
        deleteButton.setPrefWidth(80);
        deleteButton.setOnAction(e -> handleXoaKhachHang());
        
        // ============ EVENT HANDLERS ============
        tableView.setOnMouseClicked(e -> handleTableRowClicked());
        
        // ============ LAYOUT ============
        VBox formVBox = new VBox(10);
        formVBox.setStyle("-fx-padding: 10; -fx-border-style: solid; -fx-border-width: 1");
        
        // Form fields
        HBox row1 = createFormRow(maLabel, maTextField, tenLabel, tenTextField);
        HBox row2 = createFormRow(sdtLabel, sdtTextField, ngayLabel, datePickerNgaySinh);
        HBox row3 = createFormRow(loaiLabel, loaiComboBox);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton);
        
        formVBox.getChildren().addAll(row1, row2, row3, buttonBox);
        
        // Main layout
        VBox mainVBox = new VBox(10);
        mainVBox.setStyle("-fx-padding: 20");
        mainVBox.getChildren().addAll(
                new Label("QUẢN LÝ KHÁCH HÀNG"),
                tableView,
                formVBox
        );
        
        // Load dữ liệu từ service
        loadKhachHangData();
        
        return new Scene(mainVBox, 800, 600);
    }
    
    /**
     * Tải dữ liệu khách hàng từ service
     */
    private void loadKhachHangData() {
        try {
            List<KhachHangDTO> list = khachHangService.getAllKhachHang();
            khachHangList = FXCollections.observableArrayList(list);
            tableView.setItems(khachHangList);
            logger.info("✅ Tải " + list.size() + " khách hàng");
        } catch (Exception e) {
            logger.severe("❌ Lỗi tải dữ liệu: " + e.getMessage());
            showError("Lỗi tải dữ liệu khách hàng");
        }
    }
    
    /**
     * Xử lý khi click vào row trong table
     */
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
    
    /**
     * Xử lý thêm khách hàng
     */
    private void handleThemKhachHang() {
        try {
            KhachHangDTO dto = new KhachHangDTO();
            dto.setHoTen(tenTextField.getText());
            dto.setSoDienThoai(sdtTextField.getText());
            dto.setNgaySinh(datePickerNgaySinh.getValue());
            dto.setLoaiKhachHang(loaiComboBox.getValue());
            
            KhachHangDTO created = khachHangService.addKhachHang(dto);
            
            if (created != null) {
                logger.info("✅ Thêm khách hàng: " + created.getMaKhachHang());
                loadKhachHangData();
                clearFields();
                showSuccess("Thêm khách hàng thành công");
            }
        } catch (Exception e) {
            logger.severe("❌ Lỗi thêm: " + e.getMessage());
            showError("Lỗi: " + e.getMessage());
        }
    }
    
    /**
     * Xử lý cập nhật khách hàng
     */
    private void handleCapNhatKhachHang() {
        try {
            if (maTextField.getText().isEmpty()) {
                showError("Vui lòng chọn khách hàng");
                return;
            }
            
            KhachHangDTO dto = new KhachHangDTO();
            dto.setMaKhachHang(maTextField.getText());
            dto.setHoTen(tenTextField.getText());
            dto.setSoDienThoai(sdtTextField.getText());
            dto.setNgaySinh(datePickerNgaySinh.getValue());
            dto.setLoaiKhachHang(loaiComboBox.getValue());
            
            KhachHangDTO updated = khachHangService.updateKhachHang(dto);
            
            if (updated != null) {
                logger.info("✅ Cập nhật khách hàng: " + updated.getMaKhachHang());
                loadKhachHangData();
                clearFields();
                showSuccess("Cập nhật khách hàng thành công");
            }
        } catch (Exception e) {
            logger.severe("❌ Lỗi cập nhật: " + e.getMessage());
            showError("Lỗi: " + e.getMessage());
        }
    }
    
    /**
     * Xử lý xóa khách hàng
     */
    private void handleXoaKhachHang() {
        try {
            if (maTextField.getText().isEmpty()) {
                showError("Vui lòng chọn khách hàng");
                return;
            }
            
            boolean success = khachHangService.deleteKhachHang(maTextField.getText());
            
            if (success) {
                logger.info("✅ Xóa khách hàng: " + maTextField.getText());
                loadKhachHangData();
                clearFields();
                showSuccess("Xóa khách hàng thành công");
            }
        } catch (Exception e) {
            logger.severe("❌ Lỗi xóa: " + e.getMessage());
            showError("Lỗi: " + e.getMessage());
        }
    }
    
    /**
     * Xóa các trường input
     */
    private void clearFields() {
        tenTextField.clear();
        sdtTextField.clear();
        datePickerNgaySinh.setValue(null);
        loaiComboBox.setValue(null);
    }
    
    /**
     * Helper: Tạo form row
     */
    private HBox createFormRow(Control... controls) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        for (Control control : controls) {
            if (control instanceof Label) {
                row.getChildren().add(control);
            } else {
                HBox.setHgrow(control, new javafx.scene.layout.Priority[] {
                    javafx.scene.layout.Priority.ALWAYS
                }[0]);
                row.getChildren().add(control);
            }
        }
        return row;
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

