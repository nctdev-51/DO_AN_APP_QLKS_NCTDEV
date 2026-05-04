package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity: TaiKhoan (Tài Khoản Đăng Nhập)
 * 
 * Tầng: CORE - Domain Layer
 * Trách nhiệm: Định nghĩa Entity Tài Khoản với mối quan hệ Many-to-One với NhanVien
 * 
 * LƯỚI ÝÝ:
 * - Mật khẩu lưu dưới dạng PLAIN TEXT (không hash)
 * - Service sẽ so sánh trực tiếp bằng .equals()
 * - Cột database: tenDangNhap, matKhau, trangThaiTK, maNhanVien
 */
@Entity
@Table(name = "tai_khoan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoan {
    
    @Id
    @Column(name = "ten_dang_nhap", length = 50)
    private String tenDangNhap;
    
    @Column(name = "mat_khau", length = 255, nullable = false)
    private String matKhau; // Plain text password (NOT hashed)
    
    @Column(name = "trang_thai_tk", nullable = false)
    private boolean trangThaiTK; // true = active, false = inactive
    
    /**
     * Mối quan hệ Many-to-One: Nhiều TaiKhoan → Một NhanVien
     * fetch = FetchType.EAGER: Lấy ngay nhân viên liên quan khi load account
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ma_nhan_vien", referencedColumnName = "ma_nhan_vien")
    private NhanVien nhanVien;
}



