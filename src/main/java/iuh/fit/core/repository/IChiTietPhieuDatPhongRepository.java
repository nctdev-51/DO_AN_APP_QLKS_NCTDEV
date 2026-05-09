package iuh.fit.core.repository;

import iuh.fit.core.entity.ChiTietPhieuDatPhong;
import iuh.fit.core.entity.ChiTietPhieuDatPhongId;
import java.util.List;
import java.util.Optional;

public interface IChiTietPhieuDatPhongRepository {
    List<ChiTietPhieuDatPhong> findByMaPhieu(String maPhieu);
    ChiTietPhieuDatPhong save(ChiTietPhieuDatPhong entity);
    void deleteByMaPhieu(String maPhieu);

    // 👉 Thêm mới
    Optional<ChiTietPhieuDatPhong> findById(ChiTietPhieuDatPhongId id);
}