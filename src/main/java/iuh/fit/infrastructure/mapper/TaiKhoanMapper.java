package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.entity.TaiKhoan;

/**
 * Class: TaiKhoanMapper (Mapper/Converter)
 * 
 * Tầng: INFRASTRUCTURE - Mapper Layer
 * Trách nhiệm: Chuyển đổi giữa Entity TaiKhoan và DTO TaiKhoanDTO
 */
public class TaiKhoanMapper {
    
    /**
     * Chuyển Entity TaiKhoan → DTO TaiKhoanDTO
     */
    public static TaiKhoanDTO entityToDTO(TaiKhoan entity) {
        if (entity == null) {
            return null;
        }
        
        TaiKhoanDTO dto = new TaiKhoanDTO();
        dto.setTenDangNhap(entity.getTenDangNhap());
        // Lưu ý: Không set mật khẩu vào DTO để gửi cho client (security)
        dto.setTrangThaiTK(entity.isTrangThaiTK());
        
        if (entity.getNhanVien() != null) {
            dto.setMaNhanVien(entity.getNhanVien().getMaNhanVien());
            dto.setHoTenNhanVien(entity.getNhanVien().getHoTen());
        }
        
        return dto;
    }
    
    /**
     * Chuyển DTO TaiKhoanDTO → Entity TaiKhoan
     */
    public static TaiKhoan dtoToEntity(TaiKhoanDTO dto) {
        if (dto == null) {
            return null;
        }
        
        TaiKhoan taiKhoan = new TaiKhoan();
        taiKhoan.setTenDangNhap(dto.getTenDangNhap());
        taiKhoan.setMatKhau(dto.getMatKhau());
        taiKhoan.setTrangThaiTK(dto.isTrangThaiTK());
        // nhanVien sẽ được fetch từ DB bởi Repository
        
        return taiKhoan;
    }
}



