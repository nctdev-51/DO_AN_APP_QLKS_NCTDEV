package iuh.fit.core.service.impl;

import iuh.fit.core.dto.PhanCongDTO;
import iuh.fit.core.entity.CaLamViec;
import iuh.fit.core.entity.NhanVien;
import iuh.fit.core.entity.PhanCongCaLamViec;
import iuh.fit.core.repository.IPhanCongRepository;
import iuh.fit.core.service.IPhanCongService;
import iuh.fit.infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.stream.Collectors;

public class PhanCongServiceImpl implements IPhanCongService {

    private final IPhanCongRepository phanCongRepo;

    public PhanCongServiceImpl(IPhanCongRepository phanCongRepo) {
        this.phanCongRepo = phanCongRepo;
    }

    @Override
    public List<PhanCongDTO> findAll() {
        return phanCongRepo.findAll().stream().map(p -> new PhanCongDTO(
                p.getMaPhanCong(),
                p.getNhanVien().getMaNhanVien(),
                p.getNhanVien().getHoTen(),
                p.getCaLamViec().getMaCa(),
                p.getCaLamViec().getTenCa(),
                p.getNgayLamViec(),
                p.getTrangThai(), // Lấy từ Entity
                p.getGhiChu()     // Lấy từ Entity
        )).collect(Collectors.toList());
    }

    @Override
    public void update(PhanCongDTO dto) throws Exception {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            // Tìm bản ghi phân công hiện tại dưới Database
            PhanCongCaLamViec pc = em.find(PhanCongCaLamViec.class, dto.getMaPhanCong());
            if (pc == null) {
                throw new Exception("Không tìm thấy phân công này trong hệ thống!");
            }

            // Cập nhật trạng thái và ghi chú (VD: VANG_MAT, Bệnh...)
            pc.setTrangThai(dto.getTrangThai());
            pc.setGhiChu(dto.getGhiChu());

            // Gọi Repository để lưu thay đổi
            phanCongRepo.update(pc);
        } finally {
            em.close();
        }
    }

    @Override
    public void save(PhanCongDTO dto) throws Exception {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            NhanVien nv = em.find(NhanVien.class, dto.getMaNhanVien());
            CaLamViec ca = em.find(CaLamViec.class, dto.getMaCa());

            if (nv == null || ca == null) {
                throw new Exception("Nhân viên hoặc Ca làm việc không tồn tại trong hệ thống!");
            }

            // Khởi tạo đối tượng rỗng và dùng Setter (Cách này an toàn nhất khi Entity có nhiều thuộc tính)
            PhanCongCaLamViec pc = new PhanCongCaLamViec();
            pc.setMaPhanCong(dto.getMaPhanCong());
            pc.setNhanVien(nv);
            pc.setCaLamViec(ca);
            pc.setNgayLamViec(dto.getNgayLamViec());

            // Gán 2 giá trị mặc định cho các trường mới thêm
            pc.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : "CHUA_LAM");
            pc.setGhiChu(dto.getGhiChu() != null ? dto.getGhiChu() : "");

            phanCongRepo.save(pc);

        } finally {
            em.close();
        }
    }

    @Override
    public void delete(String maPhanCong) throws Exception {
        if (!phanCongRepo.deleteById(maPhanCong)) {
            throw new Exception("Xóa thất bại! Không tìm thấy mã phân công này.");
        }
    }
}