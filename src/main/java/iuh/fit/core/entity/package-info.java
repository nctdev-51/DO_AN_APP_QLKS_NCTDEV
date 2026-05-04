/**
 * Package: iuh.fit.core.entity
 * 
 * TẦNG: CORE - Domain Layer
 * 
 * TRÁCH NHIỆM:
 * - Định nghĩa Entity classes (Domain Models)
 * - Map Entity vào Database tables thông qua @Entity
 * - Định nghĩa Enum types cho business constants
 * 
 * NGUYÊN TẮC:
 * ✅ Chỉ chứa Entity và Enum
 * ✅ Sử dụng @Entity, @Table, @Column, @Enumerated annotations (JPA)
 * ✅ Định nghĩa relationships (@ManyToOne, @OneToMany, etc.)
 * ❌ KHÔNG chứa business logic
 * ❌ KHÔNG chứa @Service, @Repository annotations
 * ❌ KHÔNG phụ thuộc vào Infrastructure hoặc Presentation layers
 * 
 * CÁC FILES:
 * - TaiKhoan.java: Entity cho tài khoản đăng nhập
 * - NhanVien.java: Entity cho nhân viên
 * - KhachHang.java: Entity cho khách hàng
 * - Phong.java: Entity cho phòng
 * - PhieuDatPhong.java: Entity cho phiếu đặt phòng
 * - HoaDon.java: Entity cho hóa đơn
 * - DichVu.java: Entity cho dịch vụ
 * - KhuyenMai.java: Entity cho khuyến mãi
 * - CaLamViec.java: Entity cho ca làm việc
 * - PhanCongCaLamViec.java: Entity cho phân công ca
 * - LoaiNhanVien.java: Enum cho loại nhân viên
 * - LoaiKhachHang.java: Enum cho loại khách hàng
 * - TinhTrangPhong.java: Enum cho trạng thái phòng
 * 
 * @author Clean Architecture Expert
 * @version 1.0
 */
package iuh.fit.core.entity;

