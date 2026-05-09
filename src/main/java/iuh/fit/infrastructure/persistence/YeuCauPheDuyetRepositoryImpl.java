// File: iuh/fit/infrastructure/persistence/YeuCauPheDuyetRepositoryImpl.java
package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.YeuCauPheDuyet;
import iuh.fit.core.repository.IYeuCauPheDuyetRepository;
import iuh.fit.infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class YeuCauPheDuyetRepositoryImpl implements IYeuCauPheDuyetRepository {

    private static final Logger logger = Logger.getLogger(YeuCauPheDuyetRepositoryImpl.class.getName());

    @Override
    public YeuCauPheDuyet save(YeuCauPheDuyet yc) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(yc);
            tx.commit();
            return yc;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.severe("Lỗi lưu YeuCauPheDuyet: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public YeuCauPheDuyet update(YeuCauPheDuyet yc) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            YeuCauPheDuyet merged = em.merge(yc);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.severe("Lỗi cập nhật YeuCauPheDuyet: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<YeuCauPheDuyet> findById(String maYeuCau) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            return Optional.ofNullable(em.find(YeuCauPheDuyet.class, maYeuCau));
        } finally {
            em.close();
        }
    }

    @Override
    public List<YeuCauPheDuyet> findAll() {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            return em.createQuery("SELECT y FROM YeuCauPheDuyet y ORDER BY y.thoiGianYeuCau DESC", YeuCauPheDuyet.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<YeuCauPheDuyet> findByTrangThai(String trangThai) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            return em.createQuery("SELECT y FROM YeuCauPheDuyet y WHERE y.trangThai = :trangThai ORDER BY y.thoiGianYeuCau DESC", YeuCauPheDuyet.class)
                    .setParameter("trangThai", trangThai)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}