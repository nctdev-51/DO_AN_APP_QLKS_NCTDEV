// File: iuh/fit/core/service/IYeuCauPheDuyetService.java
package iuh.fit.core.service;

import iuh.fit.core.entity.YeuCauPheDuyet;
import java.util.List;

public interface IYeuCauPheDuyetService {
    void taoYeuCau(String maNhanVien, String tenNhanVien, double tienDauCa, String lyDo);
    void duyetYeuCau(String maYeuCau, String maQuanLy, boolean dongY);
    List<YeuCauPheDuyet> getYeuCauChuaDuyet();
}