// File: iuh/fit/core/repository/IYeuCauPheDuyetRepository.java
package iuh.fit.core.repository;

import iuh.fit.core.entity.YeuCauPheDuyet;
import java.util.List;
import java.util.Optional;

public interface IYeuCauPheDuyetRepository {
    YeuCauPheDuyet save(YeuCauPheDuyet yc);
    YeuCauPheDuyet update(YeuCauPheDuyet yc);
    Optional<YeuCauPheDuyet> findById(String maYeuCau);
    List<YeuCauPheDuyet> findAll();
    List<YeuCauPheDuyet> findByTrangThai(String trangThai);
}