package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.ChiTietHoaDon;
import iuh.fit.core.entity.ChiTietHoaDonId;
import iuh.fit.core.repository.IChiTietHoaDonRepository;
import iuh.fit.infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Optional;

public class ChiTietHoaDonRepositoryImpl implements IChiTietHoaDonRepository {

    @Override
    public List<ChiTietHoaDon> findAll() {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            Query query = em.createQuery("SELECT c FROM ChiTietHoaDon c", ChiTietHoaDon.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<ChiTietHoaDon> findById(ChiTietHoaDonId id) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            ChiTietHoaDon chiTiet = em.find(ChiTietHoaDon.class, id);
            return Optional.ofNullable(chiTiet);
        } finally {
            em.close();
        }
    }

    @Override
    public ChiTietHoaDon save(ChiTietHoaDon chiTietHoaDon) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            em.persist(chiTietHoaDon);
            et.commit();
            return chiTietHoaDon;
        } catch (Exception e) {
            if (et.isActive()) et.rollback();
            throw new RuntimeException("Lỗi lưu chi tiết hóa đơn: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public ChiTietHoaDon update(ChiTietHoaDon chiTietHoaDon) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            ChiTietHoaDon merged = em.merge(chiTietHoaDon);
            et.commit();
            return merged;
        } catch (Exception e) {
            if (et.isActive()) et.rollback();
            throw new RuntimeException("Lỗi cập nhật chi tiết hóa đơn: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(ChiTietHoaDonId id) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            ChiTietHoaDon chiTiet = em.find(ChiTietHoaDon.class, id);
            if (chiTiet != null) {
                em.remove(chiTiet);
            }
            et.commit();
        } catch (Exception e) {
            if (et.isActive()) et.rollback();
            throw new RuntimeException("Lỗi xóa chi tiết hóa đơn: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<ChiTietHoaDon> findByHoaDon(String maHoaDon) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            Query query = em.createQuery("SELECT c FROM ChiTietHoaDon c WHERE c.hoaDon.maHoaDon = :maHoaDon", ChiTietHoaDon.class);
            query.setParameter("maHoaDon", maHoaDon);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteByHoaDon(String maHoaDon) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            Query query = em.createQuery("DELETE FROM ChiTietHoaDon c WHERE c.hoaDon.maHoaDon = :maHoaDon");
            query.setParameter("maHoaDon", maHoaDon);
            query.executeUpdate();
            et.commit();
        } catch (Exception e) {
            if (et.isActive()) et.rollback();
            throw new RuntimeException("Lỗi xóa chi tiết hóa đơn: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}

