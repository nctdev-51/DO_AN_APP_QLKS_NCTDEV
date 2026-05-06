package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.KhachHangDTO;
import iuh.fit.core.entity.KhachHang;

/**
 * Class: KhachHangMapper (Mapper/Converter)
 *
 * Tầng: INFRASTRUCTURE - Mapper Layer
 * Trách nhiệm: Chuyển đổi giữa Entity KhachHang và DTO KhachHangDTO
 */
public class KhachHangMapper {

    /**
     * Chuyển Entity KhachHang → DTO KhachHangDTO
     */
    public static KhachHangDTO entityToDTO(KhachHang entity) {
        if (entity == null) {
            return null;
        }

        return new KhachHangDTO(
                entity.getMaKhachHang(),
                entity.getHoTen(),
                entity.getSoDienThoai(),
                entity.getNgaySinh(),
                entity.getLoaiKhachHang() // Đã chuyển thành String, truyền thẳng trực tiếp
        );
    }

    /**
     * Chuyển DTO KhachHangDTO → Entity KhachHang
     */
    public static KhachHang dtoToEntity(KhachHangDTO dto) {
        if (dto == null) {
            return null;
        }

        KhachHang entity = new KhachHang();
        entity.setMaKhachHang(dto.getMaKhachHang());
        entity.setHoTen(dto.getHoTen());
        entity.setSoDienThoai(dto.getSoDienThoai());
        entity.setNgaySinh(dto.getNgaySinh());
        entity.setLoaiKhachHang(dto.getLoaiKhachHang()); // Gán trực tiếp String sang String

        return entity;
    }
}