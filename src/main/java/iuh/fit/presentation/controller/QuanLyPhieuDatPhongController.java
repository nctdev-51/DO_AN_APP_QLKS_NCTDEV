package iuh.fit.presentation.controller;

import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.service.IPhieuDatPhongService;
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

/**
 * Controller: Quản lý Phiếu Đặt Phòng
 * Hiển thị danh sách phiếu đặt phòng, cho phép cập nhật trạng thái, xóa, xem chi tiết
 */
public class QuanLyPhieuDatPhongController {

    private TableView<PhieuDatPhongDTO> tableView;
    private TextField searchField;
    private TextField maPhieuTextField, maKhachHangTextField, maPhongTextField;
    private DatePicker ngayDatPicker, ngayNhanPicker, ngayTraPicker;
    private ComboBox<String> trangThaiComboBox;
    private TextField tongTienTextField;
    private Button btnCapNhat, btnChiTiet, btnXoa, btnLamMoi;
    private IPhieuDatPhongService phieuDatPhongService;
    private ObservableList<PhieuDatPhongDTO> phieuList;

    // --- BẢNG MÀU ---
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_DANGER = "#ef4444";
    private final String COLOR_WARNING = "#f59e0b";
    private final String COLOR_TEXT_MAIN = "#1e293b";
    private final String COLOR_BORDER = "#cbd5e1";
    private final String COLOR_BG_LIGHT = "#f1f5f9";

    public QuanLyPhieuDatPhongController(IPhieuDatPhongService phieuDatPhongService) {
        this.phieuDatPhongService = phieuDatPhongService;
    }

    public VBox createQuanLyPhieuDatPhongView() {
        VBox mainVBox = new VBox(20);
        mainVBox.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + "; -fx-padding: 25;");

        // 1. TIÊU ĐỀ
        Label lblTitle = new Label("📋 QUẢN LÝ PHIẾU ĐẶT PHÒNG");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        // 2. THANH TÌM KIẾM & BỘ LỌC
        HBox filterBox = createFilterBox();

        // 3. BẢNG PHIẾU ĐẶT
        HBox tableBox = createTableBox();

        // 4. FORM CHI TIẾT PHIẾU
        HBox detailBox = createDetailBox();

        mainVBox.getChildren().addAll(lblTitle, filterBox, tableBox, detailBox);
        return mainVBox;
    }

    // --- THANH TÌM KIẾM ---
    private HBox createFilterBox() {
        HBox filterBox = new HBox(15);
        filterBox.setPadding(new Insets(15));
        filterBox.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-border-color: " + COLOR_BORDER + ";");
        filterBox.setAlignment(Pos.CENTER_LEFT);

        Label lblSearch = new Label("🔍 Tìm kiếm:");
        lblSearch.setFont(Font.font("Segoe UI", 12));

        searchField = new TextField();
        searchField.setPromptText("Tìm theo mã phiếu hoặc mã khách hàng...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-border-radius: 5; -fx-padding: 8;");

        Button btnSearch = new Button("Tìm");
        btnSearch.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 5; -fx-cursor: hand;");
        btnSearch.setOnAction(e -> refreshTable());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblStatus = new Label("Trạng Thái:");
        lblStatus.setFont(Font.font("Segoe UI", 12));

        ComboBox<String> filterStatus = new ComboBox<>();
        filterStatus.setItems(FXCollections.observableArrayList("Tất cả", "CHO_NHAN_PHONG", "DA_NHAN_PHONG", "DA_TRA_PHONG", "DA_HUY"));
        filterStatus.setValue("Tất cả");
        filterStatus.setPrefWidth(150);
        filterStatus.setStyle("-fx-border-radius: 5;");

        Button btnThem = new Button("➕ Thêm Phiếu");
        btnThem.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;");
        btnThem.setOnAction(e -> showAddPhieuDialog());

        filterBox.getChildren().addAll(lblSearch, searchField, btnSearch, lblStatus, filterStatus, spacer, btnThem);
        return filterBox;
    }

    // --- BẢNG PHIẾU ĐẶT ---
    private HBox createTableBox() {
        HBox tableBox = new HBox();
        tableBox.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-border-color: " + COLOR_BORDER + ";");
        tableBox.setPadding(new Insets(15));

        tableView = new TableView<>();
        tableView.setStyle("-fx-font-size: 12px;");

        TableColumn<PhieuDatPhongDTO, String> colMa = new TableColumn<>("Mã Phiếu");
        colMa.setCellValueFactory(new PropertyValueFactory<>("maPhieu"));
        colMa.setPrefWidth(100);

        TableColumn<PhieuDatPhongDTO, String> colKH = new TableColumn<>("Khách Hàng");
        colKH.setCellValueFactory(new PropertyValueFactory<>("tenKhachHang"));
        colKH.setPrefWidth(180);

        TableColumn<PhieuDatPhongDTO, String> colPhong = new TableColumn<>("Phòng");
        colPhong.setCellValueFactory(new PropertyValueFactory<>("tenPhong"));
        colPhong.setPrefWidth(150);

        TableColumn<PhieuDatPhongDTO, LocalDate> colNhan = new TableColumn<>("Ngày Nhận");
        colNhan.setCellValueFactory(new PropertyValueFactory<>("ngayNhan"));
        colNhan.setPrefWidth(130);

        TableColumn<PhieuDatPhongDTO, LocalDate> colTra = new TableColumn<>("Ngày Trả");
        colTra.setCellValueFactory(new PropertyValueFactory<>("ngayTra"));
        colTra.setPrefWidth(130);

        TableColumn<PhieuDatPhongDTO, String> colTrangThai = new TableColumn<>("Trạng Thái");
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTrangThai.setPrefWidth(130);

        TableColumn<PhieuDatPhongDTO, Double> colTong = new TableColumn<>("Tổng Tiền");
        colTong.setCellValueFactory(new PropertyValueFactory<>("tongTien"));
        colTong.setPrefWidth(120);

        tableView.getColumns().addAll(colMa, colKH, colPhong, colNhan, colTra, colTrangThai, colTong);

        refreshTable();

        tableView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                PhieuDatPhongDTO selected = tableView.getSelectionModel().getSelectedItem();
                if (selected != null) displayPhieuDetail(selected);
            }
        });

        tableBox.getChildren().add(tableView);
        HBox.setHgrow(tableView, Priority.ALWAYS);
        return tableBox;
    }

    // --- FORM CHI TIẾT PHIẾU ---
    private HBox createDetailBox() {
        HBox detailBox = new HBox(20);
        detailBox.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-border-color: " + COLOR_BORDER + ";");
        detailBox.setPadding(new Insets(20));

        // Form bên trái
        VBox formBox = new VBox(12);
        formBox.setPrefWidth(450);

        Label lblDetail = new Label("📝 CHI TIẾT PHIẾU ĐẶT");
        lblDetail.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        // Mã Phiếu
        VBox vbMa = new VBox(5);
        Label lbl1 = new Label("Mã Phiếu:");
        lbl1.setFont(Font.font("Segoe UI", 11));
        maPhieuTextField = new TextField();
        maPhieuTextField.setEditable(false);
        maPhieuTextField.setStyle("-fx-control-inner-background: #f1f5f9; -fx-border-radius: 5;");
        vbMa.getChildren().addAll(lbl1, maPhieuTextField);

        // Mã Khách Hàng
        VBox vbKH = new VBox(5);
        Label lbl2 = new Label("Khách Hàng:");
        lbl2.setFont(Font.font("Segoe UI", 11));
        maKhachHangTextField = new TextField();
        maKhachHangTextField.setStyle("-fx-border-radius: 5;");
        vbKH.getChildren().addAll(lbl2, maKhachHangTextField);

        // Mã Phòng
        VBox vbPhong = new VBox(5);
        Label lbl3 = new Label("Phòng:");
        lbl3.setFont(Font.font("Segoe UI", 11));
        maPhongTextField = new TextField();
        maPhongTextField.setStyle("-fx-border-radius: 5;");
        vbPhong.getChildren().addAll(lbl3, maPhongTextField);

        // Ngày Đặt
        VBox vbNgayDat = new VBox(5);
        Label lbl4 = new Label("Ngày Đặt:");
        lbl4.setFont(Font.font("Segoe UI", 11));
        ngayDatPicker = new DatePicker();
        ngayDatPicker.setStyle("-fx-border-radius: 5;");
        vbNgayDat.getChildren().addAll(lbl4, ngayDatPicker);

        // Ngày Nhận
        VBox vbNgayNhan = new VBox(5);
        Label lbl5 = new Label("Ngày Nhận:");
        lbl5.setFont(Font.font("Segoe UI", 11));
        ngayNhanPicker = new DatePicker();
        ngayNhanPicker.setStyle("-fx-border-radius: 5;");
        vbNgayNhan.getChildren().addAll(lbl5, ngayNhanPicker);

        // Ngày Trả
        VBox vbNgayTra = new VBox(5);
        Label lbl6 = new Label("Ngày Trả:");
        lbl6.setFont(Font.font("Segoe UI", 11));
        ngayTraPicker = new DatePicker();
        ngayTraPicker.setStyle("-fx-border-radius: 5;");
        vbNgayTra.getChildren().addAll(lbl6, ngayTraPicker);

        // Trạng Thái
        VBox vbStatus = new VBox(5);
        Label lbl7 = new Label("Trạng Thái:");
        lbl7.setFont(Font.font("Segoe UI", 11));
        trangThaiComboBox = new ComboBox<>();
        trangThaiComboBox.setItems(FXCollections.observableArrayList(
                "CHO_NHAN_PHONG", "DA_NHAN_PHONG", "DA_TRA_PHONG", "DA_HUY"
        ));
        trangThaiComboBox.setStyle("-fx-border-radius: 5;");
        vbStatus.getChildren().addAll(lbl7, trangThaiComboBox);

        // Tổng Tiền
        VBox vbTong = new VBox(5);
        Label lbl8 = new Label("Tổng Tiền (đ):");
        lbl8.setFont(Font.font("Segoe UI", 11));
        tongTienTextField = new TextField();
        tongTienTextField.setStyle("-fx-border-radius: 5;");
        vbTong.getChildren().addAll(lbl8, tongTienTextField);

        formBox.getChildren().addAll(lblDetail, vbMa, vbKH, vbPhong, vbNgayDat, vbNgayNhan, vbNgayTra, vbStatus, vbTong);

        // Nút bên phải
        VBox btnBox = new VBox(10);
        btnBox.setAlignment(Pos.TOP_CENTER);
        btnBox.setPrefWidth(150);

        btnCapNhat = new Button("✏️ Cập Nhật");
        btnCapNhat.setMaxWidth(Double.MAX_VALUE);
        btnCapNhat.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 5;");
        btnCapNhat.setOnAction(e -> updatePhieu());

        btnChiTiet = new Button("👁 Chi Tiết");
        btnChiTiet.setMaxWidth(Double.MAX_VALUE);
        btnChiTiet.setStyle("-fx-background-color: " + COLOR_WARNING + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 5;");
        btnChiTiet.setOnAction(e -> {
            PhieuDatPhongDTO selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) showPhieuDetailDialog(selected);
            else showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn phiếu!");
        });

        btnXoa = new Button("🗑 Xóa");
        btnXoa.setMaxWidth(Double.MAX_VALUE);
        btnXoa.setStyle("-fx-background-color: " + COLOR_DANGER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 5;");
        btnXoa.setOnAction(e -> deletePhieu());

        btnLamMoi = new Button("🔄 Làm Mới");
        btnLamMoi.setMaxWidth(Double.MAX_VALUE);
        btnLamMoi.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 5;");
        btnLamMoi.setOnAction(e -> {
            clearForm();
            refreshTable();
        });

        btnBox.getChildren().addAll(btnCapNhat, btnChiTiet, btnXoa, btnLamMoi);

        detailBox.getChildren().addAll(formBox, btnBox);
        return detailBox;
    }

    // --- HỖ TRỢ ---
    private void refreshTable() {
        try {
            phieuList = FXCollections.observableArrayList();
            List<PhieuDatPhongDTO> list = phieuDatPhongService.getAllPhieuDatPhong();
            if (list != null) {
                phieuList.addAll(list);
            }

            FilteredList<PhieuDatPhongDTO> filteredData = new FilteredList<>(phieuList, p -> true);
            searchField.textProperty().addListener((obs, oldVal, newVal) ->
                    filteredData.setPredicate(p ->
                            newVal.isEmpty() ||
                                    p.getMaPhieu().toLowerCase().contains(newVal.toLowerCase()) ||
                                    (p.getMaKhachHang() != null && p.getMaKhachHang().toLowerCase().contains(newVal.toLowerCase()))
                    )
            );

            SortedList<PhieuDatPhongDTO> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.setItems(sortedData);
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải dữ liệu: " + ex.getMessage());
        }
    }

    private void displayPhieuDetail(PhieuDatPhongDTO phieu) {
        if (phieu != null) {
            maPhieuTextField.setText(phieu.getMaPhieu() != null ? phieu.getMaPhieu() : "");
            maKhachHangTextField.setText(phieu.getMaKhachHang() != null ? phieu.getMaKhachHang() : "");
            maPhongTextField.setText(phieu.getMaPhong() != null ? phieu.getMaPhong() : "");
            if (phieu.getNgayDat() != null) ngayDatPicker.setValue(phieu.getNgayDat());
            if (phieu.getNgayNhan() != null) ngayNhanPicker.setValue(phieu.getNgayNhan());
            if (phieu.getNgayTra() != null) ngayTraPicker.setValue(phieu.getNgayTra());
            if (phieu.getTrangThai() != null) trangThaiComboBox.setValue(phieu.getTrangThai());
            tongTienTextField.setText(phieu.getTongTien() != null ? String.format("%,.0f", phieu.getTongTien()) : "0");
        }
    }

    private void clearForm() {
        maPhieuTextField.clear();
        maKhachHangTextField.clear();
        maPhongTextField.clear();
        ngayDatPicker.setValue(null);
        ngayNhanPicker.setValue(null);
        ngayTraPicker.setValue(null);
        trangThaiComboBox.setValue(null);
        tongTienTextField.clear();
        tableView.getSelectionModel().clearSelection();
    }

    private void updatePhieu() {
        PhieuDatPhongDTO selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn phiếu để cập nhật!");
            return;
        }

        try {
            selected.setTrangThai(trangThaiComboBox.getValue());
            selected.setNgayNhan(ngayNhanPicker.getValue());
            selected.setNgayTra(ngayTraPicker.getValue());
            if (!tongTienTextField.getText().isEmpty()) {
                selected.setTongTien(Double.parseDouble(tongTienTextField.getText().replace(",", "")));
            }

            phieuDatPhongService.updatePhieuDatPhong(selected);
            refreshTable();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật phiếu thành công!");
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể cập nhật: " + ex.getMessage());
        }
    }

    private void deletePhieu() {
        PhieuDatPhongDTO selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn phiếu để xóa!");
            return;
        }

        if (confirmDelete("Xóa phiếu " + selected.getMaPhieu() + "?")) {
            try {
                phieuDatPhongService.deletePhieuDatPhong(selected.getMaPhieu());
                refreshTable();
                clearForm();
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa phiếu thành công!");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa: " + ex.getMessage());
            }
        }
    }

    private void showAddPhieuDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thêm Phiếu");
        alert.setHeaderText("Chức năng Thêm Phiếu Đặt");
        alert.setContentText("Tính năng này sẽ được phát triển sau.\nHiện tại, vui lòng sử dụng chức năng 'Đặt & Nhận Phòng' để tạo phiếu mới.");
        alert.showAndWait();
    }

    private void showPhieuDetailDialog(PhieuDatPhongDTO phieu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi Tiết Phiếu");
        alert.setHeaderText("Phiếu " + phieu.getMaPhieu());
        alert.setContentText(
                "Khách: " + (phieu.getTenKhachHang() != null ? phieu.getTenKhachHang() : "N/A") + "\n" +
                        "Phòng: " + (phieu.getTenPhong() != null ? phieu.getTenPhong() : "N/A") + "\n" +
                        "Nhận: " + phieu.getNgayNhan() + "\n" +
                        "Trả: " + phieu.getNgayTra() + "\n" +
                        "Trạng thái: " + phieu.getTrangThai() + "\n" +
                        "Tổng tiền: " + String.format("%,.0f", phieu.getTongTien() != null ? phieu.getTongTien() : 0) + " đ"
        );
        alert.showAndWait();
    }

    private boolean confirmDelete(String msg) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type, msg);
        alert.setTitle(title);
        alert.showAndWait();
    }
}

