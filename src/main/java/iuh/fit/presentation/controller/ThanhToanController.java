package iuh.fit.presentation.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
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

    // Sử dụng StringProperty để hỗ trợ nhiều hơn 2 phương thức
    private final SimpleStringProperty paymentMethod = new SimpleStringProperty("Tiền mặt");

    private final Locale vnLocale = new Locale("vi", "VN");
    private final NumberFormat formatter = NumberFormat.getCurrencyInstance(vnLocale);

    // --- BẢNG MÀU UI/UX HIỆN ĐẠI ---
    private final String COLOR_BG = "#f1f5f9";
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_SUCCESS = "#10b981";
    private final String COLOR_DANGER = "#ef4444";
    private final String COLOR_EWALLET = "#db2777"; // Màu hồng cho Ví điện tử (Momo)
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_BORDER = "#e2e8f0";
    private final String COLOR_TEXT_MUTED = "#64748b";

    public ThanhToanController(double tongCanThanhToan) {
        this.tongCanThanhToan = tongCanThanhToan;
    }

    /**
     * @return Trả về String phương thức thanh toán ("Tiền mặt", "Chuyển khoản", "Ví điện tử").
     * Trả về null nếu khách bấm Hủy.
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
        pnlPayment.setPrefWidth(550); // Tăng width một chút để chứa 3 nút
        pnlPayment.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 12;");
        applyShadow(pnlPayment);

        // --- NÚT CHUYỂN ĐỔI TAB (TOGGLE) ---
        HBox tabBox = new HBox(10);
        tabBox.setAlignment(Pos.CENTER);

        ToggleButton btnTabCash = new ToggleButton("💵 TIỀN MẶT");
        ToggleButton btnTabBank = new ToggleButton("🏦 CHUYỂN KHOẢN");
        ToggleButton btnTabEWallet = new ToggleButton("📱 VÍ ĐIỆN TỬ");

        ToggleGroup group = new ToggleGroup();
        btnTabCash.setToggleGroup(group);
        btnTabBank.setToggleGroup(group);
        btnTabEWallet.setToggleGroup(group);
        btnTabCash.setSelected(true);

        styleToggleButton(btnTabCash, COLOR_PRIMARY);
        styleToggleButton(btnTabBank, COLOR_PRIMARY);
        styleToggleButton(btnTabEWallet, COLOR_EWALLET); // Đổi màu tab ví điện tử cho nổi bật

        btnTabCash.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnTabCash, Priority.ALWAYS);
        btnTabBank.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnTabBank, Priority.ALWAYS);
        btnTabEWallet.setMaxWidth(Double.MAX_VALUE); HBox.setHgrow(btnTabEWallet, Priority.ALWAYS);
        tabBox.getChildren().addAll(btnTabCash, btnTabBank, btnTabEWallet);

        // --- KHU VỰC HIỂN THỊ NỘI DUNG TƯƠNG ỨNG TỪNG TAB ---
        StackPane contentSwitcher = new StackPane();
        VBox viewCash = createCashView();
        VBox viewBank = createBankView();
        VBox viewEWallet = createEWalletView();

        contentSwitcher.getChildren().addAll(viewEWallet, viewBank, viewCash); // Add ngược để Cash hiện lên đầu

        // Đổi giao diện và cập nhật biến theo dõi khi bấm nút
        btnTabCash.setOnAction(e -> { viewCash.toFront(); paymentMethod.set("Tiền mặt"); });
        btnTabBank.setOnAction(e -> { viewBank.toFront(); paymentMethod.set("Chuyển khoản"); });
        btnTabEWallet.setOnAction(e -> { viewEWallet.toFront(); paymentMethod.set("Ví điện tử"); });

        // --- CHECKBOX IN HÓA ĐƠN ---
        CheckBox chkInHoaDon = new CheckBox("🖨 In biên lai sau khi thanh toán");
        chkInHoaDon.setSelected(true); // Mặc định là có in
        chkInHoaDon.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        chkInHoaDon.setTextFill(Color.web(COLOR_TEXT_MAIN));
        chkInHoaDon.setCursor(Cursor.HAND);

        // --- NÚT HOÀN TẤT ---
        final String[] finalPaymentMethod = {null};

        Button btnFinish = new Button("✅ HOÀN TẤT THANH TOÁN");
        btnFinish.setMaxWidth(Double.MAX_VALUE);
        btnFinish.setPrefHeight(50);
        btnFinish.setCursor(Cursor.HAND);
        btnFinish.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-background-radius: 8;");

        // Logic vô hiệu hóa nút: Chỉ khóa nếu đang ở chế độ Tiền mặt VÀ tiền khách đưa chưa đủ
        btnFinish.disableProperty().bind(Bindings.createBooleanBinding(
                () -> paymentMethod.get().equals("Tiền mặt") && tienKhachDua.get() < tongCanThanhToan,
                paymentMethod, tienKhachDua
        ));

        btnFinish.setOnAction(e -> {
            finalPaymentMethod[0] = paymentMethod.get();

            // Xử lý logic In Hóa Đơn
            if (chkInHoaDon.isSelected()) {
                inHoaDonAo(finalPaymentMethod[0]);
            }

            stage.close();
        });

        pnlPayment.getChildren().addAll(tabBox, new Separator(), contentSwitcher, new Separator(), chkInHoaDon, btnFinish);

        root.getChildren().addAll(pnlSummary, pnlPayment);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();

        // TRẢ VỀ "true" để khớp với logic Boolean.parseBoolean(...) ở DatPhongController,
        // hoặc trả về đúng tên phương thức tùy vào logic parse của hệ thống bạn.
        // Tạm thời mình trả về tên phương thức chuẩn:
        return finalPaymentMethod[0];
    }

    // =========================================================================
    // GIAO DIỆN TAB 1: TIỀN MẶT
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
            gridCash.add(btn, i % 4, i / 4); // Chỉnh lại thành 4 cột cho gọn với panel 550px
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
    // GIAO DIỆN TAB 2: NGÂN HÀNG (CHUYỂN KHOẢN)
    // =========================================================================
    private VBox createBankView() {
        VBox bankBox = new VBox(15);
        bankBox.setAlignment(Pos.CENTER);
        bankBox.setStyle("-fx-background-color: white;");

        Label lblInstruct = new Label("Yêu cầu khách hàng quét mã VietQR để thanh toán:");
        lblInstruct.setTextFill(Color.web(COLOR_TEXT_MUTED));
        lblInstruct.setFont(Font.font("Segoe UI", 14));

        Label lblAmount = new Label(formatter.format(tongCanThanhToan));
        lblAmount.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 30));
        lblAmount.setTextFill(Color.web(COLOR_PRIMARY));

        Label lblQR = new Label("🏦\nQuét mã VietQR");
        lblQR.setAlignment(Pos.CENTER);
        lblQR.setTextAlignment(TextAlignment.CENTER);
        lblQR.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblQR.setTextFill(Color.web("#94a3b8"));
        lblQR.setPrefSize(180, 180);
        lblQR.setStyle("-fx-border-color: " + COLOR_PRIMARY + "; -fx-border-width: 2; -fx-border-radius: 12; -fx-border-style: dashed; -fx-background-color: #eff6ff; -fx-background-radius: 12;");

        TextField txtTxnCode = new TextField();
        txtTxnCode.setPromptText("Nhập mã giao dịch (VD: FT240508...)");
        txtTxnCode.setStyle("-fx-padding: 10; -fx-font-size: 14px; -fx-background-radius: 6; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 6;");
        txtTxnCode.setMaxWidth(300);

        bankBox.getChildren().addAll(lblInstruct, lblAmount, lblQR, txtTxnCode);
        return bankBox;
    }

    // =========================================================================
    // GIAO DIỆN TAB 3: VÍ ĐIỆN TỬ (MOMO / ZALOPAY)
    // =========================================================================
    private VBox createEWalletView() {
        VBox walletBox = new VBox(15);
        walletBox.setAlignment(Pos.CENTER);
        walletBox.setStyle("-fx-background-color: white;");

        Label lblInstruct = new Label("Mở ứng dụng Momo / ZaloPay để quét mã:");
        lblInstruct.setTextFill(Color.web(COLOR_TEXT_MUTED));
        lblInstruct.setFont(Font.font("Segoe UI", 14));

        Label lblAmount = new Label(formatter.format(tongCanThanhToan));
        lblAmount.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 30));
        lblAmount.setTextFill(Color.web(COLOR_EWALLET)); // Sử dụng màu hồng Momo

        Label lblQR = new Label("📱\nQuét mã Ví Điện Tử");
        lblQR.setAlignment(Pos.CENTER);
        lblQR.setTextAlignment(TextAlignment.CENTER);
        lblQR.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblQR.setTextFill(Color.web("#94a3b8"));
        lblQR.setPrefSize(180, 180);
        lblQR.setStyle("-fx-border-color: " + COLOR_EWALLET + "; -fx-border-width: 2; -fx-border-radius: 12; -fx-border-style: solid; -fx-background-color: #fdf2f8; -fx-background-radius: 12;");

        TextField txtTxnCode = new TextField();
        txtTxnCode.setPromptText("Nhập số điện thoại khách hoặc mã GD...");
        txtTxnCode.setStyle("-fx-padding: 10; -fx-font-size: 14px; -fx-background-radius: 6; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 6;");
        txtTxnCode.setMaxWidth(300);

        walletBox.getChildren().addAll(lblInstruct, lblAmount, lblQR, txtTxnCode);
        return walletBox;
    }

    // =========================================================================
    // HELPER METHODS VÀ LOGIC MÔ PHỎNG IN
    // =========================================================================

    private void styleToggleButton(ToggleButton btn, String activeColor) {
        btn.setPrefHeight(45);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btn.setCursor(Cursor.HAND);
        String idleStyle = "-fx-background-color: transparent; -fx-text-fill: #64748b; -fx-border-color: transparent transparent #cbd5e1 transparent; -fx-border-width: 3;";
        String activeStyle = "-fx-background-color: transparent; -fx-text-fill: " + activeColor + "; -fx-border-color: transparent transparent " + activeColor + " transparent; -fx-border-width: 3;";

        btn.setStyle(idleStyle);
        btn.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) btn.setStyle(activeStyle);
            else btn.setStyle(idleStyle);
        });
    }

    private Button createMoneyButton(String label, double value) {
        Button btn = new Button(label);
        btn.setPrefSize(140, 50);
        btn.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(btn, Priority.ALWAYS);
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

    // --- MÔ PHỎNG IN HÓA ĐƠN ---
    private void inHoaDonAo(String pMethod) {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================\n");
        sb.append("         HÓA ĐƠN THANH TOÁN         \n");
        sb.append("====================================\n");
        sb.append(String.format("Ngày In: %s\n", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
        sb.append("------------------------------------\n");
        sb.append(String.format("Tổng tiền : %,15.0f đ\n", tongCanThanhToan));
        sb.append(String.format("P.Thức TT : %15s\n", pMethod));
        if (pMethod.equals("Tiền mặt")) {
            sb.append(String.format("Khách đưa : %,15.0f đ\n", tienKhachDua.get()));
            sb.append(String.format("Tiền thừa : %,15.0f đ\n", tienKhachDua.get() - tongCanThanhToan));
        }
        sb.append("====================================\n");
        sb.append("     CẢM ƠN QUÝ KHÁCH. HẸN GẶP LẠI! \n");

        TextArea textArea = new TextArea(sb.toString());
        textArea.setFont(Font.font("Monospaced", 14));
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefSize(350, 400);
        textArea.setStyle("-fx-control-inner-background: white; -fx-font-family: 'Consolas', monospace;");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Máy In Hóa Đơn");
        alert.setHeaderText("Đang xuất hóa đơn qua máy in nhiệt...");
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }
}