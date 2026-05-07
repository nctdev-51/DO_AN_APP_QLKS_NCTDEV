package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.DichVuDTO;
import iuh.fit.core.entity.DichVu;

public class DichVuMapper {
    public static DichVuDTO entityToDTO(DichVu entity) {
        if (entity == null) return null;
        return new DichVuDTO(
                entity.getMaDichVu(),
                entity.getTenDichVu(),
                entity.getGiaTien(), // Trả lại như cũ, không cần check null vì đây là double nguyên thủy
                entity.getMoTa()
        );
    }

    public static DichVu dtoToEntity(DichVuDTO dto) {
        if (dto == null) return null;
        return new DichVu(
                dto.getMaDichVu(),
                dto.getTenDichVu(),
                dto.getGiaTien(),
                dto.getMoTa()
        );
    }
}