package iuh.fit.presentation.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Consumer;

public class ThanhToanController {

    private final double tongCanThanhToan;

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
        tongKhachDua.bind(tienMat.add(chuyenKhoan));
    }

    // =========================================================================
    // KỸ THUẬT SPA: NHÚNG GIAO DIỆN THANH TOÁN VÀO KHUNG DƯỚI BẰNG CALLBACK
    // =========================================================================
    public void showThanhToanDialog(Stage sourceStage, Consumer<String> onPaymentComplete) {
        Stage mainStage = sourceStage;
        if (sourceStage != null && sourceStage.getOwner() instanceof Stage) {
            mainStage = (Stage) sourceStage.getOwner();
        }

        final Runnable[] closeActionHolder = new Runnable[1];
        VBox mainLayout = createMainLayout(() -> closeActionHolder[0].run(), onPaymentComplete);

        // Nhúng đè layer vào khung (StackPane)
        if (mainStage != null && mainStage.getScene() != null) {
            javafx.scene.Parent root = mainStage.getScene().getRoot();
            if (root instanceof BorderPane borderPane) {
                if (borderPane.getCenter() instanceof StackPane contentArea) {

                    closeActionHolder[0] = () -> {
                        contentArea.getChildren().removeIf(n -> n.getId() != null && n.getId().equals("THANH_TOAN_LAYER"));
                    };

                    ScrollPane scrollWrapper = new ScrollPane(mainLayout);
                    scrollWrapper.setId("THANH_TOAN_LAYER");
                    scrollWrapper.setFitToWidth(true);
                    scrollWrapper.setFitToHeight(true);
                    scrollWrapper.setStyle("-fx-background-color: " + COLOR_BG + "; -fx-control-inner-background: " + COLOR_BG + "; -fx-border-color: transparent;");

                    contentArea.getChildren().add(scrollWrapper);
                    return;
                }
            }
        }

        // Fallback mở Popup nếu không tìm thấy khung StackPane
        Stage popupStage = new Stage();
        popupStage.setTitle("Cổng Thanh Toán / Payment Gateway");
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(sourceStage);
        popupStage.setWidth(1000);
        popupStage.setHeight(750);

        closeActionHolder[0] = () -> popupStage.close();

        ScrollPane sp = new ScrollPane(mainLayout);
        sp.setFitToWidth(true);
        popupStage.setScene(new Scene(sp));
        popupStage.centerOnScreen();
        popupStage.show();
    }

    private VBox createMainLayout(Runnable closeAction, Consumer<String> onPaymentComplete) {
        VBox root = new VBox(25);
        root.setStyle("-fx-background-color: " + COLOR_BG + "; -fx-padding: 30;");

        // --- HEADER ---
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(5);
        Label lblTitleMain = new Label("CỔNG THANH TOÁN (PAYMENT GATEWAY)");
        lblTitleMain.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 26));
        lblTitleMain.setTextFill(Color.web(COLOR_TEXT_MAIN));
        Label lblSubTitle = new Label("Vui lòng hoàn tất thanh toán trước khi đóng hóa đơn.");
        lblSubTitle.setFont(Font.font("Segoe UI", 14));
        lblSubTitle.setTextFill(Color.web(COLOR_TEXT_MUTED));
        titleBox.getChildren().addAll(lblTitleMain, lblSubTitle);

        Region spTop = new Region(); HBox.setHgrow(spTop, Priority.ALWAYS);
        Button btnClose = new Button("✖ Hủy Thanh Toán");
        btnClose.setCursor(Cursor.HAND);
        btnClose.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 8;");
        btnClose.setOnAction(e -> {
            closeAction.run();
            onPaymentComplete.accept(null); // Trả về null báo hiệu Hủy
        });

        headerBox.getChildren().addAll(titleBox, spTop, btnClose);

        HBox bodyContainer = new HBox(30);

        // =====================================================================
        // CỘT TRÁI: TÓM TẮT HÓA ĐƠN
        // =====================================================================
        VBox pnlSummary = new VBox(20);
        pnlSummary.setPrefWidth(380);
        pnlSummary.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 12;");
        applyShadow(pnlSummary);

        Label lblTitle = new Label("TÓM TẮT GIAO DỊCH");
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BLACK, 18));
        lblTitle.setTextFill(Color.web(COLOR_TEXT_MAIN));

        VBox infoBox = new VBox(12);
        infoBox.setStyle("-fx-background-color: #f8fafc; -fx-padding: 20; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10;");
        infoBox.getChildren().addAll(
                createPriceRow("Tổng tiền hàng:", tongCanThanhToan),
                createPriceRow("Giảm giá / Voucher:", 0.0),
                new Separator(),
                createTotalRow("CẦN THANH TOÁN", tongCanThanhToan, COLOR_DANGER)
        );

        // KẾT QUẢ TIỀN THỪA / THIẾU
        VBox changeBox = new VBox(5);
        changeBox.setAlignment(Pos.CENTER);
        changeBox.setPadding(new Insets(20));
        changeBox.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 10; -fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-border-width: 2;");

        Label lblStatusTitle = new Label("TRẠNG THÁI GIAO DỊCH");
        lblStatusTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        lblStatusTitle.setTextFill(Color.web(COLOR_TEXT_MUTED));

        Label lblChangeVal = new Label();
        lblChangeVal.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 22));
        lblChangeVal.textProperty().bind(Bindings.createStringBinding(() -> {
            double diff = tongKhachDua.get() - tongCanThanhToan;
            if (diff < 0) {
                lblChangeVal.setTextFill(Color.web(COLOR_DANGER));
                changeBox.setStyle("-fx-background-color: #fef2f2; -fx-background-radius: 10; -fx-border-color: #fca5a5; -fx-border-radius: 10; -fx-border-width: 2;");
                return "CÒN THIẾU\n" + String.format("%,.0f đ", Math.abs(diff));
            } else {
                lblChangeVal.setTextFill(Color.web(COLOR_SUCCESS));
                changeBox.setStyle("-fx-background-color: #ecfdf5; -fx-background-radius: 10; -fx-border-color: #6ee7b7; -fx-border-radius: 10; -fx-border-width: 2;");
                return "TIỀN THỐI LẠI\n" + String.format("%,.0f đ", diff);
            }
        }, tongKhachDua));
        lblChangeVal.setAlignment(Pos.CENTER);
        lblChangeVal.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        changeBox.getChildren().addAll(lblStatusTitle, lblChangeVal);

        Region spacerLeft = new Region(); VBox.setVgrow(spacerLeft, Priority.ALWAYS);
        pnlSummary.getChildren().addAll(lblTitle, infoBox, spacerLeft, changeBox);

        // =====================================================================
        // CỘT PHẢI: NHẬP TIỀN SONG SONG (CASH & BANK)
        // =====================================================================
        VBox pnlPayment = new VBox(20);
        HBox.setHgrow(pnlPayment, Priority.ALWAYS);

        // --- TIỀN MẶT ---
        VBox boxTienMat = new VBox(15);
        boxTienMat.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 12;");
        applyShadow(boxTienMat);

        Label lblTienMat = new Label("💵 Khách Trả Tiền Mặt:");
        lblTienMat.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        lblTienMat.setTextFill(Color.web("#059669"));

        TextField txtTienMat = new TextField();
        txtTienMat.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #10b981; -fx-alignment: center-right; -fx-background-radius: 8; -fx-border-color: #cbd5e1; -fx-border-radius: 8;");
        txtTienMat.setText("0");
        setupMoneyField(txtTienMat, tienMat);

        GridPane gridCash = new GridPane();
        gridCash.setHgap(10); gridCash.setVgap(10);
        ColumnConstraints cc = new ColumnConstraints(); cc.setPercentWidth(33.33);
        gridCash.getColumnConstraints().addAll(cc, cc, cc);
        int[] menhGia = {500000, 200000, 100000, 50000, 20000, 10000};
        String[] labels = {"+ 500k", "+ 200k", "+ 100k", "+ 50k", "+ 20k", "+ 10k"};
        for (int i = 0; i < menhGia.length; i++) {
            gridCash.add(createQuickMoneyBtn(labels[i], menhGia[i]), i % 3, i / 3);
        }
        boxTienMat.getChildren().addAll(lblTienMat, txtTienMat, gridCash);

        // --- CHUYỂN KHOẢN KÈM MÃ QR ĐỘNG ---
        VBox boxChuyenKhoan = new VBox(15);
        boxChuyenKhoan.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 12;");
        applyShadow(boxChuyenKhoan);

        HBox headerCK = new HBox();
        headerCK.setAlignment(Pos.CENTER_LEFT);
        Label lblChuyenKhoan = new Label("🏦 Chuyển Khoản / Quẹt Thẻ:");
        lblChuyenKhoan.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        lblChuyenKhoan.setTextFill(Color.web(COLOR_PRIMARY));

        Button btnAutoFillRest = new Button("🔄 Tự điền số còn thiếu");
        btnAutoFillRest.setCursor(Cursor.HAND);
        btnAutoFillRest.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #2563eb; -fx-font-weight: bold; -fx-border-color: #bfdbfe; -fx-border-radius: 15; -fx-background-radius: 15;");
        btnAutoFillRest.setOnAction(e -> {
            double conThieu = tongCanThanhToan - tienMat.get();
            if (conThieu > 0) chuyenKhoan.set(conThieu);
        });
        Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
        headerCK.getChildren().addAll(lblChuyenKhoan, sp2, btnAutoFillRest);

        TextField txtChuyenKhoan = new TextField();
        txtChuyenKhoan.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #2563eb; -fx-alignment: center-right; -fx-background-radius: 8; -fx-background-color: #eff6ff; -fx-border-color: #bfdbfe; -fx-border-radius: 8;");
        txtChuyenKhoan.setText("0");
        setupMoneyField(txtChuyenKhoan, chuyenKhoan);

        HBox qrContainer = new HBox(20);
        qrContainer.setAlignment(Pos.CENTER_LEFT);

        // 🌟 Tích hợp API VietQR sinh ảnh QR động theo giá tiền
        VBox imgBox = new VBox();
        imgBox.setStyle("-fx-border-color: #cbd5e1; -fx-border-width: 1; -fx-border-radius: 8; -fx-padding: 5;");
        ImageView qrView = new ImageView();
        try {
            // Thay "970436" và "1015696515" bằng mã Ngân hàng và STK thực tế của bạn nếu muốn
            String qrUrl = "https://img.vietqr.io/image/970436-1015696515-compact2.jpg?amount=" + (long)tongCanThanhToan + "&addInfo=ThanhToanKhachSan";
            Image qrImage = new Image(qrUrl, 120, 120, true, true);
            qrView.setImage(qrImage);
        } catch (Exception ex) { }
        imgBox.getChildren().add(qrView);

        VBox txnBox = new VBox(8);
        Label lblInst = new Label("Quét mã QR bên cạnh để thanh toán nhanh phần tiền chuyển khoản.");
        lblInst.setFont(Font.font("Segoe UI", 13));
        lblInst.setTextFill(Color.web(COLOR_TEXT_MUTED));
        lblInst.setWrapText(true);

        Label lblTxn = new Label("Mã giao dịch (Nếu có):");
        lblTxn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        TextField txtTxnCode = new TextField();
        txtTxnCode.setPromptText("VD: FT2405...");
        txtTxnCode.setStyle("-fx-padding: 10; -fx-background-radius: 6; -fx-border-color: #cbd5e1; -fx-border-radius: 6;");

        txnBox.getChildren().addAll(lblInst, lblTxn, txtTxnCode);
        HBox.setHgrow(txnBox, Priority.ALWAYS);
        qrContainer.getChildren().addAll(imgBox, txnBox);
        boxChuyenKhoan.getChildren().addAll(headerCK, txtChuyenKhoan, qrContainer);

        // --- NÚT HOÀN TẤT ---
        Button btnFinish = new Button("✅ ĐÓNG HÓA ĐƠN & HOÀN TẤT");
        btnFinish.setMaxWidth(Double.MAX_VALUE);
        btnFinish.setPrefHeight(50);
        btnFinish.setCursor(Cursor.HAND);
        btnFinish.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 8;");
        btnFinish.disableProperty().bind(tongKhachDua.lessThan(tongCanThanhToan));

        btnFinish.setOnAction(e -> {
            String method = (tienMat.get() > 0 && chuyenKhoan.get() > 0) ? "Tiền mặt & Chuyển khoản" :
                    (chuyenKhoan.get() > 0) ? "Chuyển khoản" : "Tiền mặt";
            closeAction.run();
            onPaymentComplete.accept(method); // Gọi Callback truyền kết quả về
        });

        Button btnClear = new Button("✖ Làm lại từ đầu");
        btnClear.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-cursor: hand; -fx-font-weight: bold;");
        btnClear.setOnAction(e -> { tienMat.set(0); chuyenKhoan.set(0); });

        pnlPayment.getChildren().addAll(boxTienMat, boxChuyenKhoan, btnFinish, btnClear);
        bodyContainer.getChildren().addAll(pnlSummary, pnlPayment);

        root.getChildren().addAll(headerBox, bodyContainer);
        return root;
    }

    private void setupMoneyField(TextField textField, SimpleDoubleProperty property) {
        textField.textProperty().addListener((obs, oldV, newV) -> {
            if (textField.isFocused()) {
                try {
                    String cleanStr = newV.replaceAll("[^\\d]", "");
                    property.set(cleanStr.isEmpty() ? 0 : Double.parseDouble(cleanStr));
                } catch (Exception ignored) {}
            }
        });
        textField.focusedProperty().addListener((obs, oldV, isFocused) -> {
            if (!isFocused) textField.setText(String.format("%,.0f", property.get()));
        });
        property.addListener((obs, oldV, newV) -> {
            if (!textField.isFocused()) textField.setText(String.format("%,.0f", newV.doubleValue()));
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
        btn.setOnAction(e -> tienMat.set(tienMat.get() + value));
        return btn;
    }

    private HBox createPriceRow(String label, double value) {
        HBox row = new HBox();
        Label lblL = new Label(label); lblL.setTextFill(Color.web(COLOR_TEXT_MUTED));
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