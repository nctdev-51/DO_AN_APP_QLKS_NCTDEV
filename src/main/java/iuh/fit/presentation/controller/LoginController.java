package iuh.fit.presentation.controller;

import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.service.IAuthenticationService;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.logging.Logger;

/**
 * Controller: LoginController (Presentation Layer - Login Screen)
 * 
 * Tầng: PRESENTATION - JavaFX Controller Layer
 * Trách nhiệm: Quản lý giao diện đăng nhập
 * 
 * Nguyên tắc:
 * - Chỉ call IAuthenticationService (thông qua dependency injection)
 * - Không gọi trực tiếp infrastructure hay entity
 * - Bind UI components với business logic
 * - Validate input trước khi gửi tới service
 * 
 * Mô hình MVC:
 * - View: FXML file hoặc UI elements dưới đây
 * - Controller: LoginController (class này)
 * - Model: TaiKhoanDTO + Service
 */
public class LoginController {
    
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());
    
    // ============ UI COMPONENTS ============
    private TextField usernameTextField;
    private PasswordField passwordField;
    private Button loginButton;
    private Button exitButton;
    private Label errorLabel;
    
    // ============ SERVICES (Dependency Injection) ============
    private IAuthenticationService authenticationService;
    
    // ============ STATE ============
    private TaiKhoanDTO currentUser;
    
    /**
     * Constructor: Inject Service
     * @param authenticationService Implementation của IAuthenticationService
     */
    public LoginController(IAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    /**
     * Khởi tạo giao diện programmatically (không dùng FXML)
     * @return Scene chứa toàn bộ login form
     */
    public Scene createLoginScene() {
        // ============ CREATE UI COMPONENTS ============
        Label titleLabel = new Label("ĐĂNG NHẬP HỆ THỐNG");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold");
        
        Label usernameLabel = new Label("Tài khoản:");
        usernameTextField = new TextField();
        usernameTextField.setPromptText("Nhập tài khoản");
        
        Label passwordLabel = new Label("Mật khẩu:");
        passwordField = new PasswordField();
        passwordField.setPromptText("Nhập mật khẩu");
        
        // Error Label
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12");
        
        // Buttons
        loginButton = new Button("Đăng Nhập");
        loginButton.setPrefWidth(100);
        loginButton.setStyle("-fx-font-size: 14; -fx-padding: 10");
        
        exitButton = new Button("Thoát");
        exitButton.setPrefWidth(100);
        exitButton.setStyle("-fx-font-size: 14; -fx-padding: 10");
        
        // ============ EVENT HANDLERS ============
        loginButton.setOnAction(e -> handleLogin());
        exitButton.setOnAction(e -> System.exit(0));
        
        // Allow Enter key to submit
        passwordField.setOnAction(e -> handleLogin());
        
        // ============ LAYOUT ============
        VBox formVBox = new VBox(10);
        formVBox.setStyle("-fx-padding: 20");
        formVBox.setAlignment(Pos.CENTER);
        
        // Username input
        VBox usernameVBox = new VBox(5);
        usernameVBox.getChildren().addAll(usernameLabel, usernameTextField);
        
        // Password input
        VBox passwordVBox = new VBox(5);
        passwordVBox.getChildren().addAll(passwordLabel, passwordField);
        
        // Buttons
        HBox buttonHBox = new HBox(10);
        buttonHBox.setAlignment(Pos.CENTER);
        buttonHBox.getChildren().addAll(loginButton, exitButton);
        
        // Add all to form
        formVBox.getChildren().addAll(
                titleLabel,
                new Separator(),
                usernameVBox,
                passwordVBox,
                errorLabel,
                buttonHBox
        );
        
        // Center in window
        BorderPane root = new BorderPane();
        root.setCenter(formVBox);
        
        return new Scene(root, 400, 350);
    }
    
    /**
     * Xử lý sự kiện đăng nhập
     * Gọi từ button click hoặc enter key
     */
    private void handleLogin() {
        // ============ INPUT VALIDATION ============
        String username = usernameTextField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ tài khoản và mật khẩu");
            return;
        }
        
        // ============ CALL SERVICE ============
        try {
            logger.info("🔑 Đang xác thực: " + username);
            
            // Gọi service để xác thực
            TaiKhoanDTO user = authenticationService.login(username, password);
            
            if (user != null) {
                logger.info("✅ Đăng nhập thành công: " + username);
                currentUser = user;
                
                // Hiển thị thông báo thành công
                showSuccess("Đăng nhập thành công!");
                
                // TODO: Navigate to main screen
                // Ví dụ: MainApp.showMainScreen(user);
                
            } else {
                logger.warning("❌ Đăng nhập thất bại: Username hoặc password sai");
                showError("Tài khoản hoặc mật khẩu không đúng");
                clearFields();
            }
        } catch (IllegalArgumentException e) {
            logger.warning("⚠️ " + e.getMessage());
            showError(e.getMessage());
        } catch (Exception e) {
            logger.severe("❌ Lỗi không xác định: " + e.getMessage());
            showError("Lỗi hệ thống: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Hiển thị thông báo lỗi
     */
    private void showError(String message) {
        errorLabel.setText("❌ " + message);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12");
    }
    
    /**
     * Hiển thị thông báo thành công
     */
    private void showSuccess(String message) {
        errorLabel.setText("✅ " + message);
        errorLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12");
    }
    
    /**
     * Xóa các trường input
     */
    private void clearFields() {
        usernameTextField.clear();
        passwordField.clear();
        usernameTextField.requestFocus();
    }
    
    /**
     * Getter: Lấy user hiện tại đã đăng nhập
     */
    public TaiKhoanDTO getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Setter: Set user
     */
    public void setCurrentUser(TaiKhoanDTO currentUser) {
        this.currentUser = currentUser;
    }
}

