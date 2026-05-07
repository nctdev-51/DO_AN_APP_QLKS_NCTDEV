package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.KhuyenMaiDTO;
import iuh.fit.core.entity.KhuyenMai;

/**
 * Class: KhuyenMaiMapper (Mapper/Converter)
 * 
 * Tầng: INFRASTRUCTURE - Mapper Layer
 * Trách nhiệm: Chuyển đổi giữa Entity KhuyenMai và DTO KhuyenMaiDTO
 */
public class KhuyenMaiMapper {
    
    /**
     * Chuyển Entity KhuyenMai → DTO KhuyenMaiDTO
     */
    public static KhuyenMaiDTO entityToDTO(KhuyenMai entity) {
        if (entity == null) {
            return null;
        }
        
        return new KhuyenMaiDTO(
                entity.getMaKhuyenMai(),
                entity.getTenKhuyenMai(),
                entity.getNgayBatDau(),
                entity.getNgayKetThuc(),
                entity.getLoaiKhuyenMai(),
                entity.getChietKhau()
        );
    }
    
    /**
     * Chuyển DTO KhuyenMaiDTO → Entity KhuyenMai
     */
    public static KhuyenMai dtoToEntity(KhuyenMaiDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return new KhuyenMai(
                dto.getMaKhuyenMai(),
                dto.getTenKhuyenMai(),
                dto.getNgayBatDau(),
                dto.getNgayKetThuc(),
                dto.getLoaiKhuyenMai(),
                dto.getChietKhau()
        );
    }
}

