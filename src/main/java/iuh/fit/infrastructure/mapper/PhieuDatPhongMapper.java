package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.entity.PhieuDatPhong;

public class PhieuDatPhongMapper {

    public static PhieuDatPhongDTO entityToDTO(PhieuDatPhong entity) {
        if (entity == null) return null;

        PhieuDatPhongDTO dto = new PhieuDatPhongDTO();

        // 1. Map các field cơ bản (Dùng .trim() để xóa khoảng trắng thừa từ CHAR)
        dto.setMaPhieu(entity.getMaPhieu() != null ? entity.getMaPhieu().trim() : null);
        dto.setNgayDat(entity.getNgayDat());
        dto.setNgayNhan(entity.getNgayNhan());
        dto.setNgayTra(entity.getNgayTra());
        dto.setTongTien(entity.getTongTien() != null ? entity.getTongTien() : 0.0);
        dto.setTrangThai(entity.getTrangThai() != null ? entity.getTrangThai().trim() : null);

        // 2. Map Khách hàng (Đảm bảo Repository đã JOIN FETCH p.khachHang)
        if (entity.getKhachHang() != null) {
            dto.setMaKhachHang(entity.getKhachHang().getMaKhachHang().trim());
            dto.setTenKhachHang(entity.getKhachHang().getHoTen());
            // Tú có thể gán thêm SĐT vào DTO nếu trong DTO có field này
        } else {
            dto.setTenKhachHang("N/A");
        }

        // 3. Map Phòng (Đảm bảo Repository đã JOIN FETCH p.phong)
        if (entity.getPhong() != null) {
            dto.setMaPhong(entity.getPhong().getMaPhong().trim());
            dto.setTenPhong(entity.getPhong().getTenPhong());
        } else {
            dto.setTenPhong("Phòng không xác định");
        }

        // 4. Map Nhân viên
        if (entity.getNhanVien() != null) {
            dto.setMaNhanVien(entity.getNhanVien().getMaNhanVien().trim());
            dto.setHoTenNhanVien(entity.getNhanVien().getHoTen());
        }

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

        // 👉 BƯỚC QUAN TRỌNG NHẤT: Tạo "vỏ" đối tượng để Hibernate lấy mã ID
        // Nếu thiếu đoạn này, Hibernate sẽ gửi NULL xuống Database cho cột maKhachHang

        if (dto.getMaKhachHang() != null) {
            iuh.fit.core.entity.KhachHang kh = new iuh.fit.core.entity.KhachHang();
            kh.setMaKhachHang(dto.getMaKhachHang()); // Gán ID từ DTO vào vỏ Entity
            entity.setKhachHang(kh); // Gán vỏ Entity vào Phiếu đặt
        }

        if (dto.getMaPhong() != null) {
            iuh.fit.core.entity.Phong phong = new iuh.fit.core.entity.Phong();
            phong.setMaPhong(dto.getMaPhong());
            entity.setPhong(phong);
        }

        if (dto.getMaNhanVien() != null) {
            iuh.fit.core.entity.NhanVien nv = new iuh.fit.core.entity.NhanVien();
            nv.setMaNhanVien(dto.getMaNhanVien());
            entity.setNhanVien(nv);
        }

        return entity;
    }
}