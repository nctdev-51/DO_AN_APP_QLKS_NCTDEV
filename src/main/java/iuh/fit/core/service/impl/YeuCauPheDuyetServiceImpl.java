// File: iuh/fit/core/service/impl/YeuCauPheDuyetServiceImpl.java
package iuh.fit.core.service.impl;

import iuh.fit.core.entity.YeuCauPheDuyet;
import iuh.fit.core.repository.IYeuCauPheDuyetRepository;
import iuh.fit.core.service.IGiaoCaService;
import iuh.fit.core.service.IYeuCauPheDuyetService;
import java.time.LocalDateTime;
import java.util.List;

public class YeuCauPheDuyetServiceImpl implements IYeuCauPheDuyetService {

    private final IYeuCauPheDuyetRepository repository;
    private final IGiaoCaService giaoCaService;

    public YeuCauPheDuyetServiceImpl(IYeuCauPheDuyetRepository repository, IGiaoCaService giaoCaService) {
        this.repository = repository;
        this.giaoCaService = giaoCaService;
    }

    @Override
    public void taoYeuCau(String maNhanVien, String tenNhanVien, double tienDauCa, String lyDo) {
        YeuCauPheDuyet yc = new YeuCauPheDuyet();
        yc.setMaYeuCau("YC" + System.currentTimeMillis());
        yc.setMaNhanVien(maNhanVien);
        yc.setTenNhanVien(tenNhanVien);
        yc.setThoiGianYeuCau(LocalDateTime.now());
        yc.setTienDauCa(tienDauCa);
        yc.setLyDo(lyDo);
        yc.setTrangThai("CHUA_DUYET");
        repository.save(yc);
    }

    @Override
    public void duyetYeuCau(String maYeuCau, String maQuanLy, boolean dongY) {
        YeuCauPheDuyet yc = repository.findById(maYeuCau)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu"));

        if (!"CHUA_DUYET".equals(yc.getTrangThai())) {
            throw new IllegalStateException("Yêu cầu đã được xử lý trước đó");
        }

        yc.setMaQuanLy(maQuanLy);
        yc.setThoiGianDuyet(LocalDateTime.now());

        if (dongY) {
            try {
                giaoCaService.nhanCa(yc.getMaNhanVien(), yc.getTienDauCa(), true);
                yc.setTrangThai("DA_DUYET");
            } catch (Exception e) {
                throw new RuntimeException("Duyệt thất bại: " + e.getMessage(), e);
            }
        } else {
            yc.setTrangThai("TU_CHOI");
        }
        repository.update(yc);
    }

    @Override
    public List<YeuCauPheDuyet> getYeuCauChuaDuyet() {
        return repository.findByTrangThai("CHUA_DUYET");
    }
}