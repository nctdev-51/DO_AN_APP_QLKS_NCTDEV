package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.ChiTietHoaDonDTO;
import iuh.fit.core.entity.ChiTietHoaDon;

public class ChiTietHoaDonMapper {

    public static ChiTietHoaDonDTO toDTO(ChiTietHoaDon chiTiet) {
        if (chiTiet == null) return null;

        ChiTietHoaDonDTO dto = new ChiTietHoaDonDTO();
        if (chiTiet.getId() != null) {
            dto.setMaHoaDon(chiTiet.getId().getMaHoaDon());
            dto.setMaDichVu(chiTiet.getId().getMaDichVu());
        }
        dto.setSoLuong(chiTiet.getSoLuong());
        dto.setGiaTienTungDichVu(chiTiet.getGiaTienTungDichVu());
        dto.setThanhTien(chiTiet.getThanhTien());

        if (chiTiet.getDichVu() != null) {
            dto.setTenDichVu(chiTiet.getDichVu().getTenDichVu());
        }

        return dto;
    }

    public static ChiTietHoaDon toEntity(ChiTietHoaDonDTO dto) {
        if (dto == null) return null;

        ChiTietHoaDon chiTiet = new ChiTietHoaDon();
        chiTiet.setSoLuong(dto.getSoLuong());
        chiTiet.setGiaTienTungDichVu(dto.getGiaTienTungDichVu());

        return chiTiet;
    }
}

