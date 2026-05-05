package iuh.fit.core.service;
import iuh.fit.core.dto.DichVuDTO;
import java.util.List;
public interface IDichVuService {
    List<DichVuDTO> getAllDichVu();
    DichVuDTO getDichVuById(String maDichVu);
    DichVuDTO addDichVu(DichVuDTO dichVuDTO) throws IllegalArgumentException;
    DichVuDTO updateDichVu(DichVuDTO dichVuDTO) throws IllegalArgumentException;
    boolean deleteDichVu(String maDichVu);
}
