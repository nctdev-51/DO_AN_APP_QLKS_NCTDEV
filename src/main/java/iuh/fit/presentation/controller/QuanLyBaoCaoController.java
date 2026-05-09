package iuh.fit.presentation.controller;

import iuh.fit.core.dto.BaoCaoDTO;
import iuh.fit.core.service.IBaoCaoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class QuanLyBaoCaoController {
    private final IBaoCaoService baoCaoService;
    private TableView<BaoCaoDTO> table;
    private ObservableList<BaoCaoDTO> listBaoCao;

    public QuanLyBaoCaoController(IBaoCaoService baoCaoService) {
        this.baoCaoService = baoCaoService;
    }

    public VBox createView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f8fafc;");

        Label lblTitle = new Label("HỘP THƯ BÁO CÁO CỦA NHÂN VIÊN");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 28));
        lblTitle.setTextFill(Color.web("#0f172a"));

        // Bộ lọc
        HBox filterBox = new HBox(15);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: #e2e8f0;");

        ComboBox<String> cbTrangThai = new ComboBox<>(FXCollections.observableArrayList("Tất cả", "CHUA_XEM", "DA_XU_LY"));
        cbTrangThai.setValue("Tất cả");
        Button btnLoc = new Button("Lọc Dữ Liệu");
        btnLoc.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold;");
        filterBox.getChildren().addAll(new Label("Trạng thái:"), cbTrangThai, btnLoc);

        // Bảng dữ liệu
        table = new TableView<>();
        table.setStyle("-fx-font-size: 14px;");
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<BaoCaoDTO, String> colNgay = new TableColumn<>("Ngày Gửi");
        colNgay.setCellValueFactory(new PropertyValueFactory<>("ngayTao"));
        colNgay.setPrefWidth(150);

        TableColumn<BaoCaoDTO, String> colNV = new TableColumn<>("Nhân Viên");
        colNV.setCellValueFactory(new PropertyValueFactory<>("hoTenNhanVien"));
        colNV.setPrefWidth(150);

        TableColumn<BaoCaoDTO, String> colLoai = new TableColumn<>("Phân Loại");
        colLoai.setCellValueFactory(new PropertyValueFactory<>("phanLoai"));
        colLoai.setPrefWidth(150);

        TableColumn<BaoCaoDTO, String> colTieuDe = new TableColumn<>("Tiêu Đề");
        colTieuDe.setCellValueFactory(new PropertyValueFactory<>("tieuDe"));
        colTieuDe.setPrefWidth(300);

        TableColumn<BaoCaoDTO, String> colTrangThai = new TableColumn<>("Trạng Thái");
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTrangThai.setPrefWidth(150);

        table.getColumns().addAll(colNgay, colNV, colLoai, colTieuDe, colTrangThai);

        // Click đúp để đọc
        table.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null) {
                xemChiTiet(table.getSelectionModel().getSelectedItem());
            }
        });

        root.getChildren().addAll(lblTitle, filterBox, table);
        loadData();
        return root;
    }

    private void loadData() {
        try {
            List<BaoCaoDTO> data = baoCaoService.findAll(); // Lấy từ DB
            listBaoCao = FXCollections.observableArrayList(data);
            table.setItems(listBaoCao);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void xemChiTiet(BaoCaoDTO bc) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Xử lý báo cáo: " + bc.getMaBaoCao());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Hiển thị ảnh nếu có
        if (bc.getHinhAnh() != null) {
            ImageView iv = new ImageView(new Image("file:" + bc.getHinhAnh()));
            iv.setFitWidth(400); iv.setPreserveRatio(true);
            content.getChildren().add(new Label("Ảnh minh chứng:"));
            content.getChildren().add(iv);
        }

        TextArea txtPhanHoi = new TextArea();
        txtPhanHoi.setPromptText("Nhập chỉ đạo hoặc kết quả xử lý...");

        Button btnSave = new Button("Xác nhận đã xử lý");
        btnSave.setStyle("-fx-background-color: #10b981; -fx-text-fill: white;");
        btnSave.setOnAction(e -> {
            bc.setTrangThai("DA_XU_LY");
            bc.setPhanHoiQuanLy(txtPhanHoi.getText()); // Lưu feedback của sếp
            try {
                baoCaoService.update(bc);
                loadData();
                dialog.close();
            } catch (Exception ignored) {}
        });

        content.getChildren().addAll(new Label("Nội dung: " + bc.getNoiDung()), new Separator(), new Label("Phản hồi của Quản lý:"), txtPhanHoi, btnSave);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
}