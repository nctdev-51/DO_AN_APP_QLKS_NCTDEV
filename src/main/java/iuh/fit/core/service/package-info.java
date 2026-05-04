/**
 * Package: iuh.fit.core.service
 * 
 * TẦNG: CORE - Service Layer (Business Logic)
 * 
 * TRÁCH NHIỆM:
 * - Implement business logic (use case flows)
 * - Gọi repository để truy cập/lưu dữ liệu
 * - Validate business rules
 * - Transform data (Entity ↔ DTO)
 * - Xử lý exceptions theo business requirements
 * 
 * CẤU TRÚC:
 * ├─ Interface (IAuthenticationService.java)
 * │  └─ Định nghĩa contract
 * └─ Implementation (AuthenticationServiceImpl.java)
 *    └─ Implement logic
 * 
 * NGUYÊN TẮC:
 * ✅ Gọi Repository interfaces (KHÔNG implementation)
 * ✅ Dependency Injection qua constructor
 * ✅ Có validation logic (input checks, business rules)
 * ✅ Transform Entity ↔ DTO
 * ✅ Handle exceptions
 * ❌ KHÔNG có @Service, @Component annotations
 * ❌ KHÔNG có @Autowired (ở adapter layer nếu dùng Spring)
 * ❌ KHÔNG direct database operations (HQL, SQL)
 * ❌ KHÔNG phụ thuộc vào Presentation (JavaFX, Web)
 * ❌ KHÔNG phụ thuộc vào Infrastructure detail
 * 
 * LUỒNG ĐIỀU HÒA (Orchestration):
 * 
 * Service Method được gọi:
 *   ↓ (1) Validate input
 *   ↓ (2) Check business rules
 *   ↓ (3) Call repository methods
 *   ↓ (4) Transform result (Entity → DTO)
 *   ↓ (5) Return DTO
 * 
 * EXAMPLE - LOGIN FLOW:
 * AuthenticationServiceImpl.login(username, password):
 *   1. Validate: username & password not empty
 *   2. Call: repository.findByTaiKhoan(username) → Entity
 *   3. Check: password match
 *   4. Transform: Entity → DTO (convert)
 *   5. Return: TaiKhoanDTO
 * 
 * CÁC FILES:
 * - IAuthenticationService.java: Interface cho xác thực
 * - AuthenticationServiceImpl.java: Implement login, register, changePassword
 * - IKhachHangService.java: Interface cho quản lý khách hàng
 * - KhachHangServiceImpl.java: Implement CRUD khách hàng
 * 
 * LƯỚI ÝÝ:
 * - Service là "orchestrator" của business logic
 * - Repository là "data accessor"
 * - Service điều hòa Repository để hoàn thành use case
 * 
 * @author Clean Architecture Expert
 * @version 1.0
 */
package iuh.fit.core.service;

