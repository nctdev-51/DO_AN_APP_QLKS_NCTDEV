package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.entity.PhieuDatPhong;

/**
 * Class: PhieuDatPhongMapper (Mapper/Converter)
 * 
 * Tầng: INFRASTRUCTURE - Mapper Layer
 * Trách nhiệm: Chuyển đổi giữa Entity PhieuDatPhong và DTO PhieuDatPhongDTO
 */
public class PhieuDatPhongMapper {
    
    public static PhieuDatPhongDTO entityToDTO(PhieuDatPhong entity) {
        if (entity == null) {
            return null;
        }
        
        PhieuDatPhongDTO dto = new PhieuDatPhongDTO();
        dto.setMaPhieu(entity.getMaPhieu());
        if (entity.getKhachHang() != null) {
            dto.setMaKhachHang(entity.getKhachHang().getMaKhachHang());
            dto.setTenKhachHang(entity.getKhachHang().getHoTen());
        }
        if (entity.getPhong() != null) {
            dto.setMaPhong(entity.getPhong().getMaPhong());
            dto.setTenPhong(entity.getPhong().getTenPhong());
        }
        dto.setNgayDat(entity.getNgayDat());
        dto.setNgayNhan(entity.getNgayNhan());
        dto.setNgayTra(entity.getNgayTra());
        dto.setTongTien(entity.getTongTien());
        dto.setTrangThai(entity.getTrangThai());
        if (entity.getNhanVien() != null) {
            dto.setMaNhanVien(entity.getNhanVien().getMaNhanVien());
            dto.setHoTenNhanVien(entity.getNhanVien().getHoTen());
        }
        
        return dto;
    }

    public static PhieuDatPhong dtoToEntity(PhieuDatPhongDTO dto) {
        if (dto == null) {
            return null;
        }
        
        PhieuDatPhong phieu = new PhieuDatPhong();
        phieu.setMaPhieu(dto.getMaPhieu());
        phieu.setNgayDat(dto.getNgayDat());
        phieu.setNgayNhan(dto.getNgayNhan());
        phieu.setNgayTra(dto.getNgayTra());
        phieu.setTongTien(dto.getTongTien());
        phieu.setTrangThai(dto.getTrangThai());
        // Relationships (khachHang, phong, nhanVien) sẽ được fetch từ DB
        
        return phieu;
    }
}

