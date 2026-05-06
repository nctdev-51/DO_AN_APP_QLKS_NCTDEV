package iuh.fit.core.repository;

import iuh.fit.core.entity.HoaDon;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IHoaDonRepository {
    List<HoaDon> findAll();
    Optional<HoaDon> findById(String maHoaDon);
    HoaDon save(HoaDon hoaDon);
    HoaDon update(HoaDon hoaDon);
    void deleteById(String maHoaDon);
    List<HoaDon> findByPhieuDatPhong(String maPhieuDat);
    List<HoaDon> findByDateRange(LocalDate startDate, LocalDate endDate);
    double getTotalRevenueByDateRange(LocalDate startDate, LocalDate endDate);
}

