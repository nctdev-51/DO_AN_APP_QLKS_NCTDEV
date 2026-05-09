package iuh.fit.core.repository;

import iuh.fit.core.entity.PhanCongCaLamViec;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IPhanCongRepository {
    PhanCongCaLamViec save(PhanCongCaLamViec phanCong);
    PhanCongCaLamViec update(PhanCongCaLamViec phanCong);
    boolean deleteById(String id);
    Optional<PhanCongCaLamViec> findById(String id);
    List<PhanCongCaLamViec> findAll();

    // Câu truy vấn tùy chỉnh để tìm ca của nhân viên trong ngày hôm nay
    PhanCongCaLamViec findPhanCongToday(String maNhanVien, LocalDate ngayLamViec);
}