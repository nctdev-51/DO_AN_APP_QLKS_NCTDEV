package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.HoaDonDTO;
import iuh.fit.core.entity.HoaDon;

/**
 * Class: HoaDonMapper (Mapper/Converter)
 * 
 * Tầng: INFRASTRUCTURE - Mapper Layer
 * Trách nhiệm: Chuyển đổi giữa Entity HoaDon và DTO HoaDonDTO
 */
public class HoaDonMapper {
    
    /**
     * Chuyển Entity HoaDon → DTO HoaDonDTO
     */
    public static HoaDonDTO entityToDTO(HoaDon entity) {
        if (entity == null) {
            return null;
        }
        
        HoaDonDTO dto = new HoaDonDTO();
        dto.setMaHoaDon(entity.getMaHoaDon());
        if (entity.getNhanVien() != null) {
            dto.setMaNhanVien(entity.getNhanVien().getMaNhanVien());
            dto.setHoTenNhanVien(entity.getNhanVien().getHoTen());
        }
        if (entity.getKhachHang() != null) {
            dto.setMaKhachHang(entity.getKhachHang().getMaKhachHang());
            dto.setTenKhachHang(entity.getKhachHang().getHoTen());
        }
        dto.setNgayLap(entity.getNgayLap());
        dto.setThueVAT(entity.getThueVAT());
        if (entity.getKhuyenMai() != null) {
            dto.setMaKhuyenMai(entity.getKhuyenMai().getMaKhuyenMai());
            dto.setTenKhuyenMai(entity.getKhuyenMai().getTenKhuyenMai());
        }
        if (entity.getPhong() != null) {
            dto.setMaPhongDat(entity.getPhong().getMaPhong());
            dto.setTenPhong(entity.getPhong().getTenPhong());
        }
        dto.setGhiChu(entity.getGhiChu());
        dto.setTongTien(entity.getTongTien());
        dto.setTongTienPhong(entity.getTongTienPhong());
        dto.setTongTienDichVu(entity.getTongTienDichVu());
        dto.setChietKhau(entity.getChietKhau());
        dto.setTrangThaiThanhToan(entity.getTrangThaiThanhToan());

        return dto;
    }
    
    /**
     * Chuyển DTO HoaDonDTO → Entity HoaDon
     */
    public static HoaDon dtoToEntity(HoaDonDTO dto) {
        if (dto == null) {
            return null;
        }
        
        HoaDon entity = new HoaDon();
        entity.setMaHoaDon(dto.getMaHoaDon());
        entity.setNgayLap(dto.getNgayLap());
        entity.setThueVAT(dto.getThueVAT());
        entity.setGhiChu(dto.getGhiChu());
        entity.setTongTien(dto.getTongTien());
        entity.setTongTienPhong(dto.getTongTienPhong());
        entity.setTongTienDichVu(dto.getTongTienDichVu());
        entity.setChietKhau(dto.getChietKhau());
        entity.setTrangThaiThanhToan(dto.getTrangThaiThanhToan());
        entity.setMaPhongDat(dto.getMaPhongDat());
        entity.setTenPhong(dto.getTenPhong());
        entity.setMaKhachHang(dto.getMaKhachHang());
        entity.setMaNhanVien(dto.getMaNhanVien());
        entity.setMaKhuyenMai(dto.getMaKhuyenMai());
        // Relationships (nhanVien, khachHang, khuyenMai, phong) sẽ được fetch từ DB
        
        return entity;
    }
}

