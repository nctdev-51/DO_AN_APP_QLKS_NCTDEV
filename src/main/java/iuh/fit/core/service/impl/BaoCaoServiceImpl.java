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
        return baoCaoRepo.findAll().stream().map(b -> {
            // ĐÃ FIX: Khởi tạo rỗng và dùng Setter để chống lỗi Constructor mismatch
            BaoCaoDTO dto = new BaoCaoDTO();
            dto.setMaBaoCao(b.getMaBaoCao());
            dto.setMaNhanVien(b.getNhanVien() != null ? b.getNhanVien().getMaNhanVien() : b.getMaNhanVien());
            dto.setHoTenNhanVien(b.getNhanVien() != null ? b.getNhanVien().getHoTen() : "Không rõ");
            dto.setTieuDe(b.getTieuDe());
            dto.setNoiDung(b.getNoiDung());
            dto.setPhanLoai(b.getPhanLoai());
            dto.setNgayTao(b.getNgayTao());
            dto.setTrangThai(b.getTrangThai());

            // Map 2 trường mới
            dto.setHinhAnh(b.getHinhAnh());
            dto.setPhanHoiQuanLy(b.getPhanHoiQuanLy());

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void save(BaoCaoDTO dto) throws Exception {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            NhanVien nv = em.find(NhanVien.class, dto.getMaNhanVien());
            if (nv == null) {
                throw new Exception("Lỗi: Không xác định được nhân viên gửi báo cáo!");
            }

            BaoCao bc = new BaoCao();
            bc.setMaBaoCao(dto.getMaBaoCao());
            bc.setNhanVien(nv);
            bc.setTieuDe(dto.getTieuDe());
            bc.setNoiDung(dto.getNoiDung());
            bc.setPhanLoai(dto.getPhanLoai());
            bc.setNgayTao(dto.getNgayTao());
            bc.setTrangThai(dto.getTrangThai());

            // Lưu đường dẫn ảnh vào DB
            bc.setHinhAnh(dto.getHinhAnh());

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

            bc.setTrangThai(dto.getTrangThai());

            // Lưu kết quả xử lý của quản lý
            bc.setPhanHoiQuanLy(dto.getPhanHoiQuanLy());

            baoCaoRepo.update(bc);
        } finally {
            em.close();
        }
    }
}