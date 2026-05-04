package iuh.fit.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO: TaiKhoanDTO (Data Transfer Object cho Tài Khoản)
 * 
 * Tầng: CORE - DTO Layer
 * Trách nhiệm: Truyền dữ liệu Tài Khoản giữa các tầng
 * 
 * Lưu ý: TaiKhoanDTO chỉ chứa thông tin cơ bản, không chứa toàn bộ chi tiết NhanVienDTO
 * để tránh overload data và tăng bảo mật (không lộ chi tiết nhân viên khi trả về account info)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoanDTO {
    private String taiKhoan;
    private String matKhau; // Lưu ý: Trong thực tế nên hash password
    private String maNhanVien; // Chỉ giữ ID reference, không giữ toàn bộ object
    private String hoTenNhanVien; // Lưu tên để hiển thị UI
}

