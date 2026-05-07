package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.ChiTietHoaDonDTO;
import iuh.fit.core.entity.ChiTietHoaDon;

public class ChiTietHoaDonMapper {

    public static ChiTietHoaDonDTO toDTO(ChiTietHoaDon entity) {
        if (entity == null) return null;
        ChiTietHoaDonDTO dto = new ChiTietHoaDonDTO();
        dto.setMaHoaDon(entity.getMaHoaDon());
        dto.setMaDichVu(entity.getMaDichVu());
        dto.setSoLuong(entity.getSoLuong());
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
        entity.setMaHoaDon(dto.getMaHoaDon());
        entity.setMaDichVu(dto.getMaDichVu());
        entity.setSoLuong(dto.getSoLuong());
        return entity;
    }
}

