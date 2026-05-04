package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.NhanVienDTO;
import iuh.fit.core.entity.NhanVien;
import iuh.fit.core.entity.LoaiNhanVien;

/**
 * Class: NhanVienMapper (Mapper/Converter)
 * 
 * Tầng: INFRASTRUCTURE - Mapper Layer
 * Trách nhiệm: Chuyển đổi giữa Entity NhanVien và DTO NhanVienDTO
 */
public class NhanVienMapper {
    
    /**
     * Chuyển Entity NhanVien → DTO NhanVienDTO
     */
    public static NhanVienDTO entityToDTO(NhanVien entity) {
        if (entity == null) {
            return null;
        }
        
        return new NhanVienDTO(
                entity.getMaNhanVien(),
                entity.getHoTen(),
                entity.getNgaySinh(),
                entity.isGioiTinh(),
                entity.getCccd(),
                entity.getSoDienThoai(),
                entity.isTrangThai(),
                entity.getLoaiNhanVien() != null ? entity.getLoaiNhanVien().name() : "",
                entity.getNgayVaoLam(),
                entity.getQueQuan()
        );
    }
    
    /**
     * Chuyển DTO NhanVienDTO → Entity NhanVien
     */
    public static NhanVien dtoToEntity(NhanVienDTO dto) {
        if (dto == null) {
            return null;
        }
        
        NhanVien entity = new NhanVien();
        entity.setMaNhanVien(dto.getMaNhanVien());
        entity.setHoTen(dto.getHoTen());
        entity.setNgaySinh(dto.getNgaySinh());
        entity.setGioiTinh(dto.isGioiTinh());
        entity.setCccd(dto.getCccd());
        entity.setSoDienThoai(dto.getSoDienThoai());
        entity.setTrangThai(dto.isTrangThai());
        
        if (dto.getLoaiNhanVien() != null && !dto.getLoaiNhanVien().isEmpty()) {
            try {
                entity.setLoaiNhanVien(LoaiNhanVien.valueOf(dto.getLoaiNhanVien()));
            } catch (IllegalArgumentException e) {
                entity.setLoaiNhanVien(LoaiNhanVien.NHAN_VIEN_LE_TAN);
            }
        }
        
        entity.setNgayVaoLam(dto.getNgayVaoLam());
        entity.setQueQuan(dto.getQueQuan());
        
        return entity;
    }
}

