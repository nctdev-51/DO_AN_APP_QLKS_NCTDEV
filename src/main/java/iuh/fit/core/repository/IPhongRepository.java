package iuh.fit.core.repository;

import iuh.fit.core.entity.Phong;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IPhongRepository {
    List<Phong> findAll();
    Optional<Phong> findById(String maPhong);
    List<Phong> findByTinhTrang(String tinhTrang);
    List<Phong> findByMaLoaiPhong(String maLoaiPhong);
    Phong save(Phong phong);
    Phong update(Phong phong);
    void deleteById(String maPhong);

    List<Phong> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, double minPrice, double maxPrice);

    // 1. Hàm này dùng để lấy Entity phục vụ cho nội bộ hoặc Service
    List<Phong> getPhongByPhieuDat(String maPhieu);

    // 2. Sửa lại hàm này để khớp với định nghĩa trong Interface IPhongRepository
    List<Phong> getDanhSachPhongTheoMaPhieu(String maPhieu);
}