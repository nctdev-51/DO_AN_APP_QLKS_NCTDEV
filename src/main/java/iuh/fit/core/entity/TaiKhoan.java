package iuh.fit.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity: TaiKhoan (Tài khoản đăng nhập)
 * Bảng: TaiKhoan
 * Mô tả: Lưu trữ thông tin tài khoản đăng nhập của nhân viên (1 nhân viên 1 tài khoản)
 */
@Entity
@Table(name = "TaiKhoan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoan {
    @Id
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "maNhanVien")
    private NhanVien nhanVien;

    @Column(name = "tenDangNhap", length = 50, nullable = false, unique = true)
    private String tenDangNhap;

    @Column(name = "matKhau", length = 255, nullable = false)
    private String matKhau;

    @Column(name = "trangThaiTK", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean trangThaiTK;
}