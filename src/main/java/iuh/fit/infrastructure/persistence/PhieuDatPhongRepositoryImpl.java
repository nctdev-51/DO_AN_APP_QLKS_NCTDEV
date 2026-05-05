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
        try (EntityManager em = JpaConfig.getEntityManager()) {
            return em.createQuery("SELECT p FROM PhieuDatPhong p", PhieuDatPhong.class).getResultList();
        }
    }

    @Override
    public Optional<PhieuDatPhong> findById(String maPhieu) {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            return Optional.ofNullable(em.find(PhieuDatPhong.class, maPhieu));
        }
    }

    @Override
    public List<PhieuDatPhong> findByMaKhachHang(String maKhachHang) {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            return em.createQuery("SELECT p FROM PhieuDatPhong p WHERE p.maKhachHang = :maKhachHang", PhieuDatPhong.class)
                    .setParameter("maKhachHang", maKhachHang)
                    .getResultList();
        }
    }

    @Override
    public List<PhieuDatPhong> findByMaPhong(String maPhong) {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            return em.createQuery("SELECT p FROM PhieuDatPhong p WHERE p.maPhong = :maPhong", PhieuDatPhong.class)
                    .setParameter("maPhong", maPhong)
                    .getResultList();
        }
    }

    @Override
    public List<PhieuDatPhong> findByNgayDatBetween(LocalDate startDate, LocalDate endDate) {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            return em.createQuery("SELECT p FROM PhieuDatPhong p WHERE p.ngayDat BETWEEN :start AND :end", PhieuDatPhong.class)
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
            logger.severe("❌ Lỗi lưu phiếu đặt phòng: " + e.getMessage());
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
            logger.severe("❌ Lỗi cập nhật phiếu đặt phòng: " + e.getMessage());
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
            if (p != null) {
                em.remove(p);
            }
            tx.commit();
            logger.info("✅ Xóa phiếu đặt phòng thành công: " + maPhieu);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.severe("❌ Lỗi xóa phiếu đặt phòng: " + e.getMessage());
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
            // Lưu phiếu đặt phòng
            em.persist(pdp);

            // Xử lý các logic cập nhật trạng thái phòng bằng Cascade hoặc JPQL
            // Nếu bạn đã map Cascade.ALL trong Entity thì dòng persist(pdp) là đủ!

            tx.commit();
            logger.info("✅ Tạo phiếu đặt phòng thành công: " + pdp.getMaPhieu());
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.severe("❌ Lỗi tạo phiếu đặt phòng: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
}