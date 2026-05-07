package iuh.fit.presentation.controller;

import iuh.fit.core.dto.DichVuDTO;
import iuh.fit.core.service.IDichVuService;
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

import java.util.List;

/**
 * Controller: Gọi Dịch Vụ (Call Services)
 * Hiển thị danh sách dịch vụ có sẵn, cho phép đặt dịch vụ cho phòng
 */
public class QuanLyGoiDichVuController {

    private TableView<DichVuDTO> tableView;
    private TextField searchField;
//    private TextField maDichVuTextField, tenTextField, giaTextField, moTaTextArea;
    private Button btnThem, btnChiTiet, btnXoa, btnLamMoi;
    private IDichVuService dichVuService;
    private ObservableList<DichVuDTO> dichVuList;

    private TextField maDichVuTextField, tenTextField, giaTextField;
    private TextArea moTaTextArea;

    // --- BẢNG MÀU ---
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_DANGER = "#ef4444";
    private final String COLOR_WARNING = "#f59e0b";
    private final String COLOR_TEXT_MAIN = "#1e293b";
    private final String COLOR_BORDER = "#cbd5e1";
    private final String COLOR_BG_LIGHT = "#f1f5f9";

    public QuanLyGoiDichVuController(IDichVuService dichVuService) {
        this.dichVuService = dichVuService;
    }

    public VBox createQuanLyGoiDichVuView() {
        VBox mainVBox = new VBox(20);
        mainVBox.setStyle("-fx-background-color: " + COLOR_BG_LIGHT + "; -fx-padding: 25;");

        // 1. TIÊU ĐỀ
        Label lblTitle = new Label("📋 GỌI DỊCH VỤ");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        // 2. THANH TÌM KIẾM & BỘ LỌC
        HBox filterBox = createFilterBox();

        // 3. BẢNG DỊCH VỤ
        HBox tableBox = createTableBox();

        // 4. FORM CHI TIẾT DỊCH VỤ
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
        searchField.setPromptText("Tìm theo tên hoặc mã dịch vụ...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-border-radius: 5; -fx-padding: 8;");

        Button btnSearch = new Button("Tìm");
        btnSearch.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 5; -fx-cursor: hand;");
        btnSearch.setOnAction(e -> refreshTable());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnThem = new Button("➕ Thêm Dịch Vụ");
        btnThem.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;");
        btnThem.setOnAction(e -> showAddServiceDialog());

        filterBox.getChildren().addAll(lblSearch, searchField, btnSearch, spacer, btnThem);
        return filterBox;
    }

    // --- BẢNG DỊCH VỤ ---
    private HBox createTableBox() {
        HBox tableBox = new HBox();
        tableBox.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-border-color: " + COLOR_BORDER + ";");
        tableBox.setPadding(new Insets(15));

        // Tạo TableView
        tableView = new TableView<>();
        tableView.setStyle("-fx-font-size: 12px;");

        TableColumn<DichVuDTO, String> colMa = new TableColumn<>("Mã Dịch Vụ");
        colMa.setCellValueFactory(new PropertyValueFactory<>("maDichVu"));
        colMa.setPrefWidth(120);

        TableColumn<DichVuDTO, String> colTen = new TableColumn<>("Tên Dịch Vụ");
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenDichVu"));
        colTen.setPrefWidth(250);

        TableColumn<DichVuDTO, Double> colGia = new TableColumn<>("Giá (đ)");
        colGia.setCellValueFactory(new PropertyValueFactory<>("giaTien"));
        colGia.setPrefWidth(150);

        TableColumn<DichVuDTO, String> colMoTa = new TableColumn<>("Mô Tả");
        colMoTa.setCellValueFactory(new PropertyValueFactory<>("moTa"));
        colMoTa.setPrefWidth(350);

        tableView.getColumns().addAll(colMa, colTen, colGia, colMoTa);

        // Load dữ liệu
        refreshTable();

        tableView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                DichVuDTO selected = tableView.getSelectionModel().getSelectedItem();
                if (selected != null) displayServiceDetail(selected);
            }
        });

        tableBox.getChildren().add(tableView);
        HBox.setHgrow(tableView, Priority.ALWAYS);
        return tableBox;
    }

    // --- FORM CHI TIẾT DỊCH VỤ ---
    private HBox createDetailBox() {
        HBox detailBox = new HBox(20);
        detailBox.setStyle("-fx-background-color: white; -fx-border-radius: 8; -fx-border-color: " + COLOR_BORDER + ";");
        detailBox.setPadding(new Insets(20));

        // Form bên trái
        VBox formBox = new VBox(12);
        formBox.setPrefWidth(400);

        Label lblDetail = new Label("📝 CHI TIẾT DỊCH VỤ");
        lblDetail.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        // Mã Dịch Vụ
        VBox vbMa = new VBox(5);
        Label lbl1 = new Label("Mã Dịch Vụ:");
        lbl1.setFont(Font.font("Segoe UI", 11));
        maDichVuTextField = new TextField();
        maDichVuTextField.setEditable(false);
        maDichVuTextField.setStyle("-fx-control-inner-background: #f1f5f9; -fx-border-radius: 5;");
        vbMa.getChildren().addAll(lbl1, maDichVuTextField);

        // Tên Dịch Vụ
        VBox vbTen = new VBox(5);
        Label lbl2 = new Label("Tên Dịch Vụ:");
        lbl2.setFont(Font.font("Segoe UI", 11));
        tenTextField = new TextField();
        tenTextField.setStyle("-fx-border-radius: 5;");
        vbTen.getChildren().addAll(lbl2, tenTextField);

        // Giá Tiền
        VBox vbGia = new VBox(5);
        Label lbl3 = new Label("Giá (đ):");
        lbl3.setFont(Font.font("Segoe UI", 11));
        giaTextField = new TextField();
        giaTextField.setStyle("-fx-border-radius: 5;");
        vbGia.getChildren().addAll(lbl3, giaTextField);

        // Mô Tả
        VBox vbMoTa = new VBox(5);
        Label lbl4 = new Label("Mô Tả:");
        lbl4.setFont(Font.font("Segoe UI", 11));
        moTaTextArea = new TextArea();
        moTaTextArea.setWrapText(true);
        moTaTextArea.setPrefHeight(80);
        moTaTextArea.setStyle("-fx-border-radius: 5;");
        vbMoTa.getChildren().addAll(lbl4, moTaTextArea);

        formBox.getChildren().addAll(lblDetail, vbMa, vbTen, vbGia, vbMoTa);

        // Nút bên phải
        VBox btnBox = new VBox(10);
        btnBox.setAlignment(Pos.TOP_CENTER);
        btnBox.setPrefWidth(150);

        btnChiTiet = new Button("👁 Xem Chi Tiết");
        btnChiTiet.setMaxWidth(Double.MAX_VALUE);
        btnChiTiet.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 5;");
        btnChiTiet.setOnAction(e -> {
            DichVuDTO selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showServiceDetailDialog(selected);
            } else {
                showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn dịch vụ!");
            }
        });

        btnXoa = new Button("🗑 Xóa");
        btnXoa.setMaxWidth(Double.MAX_VALUE);
        btnXoa.setStyle("-fx-background-color: " + COLOR_DANGER + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 5;");
        btnXoa.setOnAction(e -> {
            DichVuDTO selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (confirmDelete("Xóa dịch vụ " + selected.getTenDichVu() + "?")) {
                    try {
                        dichVuService.deleteDichVu(selected.getMaDichVu());
                        refreshTable();
                        clearForm();
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa dịch vụ thành công!");
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa: " + ex.getMessage());
                    }
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn dịch vụ để xóa!");
            }
        });

        btnLamMoi = new Button("🔄 Làm Mới");
        btnLamMoi.setMaxWidth(Double.MAX_VALUE);
        btnLamMoi.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 5;");
        btnLamMoi.setOnAction(e -> {
            clearForm();
            refreshTable();
        });

        btnBox.getChildren().addAll(btnChiTiet, btnXoa, btnLamMoi);

        detailBox.getChildren().addAll(formBox, btnBox);
        return detailBox;
    }

    // --- HỖ TRỢ ---
    private void refreshTable() {
        try {
            dichVuList = FXCollections.observableArrayList();
            List<DichVuDTO> list = dichVuService.getAllDichVu();
            if (list != null) {
                dichVuList.addAll(list);
            }

            // Áp dụng filter tìm kiếm
            FilteredList<DichVuDTO> filteredData = new FilteredList<>(dichVuList, p -> true);
            searchField.textProperty().addListener((obs, oldVal, newVal) ->
                    filteredData.setPredicate(dv ->
                            newVal.isEmpty() ||
                                    dv.getMaDichVu().toLowerCase().contains(newVal.toLowerCase()) ||
                                    dv.getTenDichVu().toLowerCase().contains(newVal.toLowerCase())
                    )
            );

            SortedList<DichVuDTO> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tableView.comparatorProperty());
            tableView.setItems(sortedData);
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải dữ liệu: " + ex.getMessage());
        }
    }

    private void displayServiceDetail(DichVuDTO dichVu) {
        if (dichVu != null) {
            maDichVuTextField.setText(dichVu.getMaDichVu() != null ? dichVu.getMaDichVu() : "");
            tenTextField.setText(dichVu.getTenDichVu() != null ? dichVu.getTenDichVu() : "");
            giaTextField.setText(String.format("%,.0f", dichVu.getGiaTien()));
            moTaTextArea.setText(dichVu.getMoTa() != null ? dichVu.getMoTa() : "");
        }
    }

    private void clearForm() {
        maDichVuTextField.clear();
        tenTextField.clear();
        giaTextField.clear();
        moTaTextArea.clear();
        tableView.getSelectionModel().clearSelection();
    }

    private void showAddServiceDialog() {
        Dialog<DichVuDTO> dialog = new Dialog<>();
        dialog.setTitle("Thêm Dịch Vụ");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField tfTen = new TextField();
        tfTen.setPromptText("Tên dịch vụ");
        TextField tfGia = new TextField();
        tfGia.setPromptText("Giá");
        TextArea taDesc = new TextArea();
        taDesc.setPromptText("Mô tả");
        taDesc.setPrefHeight(100);

        grid.add(new Label("Tên Dịch Vụ:"), 0, 0);
        grid.add(tfTen, 1, 0);
        grid.add(new Label("Giá:"), 0, 1);
        grid.add(tfGia, 1, 1);
        grid.add(new Label("Mô Tả:"), 0, 2);
        grid.add(taDesc, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btnType -> {
            if (btnType == ButtonType.OK) {
                try {
                    DichVuDTO dv = new DichVuDTO();
                    dv.setTenDichVu(tfTen.getText());
                    dv.setGiaTien(Double.parseDouble(tfGia.getText()));
                    dv.setMoTa(taDesc.getText());
                    dichVuService.addDichVu(dv);
                    refreshTable();
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thêm dịch vụ thành công!");
                    return null;
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi thêm dịch vụ: " + ex.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showServiceDetailDialog(DichVuDTO dichVu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi Tiết Dịch Vụ");
        alert.setHeaderText(dichVu.getTenDichVu());
        alert.setContentText(
                "Mã: " + dichVu.getMaDichVu() + "\n" +
                        "Giá: " + String.format("%,.0f", dichVu.getGiaTien()) + " đ\n" +
                        "Mô Tả: " + (dichVu.getMoTa() != null ? dichVu.getMoTa() : "N/A")
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

