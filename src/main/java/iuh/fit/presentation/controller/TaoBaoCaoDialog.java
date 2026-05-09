package iuh.fit.presentation.controller;

import iuh.fit.core.dto.BaoCaoDTO;
import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.IBaoCaoService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaoBaoCaoDialog {

    private final IBaoCaoService baoCaoService;
    private final TaiKhoanDTO currentUser;
    private File selectedImageFile;
    private ImageView imagePreview;
    private Label lblImageStatus;
    private Button btnRemoveImage;

    private static final String IMAGE_UPLOAD_DIR = "images/reports/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public TaoBaoCaoDialog(IBaoCaoService baoCaoService, TaiKhoanDTO currentUser) {
        this.baoCaoService = baoCaoService;
        this.currentUser = currentUser;
    }

    public void showDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Gửi Báo Cáo & Yêu Cầu Hỗ Trợ");
        dialog.setResizable(false);

        VBox root = new VBox(15);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #ffffff;");

        // Header
        Label lblTitle = new Label("📝 GỬI BÁO CÁO CHO QUẢN LÝ");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        lblTitle.setTextFill(Color.web("#2563eb"));

        // Phân loại
        ComboBox<String> cbLoai = new ComboBox<>();
        cbLoai.getItems().addAll("Báo cáo Sự cố", "Báo cáo Tài chính (Lệch ca)", "Đề xuất / Yêu cầu", "Khác");
        cbLoai.setValue("Báo cáo Sự cố");
        cbLoai.setMaxWidth(Double.MAX_VALUE);
        cbLoai.setStyle("-fx-font-size: 14px; -fx-padding: 8; -fx-background-radius: 6; -fx-border-color: #cbd5e1;");

        // Tiêu đề
        TextField txtTieuDe = new TextField();
        txtTieuDe.setPromptText("Nhập tiêu đề ngắn gọn...");
        txtTieuDe.setStyle("-fx-padding: 10; -fx-font-size: 14px; -fx-background-radius: 6; -fx-border-color: #cbd5e1;");

        // Nội dung
        TextArea txtNoiDung = new TextArea();
        txtNoiDung.setPromptText("Mô tả chi tiết sự cố hoặc yêu cầu hỗ trợ...");
        txtNoiDung.setPrefRowCount(6);
        txtNoiDung.setWrapText(true);
        txtNoiDung.setStyle("-fx-padding: 10; -fx-font-size: 14px; -fx-background-radius: 6; -fx-border-color: #cbd5e1;");

        // Khu vực đính kèm ảnh
        HBox imageBox = new HBox(15);
        imageBox.setAlignment(Pos.CENTER_LEFT);

        Button btnChonAnh = new Button("📸 Chọn ảnh");
        btnChonAnh.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #1e293b; -fx-font-weight: bold; -fx-padding: 10 15; -fx-background-radius: 6; -fx-border-color: #cbd5e1; -fx-cursor: hand;");

        lblImageStatus = new Label("Chưa có ảnh đính kèm");
        lblImageStatus.setFont(Font.font("Segoe UI", 12));
        lblImageStatus.setTextFill(Color.web("#64748b"));

        imagePreview = new ImageView();
        imagePreview.setFitWidth(100);
        imagePreview.setFitHeight(100);
        imagePreview.setPreserveRatio(true);
        imagePreview.setVisible(false);

        btnRemoveImage = new Button("✖ Xóa ảnh");
        btnRemoveImage.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-padding: 8 12; -fx-background-radius: 6; -fx-cursor: hand;");
        btnRemoveImage.setVisible(false);
        btnRemoveImage.setOnAction(e -> {
            selectedImageFile = null;
            imagePreview.setImage(null);
            imagePreview.setVisible(false);
            btnRemoveImage.setVisible(false);
            lblImageStatus.setText("Chưa có ảnh đính kèm");
            lblImageStatus.setTextFill(Color.web("#64748b"));
        });

        btnChonAnh.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ảnh (jpg, png, jpeg)", "*.jpg", "*.jpeg", "*.png"));
            File file = fileChooser.showOpenDialog(dialog);
            if (file != null) {
                if (file.length() > MAX_FILE_SIZE) {
                    showAlert(Alert.AlertType.WARNING, "Ảnh quá lớn", "Vui lòng chọn ảnh dưới 5MB.");
                    return;
                }
                selectedImageFile = file;
                try {
                    Image image = new Image(file.toURI().toString(), 100, 100, true, true);
                    imagePreview.setImage(image);
                    imagePreview.setVisible(true);
                    btnRemoveImage.setVisible(true);
                    lblImageStatus.setText(file.getName());
                    lblImageStatus.setTextFill(Color.web("#10b981"));
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi tải ảnh", "Không thể hiển thị ảnh đã chọn.");
                }
            }
        });

        imageBox.getChildren().addAll(btnChonAnh, imagePreview, btnRemoveImage, lblImageStatus);

        // Nút gửi
        Button btnGui = new Button("📤 GỬI BÁO CÁO");
        btnGui.setMaxWidth(Double.MAX_VALUE);
        btnGui.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 12; -fx-background-radius: 8; -fx-cursor: hand;");

        // Xử lý gửi báo cáo
        btnGui.setOnAction(e -> {
            if (txtTieuDe.getText().trim().isEmpty() || txtNoiDung.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập Tiêu đề và Nội dung báo cáo.");
                return;
            }

            try {
                BaoCaoDTO bc = new BaoCaoDTO();
                bc.setMaBaoCao("BC_" + System.currentTimeMillis());
                bc.setMaNhanVien(currentUser.getMaNhanVien());
                bc.setHoTenNhanVien(currentUser.getHoTenNhanVien());
                bc.setPhanLoai(cbLoai.getValue());
                bc.setTieuDe(txtTieuDe.getText().trim());
                bc.setNoiDung(txtNoiDung.getText().trim());
                bc.setNgayTao(LocalDateTime.now());
                bc.setTrangThai("CHUA_XEM");

                // Xử lý lưu ảnh nếu có
                if (selectedImageFile != null) {
                    String savedImagePath = saveImageToProjectDir(selectedImageFile);
                    bc.setHinhAnh(savedImagePath);
                }

                baoCaoService.save(bc);
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Báo cáo đã được gửi đến quản lý!");
                dialog.close();
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi gửi báo cáo", ex.getMessage());
                ex.printStackTrace();
            }
        });

        root.getChildren().addAll(
                lblTitle,
                new Label("Phân loại:"),
                cbLoai,
                new Label("Tiêu đề:"),
                txtTieuDe,
                new Label("Nội dung chi tiết:"),
                txtNoiDung,
                new Label("Đính kèm ảnh minh chứng:"),
                imageBox,
                btnGui
        );

        Scene scene = new Scene(root, 520, 620);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    /**
     * Sao chép file ảnh vào thư mục images/reports/ trong project.
     * Trả về đường dẫn tương đối để lưu vào database.
     */
    private String saveImageToProjectDir(File sourceFile) throws IOException {
        Path targetDir = Paths.get(IMAGE_UPLOAD_DIR);
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        String fileName = System.currentTimeMillis() + "_" + sourceFile.getName();
        Path targetFile = targetDir.resolve(fileName);

        Files.copy(sourceFile.toPath(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        return IMAGE_UPLOAD_DIR + fileName; // Đường dẫn tương đối
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}