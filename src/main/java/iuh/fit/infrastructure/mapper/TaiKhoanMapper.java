package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.TaiKhoanDTO;
import iuh.fit.core.entity.TaiKhoan;

/**
 * Class: TaiKhoanMapper (Mapper/Converter)
 * 
 * Tầng: INFRASTRUCTURE - Mapper Layer
 * Trách nhiệm: Chuyển đổi giữa Entity và DTO
 * 
 * Lý do cần Mapper:
 * - Entity không được expose trực tiếp ra ngoài
 * - DTO dùng để truyền dữ liệu giữa các tầng
 * - Mapper tập trung logic chuyển đổi ở một chỗ
 * 
 * Pattern: Object Mapping
 * - entity → dto (khi gửi dữ liệu từ DB ra presentation)
 * - dto → entity (khi nhận dữ liệu từ presentation vào DB)
 */
public class TaiKhoanMapper {
    
    /**
     * Chuyển Entity TaiKhoan → DTO TaiKhoanDTO
     * Dùng khi: Lấy dữ liệu từ DB trả về cho presentation
     * 
     * @param entity Entity TaiKhoan từ database
     * @return DTO TaiKhoanDTO để gửi cho client
     */
    public static TaiKhoanDTO entityToDTO(TaiKhoan entity) {
        if (entity == null) {
            return null;
        }
        
        TaiKhoanDTO dto = new TaiKhoanDTO();
        dto.setTaiKhoan(entity.getTenDangNhap());
        // Lưu ý: Không set mật khẩu vào DTO để gửi cho client (security)
        // dto.setMatKhau(entity.getMatKhau()); // ❌ Không nên
        
        if (entity.getNhanVien() != null) {
            dto.setMaNhanVien(entity.getNhanVien().getMaNhanVien());
            dto.setHoTenNhanVien(entity.getNhanVien().getHoTen());
        }
        
        return dto;
    }
    
    /**
     * Chuyển DTO TaiKhoanDTO → Entity TaiKhoan
     * Dùng khi: Nhận dữ liệu từ presentation lưu vào DB
     * 
     * @param dto DTO TaiKhoanDTO từ client
     * @return Entity TaiKhoan để lưu vào database
     */
    public static TaiKhoan dtoToEntity(TaiKhoanDTO dto) {
        if (dto == null) {
            return null;
        }
        
        TaiKhoan entity = new TaiKhoan();
        entity.setTenDangNhap(dto.getTaiKhoan());
        entity.setMatKhau(dto.getMatKhau());
        // Lưu ý: NhanVien phải được load từ repository, không set trực tiếp từ DTO
        // entity.setNhanVien(...); // Phải load từ DB
        
        return entity;
    }
}

