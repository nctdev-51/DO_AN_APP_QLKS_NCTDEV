package iuh.fit.core.service;

import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.entity.Phong;

import java.time.LocalDate;
import java.util.List;

public interface IPhongService {
    List<PhongDTO> getAllPhong();
    PhongDTO getPhongById(String maPhong);
    List<PhongDTO> getPhongByTinhTrang(String tinhTrang);
    List<PhongDTO> getPhongByMaLoaiPhong(String maLoaiPhong);
    PhongDTO addPhong(PhongDTO phongDTO) throws IllegalArgumentException;
    PhongDTO updatePhong(PhongDTO phongDTO) throws IllegalArgumentException;
    boolean deletePhong(String maPhong);

    // ✅ Cải thiện: Lọc phòng theo ngày + giá + tình trạng
    List<PhongDTO> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, double minPrice, double maxPrice, String tinhTrang);

    // ✅ Thêm mới: Cập nhật trạng thái phòng (Trống → Đặt → Đang sử dụng → Bảo trì)
    boolean updatePhongTrangThai(String maPhong, String trangThai);

    List<PhongDTO> getPhongByPhieuDat(String maPhieu);
    List<Phong> findPhongByMaPhieu(String maPhieu);
}