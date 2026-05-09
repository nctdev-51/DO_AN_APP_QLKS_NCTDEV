package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.BaoCao;
import iuh.fit.core.repository.IBaoCaoRepository;
import iuh.fit.infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class BaoCaoRepositoryImpl implements IBaoCaoRepository {

    @Override
    public List<BaoCao> findAll() {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            // Lấy danh sách báo cáo, ưu tiên cái mới nhất lên đầu
            return em.createQuery("SELECT b FROM BaoCao b ORDER BY b.ngayTao DESC", BaoCao.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public BaoCao save(BaoCao baoCao) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(baoCao);
            tx.commit();
            return baoCao;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public BaoCao update(BaoCao baoCao) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            BaoCao merged = em.merge(baoCao);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}