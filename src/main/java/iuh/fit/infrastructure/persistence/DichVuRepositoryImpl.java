package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.DichVu;
import iuh.fit.core.repository.IDichVuRepository;
import iuh.fit.infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Optional;

public class DichVuRepositoryImpl implements IDichVuRepository {

    @Override
    public List<DichVu> findAll() {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            Query query = em.createQuery("SELECT d FROM DichVu d", DichVu.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<DichVu> findById(String maDichVu) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            DichVu dichVu = em.find(DichVu.class, maDichVu);
            return Optional.ofNullable(dichVu);
        } finally {
            em.close();
        }
    }

    @Override
    public DichVu save(DichVu dichVu) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            em.persist(dichVu);
            et.commit();
            return dichVu;
        } catch (Exception e) {
            if (et.isActive()) et.rollback();
            throw new RuntimeException("Lỗi lưu dịch vụ: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public DichVu update(DichVu dichVu) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            DichVu merged = em.merge(dichVu);
            et.commit();
            return merged;
        } catch (Exception e) {
            if (et.isActive()) et.rollback();
            throw new RuntimeException("Lỗi cập nhật dịch vụ: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(String maDichVu) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            DichVu dichVu = em.find(DichVu.class, maDichVu);
            if (dichVu != null) {
                em.remove(dichVu);
            }
            et.commit();
        } catch (Exception e) {
            if (et.isActive()) et.rollback();
            throw new RuntimeException("Lỗi xóa dịch vụ: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}

