package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.LoaiPhongDTO;
import iuh.fit.core.entity.LoaiPhong;

/**
 * Class: LoaiPhongMapper (Mapper/Converter)
 * 
 * Tầng: INFRASTRUCTURE - Mapper Layer
 * Trách nhiệm: Chuyển đổi giữa Entity LoaiPhong và DTO LoaiPhongDTO
 */
public class LoaiPhongMapper {
    
    /**
     * Chuyển Entity LoaiPhong → DTO LoaiPhongDTO
     */
    public static LoaiPhongDTO entityToDTO(LoaiPhong entity) {
        if (entity == null) {
            return null;
        }
        
        return new LoaiPhongDTO(
                entity.getMaLoaiPhong(),
                entity.getTenLoaiPhong(),
                entity.getMoTa()
        );
    }
    
    /**
     * Chuyển DTO LoaiPhongDTO → Entity LoaiPhong
     */
    public static LoaiPhong dtoToEntity(LoaiPhongDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return new LoaiPhong(
                dto.getMaLoaiPhong(),
                dto.getTenLoaiPhong(),
                dto.getMoTa()
        );
    }
}

