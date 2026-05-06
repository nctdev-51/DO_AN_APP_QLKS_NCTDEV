package iuh.fit.core.repository;

import iuh.fit.core.entity.ChiTietHoaDon;
import iuh.fit.core.entity.ChiTietHoaDonId;
import java.util.List;
import java.util.Optional;

public interface IChiTietHoaDonRepository {
    List<ChiTietHoaDon> findAll();
    Optional<ChiTietHoaDon> findById(ChiTietHoaDonId id);
    ChiTietHoaDon save(ChiTietHoaDon chiTietHoaDon);
    ChiTietHoaDon update(ChiTietHoaDon chiTietHoaDon);
    void deleteById(ChiTietHoaDonId id);
    List<ChiTietHoaDon> findByHoaDon(String maHoaDon);
    void deleteByHoaDon(String maHoaDon);
}

