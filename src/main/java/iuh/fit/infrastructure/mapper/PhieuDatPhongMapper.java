package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.entity.PhieuDatPhong;

public class PhieuDatPhongMapper {

    public static PhieuDatPhongDTO entityToDTO(PhieuDatPhong entity) {
        if (entity == null) return null;

        // Lấy mã và tên từ các entity liên kết (phải kiểm tra null)
        String maKhachHang = entity.getKhachHang() != null ?
                entity.getKhachHang().getMaKhachHang() : null;
        String tenKhachHang = entity.getKhachHang() != null ?
                entity.getKhachHang().getHoTen() : null;

        String maPhong = entity.getPhong() != null ?
                entity.getPhong().getMaPhong() : null;
        String tenPhong = entity.getPhong() != null ?
                entity.getPhong().getTenPhong() : null;

        String maNhanVien = entity.getNhanVien() != null ?
                entity.getNhanVien().getMaNhanVien() : null;
        String hoTenNhanVien = entity.getNhanVien() != null ?
                entity.getNhanVien().getHoTen() : null;

        // Khớp đúng thứ tự constructor trong PhieuDatPhongDTO:
        // (maPhieu, maKhachHang, tenKhachHang, maPhong, tenPhong,
        //  ngayDat, ngayNhan, ngayTra, tongTien, trangThai,
        //  maNhanVien, hoTenNhanVien)
        return new PhieuDatPhongDTO(
                entity.getMaPhieu(),
                maKhachHang,
                tenKhachHang,
                maPhong,
                tenPhong,
                entity.getNgayDat(),
                entity.getNgayNhan(),
                entity.getNgayTra(),
                entity.getTongTien() != null ? entity.getTongTien() : 0.0,
                entity.getTrangThai(),
                maNhanVien,
                hoTenNhanVien
        );
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

        // ⚠️ Lưu ý: không set trực tiếp khachHang, phong, nhanVien vì cần entity đầy đủ.
        // Bạn cần tự lấy từ DB hoặc dùng setKhachHang(new KhachHang(...)).
        // Nếu dùng để lưu, thường service sẽ xử lý phần này.
        return entity;
    }
}