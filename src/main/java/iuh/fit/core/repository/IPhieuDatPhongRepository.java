package iuh.fit.core.repository;

import iuh.fit.core.entity.PhieuDatPhong;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IPhieuDatPhongRepository {
    List<PhieuDatPhong> findAll();
    Optional<PhieuDatPhong> findById(String maPhieu);
    List<PhieuDatPhong> findByMaKhachHang(String maKhachHang);
    List<PhieuDatPhong> findByMaPhong(String maPhong);
    List<PhieuDatPhong> findByNgayDatBetween(LocalDate startDate, LocalDate endDate);
    PhieuDatPhong save(PhieuDatPhong phieuDatPhong);
    PhieuDatPhong update(PhieuDatPhong phieuDatPhong);
    void deleteById(String maPhieu);

    List<PhieuDatPhong> findByTrangThai(String trangThai);
    boolean saveBookingTransaction(PhieuDatPhong entity);

    String phatSinhMaPhieuMoi();
}