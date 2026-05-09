package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO: TaiKhoanDTO
 * Mô tả: Data Transfer Object cho Tài khoản đăng nhập
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoanDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String maNhanVien;
    private String tenDangNhap;
    private String matKhau;
    private boolean trangThaiTK;
    private String hoTenNhanVien; // Thêm để hiển thị trên UI
}