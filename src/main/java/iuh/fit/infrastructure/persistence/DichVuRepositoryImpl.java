package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.DichVu;
import iuh.fit.core.repository.IDichVuRepository;
import iuh.fit.infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

public class DichVuRepositoryImpl implements IDichVuRepository {
    @Override
    public List<DichVu> findAll() {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            return em.createQuery("SELECT d FROM DichVu d", DichVu.class).getResultList();
        }
    }

    @Override
    public Optional<DichVu> findById(String maDichVu) {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            return Optional.ofNullable(em.find(DichVu.class, maDichVu));
        }
    }

    @Override
    public DichVu save(DichVu dichVu) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(dichVu);
            tx.commit();
            return dichVu;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally { em.close(); }
    }

    @Override
    public DichVu update(DichVu dichVu) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            DichVu updated = em.merge(dichVu);
            tx.commit();
            return updated;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally { em.close(); }
    }

    @Override
    public void deleteById(String maDichVu) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            DichVu d = em.find(DichVu.class, maDichVu);
            if (d != null) em.remove(d);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally { em.close(); }
    }
}