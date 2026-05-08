package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.PhieuDatPhong;
import iuh.fit.core.repository.IPhieuDatPhongRepository;
import iuh.fit.infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class PhieuDatPhongRepositoryImpl implements IPhieuDatPhongRepository {
    private static final Logger logger = Logger.getLogger(PhieuDatPhongRepositoryImpl.class.getName());


    @Override
    public List<PhieuDatPhong> findAll() {
        // Phải dùng EntityManager mới để đảm bảo session không bị đóng
        EntityManager em = JpaConfig.getEntityManager();
        try {
            // 👉 ÉP BUỘC lấy thêm thông tin Khách và Phòng bằng JOIN FETCH
            String jpql = "SELECT DISTINCT p FROM PhieuDatPhong p " +
                    "JOIN FETCH p.khachHang " +
                    "JOIN FETCH p.phong";
            return em.createQuery(jpql, PhieuDatPhong.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        } finally {
            em.close();
        }
    }

    @Override
    public List<PhieuDatPhong> findByTrangThai(String trangThai) {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            String jpql = "SELECT p FROM PhieuDatPhong p " +
                    "JOIN FETCH p.khachHang JOIN FETCH p.phong " +
                    "WHERE p.trangThai = :trangThai";
            return em.createQuery(jpql, PhieuDatPhong.class)
                    .setParameter("trangThai", trangThai)
                    .getResultList();
        }
    }

    @Override
    public Optional<PhieuDatPhong> findById(String maPhieu) {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            // Dùng JOIN FETCH ngay cả khi tìm 1 ID để đảm bảo đủ dữ liệu hiển thị Chi tiết
            String jpql = "SELECT p FROM PhieuDatPhong p " +
                    "JOIN FETCH p.khachHang JOIN FETCH p.phong " +
                    "WHERE p.maPhieu = :id";
            try {
                PhieuDatPhong p = em.createQuery(jpql, PhieuDatPhong.class)
                        .setParameter("id", maPhieu)
                        .getSingleResult();
                return Optional.of(p);
            } catch (Exception e) {
                return Optional.empty();
            }
        }
    }

    @Override
    public List<PhieuDatPhong> findByMaKhachHang(String maKhachHang) {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            // Sửa lại query cho đúng tên field trong Entity (p.khachHang.maKhachHang)
            return em.createQuery("SELECT p FROM PhieuDatPhong p WHERE p.khachHang.maKhachHang = :maKH", PhieuDatPhong.class)
                    .setParameter("maKH", maKhachHang)
                    .getResultList();
        }
    }

    @Override
    public List<PhieuDatPhong> findByMaPhong(String maPhong) {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            return em.createQuery("SELECT p FROM PhieuDatPhong p WHERE p.phong.maPhong = :maP", PhieuDatPhong.class)
                    .setParameter("maP", maPhong)
                    .getResultList();
        }
    }

    @Override
    public List<PhieuDatPhong> findByNgayDatBetween(LocalDate startDate, LocalDate endDate) {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            return em.createQuery("SELECT p FROM PhieuDatPhong p JOIN FETCH p.khachHang JOIN FETCH p.phong " +
                            "WHERE p.ngayDat BETWEEN :start AND :end", PhieuDatPhong.class)
                    .setParameter("start", startDate)
                    .setParameter("end", endDate)
                    .getResultList();
        }
    }

    @Override
    public PhieuDatPhong save(PhieuDatPhong phieuDatPhong) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(phieuDatPhong);
            tx.commit();
            return phieuDatPhong;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.severe("❌ Lỗi save phiếu: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public PhieuDatPhong update(PhieuDatPhong phieuDatPhong) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            PhieuDatPhong merged = em.merge(phieuDatPhong);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.severe("❌ Lỗi update phiếu: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(String maPhieu) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            PhieuDatPhong p = em.find(PhieuDatPhong.class, maPhieu);
            if (p != null) em.remove(p);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.severe("❌ Lỗi xóa phiếu: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    @Override
    public boolean saveBookingTransaction(PhieuDatPhong pdp) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // Nạp lại các đối tượng từ DB để đảm bảo không bị lỗi Detached Entity
            if (pdp.getKhachHang() != null)
                pdp.setKhachHang(em.find(iuh.fit.core.entity.KhachHang.class, pdp.getKhachHang().getMaKhachHang()));
            if (pdp.getPhong() != null)
                pdp.setPhong(em.find(iuh.fit.core.entity.Phong.class, pdp.getPhong().getMaPhong()));
            if (pdp.getNhanVien() != null)
                pdp.setNhanVien(em.find(iuh.fit.core.entity.NhanVien.class, pdp.getNhanVien().getMaNhanVien()));
            if (pdp.getKhachHang() != null) {
                iuh.fit.core.entity.KhachHang khEntity = em.find(iuh.fit.core.entity.KhachHang.class, pdp.getKhachHang().getMaKhachHang());
                if (khEntity == null) {
                    // Nếu tìm không ra thì ngưng luôn, không cố lưu nữa
                    throw new Exception("Không tìm thấy Khách hàng trong Database với mã: " + pdp.getKhachHang().getMaKhachHang());
                }
                pdp.setKhachHang(khEntity);
            }
            em.persist(pdp);
            em.flush(); // 👉 Ép xuống Database ngay lập tức
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace(); // 🔍 NHÌN VÀO ĐÂY: Nếu lỗi, nó sẽ hiện chữ đỏ ở Console
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public String phatSinhMaPhieuMoi() {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            // Lấy tất cả mã phiếu đang có
            String jpql = "SELECT p.maPhieu FROM PhieuDatPhong p";
            List<String> ids = em.createQuery(jpql, String.class).getResultList();

            if (ids.isEmpty()) return "PDP001";

            // Tìm số lớn nhất
            int max = 0;
            for (String id : ids) {
                try {
                    // Bước 1: Bỏ chữ "PDP"
                    String s = id.substring(3);
                    // Bước 2: Nếu có dấu "-" (như PDP020-1) thì chỉ lấy phần trước dấu "-"
                    if (s.contains("-")) {
                        s = s.split("-")[0];
                    }
                    // Bước 3: Chuyển về số để so sánh
                    int num = Integer.parseInt(s);
                    if (num > max) max = num;
                } catch (Exception ignored) {}
            }

            // Bước 4: Tăng số lớn nhất lên 1 và định dạng lại thành PDP0xx
            return String.format("PDP%03d", max + 1);

        } finally {
            em.close();
        }
    }
}