package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.entity.Phong;

/**
 * Class: PhongMapper (Mapper/Converter)
 * 
 * Tầng: INFRASTRUCTURE - Mapper Layer
 * Trách nhiệm: Chuyển đổi giữa Entity Phong và DTO PhongDTO
 */
public class PhongMapper {

    public static PhongDTO entityToDTO(Phong entity) {
        if (entity == null) {
            return null;
        }
        
        PhongDTO dto = new PhongDTO();
        dto.setMaPhong(entity.getMaPhong());
        dto.setTenPhong(entity.getTenPhong());
        if (entity.getLoaiPhong() != null) {
            dto.setMaLoaiPhong(entity.getLoaiPhong().getMaLoaiPhong());
            dto.setTenLoaiPhong(entity.getLoaiPhong().getTenLoaiPhong());
        }
        dto.setGiaPhong(entity.getGiaPhong());
        dto.setTinhTrang(entity.getTinhTrang());
        
        return dto;
    }

    public static Phong dtoToEntity(PhongDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Phong phong = new Phong();
        phong.setMaPhong(dto.getMaPhong());
        phong.setTenPhong(dto.getTenPhong());
        phong.setGiaPhong(dto.getGiaPhong());
        phong.setTinhTrang(dto.getTinhTrang());
        // loaiPhong sẽ được fetch từ DB bởi Repository
        
        return phong;
    }
}

