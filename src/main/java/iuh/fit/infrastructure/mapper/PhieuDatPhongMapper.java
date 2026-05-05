package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.entity.PhieuDatPhong;

public class PhieuDatPhongMapper {
    public static PhieuDatPhongDTO entityToDTO(PhieuDatPhong entity) {
        if (entity == null) return null;
        return new PhieuDatPhongDTO(
                entity.getMaPhieu(),
                entity.getMaKhachHang(),
                entity.getMaPhong(),
                entity.getNgayDat(),
                entity.getNgayNhan(),
                entity.getNgayTra(),
                entity.getTongTien(),
                entity.getTrangThai(),
                entity.getMaNhanVien()
        );
    }

    public static PhieuDatPhong dtoToEntity(PhieuDatPhongDTO dto) {
        if (dto == null) return null;
        PhieuDatPhong entity = new PhieuDatPhong();
        entity.setMaPhieu(dto.getMaPhieu());
        entity.setMaKhachHang(dto.getMaKhachHang());
        entity.setMaPhong(dto.getMaPhong());
        entity.setNgayDat(dto.getNgayDat());
        entity.setNgayNhan(dto.getNgayNhan());
        entity.setNgayTra(dto.getNgayTra());
        entity.setTongTien(dto.getTongTien());
        entity.setTrangThai(dto.getTrangThai());
        entity.setMaNhanVien(dto.getMaNhanVien());
        return entity;
    }
}