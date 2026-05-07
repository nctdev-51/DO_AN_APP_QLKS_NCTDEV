package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.NhanVienDTO;
import iuh.fit.core.entity.NhanVien;

public class NhanVienMapper {

    public static NhanVienDTO entityToDTO(NhanVien entity) {
        if (entity == null) return null;
        return new NhanVienDTO(
                entity.getMaNhanVien(),
                entity.getHoTen(),
                entity.getNgaySinh(),
                entity.isGioiTinh(),
                entity.getCccd(),
                entity.getSoDienThoai(),
                entity.isTrangThai(),
                entity.getLoaiNhanVien(),          // đã là String
                entity.getNgayVaoLam(),
                entity.getQueQuan()
        );
    }

    public static NhanVien dtoToEntity(NhanVienDTO dto) {
        if (dto == null) return null;
        NhanVien entity = new NhanVien();
        entity.setMaNhanVien(dto.getMaNhanVien());
        entity.setHoTen(dto.getHoTen());
        entity.setNgaySinh(dto.getNgaySinh());
        entity.setGioiTinh(dto.isGioiTinh());
        entity.setCccd(dto.getCccd());
        entity.setSoDienThoai(dto.getSoDienThoai());
        entity.setTrangThai(dto.isTrangThai());
        entity.setLoaiNhanVien(dto.getLoaiNhanVien());   // String -> String
        entity.setNgayVaoLam(dto.getNgayVaoLam());
        entity.setQueQuan(dto.getQueQuan());
        return entity;
    }
}