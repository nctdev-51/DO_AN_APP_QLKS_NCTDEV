package iuh.fit.core.service.impl;

import iuh.fit.core.dto.BaoCaoDTO;
import iuh.fit.core.entity.BaoCao;
import iuh.fit.core.entity.NhanVien;
import iuh.fit.core.repository.IBaoCaoRepository;
import iuh.fit.core.service.IBaoCaoService;
import iuh.fit.infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.stream.Collectors;

public class BaoCaoServiceImpl implements IBaoCaoService {

    private final IBaoCaoRepository baoCaoRepo;

    public BaoCaoServiceImpl(IBaoCaoRepository baoCaoRepo) {
        this.baoCaoRepo = baoCaoRepo;
    }

    @Override
    public List<BaoCaoDTO> findAll() {
        return baoCaoRepo.findAll().stream().map(b -> new BaoCaoDTO(
                b.getMaBaoCao(),
                b.getNhanVien() != null ? b.getNhanVien().getMaNhanVien() : b.getMaNhanVien(),
                b.getNhanVien() != null ? b.getNhanVien().getHoTen() : "Không rõ",
                b.getTieuDe(),
                b.getNoiDung(),
                b.getPhanLoai(),
                b.getNgayTao(),
                b.getTrangThai()
        )).collect(Collectors.toList());
    }

    @Override
    public void save(BaoCaoDTO dto) throws Exception {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            // Tìm nhân viên đang lập báo cáo
            NhanVien nv = em.find(NhanVien.class, dto.getMaNhanVien());
            if (nv == null) {
                throw new Exception("Lỗi: Không xác định được nhân viên gửi báo cáo!");
            }

            // Chuyển từ DTO sang Entity để lưu
            BaoCao bc = new BaoCao();
            bc.setMaBaoCao(dto.getMaBaoCao());
            bc.setNhanVien(nv);
            bc.setTieuDe(dto.getTieuDe());
            bc.setNoiDung(dto.getNoiDung());
            bc.setPhanLoai(dto.getPhanLoai());
            bc.setNgayTao(dto.getNgayTao());
            bc.setTrangThai(dto.getTrangThai());

            baoCaoRepo.save(bc);
        } finally {
            em.close();
        }
    }

    @Override
    public void update(BaoCaoDTO dto) throws Exception {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            BaoCao bc = em.find(BaoCao.class, dto.getMaBaoCao());
            if (bc == null) {
                throw new Exception("Lỗi: Không tìm thấy báo cáo này trong hệ thống!");
            }

            // Ở chức năng này, quản lý chủ yếu cập nhật trạng thái thành DA_XU_LY
            bc.setTrangThai(dto.getTrangThai());

            baoCaoRepo.update(bc);
        } finally {
            em.close();
        }
    }
}