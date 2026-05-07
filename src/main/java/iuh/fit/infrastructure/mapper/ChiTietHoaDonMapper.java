package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.ChiTietHoaDonDTO;
import iuh.fit.core.entity.ChiTietHoaDon;
import iuh.fit.core.entity.ChiTietHoaDonId; // Bổ sung import khóa chính ghép

public class ChiTietHoaDonMapper {

    public static ChiTietHoaDonDTO toDTO(ChiTietHoaDon entity) {
        if (entity == null) return null;
        ChiTietHoaDonDTO dto = new ChiTietHoaDonDTO();

        // 1. Lấy mã từ object khóa chính (id)
        if (entity.getId() != null) {
            dto.setMaHoaDon(entity.getId().maHoaDon);
            dto.setMaDichVu(entity.getId().maDichVu);
        }

        dto.setSoLuong(entity.getSoLuong());

        // Lấy thông tin dịch vụ (nếu đã được map)
        if (entity.getDichVu() != null) {
            dto.setTenDichVu(entity.getDichVu().getTenDichVu());
            dto.setGiaTienTungDichVu(entity.getDichVu().getGiaTien());
        }

        dto.setThanhTien(dto.getSoLuong() * dto.getGiaTienTungDichVu());
        return dto;
    }

    public static ChiTietHoaDon toEntity(ChiTietHoaDonDTO dto) {
        if (dto == null) return null;
        ChiTietHoaDon entity = new ChiTietHoaDon();

        // 2. Set dữ liệu thông qua khóa chính ghép ChiTietHoaDonId
        ChiTietHoaDonId id = new ChiTietHoaDonId(dto.getMaHoaDon(), dto.getMaDichVu());
        entity.setId(id);

        entity.setSoLuong(dto.getSoLuong());
        return entity;
    }
}