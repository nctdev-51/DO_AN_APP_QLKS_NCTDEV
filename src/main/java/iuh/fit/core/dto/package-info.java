/**
 * Package: iuh.fit.core.dto
 * 
 * TẦNG: CORE - DTO (Data Transfer Object) Layer
 * 
 * TRÁCH NHIỆM:
 * - Định nghĩa DTO classes để truyền dữ liệu giữa các tầng
 * - Tách biệt Entity khỏi Presentation layer (không lộ Entity)
 * - Định hình API contract giữa Service và Controller
 * 
 * NGUYÊN TẮC:
 * ✅ Chứa các trường cần thiết cho transfer (không cần toàn bộ entity fields)
 * ✅ Sử dụng @Data, @Getter, @Setter (Lombok)
 * ✅ Chỉ chứa data, KHÔNG có business logic
 * ✅ Có thể chứa field đã transform (VD: Enum → String)
 * ❌ KHÔNG chứa @Entity, @Table
 * ❌ KHÔNG chứa relationships (thay vào đó dùng ID hoặc String)
 * ❌ KHÔNG phụ thuộc vào JPA/Hibernate
 * 
 * LUỒNG SỬ DỤNG:
 * Service → Mapper → Entity → Database
 *    ↓
 * Database → Entity → Mapper → Service → DTO → Controller → UI
 * 
 * CÁC FILES:
 * - TaiKhoanDTO.java: DTO cho tài khoản (tên + ID nhân viên, không mật khẩu trong response)
 * - NhanVienDTO.java: DTO cho nhân viên
 * - KhachHangDTO.java: DTO cho khách hàng
 * 
 * ƯỠNG:
 * DTO không nên chứa sensitive data (VD: password) trong response
 * 
 * @author Clean Architecture Expert
 * @version 1.0
 */
package iuh.fit.core.dto;

