package iuh.fit.presentation.controller;

import iuh.fit.core.dto.KhachHangDTO;
import iuh.fit.core.service.IKhachHangService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class QuanLyKhachHangController {
    private static final Logger logger = Logger.getLogger(QuanLyKhachHangController.class.getName());
    private TableView<KhachHangDTO> tableView;
    private TextField maTextField, tenTextField, sdtTextField;
    private DatePicker datePickerNgaySinh;
    private ComboBox<String> loaiComboBox;
    private Button addButton, updateButton, deleteButton;
    private IKhachHangService khachHangService;
    private ObservableList<KhachHangDTO> khachHangList;

    public QuanLyKhachHangController(IKhachHangService khachHangService) {
        this.khachHangService = khachHangService;
    }

    public Scene createQuanLyKhachHangScene() {
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

        Label maLabel = new Label("Mã KH:");
        maTextField = new TextField();
        maTextField.setDisable(true);
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

        addButton = new Button("Thêm");
        addButton.setPrefWidth(80);
        addButton.setOnAction(e -> handleThemKhachHang());
        updateButton = new Button("Cập nhật");
        updateButton.setPrefWidth(80);
        updateButton.setOnAction(e -> handleCapNhatKhachHang());
        deleteButton = new Button("Xóa");
        deleteButton.setPrefWidth(80);
        deleteButton.setOnAction(e -> handleXoaKhachHang());

        tableView.setOnMouseClicked(e -> handleTableRowClicked());

        VBox formVBox = new VBox(10);
        formVBox.setStyle("-fx-padding: 10; -fx-border-style: solid; -fx-border-width: 1");
        HBox row1 = createFormRow(maLabel, maTextField, tenLabel, tenTextField);
        HBox row2 = createFormRow(sdtLabel, sdtTextField, ngayLabel, datePickerNgaySinh);
        HBox row3 = createFormRow(loaiLabel, loaiComboBox);
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton);

        formVBox.getChildren().addAll(row1, row2, row3, buttonBox);

        VBox mainVBox = new VBox(10);
        mainVBox.setStyle("-fx-padding: 20");
        mainVBox.getChildren().addAll(new Label("QUẢN LÝ KHÁCH HÀNG"), tableView, formVBox);

        loadKhachHangData();
        return new Scene(mainVBox, 800, 600);
    }

    private void loadKhachHangData() {
        try {
            List<KhachHangDTO> list = khachHangService.getAllKhachHang();
            khachHangList = FXCollections.observableArrayList(list);
            tableView.setItems(khachHangList);
        } catch (Exception e) {
            showError("Lỗi tải dữ liệu khách hàng");
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
            dto.setHoTen(tenTextField.getText());
            dto.setSoDienThoai(sdtTextField.getText());
            dto.setNgaySinh(datePickerNgaySinh.getValue());
            dto.setLoaiKhachHang(loaiComboBox.getValue());
            KhachHangDTO created = khachHangService.addKhachHang(dto);
            if (created != null) {
                loadKhachHangData();
                clearFields();
                showSuccess("Thêm khách hàng thành công");
            }
        } catch (Exception e) {
            showError("Lỗi: " + e.getMessage());
        }
    }

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
                loadKhachHangData();
                clearFields();
                showSuccess("Cập nhật khách hàng thành công");
            }
        } catch (Exception e) {
            showError("Lỗi: " + e.getMessage());
        }
    }

    private void handleXoaKhachHang() {
        try {
            if (maTextField.getText().isEmpty()) {
                showError("Vui lòng chọn khách hàng");
                return;
            }
            boolean success = khachHangService.deleteKhachHang(maTextField.getText());
            if (success) {
                loadKhachHangData();
                clearFields();
                showSuccess("Xóa khách hàng thành công");
            }
        } catch (Exception e) {
            showError("Lỗi: " + e.getMessage());
        }
    }

    private void clearFields() {
        tenTextField.clear();
        sdtTextField.clear();
        datePickerNgaySinh.setValue(null);
        loaiComboBox.setValue(null);
    }

    private HBox createFormRow(Control... controls) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        for (Control control : controls) {
            if (control instanceof Label) {
                row.getChildren().add(control);
            } else {
                HBox.setHgrow(control, javafx.scene.layout.Priority.ALWAYS);
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