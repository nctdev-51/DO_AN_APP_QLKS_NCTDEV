package iuh.fit.core.service;

import iuh.fit.core.dto.BaoCaoDTO;
import java.util.List;

public interface IBaoCaoService {
    List<BaoCaoDTO> findAll();
    void save(BaoCaoDTO dto) throws Exception;
    void update(BaoCaoDTO dto) throws Exception;
}