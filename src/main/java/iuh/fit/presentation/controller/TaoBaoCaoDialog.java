package iuh.fit.presentation.controller;

import iuh.fit.core.dto.BaoCaoDTO;
import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.IBaoCaoService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDateTime;

public class TaoBaoCaoDialog {
    private final IBaoCaoService baoCaoService;
    private final TaiKhoanDTO currentUser;

    public TaoBaoCaoDialog(IBaoCaoService baoCaoService, TaiKhoanDTO currentUser) {
        this.baoCaoService = baoCaoService;
        this.currentUser = currentUser;
    }

    public void showDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Soạn Báo Cáo / Đề Xuất");

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: white;");

        Label lblTitle = new Label("GỬI BÁO CÁO CHO QUẢN LÝ");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 20));
        lblTitle.setTextFill(Color.web("#2563eb"));

        ComboBox<String> cbLoai = new ComboBox<>();
        cbLoai.getItems().addAll("Báo cáo Sự cố", "Báo cáo Tài chính (Lệch ca)", "Đề xuất / Yêu cầu", "Khác");
        cbLoai.setValue("Báo cáo Sự cố");
        cbLoai.setMaxWidth(Double.MAX_VALUE);

        TextField txtTieuDe = new TextField();
        txtTieuDe.setPromptText("Nhập tiêu đề ngắn gọn...");
        txtTieuDe.setStyle("-fx-padding: 10; -fx-font-size: 14px;");

        TextArea txtNoiDung = new TextArea();
        txtNoiDung.setPromptText("Mô tả chi tiết nội dung cần báo cáo...");
        txtNoiDung.setPrefRowCount(6);
        txtNoiDung.setStyle("-fx-padding: 10; -fx-font-size: 14px;");

        Button btnGui = new Button("📤 GỬI BÁO CÁO");
        btnGui.setMaxWidth(Double.MAX_VALUE);
        btnGui.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-cursor: hand; -fx-background-radius: 8;");

        btnGui.setOnAction(e -> {
            if (txtTieuDe.getText().isEmpty() || txtNoiDung.getText().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đủ Tiêu đề và Nội dung!").show();
                return;
            }
            try {
                BaoCaoDTO bc = new BaoCaoDTO();
                bc.setMaBaoCao("BC_" + System.currentTimeMillis());
                bc.setMaNhanVien(currentUser.getMaNhanVien());
                bc.setPhanLoai(cbLoai.getValue());
                bc.setTieuDe(txtTieuDe.getText());
                bc.setNoiDung(txtNoiDung.getText());
                bc.setNgayTao(LocalDateTime.now());
                bc.setTrangThai("CHUA_XEM");

                baoCaoService.save(bc); // Cần gọi service để lưu vào DB
                new Alert(Alert.AlertType.INFORMATION, "Đã gửi báo cáo thành công!").showAndWait();
                dialog.close();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Lỗi gửi báo cáo: " + ex.getMessage()).show();
            }
        });

        root.getChildren().addAll(lblTitle, new Label("Phân loại:"), cbLoai, new Label("Tiêu đề:"), txtTieuDe, new Label("Nội dung chi tiết:"), txtNoiDung, btnGui);
        dialog.setScene(new Scene(root, 450, 500));
        dialog.show();
    }
}