package iuh.fit.presentation.controller;

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
    private TextField passwordVisibleField; // Thêm ô nhập text thường để hiện mật khẩu
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

    // --- BẢNG MÀU UI ---
    private final String COLOR_PRIMARY = "#0066cc";  // Xanh lam đậm khớp logo
    private final String COLOR_ACCENT = "#17a2b8";   // Xanh lục khớp logo
    private final String COLOR_PRIMARY_HOVER = "#004999";
    private final String COLOR_BG_START = "#f0f8ff";
    private final String COLOR_BG_END = "#ffffff";
    private final String COLOR_TEXT_MAIN = "#1e293b";
    private final String COLOR_TEXT_MUTED = "#64748b";
    private final String COLOR_BORDER = "#cbd5e1";
    private final String COLOR_CARD_BORDER = "#d3d3d3";

    public LoginController(IAuthenticationService authenticationService,
                           IKhachHangService khachHangService,
                           INhanVienService nhanVienService,
                           IPhongService phongService,
                           IPhieuDatPhongService phieuDatPhongService) {
        this.authenticationService = authenticationService;
        this.khachHangService = khachHangService;
        this.nhanVienService = nhanVienService;
        this.phongService = phongService;
        this.phieuDatPhongService = phieuDatPhongService;
    }

    public Scene createLoginScene() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: " + COLOR_BG_START + ";");

        VBox cardBox = new VBox(20);
        cardBox.setPadding(new Insets(40, 40, 40, 40));
        cardBox.setMaxWidth(400);
        cardBox.setMaxHeight(450);
        cardBox.setAlignment(Pos.TOP_CENTER);
        cardBox.setStyle("-fx-background-color: white; -fx-background-radius: 16; -fx-border-radius: 16; -fx-border-color: #dbeafe; -fx-border-width: 1.2;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.08));
        shadow.setRadius(20);
        shadow.setOffsetY(10);
        cardBox.setEffect(shadow);

        // Header
         VBox headerBox = new VBox(5);
         headerBox.setAlignment(Pos.CENTER);

         // Logo + Title
         HBox logoTitleBox = new HBox(6);
         logoTitleBox.setAlignment(Pos.CENTER);

         // Tạo ImageView cho logo - To hơn
         ImageView logoImageView = new ImageView();
         try {
             Image logoImage = new Image(getClass().getResourceAsStream("/images/logo_ttv.png"));
             logoImageView.setImage(logoImage);
             logoImageView.setFitWidth(78);
             logoImageView.setFitHeight(78);
             logoImageView.setPreserveRatio(true);

             // Thêm shadow effect cho logo
             DropShadow logoShadow = new DropShadow();
             logoShadow.setColor(Color.web(COLOR_PRIMARY, 0.3));
             logoShadow.setRadius(8);
             logoShadow.setOffsetY(2);
             logoImageView.setEffect(logoShadow);
         } catch (Exception e) {
             // Nếu logo không tìm được, sử dụng emoji
             Label logoEmoji = new Label("🏨");
             logoEmoji.setFont(Font.font(60));
             logoTitleBox.getChildren().add(logoEmoji);
         }

         if (logoImageView.getImage() != null) {
             logoTitleBox.getChildren().add(logoImageView);
         }

         // Tên với kiểu chữ phong cách hơn
         Label titleLabel = new Label("TTV HOTEL");
         titleLabel.setFont(Font.font("Segoe UI Semibold", FontWeight.EXTRA_BOLD, 32));
         titleLabel.setTextFill(new LinearGradient(
                 0, 0, 1, 0,
                 true, CycleMethod.NO_CYCLE,
                 new Stop(0.0, Color.web(COLOR_ACCENT)),
                 new Stop(1.0, Color.web(COLOR_PRIMARY))
         ));

         // Thêm shadow effect cho text
         DropShadow titleShadow = new DropShadow();
         titleShadow.setColor(Color.web(COLOR_PRIMARY, 0.2));
         titleShadow.setRadius(5);
         titleShadow.setOffsetY(2);
         titleLabel.setEffect(titleShadow);

         logoTitleBox.getChildren().add(titleLabel);

         // Subtitle
         Label subTitleLabel = new Label("Hệ thống quản lý khách sạn TTV");
         subTitleLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
         subTitleLabel.setTextFill(Color.web(COLOR_TEXT_MUTED));

         headerBox.getChildren().addAll(logoTitleBox, subTitleLabel);

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
        togglePasswordBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_ACCENT + "; -fx-cursor: hand; -fx-font-size: 15px;");
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

        // Progress Bar
        progressBar = new ProgressBar();
        progressBar.setVisible(false);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(10);
        progressBar.setStyle("-fx-accent: " + COLOR_ACCENT + ";");

        // Buttons
        VBox buttonBox = new VBox(12);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        loginButton = new Button("Đăng Nhập");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setCursor(Cursor.HAND);
        loginButton.setStyle("-fx-background-color: linear-gradient(to right, " + COLOR_ACCENT + ", " + COLOR_PRIMARY + "); -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 10;");

        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: linear-gradient(to right, #0ea5a4, " + COLOR_PRIMARY_HOVER + "); -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 10;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: linear-gradient(to right, " + COLOR_ACCENT + ", " + COLOR_PRIMARY + "); -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 10;"));

        exitButton = new Button("Thoát Hệ Thống");
        exitButton.setMaxWidth(Double.MAX_VALUE);
        exitButton.setCursor(Cursor.HAND);
        exitButton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_PRIMARY + "; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10; -fx-border-color: " + COLOR_PRIMARY + "; -fx-border-radius: 10; -fx-background-radius: 10;");

        exitButton.setOnMouseEntered(e -> exitButton.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: " + COLOR_ACCENT + "; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10; -fx-border-color: " + COLOR_ACCENT + "; -fx-border-radius: 10; -fx-background-radius: 10;"));
        exitButton.setOnMouseExited(e -> exitButton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + COLOR_PRIMARY + "; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10; -fx-border-color: " + COLOR_PRIMARY + "; -fx-border-radius: 10; -fx-background-radius: 10;"));

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
                progressBar,
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
            progressBar.setVisible(true);

            TaiKhoanDTO user = authenticationService.login(username, password);

            if (user != null) {
                currentUser = user;
                showSuccess("Đăng nhập thành công! Đang chuyển trang...");
                // Thêm hiệu ứng delay 1 giây trước khi chuyển trang
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(event -> {
                    try {
                        Stage currentStage = (Stage) loginButton.getScene().getWindow();

                        MainController mainController = new MainController(
                                currentStage, user,
                                khachHangService, nhanVienService,
                                phongService, phieuDatPhongService
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
        progressBar.setVisible(false);
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
