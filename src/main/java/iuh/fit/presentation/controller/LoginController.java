package iuh.fit.presentation.controller;

import iuh.fit.core.dto.LichSuCaLamViecDTO;
import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.*;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.logging.Logger;

public class LoginController {
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    private TextField usernameTextField;
    private PasswordField passwordField;
    private TextField passwordVisibleField;
    private Button loginButton;
    private Button exitButton;
    private Label errorLabel;
    private ProgressBar progressBar;

    private IAuthenticationService authenticationService;
    private TaiKhoanDTO currentUser;

    private IKhachHangService khachHangService;
    private INhanVienService nhanVienService;
    private IPhongService phongService;
    private IPhieuDatPhongService phieuDatPhongService;
    private IDichVuService dichVuService;
    private IHoaDonService hoaDonService;
    private IChiTietHoaDonService chiTietHoaDonService;

    // Tích hợp Service Giao Ca
    private IGiaoCaService giaoCaService;

    // --- BẢNG MÀU UI CẢI TIẾN HIỆN ĐẠI ---
    private final String COLOR_PRIMARY = "#1e3a8a";  // Xanh dương đậm sang trọng
    private final String COLOR_ACCENT = "#0284c7";   // Xanh da trời sáng
    private final String COLOR_TEXT_MAIN = "#0f172a";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_BORDER = "#cbd5e1";
    private final String COLOR_INPUT_BG = "#f8fafc";

    public LoginController(IAuthenticationService authenticationService,
                           IKhachHangService khachHangService,
                           INhanVienService nhanVienService,
                           IPhongService phongService,
                           IPhieuDatPhongService phieuDatPhongService,
                           IDichVuService dichVuService,
                           IHoaDonService hoaDonService,
                           IChiTietHoaDonService chiTietHoaDonService,
                           IGiaoCaService giaoCaService) { // Bổ sung IGiaoCaService vào Constructor
        this.authenticationService = authenticationService;
        this.khachHangService = khachHangService;
        this.nhanVienService = nhanVienService;
        this.phongService = phongService;
        this.phieuDatPhongService = phieuDatPhongService;
        this.dichVuService = dichVuService;
        this.hoaDonService = hoaDonService;
        this.chiTietHoaDonService = chiTietHoaDonService;
        this.giaoCaService = giaoCaService;
    }

    public Scene createLoginScene() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #e0f2fe, #bae6fd);");

        VBox cardBox = new VBox(22);
        cardBox.setPadding(new Insets(45, 45, 45, 45));
        cardBox.setMaxWidth(480);
        cardBox.setMaxHeight(500);
        cardBox.setAlignment(Pos.TOP_CENTER);
        cardBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.98); -fx-background-radius: 20; -fx-border-radius: 20; -fx-border-color: #ffffff; -fx-border-width: 2;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.1));
        shadow.setRadius(25);
        shadow.setOffsetY(12);
        cardBox.setEffect(shadow);

        // ================= HEADER =================
        VBox headerBox = new VBox(8);
        headerBox.setAlignment(Pos.CENTER);

        HBox logoTitleBox = new HBox(12);
        logoTitleBox.setAlignment(Pos.CENTER);

        ImageView logoImageView = new ImageView();
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/images/logo_ttv.png"));
            logoImageView.setImage(logoImage);
            logoImageView.setFitWidth(85);
            logoImageView.setFitHeight(85);
            logoImageView.setPreserveRatio(true);

            DropShadow logoShadow = new DropShadow();
            logoShadow.setColor(Color.web(COLOR_ACCENT, 0.4));
            logoShadow.setRadius(10);
            logoShadow.setOffsetY(4);
            logoImageView.setEffect(logoShadow);
        } catch (Exception e) {
            Label logoEmoji = new Label("🏨");
            logoEmoji.setFont(Font.font(65));
            logoTitleBox.getChildren().add(logoEmoji);
        }

        if (logoImageView.getImage() != null) {
            logoTitleBox.getChildren().add(logoImageView);
        }

        Label titleLabel = new Label("TTV HOTEL");
        titleLabel.setFont(Font.font("Verdana", FontWeight.BLACK, 38));
        titleLabel.setTextFill(new LinearGradient(
                0, 0, 1, 0,
                true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web(COLOR_ACCENT)),
                new Stop(1.0, Color.web(COLOR_PRIMARY))
        ));

        DropShadow titleShadow = new DropShadow();
        titleShadow.setColor(Color.web(COLOR_PRIMARY, 0.25));
        titleShadow.setRadius(6);
        titleShadow.setOffsetY(3);
        titleLabel.setEffect(titleShadow);

        logoTitleBox.getChildren().add(titleLabel);

        Label subTitleLabel = new Label("HỆ THỐNG QUẢN LÝ KHÁCH SẠN");
        subTitleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        subTitleLabel.setTextFill(Color.web(COLOR_TEXT_MUTED));

        headerBox.getChildren().addAll(logoTitleBox, subTitleLabel);

        // ================= INPUT FORM =================
        String inputStyle = "-fx-padding: 14 40 14 15; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + COLOR_BORDER + "; -fx-background-color: " + COLOR_INPUT_BG + "; -fx-font-size: 14px; -fx-text-fill: " + COLOR_TEXT_MAIN + ";";
        String inputFocusStyle = "-fx-border-color: " + COLOR_ACCENT + "; -fx-background-color: #ffffff; -fx-effect: dropshadow(three-pass-box, rgba(2, 132, 199, 0.2), 5, 0, 0, 0);";

        // Username
        VBox usernameVBox = new VBox(6);
        Label usernameLabel = new Label("Tên đăng nhập");
        usernameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        usernameLabel.setTextFill(Color.web(COLOR_TEXT_MAIN));
        usernameTextField = new TextField();
        usernameTextField.setPromptText("Nhập tài khoản của bạn...");
        usernameTextField.setStyle(inputStyle);
        usernameTextField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) usernameTextField.setStyle(inputStyle + inputFocusStyle);
            else usernameTextField.setStyle(inputStyle);
        });
        usernameVBox.getChildren().addAll(usernameLabel, usernameTextField);

        // Password
        VBox passwordVBox = new VBox(6);
        Label passwordLabel = new Label("Mật khẩu");
        passwordLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        passwordLabel.setTextFill(Color.web(COLOR_TEXT_MAIN));

        passwordField = new PasswordField();
        passwordField.setPromptText("Nhập mật khẩu...");
        passwordField.setStyle(inputStyle);

        passwordVisibleField = new TextField();
        passwordVisibleField.setPromptText("Nhập mật khẩu...");
        passwordVisibleField.setStyle(inputStyle);
        passwordVisibleField.setVisible(false);

        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) passwordField.setStyle(inputStyle + inputFocusStyle);
            else passwordField.setStyle(inputStyle);
        });
        passwordVisibleField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) passwordVisibleField.setStyle(inputStyle + inputFocusStyle);
            else passwordVisibleField.setStyle(inputStyle);
        });

        Button togglePasswordBtn = new Button("👁");
        togglePasswordBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_TEXT_MUTED + "; -fx-cursor: hand; -fx-font-size: 16px;");
        togglePasswordBtn.setPadding(new Insets(0, 12, 0, 0));

        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());

        togglePasswordBtn.setOnAction(e -> {
            if (passwordField.isVisible()) {
                passwordField.setVisible(false);
                passwordVisibleField.setVisible(true);
                togglePasswordBtn.setText("🙈");
                togglePasswordBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_ACCENT + "; -fx-cursor: hand; -fx-font-size: 16px;");
            } else {
                passwordField.setVisible(true);
                passwordVisibleField.setVisible(false);
                togglePasswordBtn.setText("👁");
                togglePasswordBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_TEXT_MUTED + "; -fx-cursor: hand; -fx-font-size: 16px;");
            }
        });

        StackPane passwordStack = new StackPane();
        passwordStack.setAlignment(Pos.CENTER_RIGHT);
        passwordStack.getChildren().addAll(passwordField, passwordVisibleField, togglePasswordBtn);
        passwordVBox.getChildren().addAll(passwordLabel, passwordStack);

        // Messages & Progress
        errorLabel = new Label();
        errorLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        errorLabel.setWrapText(true);
        errorLabel.setAlignment(Pos.CENTER);
        errorLabel.setMaxWidth(Double.MAX_VALUE);

        progressBar = new ProgressBar();
        progressBar.setVisible(false);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(6);
        progressBar.setStyle("-fx-accent: " + COLOR_ACCENT + "; -fx-control-inner-background: #e2e8f0;");

        // ================= BUTTONS =================
        VBox buttonBox = new VBox(14);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        loginButton = new Button("Đăng Nhập Hệ Thống");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setCursor(Cursor.HAND);
        String btnLoginStyle = "-fx-background-color: linear-gradient(to right, " + COLOR_ACCENT + ", " + COLOR_PRIMARY + "); -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 14; -fx-background-radius: 8;";
        String btnLoginHover = "-fx-background-color: linear-gradient(to right, #0369a1, #1e3a8a); -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 14; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(2, 132, 199, 0.4), 10, 0, 0, 4);";

        loginButton.setStyle(btnLoginStyle);
        loginButton.setOnMouseEntered(e -> loginButton.setStyle(btnLoginHover));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(btnLoginStyle));

        exitButton = new Button("Thoát Khỏi Trình Ứng Dụng");
        exitButton.setMaxWidth(Double.MAX_VALUE);
        exitButton.setCursor(Cursor.HAND);
        String btnExitStyle = "-fx-background-color: transparent; -fx-text-fill: " + COLOR_TEXT_MUTED + "; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12; -fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 8; -fx-background-radius: 8;";
        String btnExitHover = "-fx-background-color: #f1f5f9; -fx-text-fill: #ef4444; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 12; -fx-border-color: #fca5a5; -fx-border-radius: 8; -fx-background-radius: 8;";

        exitButton.setStyle(btnExitStyle);
        exitButton.setOnMouseEntered(e -> exitButton.setStyle(btnExitHover));
        exitButton.setOnMouseExited(e -> exitButton.setStyle(btnExitStyle));

        buttonBox.getChildren().addAll(loginButton, exitButton);

        loginButton.setOnAction(e -> handleLogin());
        exitButton.setOnAction(e -> System.exit(0));
        passwordField.setOnAction(e -> handleLogin());
        passwordVisibleField.setOnAction(e -> handleLogin());

        cardBox.getChildren().addAll(
                headerBox,
                usernameVBox,
                passwordVBox,
                errorLabel,
                progressBar,
                buttonBox
        );

        root.getChildren().add(cardBox);
        return new Scene(root, 650, 580);
    }

    private void handleLogin() {
        String username = usernameTextField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ tài khoản và mật khẩu");
            return;
        }

        try {
            loginButton.setText("Đang Xác Thực...");
            loginButton.setDisable(true);
            progressBar.setVisible(true);

            TaiKhoanDTO user = authenticationService.login(username, password);

            if (user != null) {
                currentUser = user;

                // ========================================================
                // 🚀 ĐÃ FIX: ĐẶC QUYỀN BYPASS CHO ADMIN / QUẢN LÝ
                // ========================================================
                boolean isManager = false;
                try {
                    // Nếu đăng nhập bằng tk admin thì tự động là quản lý (phục vụ demo nhanh)
                    if (username.equalsIgnoreCase("admin")) {
                        isManager = true;
                    } else {
                        // Kiểm tra chức vụ thực tế dưới Database
                        var nv = nhanVienService.getNhanVienById(user.getMaNhanVien());
                        if (nv != null && (nv.getLoaiNhanVien().contains("QUAN_LY") || nv.getLoaiNhanVien().contains("GIAM_DOC"))) {
                            isManager = true;
                        }
                    }
                } catch (Exception ignored) {}

                // CHỈ YÊU CẦU NHẬN CA ĐỐI VỚI NHÂN VIÊN LỄ TÂN
                if (!isManager) {
                    LichSuCaLamViecDTO caDangLam = giaoCaService.getCaDangLam(user.getMaNhanVien());
                    if (caDangLam == null) {
                        GiaoNhanCaDialog dialog = new GiaoNhanCaDialog(giaoCaService, user);
                        boolean isNhanCa = dialog.showNhanCaDialog();

                        if (!isNhanCa) {
                            showError("Đăng nhập bị hủy: Bạn chưa xác nhận Nhận Ca.");
                            resetLoginButton();
                            currentUser = null;
                            return; // Chặn lại, không cho load MainController
                        }
                    }
                }

                showSuccess("Thành công! Đang truy cập hệ thống...");

                PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
                pause.setOnFinished(event -> {
                    try {
                        Stage currentStage = (Stage) loginButton.getScene().getWindow();

                        MainController mainController = new MainController(
                                currentStage, currentUser,
                                khachHangService, nhanVienService,
                                phongService, phieuDatPhongService,
                                dichVuService, hoaDonService, chiTietHoaDonService,
                                giaoCaService
                        );
                        mainController.showMainScreen();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showError("Lỗi khi tải trang chủ: " + ex.getMessage());
                        resetLoginButton();
                    }
                });
                pause.play();

            } else {
                showError("Tài khoản hoặc mật khẩu không chính xác.");
                clearFields();
                resetLoginButton();
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            resetLoginButton();
        } catch (Exception e) {
            showError("Lỗi kết nối hệ thống: " + e.getMessage());
            e.printStackTrace();
            resetLoginButton();
        }
    }

    private void resetLoginButton() {
        loginButton.setText("Đăng Nhập Hệ Thống");
        loginButton.setDisable(false);
        progressBar.setVisible(false);
    }

    private void showError(String message) {
        errorLabel.setText("⚠️ " + message);
        errorLabel.setStyle("-fx-text-fill: #b91c1c; -fx-background-color: #fef2f2; -fx-padding: 10 15; -fx-background-radius: 8; -fx-border-color: #fecaca; -fx-border-radius: 8;");
    }

    private void showSuccess(String message) {
        errorLabel.setText("✅ " + message);
        errorLabel.setStyle("-fx-text-fill: #047857; -fx-background-color: #ecfdf5; -fx-padding: 10 15; -fx-background-radius: 8; -fx-border-color: #a7f3d0; -fx-border-radius: 8;");
    }

    private void clearFields() {
        passwordField.clear();
        passwordField.requestFocus();
    }

    public TaiKhoanDTO getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(TaiKhoanDTO currentUser) {
        this.currentUser = currentUser;
    }
}