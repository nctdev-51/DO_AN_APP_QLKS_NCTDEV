package iuh.fit.presentation.controller;

import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.*;
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
import javafx.stage.Stage;

import java.util.logging.Logger;

public class LoginController {
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    private TextField usernameTextField;
    private PasswordField passwordField;
    private TextField passwordVisibleField; // Thêm ô nhập text thường để hiện mật khẩu
    private Button loginButton;
    private Button exitButton;
    private Label errorLabel;

    private IAuthenticationService authenticationService;
    private TaiKhoanDTO currentUser;
    private IKhachHangService khachHangService;
    private INhanVienService nhanVienService;
    private IPhongService phongService;
    private IPhieuDatPhongService phieuDatPhongService;
    private IDichVuService dichVuService;

    // --- BẢNG MÀU UI ---
    private final String COLOR_PRIMARY = "#2563eb";
    private final String COLOR_PRIMARY_HOVER = "#1d4ed8";
    private final String COLOR_BG = "#f1f5f9";
    private final String COLOR_TEXT_MAIN = "#1e293b";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_BORDER = "#cbd5e1";

    public LoginController(IAuthenticationService authenticationService,
                           IKhachHangService khachHangService,
                           INhanVienService nhanVienService,
                           IPhongService phongService,
                           IPhieuDatPhongService phieuDatPhongService,
                           IDichVuService dichVuService) {

        this.authenticationService = authenticationService;
        this.khachHangService = khachHangService;
        this.nhanVienService = nhanVienService;
        this.phongService = phongService;
        this.phieuDatPhongService = phieuDatPhongService;
        this.dichVuService = dichVuService;
    }

    public Scene createLoginScene() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: " + COLOR_BG + ";");

        VBox cardBox = new VBox(20);
        cardBox.setPadding(new Insets(40, 40, 40, 40));
        cardBox.setMaxWidth(400);
        cardBox.setMaxHeight(450);
        cardBox.setAlignment(Pos.TOP_CENTER);
        cardBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.08));
        shadow.setRadius(20);
        shadow.setOffsetY(10);
        cardBox.setEffect(shadow);

        // Header
        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);

        Label lblLogo = new Label("🏨");
        lblLogo.setFont(Font.font(40));

        Label titleLabel = new Label("TATP HOTEL");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 24));
        titleLabel.setTextFill(Color.web(COLOR_PRIMARY));

        Label subTitleLabel = new Label("Đăng nhập vào hệ thống quản lý");
        subTitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        subTitleLabel.setTextFill(Color.web(COLOR_TEXT_MUTED));

        headerBox.getChildren().addAll(lblLogo, titleLabel, subTitleLabel);

        // Input Styling
        // Thêm padding bên phải (35px) để chữ không bị đè lên icon con mắt
        String inputStyle = "-fx-padding: 12 35 12 12; -fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: " + COLOR_BORDER + "; -fx-background-color: #f8fafc; -fx-font-size: 14px;";

        // Username Box
        VBox usernameVBox = new VBox(8);
        Label usernameLabel = new Label("Tài khoản");
        usernameLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        usernameLabel.setTextFill(Color.web(COLOR_TEXT_MAIN));
        usernameTextField = new TextField();
        usernameTextField.setPromptText("Nhập tên đăng nhập...");
        usernameTextField.setStyle(inputStyle);
        usernameVBox.getChildren().addAll(usernameLabel, usernameTextField);

        // Password Box
        VBox passwordVBox = new VBox(8);
        Label passwordLabel = new Label("Mật khẩu");
        passwordLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 13));
        passwordLabel.setTextFill(Color.web(COLOR_TEXT_MAIN));

        // 1. Ô ẩn mật khẩu (PasswordField)
        passwordField = new PasswordField();
        passwordField.setPromptText("Nhập mật khẩu...");
        passwordField.setStyle(inputStyle);

        // 2. Ô hiện mật khẩu (TextField)
        passwordVisibleField = new TextField();
        passwordVisibleField.setPromptText("Nhập mật khẩu...");
        passwordVisibleField.setStyle(inputStyle);
        passwordVisibleField.setVisible(false); // Mặc định ẩn

        // 3. Nút con mắt
        Button togglePasswordBtn = new Button("👁"); // Dùng emoji hoặc có thể dùng Icon thật nếu bạn có thư viện
        togglePasswordBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_TEXT_MUTED + "; -fx-cursor: hand; -fx-font-size: 14px;");
        togglePasswordBtn.setPadding(new Insets(0, 10, 0, 0)); // Căn icon cách lề phải 10px

        // Đồng bộ dữ liệu 2 chiều giữa 2 ô nhập (gõ ô này, ô kia cũng nhận)
        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());

        // Sự kiện click nút con mắt
        togglePasswordBtn.setOnAction(e -> {
            if (passwordField.isVisible()) {
                // Đang ẩn -> Chuyển sang Hiện
                passwordField.setVisible(false);
                passwordVisibleField.setVisible(true);
                togglePasswordBtn.setText("🙈"); // Icon nhắm mắt
            } else {
                // Đang hiện -> Chuyển sang Ẩn
                passwordField.setVisible(true);
                passwordVisibleField.setVisible(false);
                togglePasswordBtn.setText("👁"); // Icon mở mắt
            }
        });

        // 4. Xếp chồng (Stack) 2 ô nhập và nút con mắt lên nhau
        StackPane passwordStack = new StackPane();
        passwordStack.setAlignment(Pos.CENTER_RIGHT); // Căn nút nằm bên phải
        passwordStack.getChildren().addAll(passwordField, passwordVisibleField, togglePasswordBtn);

        passwordVBox.getChildren().addAll(passwordLabel, passwordStack);

        // Thông báo lỗi
        errorLabel = new Label();
        errorLabel.setFont(Font.font("Segoe UI", 13));
        errorLabel.setWrapText(true);
        errorLabel.setAlignment(Pos.CENTER);
        errorLabel.setMaxWidth(Double.MAX_VALUE);

        // Buttons
        VBox buttonBox = new VBox(12);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        loginButton = new Button("Đăng Nhập");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setCursor(Cursor.HAND);
        loginButton.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 6;");

        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: " + COLOR_PRIMARY_HOVER + "; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 6;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 6;"));

        exitButton = new Button("Thoát Hệ Thống");
        exitButton.setMaxWidth(Double.MAX_VALUE);
        exitButton.setCursor(Cursor.HAND);
        exitButton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_TEXT_MUTED + "; -fx-font-size: 14px; -fx-padding: 10; -fx-border-color: transparent;");

        exitButton.setOnMouseEntered(e -> exitButton.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #0f172a; -fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 6;"));
        exitButton.setOnMouseExited(e -> exitButton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_TEXT_MUTED + "; -fx-font-size: 14px; -fx-padding: 10;"));

        buttonBox.getChildren().addAll(loginButton, exitButton);

        // Xử lý sự kiện Enter
        loginButton.setOnAction(e -> handleLogin());
        exitButton.setOnAction(e -> System.exit(0));
        passwordField.setOnAction(e -> handleLogin()); // Bấm Enter khi đang ở chế độ ẩn mk
        passwordVisibleField.setOnAction(e -> handleLogin()); // Bấm Enter khi đang ở chế độ hiện mk

        cardBox.getChildren().addAll(
                headerBox,
                usernameVBox,
                passwordVBox,
                errorLabel,
                buttonBox
        );

        root.getChildren().add(cardBox);
        return new Scene(root, 600, 500);
    }

    private void handleLogin() {
        String username = usernameTextField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ tài khoản và mật khẩu");
            return;
        }

        try {
            loginButton.setText("Đang xử lý...");
            loginButton.setDisable(true);

            TaiKhoanDTO user = authenticationService.login(username, password);

            if (user != null) {
                currentUser = user;
                showSuccess("Đăng nhập thành công! Đang chuyển trang...");
                try {
                    Stage currentStage = (Stage) loginButton.getScene().getWindow();

                    MainController mainController = new MainController(
                            currentStage, user,
                            khachHangService, nhanVienService,
                            phongService, phieuDatPhongService,
                            dichVuService
                    );
                    mainController.showMainScreen();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Lỗi khi tải trang chủ: " + ex.getMessage());
                    resetLoginButton();
                }
            } else {
                showError("Tài khoản hoặc mật khẩu không đúng");
                clearFields();
                resetLoginButton();
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            resetLoginButton();
        } catch (Exception e) {
            showError("Lỗi hệ thống: " + e.getMessage());
            e.printStackTrace();
            resetLoginButton();
        }
    }

    private void resetLoginButton() {
        loginButton.setText("Đăng Nhập");
        loginButton.setDisable(false);
    }

    private void showError(String message) {
        errorLabel.setText("⚠️ " + message);
        errorLabel.setStyle("-fx-text-fill: #ef4444; -fx-background-color: #fef2f2; -fx-padding: 8 12; -fx-background-radius: 6; -fx-border-color: #fecaca; -fx-border-radius: 6;");
    }

    private void showSuccess(String message) {
        errorLabel.setText("✅ " + message);
        errorLabel.setStyle("-fx-text-fill: #059669; -fx-background-color: #ecfdf5; -fx-padding: 8 12; -fx-background-radius: 6; -fx-border-color: #a7f3d0; -fx-border-radius: 6;");
    }

    private void clearFields() {
        passwordField.clear();
        // Do đã bindBidirectional nên passwordField.clear() sẽ tự động làm sạch cả passwordVisibleField
        passwordField.requestFocus();
    }

    public TaiKhoanDTO getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(TaiKhoanDTO currentUser) {
        this.currentUser = currentUser;
    }
}