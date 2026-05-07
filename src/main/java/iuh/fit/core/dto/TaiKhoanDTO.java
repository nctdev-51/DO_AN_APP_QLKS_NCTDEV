package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO: TaiKhoanDTO
 * Mô tả: Data Transfer Object cho Tài khoản đăng nhập
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoanDTO {
    private String maNhanVien;
    private String tenDangNhap;
    private String matKhau;
    private boolean trangThaiTK;
    private String hoTenNhanVien; // Thêm để hiển thị trên UI
}