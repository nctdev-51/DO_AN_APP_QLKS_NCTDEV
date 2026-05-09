package iuh.fit.core.service;

import iuh.fit.core.dto.YeuCauPheDuyetDTO;
import java.util.List;

public interface IYeuCauPheDuyetService {
    void taoYeuCau(String maNhanVien, String tenNhanVien, double tienDauCa, String lyDo);
    void duyetYeuCau(String maYeuCau, String maQuanLy, boolean dongY);
    List<YeuCauPheDuyetDTO> getYeuCauChuaDuyet(); // Trả về DTO
}