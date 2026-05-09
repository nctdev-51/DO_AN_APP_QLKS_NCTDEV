package iuh.fit.core.repository;

import iuh.fit.core.entity.BaoCao;
import java.util.List;

public interface IBaoCaoRepository {
    List<BaoCao> findAll();
    BaoCao save(BaoCao baoCao);
    BaoCao update(BaoCao baoCao);
}