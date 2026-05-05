package iuh.fit.presentation.controller;

import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.repository.IKhachHangRepository;
import iuh.fit.core.repository.INhanVienRepository;
import iuh.fit.core.repository.IPhieuDatPhongRepository;
import iuh.fit.core.repository.IPhongRepository;
import iuh.fit.core.service.*;
import iuh.fit.core.service.impl.KhachHangServiceImpl;
import iuh.fit.core.service.impl.NhanVienServiceImpl;
import iuh.fit.core.service.impl.PhieuDatPhongServiceImpl;
import iuh.fit.core.service.impl.PhongServiceImpl;
import iuh.fit.infrastructure.persistence.KhachHangRepositoryImpl;
import iuh.fit.infrastructure.persistence.NhanVienRepositoryImpl;
import iuh.fit.infrastructure.persistence.PhieuDatPhongRepositoryImpl;
import iuh.fit.infrastructure.persistence.PhongRepositoryImpl;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.logging.Logger;

public class LoginController {
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());
    private TextField usernameTextField;
    private PasswordField passwordField;
    private Button loginButton;
    private Button exitButton;
    private Label errorLabel;
    private IAuthenticationService authenticationService;
    private TaiKhoanDTO currentUser;
    private IKhachHangService khachHangService;
    private INhanVienService nhanVienService;
    private IPhongService phongService;
    private IPhieuDatPhongService phieuDatPhongService;

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
        Label titleLabel = new Label("ĐĂNG NHẬP HỆ THỐNG");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold");
        Label usernameLabel = new Label("Tài khoản:");
        usernameTextField = new TextField();
        usernameTextField.setPromptText("Nhập tài khoản");
        Label passwordLabel = new Label("Mật khẩu:");
        passwordField = new PasswordField();
        passwordField.setPromptText("Nhập mật khẩu");
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12");
        loginButton = new Button("Đăng Nhập");
        loginButton.setPrefWidth(100);
        loginButton.setStyle("-fx-font-size: 14; -fx-padding: 10");
        exitButton = new Button("Thoát");
        exitButton.setPrefWidth(100);
        exitButton.setStyle("-fx-font-size: 14; -fx-padding: 10");

        loginButton.setOnAction(e -> handleLogin());
        exitButton.setOnAction(e -> System.exit(0));
        passwordField.setOnAction(e -> handleLogin());

        VBox formVBox = new VBox(10);
        formVBox.setStyle("-fx-padding: 20");
        formVBox.setAlignment(Pos.CENTER);
        VBox usernameVBox = new VBox(5);
        usernameVBox.getChildren().addAll(usernameLabel, usernameTextField);
        VBox passwordVBox = new VBox(5);
        passwordVBox.getChildren().addAll(passwordLabel, passwordField);
        HBox buttonHBox = new HBox(10);
        buttonHBox.setAlignment(Pos.CENTER);
        buttonHBox.getChildren().addAll(loginButton, exitButton);

        formVBox.getChildren().addAll(
                titleLabel,
                new Separator(),
                usernameVBox,
                passwordVBox,
                errorLabel,
                buttonHBox
        );
        BorderPane root = new BorderPane();
        root.setCenter(formVBox);
        return new Scene(root, 400, 350);
    }

    private void handleLogin() {
        String username = usernameTextField.getText().trim();
        String password = passwordField.getText().trim();
        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ tài khoản và mật khẩu");
            return;
        }
        try {
            TaiKhoanDTO user = authenticationService.login(username, password);
            if (user != null) {
                currentUser = user;
                showSuccess("Đăng nhập thành công! Đang chuyển trang...");
                try {
                    Stage currentStage = (Stage) loginButton.getScene().getWindow();

                    IKhachHangRepository khRepo = new KhachHangRepositoryImpl();
                    INhanVienRepository nvRepo = new NhanVienRepositoryImpl();
                    IPhongRepository phongRepo = new PhongRepositoryImpl();
                    IPhieuDatPhongRepository phieuRepo = new PhieuDatPhongRepositoryImpl();

                    IKhachHangService khService = new KhachHangServiceImpl(khRepo);
                    INhanVienService nvService = new NhanVienServiceImpl(nvRepo);
                    IPhongService phongService = new PhongServiceImpl(phongRepo);
                    IPhieuDatPhongService phieuService = new PhieuDatPhongServiceImpl(phieuRepo);

                    MainController mainController = new MainController(
                            currentStage, user,
                            khService, nvService,
                            phongService, phieuService
                    );
                    mainController.showMainScreen();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Lỗi khi tải trang chủ: " + ex.getMessage());
                }
            } else {
                showError("Tài khoản hoặc mật khẩu không đúng");
                clearFields();
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Lỗi hệ thống: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12");
    }

    private void showSuccess(String message) {
        errorLabel.setText("✅ " + message);
        errorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12");
    }

    private void clearFields() {
        usernameTextField.clear();
        passwordField.clear();
        usernameTextField.requestFocus();
    }

    public TaiKhoanDTO getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(TaiKhoanDTO currentUser) {
        this.currentUser = currentUser;
    }
}