/**
 * Package: iuh.fit.presentation.controller
 * 
 * TẦNG: PRESENTATION - JavaFX Controller Layer
 * 
 * TRÁCH NHIỆM:
 * - Handle UI events (button clicks, table selections, input changes)
 * - Display data on JavaFX components (TableView, TextField, ComboBox, etc.)
 * - Bind user input with business logic (call Services)
 * - Show feedback (success/error dialogs)
 * - Navigate between screens
 * 
 * NGUYÊN TẮC (MVC - Model View Controller):
 * 
 * Model (DTO):        View (FXML/UI):      Controller (class này):
 * ├─ KhachHangDTO     ├─ FXML file         ├─ @FXML annotations
 * └─ Data fields      ├─ UI components     ├─ Event handlers
 *                     └─ Layout            └─ Business calls
 * 
 * NGUYÊN TẮC:
 * ✅ Gọi Service thông qua INTERFACE (dependency injection)
 * ✅ Làm việc với DTO (KHÔNG Entity)
 * ✅ Display feedback (errors, success messages)
 * ✅ Validate input trước gửi service
 * ✅ Exception handling & user-friendly messages
 * ✅ Logging
 * ❌ KHÔNG direct repository access
 * ❌ KHÔNG direct entity manipulation
 * ❌ KHÔNG business logic
 * ❌ KHÔNG database queries (JPA, SQL)
 * 
 * LUỒNG CONTROLLER:
 * 
 * 1. USER INTERACTION:
 *    User clicks Button
 *      ↓
 *    handleButtonClick() event handler triggered
 * 
 * 2. INPUT VALIDATION:
 *    Validate textfield.getText()
 *      ↓
 *    If invalid → Show error message → Return
 * 
 * 3. CREATE DTO:
 *    Create KhachHangDTO from UI fields
 *      ↓
 *    Build DTO with user input
 * 
 * 4. CALL SERVICE:
 *    service.addKhachHang(dto)
 *      ↓
 *    Service handles business logic + database
 * 
 * 5. HANDLE RESULT:
 *    If success → Reload data, clear fields, show success
 *    If error → Show error message, keep data for retry
 * 
 * EXAMPLE - ADD CUSTOMER:
 * 
 * @FXML
 * private TextField tenTextField;
 * private TextField sdtTextField;
 * 
 * private final IKhachHangService khachHangService;
 * 
 * public QuanLyKhachHangController(IKhachHangService service) {
 *     this.khachHangService = service;  // Dependency Injection
 * }
 * 
 * @FXML
 * private void handleThemKhachHang() {
 *     try {
 *         // INPUT VALIDATION
 *         String ten = tenTextField.getText().trim();
 *         if (ten.isEmpty()) {
 *             showError("Tên không được trống");
 *             return;
 *         }
 * 
 *         // CREATE DTO
 *         KhachHangDTO dto = new KhachHangDTO();
 *         dto.setHoTen(ten);
 *         dto.setSoDienThoai(sdtTextField.getText());
 * 
 *         // CALL SERVICE
 *         KhachHangDTO created = khachHangService.addKhachHang(dto);
 * 
 *         // HANDLE SUCCESS
 *         if (created != null) {
 *             showSuccess("Thêm khách hàng thành công");
 *             loadKhachHangData();  // Reload table
 *             clearFields();        // Reset form
 *         }
 *     } catch (IllegalArgumentException e) {
 *         showError("Validation Error: " + e.getMessage());
 *     } catch (Exception e) {
 *         showError("System Error: " + e.getMessage());
 *         logger.severe(e.getMessage());
 *     }
 * }
 * 
 * CÁC FILES:
 * - LoginController.java
 *   ├─ createLoginScene(): Scene
 *   ├─ handleLogin(): xử lý đăng nhập
 *   └─ Dependencies: IAuthenticationService
 * 
 * - QuanLyKhachHangController.java
 *   ├─ createQuanLyKhachHangScene(): Scene
 *   ├─ loadKhachHangData(): load từ service
 *   ├─ handleThemKhachHang(): thêm mới
 *   ├─ handleCapNhatKhachHang(): update
 *   ├─ handleXoaKhachHang(): delete
 *   └─ Dependencies: IKhachHangService
 * 
 * FXML vs PROGRAMMATIC:
 * 
 * Ứng dụng này dùng PROGRAMMATIC (tạo UI trong code Java)
 * Điều này fine cho demo, nhưng trong production nên dùng FXML:
 * 
 * FXML (Declarative):
 * ├─ login.fxml: Define UI structure
 * ├─ quanlykhachhang.fxml: UI components
 * └─ LoginController: Handle events
 * 
 * @author Clean Architecture Expert
 * @version 1.0
 */
package iuh.fit.presentation.controller;

