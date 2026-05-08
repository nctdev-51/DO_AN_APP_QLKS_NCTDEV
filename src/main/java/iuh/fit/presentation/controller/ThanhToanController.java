package iuh.fit.presentation.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.Locale;

public class ThanhToanController {

    private final double tongCanThanhToan;

    // Thuộc tính lưu trữ tiền của 2 phương thức nhập song song
    private final SimpleDoubleProperty tienMat = new SimpleDoubleProperty(0.0);
    private final SimpleDoubleProperty chuyenKhoan = new SimpleDoubleProperty(0.0);
    private final SimpleDoubleProperty tongKhachDua = new SimpleDoubleProperty(0.0);

    private final Locale vnLocale = new Locale("vi", "VN");
    private final NumberFormat formatter = NumberFormat.getCurrencyInstance(vnLocale);

    // --- BẢNG MÀU UI/UX HIỆN ĐẠI ---
    private final String COLOR_BG = "#f1f5f9";
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_DANGER = "#ef4444";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_BORDER = "#e2e8f0";
    private final String COLOR_TEXT_MUTED = "#64748b";

    public ThanhToanController(double tongCanThanhToan) {
        this.tongCanThanhToan = tongCanThanhToan;
        // Tổng khách đưa luôn bằng Tiền mặt + Chuyển khoản
        tongKhachDua.bind(tienMat.add(chuyenKhoan));
    }

    /**
     * @return Trả về phương thức (VD: "Tiền mặt", "Chuyển khoản", "Tiền mặt & Chuyển khoản"). Null nếu hủy.
     */
    public String showThanhToanDialog(Stage parentStage) {
        Stage stage = new Stage();
        stage.setTitle("Xác Nhận Thanh Toán Đa Phương Thức");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parentStage);
        stage.setResizable(false);

        HBox root = new HBox(25);
        root.setStyle("-fx-background-color: " + COLOR_BG + "; -fx-padding: 30;");

        // =====================================================================
        // 1. CỘT TRÁI: TÓM TẮT HÓA ĐƠN
        // =====================================================================
        VBox pnlSummary = new VBox(20);
        pnlSummary.setPrefWidth(350);
        pnlSummary.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 12;");
        applyShadow(pnlSummary);

        Label lblTitle = new Label("HÓA ĐƠN");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

        VBox infoBox = new VBox(10);
        infoBox.getChildren().addAll(
                createPriceRow("Tổng tiền hàng:", tongCanThanhToan),
                createPriceRow("Giảm giá:", 0.0),
                new Separator(),
                createTotalRow("CẦN THANH TOÁN:", tongCanThanhToan, COLOR_DANGER)
        );
        pnlSummary.getChildren().addAll(lblTitle, infoBox);

        // =====================================================================
        // 2. CỘT PHẢI: NHẬP TIỀN SONG SONG (CASH & BANK)
        // =====================================================================
        VBox pnlPayment = new VBox(20);
        pnlPayment.setPrefWidth(550);
        pnlPayment.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 12;");
        applyShadow(pnlPayment);

        Label lblPayTitle = new Label("SỐ TIỀN KHÁCH THANH TOÁN");
        lblPayTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblPayTitle.setTextFill(Color.web(COLOR_PRIMARY));

        // --- KHU VỰC NHẬP TIỀN MẶT ---
        VBox boxTienMat = new VBox(10);
        boxTienMat.setStyle("-fx-background-color: #f8fafc; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: #e2e8f0; -fx-border-radius: 8;");

        Label lblTienMat = new Label("💵 Tiền Mặt Khách Đưa:");
        lblTienMat.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        TextField txtTienMat = new TextField();
        txtTienMat.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #10b981; -fx-alignment: center-right; -fx-background-radius: 6;");
        txtTienMat.setText("0");
        setupMoneyField(txtTienMat, tienMat);

        // 👉 ĐÃ FIX: Phím tắt tiền mặt nhanh (Hiển thị đủ 9 mệnh giá thành lưới 3x3)
        GridPane gridCash = new GridPane();
        gridCash.setHgap(8); gridCash.setVgap(8);

        // Căn đều 3 cột
        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(33.33);
        gridCash.getColumnConstraints().addAll(cc, cc, cc);

        int[] menhGia = {500000, 200000, 100000, 50000, 20000, 10000, 5000, 2000, 1000};
        String[] labels = {"+ 500k", "+ 200k", "+ 100k", "+ 50k", "+ 20k", "+ 10k", "+ 5k", "+ 2k", "+ 1k"};

        for (int i = 0; i < menhGia.length; i++) {
            Button btn = createQuickMoneyBtn(labels[i], menhGia[i]);
            gridCash.add(btn, i % 3, i / 3); // Chia thành 3 cột, 3 hàng
        }

        boxTienMat.getChildren().addAll(lblTienMat, txtTienMat, gridCash);

        // --- KHU VỰC NHẬP CHUYỂN KHOẢN ---
        VBox boxChuyenKhoan = new VBox(10);
        boxChuyenKhoan.setStyle("-fx-background-color: #eff6ff; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: #bfdbfe; -fx-border-radius: 8;");

        HBox headerCK = new HBox();
        Label lblChuyenKhoan = new Label("🏦 Chuyển Khoản / Quẹt Thẻ:");
        lblChuyenKhoan.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        Button btnAutoFillRest = new Button("🔄 Chuyển khoản phần còn lại");
        btnAutoFillRest.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand; -fx-background-radius: 15;");
        btnAutoFillRest.setOnAction(e -> {
            double conThieu = tongCanThanhToan - tienMat.get();
            if (conThieu > 0) {
                chuyenKhoan.set(conThieu);
            }
        });
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        headerCK.getChildren().addAll(lblChuyenKhoan, sp, btnAutoFillRest);

        TextField txtChuyenKhoan = new TextField();
        txtChuyenKhoan.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2563eb; -fx-alignment: center-right; -fx-background-radius: 6;");
        txtChuyenKhoan.setText("0");
        setupMoneyField(txtChuyenKhoan, chuyenKhoan);

        HBox qrContainer = new HBox(15);
        qrContainer.setAlignment(Pos.CENTER_LEFT);

        Label lblQR = new Label("📱 Quét VietQR");
        lblQR.setAlignment(Pos.CENTER);
        lblQR.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        lblQR.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lblQR.setTextFill(Color.web("#94a3b8"));
        lblQR.setPrefSize(100, 100);
        lblQR.setStyle("-fx-border-color: #2563eb; -fx-border-width: 2; -fx-border-radius: 8; -fx-border-style: dashed; -fx-background-color: #ffffff; -fx-background-radius: 8;");

        VBox txnBox = new VBox(8);
        txnBox.setAlignment(Pos.CENTER_LEFT);
        Label lblTxn = new Label("Mã giao dịch (Không bắt buộc):");
        lblTxn.setFont(Font.font("Segoe UI", 12));
        lblTxn.setTextFill(Color.web(COLOR_TEXT_MUTED));

        TextField txtTxnCode = new TextField();
        txtTxnCode.setPromptText("VD: FT2405...");
        txtTxnCode.setStyle("-fx-padding: 8; -fx-background-radius: 6; -fx-border-color: #cbd5e1; -fx-border-radius: 6;");

        txnBox.getChildren().addAll(lblTxn, txtTxnCode);
        HBox.setHgrow(txnBox, Priority.ALWAYS);

        qrContainer.getChildren().addAll(lblQR, txnBox);

        boxChuyenKhoan.getChildren().addAll(headerCK, txtChuyenKhoan, qrContainer);

        // --- KẾT QUẢ: TỔNG KHÁCH ĐƯA & TIỀN THỪA ---
        VBox changeBox = new VBox(5);
        changeBox.setAlignment(Pos.CENTER);
        changeBox.setPadding(new Insets(15));
        changeBox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");

        Label lblChangeVal = new Label();
        lblChangeVal.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 22));
        lblChangeVal.textProperty().bind(Bindings.createStringBinding(() -> {
            double diff = tongKhachDua.get() - tongCanThanhToan;
            if (diff < 0) {
                lblChangeVal.setTextFill(Color.web(COLOR_DANGER));
                return "CÒN THIẾU: " + String.format("%,.0f đ", Math.abs(diff));
            } else {
                lblChangeVal.setTextFill(Color.web(COLOR_SUCCESS));
                return "TIỀN THỐI LẠI: " + String.format("%,.0f đ", diff);
            }
        }, tongKhachDua));
        changeBox.getChildren().add(lblChangeVal);

        // --- NÚT HOÀN TẤT ---
        final String[] finalPaymentMethod = {null};
        Button btnFinish = new Button("✅ ĐÓNG HÓA ĐƠN & HOÀN TẤT");
        btnFinish.setMaxWidth(Double.MAX_VALUE);
        btnFinish.setPrefHeight(50);
        btnFinish.setCursor(Cursor.HAND);
        btnFinish.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 8;");

        // KHÓA NÚT NẾU TỔNG KHÁCH ĐƯA CHƯA ĐỦ
        btnFinish.disableProperty().bind(tongKhachDua.lessThan(tongCanThanhToan));

        btnFinish.setOnAction(e -> {
            if (tienMat.get() > 0 && chuyenKhoan.get() > 0) {
                finalPaymentMethod[0] = "Tiền mặt & Chuyển khoản";
            } else if (chuyenKhoan.get() > 0) {
                finalPaymentMethod[0] = "Chuyển khoản";
            } else {
                finalPaymentMethod[0] = "Tiền mặt";
            }
            stage.close();
        });

        // Nút Xóa trắng
        Button btnClear = new Button("✖ Làm lại từ đầu");
        btnClear.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-cursor: hand; -fx-font-weight: bold;");
        btnClear.setOnAction(e -> { tienMat.set(0); chuyenKhoan.set(0); });

        pnlPayment.getChildren().addAll(lblPayTitle, boxTienMat, boxChuyenKhoan, changeBox, btnFinish, btnClear);

        root.getChildren().addAll(pnlSummary, pnlPayment);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();

        return finalPaymentMethod[0];
    }

    // =========================================================================
    // HELPER METHODS: ĐỒNG BỘ TEXTFIELD VỚI DOUBLE PROPERTY
    // =========================================================================
    private void setupMoneyField(TextField textField, SimpleDoubleProperty property) {
        textField.textProperty().addListener((obs, oldV, newV) -> {
            if (textField.isFocused()) {
                try {
                    String cleanStr = newV.replaceAll("[^\\d]", "");
                    double val = cleanStr.isEmpty() ? 0 : Double.parseDouble(cleanStr);
                    property.set(val);
                } catch (Exception ignored) {}
            }
        });

        textField.focusedProperty().addListener((obs, oldV, isFocused) -> {
            if (!isFocused) {
                textField.setText(String.format("%,.0f", property.get()));
            }
        });

        property.addListener((obs, oldV, newV) -> {
            if (!textField.isFocused()) {
                textField.setText(String.format("%,.0f", newV.doubleValue()));
            }
        });
    }

    private Button createQuickMoneyBtn(String label, double value) {
        Button btn = new Button(label);
        btn.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(btn, Priority.ALWAYS);
        btn.setCursor(Cursor.HAND);
        btn.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 6; -fx-border-radius: 6; -fx-padding: 8;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 6;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: white; -fx-border-color: #cbd5e1; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 6; -fx-border-radius: 6; -fx-padding: 8;"));

        btn.setOnAction(e -> {
            tienMat.set(tienMat.get() + value);
        });
        return btn;
    }

    private HBox createPriceRow(String label, double value) {
        HBox row = new HBox();
        Label lblL = new Label(label); lblL.setTextFill(Color.web("#64748b"));
        Label lblR = new Label(formatter.format(value)); lblR.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS);
        row.getChildren().addAll(lblL, s, lblR);
        return row;
    }

    private HBox createTotalRow(String label, double value, String colorHex) {
        HBox row = new HBox();
        Label lblL = new Label(label); lblL.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        Label lblR = new Label(formatter.format(value)); lblR.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 18)); lblR.setTextFill(Color.web(colorHex));
        Region s = new Region(); HBox.setHgrow(s, Priority.ALWAYS);
        row.getChildren().addAll(lblL, s, lblR);
        return row;
    }

    private void applyShadow(VBox box) {
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#000000", 0.05));
        ds.setRadius(10); ds.setOffsetY(3);
        box.setEffect(ds);
    }
}