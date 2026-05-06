package iuh.fit.infrastructure.mapper;

import iuh.fit.core.dto.HoaDonDTO;
import iuh.fit.core.dto.ChiTietHoaDonDTO;
import iuh.fit.core.entity.HoaDon;
import java.util.stream.Collectors;

public class HoaDonMapper {

    public static HoaDonDTO toDTO(HoaDon hoaDon) {
        if (hoaDon == null) return null;

        HoaDonDTO dto = new HoaDonDTO();
        dto.setMaHoaDon(hoaDon.getMaHoaDon());
        dto.setTongTien(hoaDon.getTongTien());
        dto.setNgayLap(hoaDon.getNgayTao());
        dto.setTrangThaiThanhToan(hoaDon.getTrangThaiThanhToan());
        dto.setThueVAT(hoaDon.getThueVAT());
        dto.setChietKhau(hoaDon.getChietKhau());
        dto.setGhiChu(hoaDon.getGhiChu());

        if (hoaDon.getPhieuDatPhong() != null) {
            dto.setMaPhongDat(hoaDon.getPhieuDatPhong().getMaPhieu());
            dto.setMaKhachHang(hoaDon.getPhieuDatPhong().getMaKhachHang());
            dto.setMaPhongDat(hoaDon.getPhieuDatPhong().getMaPhong());
        }

        if (hoaDon.getChiTietHoaDons() != null) {
            dto.setChiTietHoaDons(
                    hoaDon.getChiTietHoaDons().stream()
                            .map(ChiTietHoaDonMapper::toDTO)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public static HoaDon toEntity(HoaDonDTO dto) {
        if (dto == null) return null;

        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon(dto.getMaHoaDon());
        hoaDon.setTongTien(dto.getTongTien());
        hoaDon.setNgayTao(dto.getNgayLap());
        hoaDon.setTrangThaiThanhToan(dto.getTrangThaiThanhToan());
        hoaDon.setThueVAT(dto.getThueVAT());
        hoaDon.setChietKhau(dto.getChietKhau());
        hoaDon.setGhiChu(dto.getGhiChu());

        return hoaDon;
    }
}

