package iuh.fit.core.repository;

import iuh.fit.core.entity.LichSuCaLamViec;
import java.util.List;
import java.util.Optional;

public interface ILichSuCaLamViecRepository {
    LichSuCaLamViec save(LichSuCaLamViec lichSu);
    LichSuCaLamViec update(LichSuCaLamViec lichSu);
    Optional<LichSuCaLamViec> findById(String maLichSu);
    List<LichSuCaLamViec> findAll();

    // Tìm ca mà nhân viên đang làm (chưa bàn giao)
    LichSuCaLamViec findCaDangLam(String maNhanVien);
}