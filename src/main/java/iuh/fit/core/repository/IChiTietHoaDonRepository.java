package iuh.fit.core.repository;

import iuh.fit.core.entity.ChiTietHoaDon;
import iuh.fit.core.entity.ChiTietHoaDonId;
import java.util.List;
import java.util.Optional;

public interface IChiTietHoaDonRepository {
    List<ChiTietHoaDon> findAll();
    Optional<ChiTietHoaDon> findById(ChiTietHoaDonId id);
    List<ChiTietHoaDon> findByHoaDon(String maHoaDon);   // ← đúng tên này
    ChiTietHoaDon save(ChiTietHoaDon entity);
    ChiTietHoaDon update(ChiTietHoaDon entity);
    void deleteById(ChiTietHoaDonId id);
    void deleteByHoaDon(String maHoaDon);                 // ← đúng tên này
}