package iuh.fit.core.repository;

import iuh.fit.core.entity.CaLamViec;
import java.util.List;
import java.util.Optional;

public interface ICaLamViecRepository {
    List<CaLamViec> findAll();
    Optional<CaLamViec> findById(String maCa);
    CaLamViec save(CaLamViec ca);
    CaLamViec update(CaLamViec ca);
    void deleteById(String maCa);
}