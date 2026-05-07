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

        // Dùng Constructor rỗng và các hàm set để tránh lỗi số lượng tham số
        KhachHangDTO dto = new KhachHangDTO();
        dto.setMaKhachHang(entity.getMaKhachHang());
        dto.setHoTen(entity.getHoTen());
        dto.setSoDienThoai(entity.getSoDienThoai());
        dto.setNgaySinh(entity.getNgaySinh());
        dto.setLoaiKhachHang(entity.getLoaiKhachHang());

        // (Tùy chọn) Nếu Entity của bạn có thuộc tính doiTuongKhach, hãy mở comment dòng dưới:
        // dto.setDoiTuongKhach(entity.getDoiTuongKhach());

        return dto;
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
        entity.setLoaiKhachHang(dto.getLoaiKhachHang());

        // (Tùy chọn) Nếu Entity của bạn có thuộc tính doiTuongKhach, hãy mở comment dòng dưới:
        // entity.setDoiTuongKhach(dto.getDoiTuongKhach());

        return entity;
    }
}