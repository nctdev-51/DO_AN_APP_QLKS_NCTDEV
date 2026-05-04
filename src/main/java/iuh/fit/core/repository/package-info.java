/**
 * Package: iuh.fit.core.repository
 * 
 * TẦNG: CORE - Repository Port (Interface Layer)
 * 
 * TRÁCH NHIỆM:
 * - Định nghĩa Repository interfaces (contracts/ports)
 * - Khai báo các phương thức để truy cập dữ liệu
 * - Tách biệt business logic khỏi database details
 * 
 * NGUYÊN TẮC (Inversion of Control / Dependency Inversion):
 * ✅ CORE layer định nghĩa interface
 * ✅ INFRASTRUCTURE layer implement interface
 * ✅ SERVICE layer gọi interface (KHÔNG biết implementation)
 * ✅ Các methods có tên business-focused (findByTaiKhoan, save, etc.)
 * ✅ KHÔNG có @Repository annotation (ở adapter layer)
 * ✅ KHÔNG có SQL/HQL queries (ở implementation layer)
 * ❌ KHÔNG import JPA/Hibernate
 * ❌ KHÔNG có implementation logic
 * ❌ KHÔNG có @Autowired, @Inject annotations
 * 
 * PATTERN: Hexagonal Architecture (Ports & Adapters)
 * 
 * Interface (Port)              Implementation (Adapter)
 * ├─ IRepository                ├─ RepositoryImpl (JPA)
 * ├─ Contract định sẵn          ├─ SQL/HQL queries
 * └─ CORE phụ thuộc vào nó    └─ INFRASTRUCTURE phụ thuộc vào Port
 * 
 * CÁC FILES:
 * - ITaiKhoanRepository.java: Port cho tài khoản CRUD
 * - INhanVienRepository.java: Port cho nhân viên CRUD
 * - IKhachHangRepository.java: Port cho khách hàng CRUD
 * 
 * LỢI ÍCH:
 * - Dễ test: có thể mock repository trong unit test
 * - Dễ thay đổi: implement repository khác mà service không biết
 * - Business logic độc lập: service chỉ biết interface
 * 
 * @author Clean Architecture Expert
 * @version 1.0
 */
package iuh.fit.core.repository;

