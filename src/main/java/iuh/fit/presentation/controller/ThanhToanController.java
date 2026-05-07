package iuh.fit.presentation.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.Locale;

public class ThanhToanController {

    private final double tongCanThanhToan;
    private final SimpleDoubleProperty tienKhachDua = new SimpleDoubleProperty(0.0);
    private final SimpleBooleanProperty isCashMode = new SimpleBooleanProperty(true); // Biến theo dõi tab hiện tại
    private final Locale vnLocale = new Locale("vi", "VN");
    private final NumberFormat formatter = NumberFormat.getCurrencyInstance(vnLocale);

    // --- BẢNG MÀU UI/UX ---
    private final String COLOR_BG = "#f1f5f9";
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_DANGER = "#ef4444";
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_BORDER = "#e2e8f0";
    private final String COLOR_TEXT_MUTED = "#64748b";

    public ThanhToanController(double tongCanThanhToan) {
        this.tongCanThanhToan = tongCanThanhToan;
    }

    /**
     * @return Trả về String phương thức thanh toán ("Tiền mặt" hoặc "Chuyển khoản"). Trả về null nếu khách bấm Hủy.
     */
    public String showThanhToanDialog(Stage parentStage) {
        Stage stage = new Stage();
        stage.setTitle("Xác Nhận Thanh Toán");
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
                createTotalRow("CẦN THANH TOÁN:", tongCanThanhToan)
        );

        pnlSummary.getChildren().addAll(lblTitle, infoBox);

        // =====================================================================
        // 2. CỘT PHẢI: CHỌN PHƯƠNG THỨC THANH TOÁN
        // =====================================================================
        VBox pnlPayment = new VBox(15);
        pnlPayment.setPrefWidth(450);
        pnlPayment.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 12;");
        applyShadow(pnlPayment);

        // --- NÚT CHUYỂN ĐỔI TAB (TOGGLE) ---
        HBox tabBox = new HBox(10);
        tabBox.setAlignment(Pos.CENTER);

        ToggleButton btnTabCash = new ToggleButton("💵 TIỀN MẶT");
        ToggleButton btnTabBank = new ToggleButton("🏦 CHUYỂN KHOẢN");

        ToggleGroup group = new ToggleGroup();
        btnTabCash.setToggleGroup(group);
        btnTabBank.setToggleGroup(group);
        btnTabCash.setSelected(true); // Mặc định chọn tiền mặt

        styleToggleButton(btnTabCash);
        styleToggleButton(btnTabBank);

        btnTabCash.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnTabCash, Priority.ALWAYS);
        btnTabBank.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnTabBank, Priority.ALWAYS);
        tabBox.getChildren().addAll(btnTabCash, btnTabBank);

        // --- KHU VỰC HIỂN THỊ NỘI DUNG TƯƠNG ỨNG TỪNG TAB ---
        StackPane contentSwitcher = new StackPane();
        VBox viewCash = createCashView();
        VBox viewBank = createBankView();

        contentSwitcher.getChildren().addAll(viewBank, viewCash);

        // Đổi giao diện khi bấm nút
        btnTabCash.setOnAction(e -> { viewCash.toFront(); isCashMode.set(true); });
        btnTabBank.setOnAction(e -> { viewBank.toFront(); isCashMode.set(false); });

        // --- NÚT HOÀN TẤT CHUNG ---
        final String[] finalPaymentMethod = {null}; // Biến lưu kết quả trả về

        Button btnFinish = new Button("✅ HOÀN TẤT THANH TOÁN");
        btnFinish.setMaxWidth(Double.MAX_VALUE);
        btnFinish.setPrefHeight(50);
        btnFinish.setCursor(Cursor.HAND);
        btnFinish.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 8;");

        // Logic vô hiệu hóa nút: Nếu là Tiền mặt thì phải nhập đủ tiền. Ngân hàng thì luôn mở.
        btnFinish.disableProperty().bind(Bindings.createBooleanBinding(
                () -> isCashMode.get() && tienKhachDua.get() < tongCanThanhToan,
                isCashMode, tienKhachDua
        ));

        btnFinish.setOnAction(e -> {
            finalPaymentMethod[0] = isCashMode.get() ? "Tiền mặt" : "Chuyển khoản";
            stage.close();
        });

        pnlPayment.getChildren().addAll(tabBox, new Separator(), contentSwitcher, btnFinish);

        root.getChildren().addAll(pnlSummary, pnlPayment);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();

        return finalPaymentMethod[0];
    }

    // =========================================================================
    // GIAO DIỆN TAB: TIỀN MẶT
    // =========================================================================
    private VBox createCashView() {
        VBox cashBox = new VBox(15);
        cashBox.setStyle("-fx-background-color: white;");

        Label lblTienKhachDua = new Label();
        lblTienKhachDua.textProperty().bind(Bindings.createStringBinding(
                () -> formatter.format(tienKhachDua.get()), tienKhachDua));
        lblTienKhachDua.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + COLOR_SUCCESS + "; -fx-background-color: #f0fdf4; -fx-padding: 10; -fx-background-radius: 8; -fx-alignment: center-right;");
        lblTienKhachDua.setMaxWidth(Double.MAX_VALUE);

        GridPane gridCash = new GridPane();
        gridCash.setHgap(10); gridCash.setVgap(10);
        int[] menhGia = {500000, 200000, 100000, 50000, 20000, 10000, 5000, 2000};
        String[] labels = {"500k", "200k", "100k", "50k", "20k", "10k", "5k", "2k"};

        for (int i = 0; i < menhGia.length; i++) {
            Button btn = createMoneyButton(labels[i], menhGia[i]);
            gridCash.add(btn, i % 3, i / 3);
        }

        HBox quickActions = new HBox(10);
        Button btnClear = createActionBtn("XÓA HẾT", COLOR_DANGER);
        btnClear.setOnAction(e -> tienKhachDua.set(0));
        Button btnExact = createActionBtn("ĐƯA ĐỦ", COLOR_PRIMARY);
        btnExact.setOnAction(e -> tienKhachDua.set(tongCanThanhToan));
        quickActions.getChildren().addAll(btnClear, btnExact);

        VBox changeBox = new VBox(5);
        changeBox.setAlignment(Pos.CENTER);
        changeBox.setPadding(new Insets(15));
        changeBox.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 10;");
        Label lblChangeTitle = new Label("TIỀN THỐI LẠI");
        lblChangeTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        Label lblChangeVal = new Label();
        lblChangeVal.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 24));
        lblChangeVal.textProperty().bind(Bindings.createStringBinding(() -> {
            double diff = tienKhachDua.get() - tongCanThanhToan;
            if (diff < 0) {
                lblChangeVal.setTextFill(Color.web(COLOR_DANGER));
                return "Còn thiếu: " + formatter.format(Math.abs(diff));
            } else {
                lblChangeVal.setTextFill(Color.web(COLOR_PRIMARY));
                return formatter.format(diff);
            }
        }, tienKhachDua));
        changeBox.getChildren().addAll(lblChangeTitle, lblChangeVal);

        cashBox.getChildren().addAll(lblTienKhachDua, gridCash, quickActions, changeBox);
        return cashBox;
    }

    // =========================================================================
    // GIAO DIỆN TAB: NGÂN HÀNG (CHUYỂN KHOẢN)
    // =========================================================================
    private VBox createBankView() {
        VBox bankBox = new VBox(15);
        bankBox.setAlignment(Pos.CENTER);
        bankBox.setStyle("-fx-background-color: white;");

        Label lblInstruct = new Label("Vui lòng quét mã QR dưới đây hoặc yêu cầu khách hàng chuyển khoản số tiền:");
        lblInstruct.setTextFill(Color.web(COLOR_TEXT_MUTED));
        lblInstruct.setWrapText(true);
        lblInstruct.setTextAlignment(TextAlignment.CENTER);

        Label lblAmount = new Label(formatter.format(tongCanThanhToan));
        lblAmount.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 30));
        lblAmount.setTextFill(Color.web(COLOR_PRIMARY));

        // Mô phỏng Mã QR Code (Bạn có thể thay bằng ImageView nạp ảnh QR thật của khách sạn)
        Label lblQR = new Label("📱\nQuét mã VietQR");
        lblQR.setAlignment(Pos.CENTER);
        lblQR.setTextAlignment(TextAlignment.CENTER);
        lblQR.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblQR.setTextFill(Color.web("#94a3b8"));
        lblQR.setPrefSize(180, 180);
        lblQR.setStyle("-fx-border-color: #cbd5e1; -fx-border-width: 2; -fx-border-radius: 12; -fx-border-style: dashed; -fx-background-color: #f8fafc; -fx-background-radius: 12;");

        TextField txtTxnCode = new TextField();
        txtTxnCode.setPromptText("Nhập mã giao dịch (Không bắt buộc)...");
        txtTxnCode.setStyle("-fx-padding: 10; -fx-font-size: 14px; -fx-background-radius: 6; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 6;");
        txtTxnCode.setMaxWidth(300);

        bankBox.getChildren().addAll(lblInstruct, lblAmount, lblQR, txtTxnCode);
        return bankBox;
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    private void styleToggleButton(ToggleButton btn) {
        btn.setPrefHeight(45);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btn.setCursor(Cursor.HAND);
        String idleStyle = "-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-border-color: transparent transparent #cbd5e1 transparent; -fx-border-width: 3;";
        String activeStyle = "-fx-background-color: transparent; -fx-text-fill: " + COLOR_PRIMARY + "; -fx-border-color: transparent transparent " + COLOR_PRIMARY + " transparent; -fx-border-width: 3;";

        btn.setStyle(idleStyle);
        btn.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) btn.setStyle(activeStyle);
            else btn.setStyle(idleStyle);
        });
    }

    private Button createMoneyButton(String label, double value) {
        Button btn = new Button(label);
        btn.setPrefSize(140, 50);
        btn.setCursor(Cursor.HAND);
        btn.setStyle("-fx-background-color: #f1f5f9; -fx-border-color: #cbd5e1; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 6; -fx-border-radius: 6;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 6;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #f1f5f9; -fx-border-color: #cbd5e1; -fx-font-weight: bold; -fx-font-size: 14px; -fx-background-radius: 6; -fx-border-radius: 6;"));
        btn.setOnAction(e -> tienKhachDua.set(tienKhachDua.get() + value));
        return btn;
    }

    private Button createActionBtn(String text, String color) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btn, Priority.ALWAYS);
        btn.setStyle("-fx-background-color: white; -fx-text-fill: " + color + "; -fx-border-color: " + color + "; -fx-font-weight: bold; -fx-background-radius: 6; -fx-border-radius: 6; -fx-padding: 10;");
        btn.setCursor(Cursor.HAND);
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

    private HBox createTotalRow(String label, double value) {
        HBox row = new HBox();
        Label lblL = new Label(label); lblL.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        Label lblR = new Label(formatter.format(value)); lblR.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 18)); lblR.setTextFill(Color.web(COLOR_DANGER));
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