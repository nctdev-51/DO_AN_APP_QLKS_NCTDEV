package iuh.fit.core.service;

import iuh.fit.core.dto.PhanCongDTO;
import java.util.List;

public interface IPhanCongService {
    List<PhanCongDTO> findAll();
    void save(PhanCongDTO dto) throws Exception;
    void delete(String maPhanCong) throws Exception;

    void update(PhanCongDTO dto) throws Exception;
}