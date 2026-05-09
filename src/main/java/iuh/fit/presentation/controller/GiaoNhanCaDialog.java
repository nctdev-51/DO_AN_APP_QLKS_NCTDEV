package iuh.fit.presentation.controller;

import iuh.fit.core.dto.LichSuCaLamViecDTO;
import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.IGiaoCaService;
import iuh.fit.core.service.IYeuCauPheDuyetService;
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
import java.time.format.DateTimeFormatter;

public class GiaoNhanCaDialog {
    private final IGiaoCaService giaoCaService;
    private final IYeuCauPheDuyetService yeuCauService; // thêm
    private final TaiKhoanDTO currentUser;

    public GiaoNhanCaDialog(IGiaoCaService giaoCaService,
                            IYeuCauPheDuyetService yeuCauService,
                            TaiKhoanDTO currentUser) {
        this.giaoCaService = giaoCaService;
        this.yeuCauService = yeuCauService;
        this.currentUser = currentUser;
    }

    // =========================================
    // HÀM TẠO BÀN PHÍM ẢO (NUMPAD) CHUẨN POS
    // =========================================
    private GridPane createPOSNumpad(TextField targetField) {
        GridPane pad = new GridPane();
        pad.setHgap(8); pad.setVgap(8);
        pad.setAlignment(Pos.CENTER);

        String btnStyle = "-fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: #f1f5f9; -fx-text-fill: #1e293b; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;";
        String btnActionStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #e2e8f0; -fx-text-fill: #ef4444; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;";
        String btnQuickStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #dbeafe; -fx-text-fill: #0284c7; -fx-border-color: #bae6fd; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;";

        // Phím số
        String[][] keys = {
                {"7", "8", "9"},
                {"4", "5", "6"},
                {"1", "2", "3"},
                {"C", "0", "000"}
        };

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 3; col++) {
                String key = keys[row][col];
                Button btn = new Button(key);
                btn.setPrefSize(70, 60);
                if (key.equals("C")) {
                    btn.setStyle(btnActionStyle);
                    btn.setOnAction(e -> targetField.setText(""));
                } else {
                    btn.setStyle(btnStyle);
                    btn.setOnAction(e -> appendToField(targetField, key));
                }
                pad.add(btn, col, row);
            }
        }

        // Cột phím điền nhanh (Quick amounts)
        VBox quickBox = new VBox(8);
        Button btn500k = new Button("+ 500K"); btn500k.setPrefSize(90, 60); btn500k.setStyle(btnQuickStyle);
        Button btn1M = new Button("+ 1 Tr"); btn1M.setPrefSize(90, 60); btn1M.setStyle(btnQuickStyle);
        Button btn2M = new Button("+ 2 Tr"); btn2M.setPrefSize(90, 60); btn2M.setStyle(btnQuickStyle);
        Button btnDel = new Button("⌫ Xóa"); btnDel.setPrefSize(90, 60); btnDel.setStyle(btnActionStyle);

        btn500k.setOnAction(e -> addAmount(targetField, 500000));
        btn1M.setOnAction(e -> addAmount(targetField, 1000000));
        btn2M.setOnAction(e -> addAmount(targetField, 2000000));
        btnDel.setOnAction(e -> {
            String text = targetField.getText().replace(",", "");
            if (text.length() > 0) {
                text = text.substring(0, text.length() - 1);
                formatAndSet(targetField, text);
            }
        });

        quickBox.getChildren().addAll(btn500k, btn1M, btn2M, btnDel);
        pad.add(quickBox, 3, 0, 1, 4);

        return pad;
    }

    private void appendToField(TextField field, String val) {
        String current = field.getText().replace(",", "");
        if (current.equals("0")) current = "";
        formatAndSet(field, current + val);
    }

    private void addAmount(TextField field, long amount) {
        String current = field.getText().replace(",", "");
        long currentVal = current.isEmpty() ? 0 : Long.parseLong(current);
        formatAndSet(field, String.valueOf(currentVal + amount));
    }

    private void formatAndSet(TextField field, String rawValue) {
        if (rawValue.isEmpty()) { field.setText(""); return; }
        try {
            long val = Long.parseLong(rawValue);
            field.setText(String.format("%,d", val));
        } catch (Exception ignored) {}
    }

    // ==========================================
    // 1. DIALOG NHẬN CA (Gọi khi mới đăng nhập)
    // ==========================================
    public boolean showNhanCaDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Xác Nhận Vào Ca");

        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(25));
        mainLayout.setStyle("-fx-background-color: white;");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPrefWidth(350);

        Label lblTitle = new Label("👋 CHÀO MỪNG VÀO CA");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 24));
        lblTitle.setTextFill(Color.web("#2563eb"));

        Label lblInfo = new Label("Nhân viên: " + currentUser.getHoTenNhanVien() + "\nGiờ hiện tại: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lblInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b; -fx-alignment: center; -fx-text-alignment: center;");

        VBox boxTien = new VBox(5);
        Label lTien = new Label("Tiền mặt nhận bàn giao (Két đầu ca):");
        lTien.setStyle("-fx-font-weight: bold;");
        TextField txtTienDauCa = new TextField();
        txtTienDauCa.setPromptText("Sử dụng bàn phím bên phải...");
        txtTienDauCa.setStyle("-fx-font-size: 20px; -fx-padding: 10; -fx-font-weight: bold; -fx-alignment: center-right; -fx-border-color: #3b82f6; -fx-border-radius: 8;");
        txtTienDauCa.setEditable(false);
        boxTien.getChildren().addAll(lTien, txtTienDauCa);

        Button btnNhanCa = new Button("XÁC NHẬN NHẬN CA");
        btnNhanCa.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12 30; -fx-background-radius: 8; -fx-cursor: hand;");
        btnNhanCa.setMaxWidth(Double.MAX_VALUE);

        final boolean[] isSuccess = {false};

        btnNhanCa.setOnAction(e -> {
            try {
                if(txtTienDauCa.getText().isEmpty()) throw new NumberFormatException();
                double tien = Double.parseDouble(txtTienDauCa.getText().replace(",", ""));

                // Gọi hàm nhận ca bình thường (isManagerOverride = false)
                giaoCaService.nhanCa(currentUser.getMaNhanVien(), tien, false);

                new Alert(Alert.AlertType.INFORMATION, "Nhận ca thành công! Chúc bạn làm việc hiệu quả.").showAndWait();
                isSuccess[0] = true;
                dialog.close();
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Vui lòng nhập số tiền hợp lệ!").show();
            } catch (Exception ex) {
                // 🚀 ĐÃ FIX: BẮT LỖI ĐẾN TRỄ VÀ HIỂN THỊ POPUP XIN QUYỀN
                if (ex.getMessage().contains("ĐẾN TRỄ")) {
                    double tien = Double.parseDouble(txtTienDauCa.getText().replace(",", ""));
                    xuLyDenTre(tien, dialog, isSuccess);
                } else {
                    new Alert(Alert.AlertType.WARNING, ex.getMessage()).show();
                }
            }
        });

        root.getChildren().addAll(lblTitle, lblInfo, boxTien, btnNhanCa);
        mainLayout.getChildren().addAll(root, new Separator(javafx.geometry.Orientation.VERTICAL), createPOSNumpad(txtTienDauCa));

        dialog.setScene(new Scene(mainLayout, 750, 400));
        dialog.showAndWait();

        return isSuccess[0];
    }

    // =========================================================================
    // 🛠️ HÀM XỬ LÝ NGHIỆP VỤ ĐẾN TRỄ (XIN CẤP PHÉP OVERRIDE)
    // =========================================================================
    private void xuLyDenTre(double tienDauCa, Stage parentDialog, boolean[] isSuccess) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hệ thống khóa tự động");
        alert.setHeaderText("⛔ BẠN ĐÃ ĐẾN TRỄ QUÁ GIỜ QUY ĐỊNH!");
        alert.setContentText("Hệ thống đã khóa ca làm việc. Bạn không thể tự ý vào ca.\n\nVui lòng chọn phương án giải quyết:");

        ButtonType btnQuanLyDuyetTaiCho = new ButtonType("Quản lý mở khóa tại máy");
        ButtonType btnXinPhepTuXa = new ButtonType("Gửi yêu cầu mạng (Client/Server)");
        ButtonType btnHuy = new ButtonType("Hủy bỏ", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnQuanLyDuyetTaiCho, btnXinPhepTuXa, btnHuy);

        alert.showAndWait().ifPresent(type -> {
            if (type == btnQuanLyDuyetTaiCho) {
                // 1. NGHIỆP VỤ MỞ KHÓA TẠI CHỖ (LOCAL OVERRIDE)
                TextInputDialog passDialog = new TextInputDialog();
                passDialog.setTitle("Xác thực Quản lý");
                passDialog.setHeaderText("Yêu cầu Quản lý nhập Mật khẩu cấp phép (VD: 123)");
                passDialog.setContentText("Mật khẩu Quản lý:");

                passDialog.showAndWait().ifPresent(pass -> {
                    // TODO: Bạn có thể gọi AuthenticationService ở đây để check mật khẩu admin
                    if (pass.equals("123")) {
                        try {
                            // Cấp quyền isManagerOverride = true để bypass logic check giờ
                            giaoCaService.nhanCa(currentUser.getMaNhanVien(), tienDauCa, true);
                            new Alert(Alert.AlertType.INFORMATION, "Quản lý đã mở khóa! Nhận ca thành công.").showAndWait();
                            isSuccess[0] = true;
                            parentDialog.close();
                        } catch (Exception ex) {
                            new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
                        }
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Mật khẩu quản lý không chính xác!").show();
                    }
                });

            } else if (type == btnXinPhepTuXa) {
                TextInputDialog lyDoDialog = new TextInputDialog();
                lyDoDialog.setTitle("Xin phép Quản lý từ xa");
                lyDoDialog.setHeaderText("Gửi yêu cầu xin vào ca trễ.");
                lyDoDialog.setContentText("Nhập lý do (Kẹt xe, Hỏng xe...):");

                lyDoDialog.showAndWait().ifPresent(lyDo -> {
                    try {
                        yeuCauService.taoYeuCau(
                                currentUser.getMaNhanVien(),
                                currentUser.getHoTenNhanVien(),
                                tienDauCa,
                                lyDo
                        );
                        new Alert(Alert.AlertType.INFORMATION, "Đã gửi yêu cầu chờ quản lý duyệt.\nVui lòng thông báo cho quản lý.").showAndWait();
                    } catch (Exception ex) {
                        new Alert(Alert.AlertType.ERROR, "Lỗi gửi yêu cầu: " + ex.getMessage()).show();
                    }
                });
            }
        });
    }

    // ==========================================
    // 2. DIALOG BÀN GIAO CA (Lúc ra về)
    // ==========================================
    public boolean showGiaoCaDialog(LichSuCaLamViecDTO caHienTai) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Bàn Giao Kết Thúc Ca");

        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(25));
        mainLayout.setStyle("-fx-background-color: white;");

        VBox root = new VBox(15);
        root.setPrefWidth(380);

        Label lblTitle = new Label("KẾT TOÁN BÀN GIAO CA");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 22));
        lblTitle.setTextFill(Color.web("#ef4444"));

        GridPane grid = new GridPane();
        grid.setVgap(10); grid.setHgap(15);
        grid.setStyle("-fx-background-color: #f8fafc; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: #e2e8f0;");

        Label vDauCa = new Label(String.format("%,.0f đ", caHienTai.getTienDauCa())); vDauCa.setStyle("-fx-font-weight: bold;");
        Label vThu = new Label(String.format("+ %,.0f đ", caHienTai.getTongThuTrongCa())); vThu.setStyle("-fx-font-weight: bold; -fx-text-fill: #10b981;");
        double tienLyThuyet = caHienTai.getTienDauCa() + caHienTai.getTongThuTrongCa();
        Label lblLyThuyet = new Label(String.format("%,.0f đ", tienLyThuyet));
        lblLyThuyet.setStyle("-fx-font-weight: 900; -fx-text-fill: #2563eb; -fx-font-size: 16px;");

        grid.addRow(0, new Label("Tiền đầu ca:"), vDauCa);
        grid.addRow(1, new Label("Hệ thống thu:"), vThu);
        grid.addRow(2, new Label("TỔNG PHẢI CÓ:"), lblLyThuyet);

        VBox boxInput = new VBox(10);
        Label lTien = new Label("Tiền mặt đếm thực tế trong két:"); lTien.setStyle("-fx-font-weight: bold;");
        TextField txtTienThucTe = new TextField();
        txtTienThucTe.setPromptText("Sử dụng numpad...");
        txtTienThucTe.setEditable(false);
        txtTienThucTe.setStyle("-fx-font-size: 20px; -fx-padding: 10; -fx-font-weight: bold; -fx-alignment: center-right; -fx-border-color: #ef4444; -fx-border-radius: 5;");

        TextArea txtGhiChu = new TextArea();
        txtGhiChu.setPromptText("Nhập ghi chú (Bắt buộc nếu tiền lệch so với hệ thống)...");
        txtGhiChu.setPrefRowCount(3);

        boxInput.getChildren().addAll(lTien, txtTienThucTe, txtGhiChu);

        Button btnChot = new Button("CHỐT SỔ & ĐĂNG XUẤT");
        btnChot.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 8; -fx-cursor: hand;");
        btnChot.setMaxWidth(Double.MAX_VALUE);

        final boolean[] isLoggedOut = {false};

        btnChot.setOnAction(e -> {
            try {
                if(txtTienThucTe.getText().isEmpty()) throw new NumberFormatException();
                double thucTe = Double.parseDouble(txtTienThucTe.getText().replace(",", ""));
                giaoCaService.giaoCa(caHienTai.getMaLichSu(), thucTe, txtGhiChu.getText());

                new Alert(Alert.AlertType.INFORMATION, "Bàn giao ca thành công! Hệ thống sẽ tự động đăng xuất.").showAndWait();
                isLoggedOut[0] = true;
                dialog.close();
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Vui lòng nhập số tiền thực tế!").show();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.WARNING, ex.getMessage()).show();
            }
        });

        root.getChildren().addAll(lblTitle, grid, boxInput, btnChot);

        mainLayout.getChildren().addAll(root, new Separator(javafx.geometry.Orientation.VERTICAL), createPOSNumpad(txtTienThucTe));

        dialog.setScene(new Scene(mainLayout, 800, 520));
        dialog.showAndWait();

        return isLoggedOut[0];
    }
}