package iuh.fit.core.repository;

import iuh.fit.core.entity.DichVu;
import java.util.List;
import java.util.Optional;

public interface IDichVuRepository {
    List<DichVu> findAll();
    Optional<DichVu> findById(String maDichVu);
    DichVu save(DichVu dichVu);
    DichVu update(DichVu dichVu);
    void deleteById(String maDichVu);
}