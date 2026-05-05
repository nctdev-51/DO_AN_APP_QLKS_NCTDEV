package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.entity.Phong;

public class PhongMapper {

    public static PhongDTO entityToDTO(Phong entity) {
        if (entity == null) return null;
        return new PhongDTO(
                entity.getMaPhong(),
                entity.getTenPhong(),
                entity.getMaLoaiPhong(),
                entity.getGiaPhong() != null ? entity.getGiaPhong() : 0.0,
                entity.getTinhTrang()
        );
    }

    public static Phong dtoToEntity(PhongDTO dto) {
        if (dto == null) return null;
        Phong entity = new Phong();
        entity.setMaPhong(dto.getMaPhong());
        entity.setTenPhong(dto.getTenPhong());
        entity.setMaLoaiPhong(dto.getMaLoaiPhong());
        entity.setGiaPhong(dto.getGiaPhong());
        entity.setTinhTrang(dto.getTinhTrang());
        return entity;
    }
}