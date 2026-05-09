package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.CaLamViec;
import iuh.fit.core.repository.ICaLamViecRepository;
import iuh.fit.infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class CaLamViecRepositoryImpl implements ICaLamViecRepository {

    @Override
    public List<CaLamViec> findAll() {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM CaLamViec c", CaLamViec.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<CaLamViec> findById(String maCa) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            return Optional.ofNullable(em.find(CaLamViec.class, maCa));
        } finally {
            em.close();
        }
    }

    @Override
    public CaLamViec save(CaLamViec ca) {
        EntityManager em = JpaConfig.getEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(ca);
            tx.commit();
            return ca;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public CaLamViec update(CaLamViec ca) {
        EntityManager em = JpaConfig.getEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            CaLamViec merged = em.merge(ca);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(String maCa) {
        EntityManager em = JpaConfig.getEntityManager();
        var tx = em.getTransaction();
        try {
            tx.begin();
            CaLamViec ca = em.find(CaLamViec.class, maCa);
            if (ca != null) em.remove(ca);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }
}