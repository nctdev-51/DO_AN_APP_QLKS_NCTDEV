package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.ChiTietHoaDon;
import iuh.fit.core.entity.ChiTietHoaDonId;
import iuh.fit.core.repository.IChiTietHoaDonRepository;
import iuh.fit.infrastructure.db.JpaConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class ChiTietHoaDonRepositoryImpl implements IChiTietHoaDonRepository {

    private static final Logger logger = Logger.getLogger(ChiTietHoaDonRepositoryImpl.class.getName());

    @Override
    public List<ChiTietHoaDon> findAll() {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            return em.createQuery("SELECT c FROM ChiTietHoaDon c", ChiTietHoaDon.class)
                    .getResultList();
        }
    }

    @Override
    public Optional<ChiTietHoaDon> findById(ChiTietHoaDonId id) {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            return Optional.ofNullable(em.find(ChiTietHoaDon.class, id));
        }
    }

    @Override
    public List<ChiTietHoaDon> findByHoaDon(String maHoaDon) {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            return em.createQuery("SELECT c FROM ChiTietHoaDon c WHERE c.maHoaDon = :maHoaDon", ChiTietHoaDon.class)
                    .setParameter("maHoaDon", maHoaDon)
                    .getResultList();
        }
    }

    @Override
    public ChiTietHoaDon save(ChiTietHoaDon entity) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity);
            tx.commit();
            logger.info("✅ Đã lưu ChiTietHoaDon");
            return entity;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.severe("❌ Lỗi lưu ChiTietHoaDon: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public ChiTietHoaDon update(ChiTietHoaDon entity) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            ChiTietHoaDon merged = em.merge(entity);
            tx.commit();
            logger.info("✅ Đã cập nhật ChiTietHoaDon");
            return merged;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.severe("❌ Lỗi cập nhật ChiTietHoaDon: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(ChiTietHoaDonId id) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            ChiTietHoaDon entity = em.find(ChiTietHoaDon.class, id);
            if (entity != null) em.remove(entity);
            tx.commit();
            logger.info("✅ Đã xóa ChiTietHoaDon");
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.severe("❌ Lỗi xóa ChiTietHoaDon: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteByHoaDon(String maHoaDon) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery("DELETE FROM ChiTietHoaDon c WHERE c.maHoaDon = :maHoaDon")
                    .setParameter("maHoaDon", maHoaDon)
                    .executeUpdate();
            tx.commit();
            logger.info("✅ Đã xóa ChiTietHoaDon theo hóa đơn: " + maHoaDon);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.severe("❌ Lỗi xóa ChiTietHoaDon theo hóa đơn: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }
}