package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.entity.PhieuDatPhong;

public class PhieuDatPhongMapper {

    public static PhieuDatPhongDTO entityToDTO(PhieuDatPhong entity) {
        if (entity == null) return null;

        // ✅ Chuyển sang dùng Constructor rỗng và dùng hàm set (an toàn hơn khi DTO thay đổi)
        PhieuDatPhongDTO dto = new PhieuDatPhongDTO();

        // 1. Map các field cơ bản
        dto.setMaPhieu(entity.getMaPhieu());
        dto.setNgayDat(entity.getNgayDat());
        dto.setNgayNhan(entity.getNgayNhan());
        dto.setNgayTra(entity.getNgayTra());
        dto.setTongTien(entity.getTongTien() != null ? entity.getTongTien() : 0.0);
        dto.setTrangThai(entity.getTrangThai());

        // 2. Map các field liên kết (Khách hàng)
        if (entity.getKhachHang() != null) {
            dto.setMaKhachHang(entity.getKhachHang().getMaKhachHang());
            dto.setTenKhachHang(entity.getKhachHang().getHoTen());
        }

        // 3. Map các field liên kết (Phòng)
        if (entity.getPhong() != null) {
            dto.setMaPhong(entity.getPhong().getMaPhong());
            dto.setTenPhong(entity.getPhong().getTenPhong());
        }

        // 4. Map các field liên kết (Nhân viên)
        if (entity.getNhanVien() != null) {
            dto.setMaNhanVien(entity.getNhanVien().getMaNhanVien());
            dto.setHoTenNhanVien(entity.getNhanVien().getHoTen());
        }

        // (Tùy chọn) Nếu Entity của bạn cũng đã có 3 trường thanh toán, hãy mở comment bên dưới:
        // dto.setLoaiThanhToan(entity.getLoaiThanhToan());
        // dto.setTienTamUng(entity.getTienTamUng());
        // dto.setTienConNo(entity.getTienConNo());

        return dto;
    }

    public static PhieuDatPhong dtoToEntity(PhieuDatPhongDTO dto) {
        if (dto == null) return null;

        PhieuDatPhong entity = new PhieuDatPhong();
        entity.setMaPhieu(dto.getMaPhieu());
        entity.setNgayDat(dto.getNgayDat());
        entity.setNgayNhan(dto.getNgayNhan());
        entity.setNgayTra(dto.getNgayTra());
        entity.setTongTien(dto.getTongTien());
        entity.setTrangThai(dto.getTrangThai());

        // (Tùy chọn) Map ngược lại nếu entity có
        // entity.setLoaiThanhToan(dto.getLoaiThanhToan());
        // entity.setTienTamUng(dto.getTienTamUng());
        // entity.setTienConNo(dto.getTienConNo());

        return entity;
    }
}