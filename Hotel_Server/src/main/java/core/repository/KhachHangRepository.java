package core.repository;

import core.entity.KhachHang;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class KhachHangRepository extends BaseRepository {

    public List<KhachHang> findAll() {
        EntityManager em = createEntityManager();
        try {
            return em.createQuery("SELECT k FROM KhachHang k", KhachHang.class).getResultList();
        } finally {
            em.close();
        }
    }

    public KhachHang findById(String maKhachHang) {
        EntityManager em = createEntityManager();
        try {
            return em.find(KhachHang.class, maKhachHang);
        } finally {
            em.close();
        }
    }

    public KhachHang create(KhachHang khachHang) {
        EntityManager em = createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(khachHang);
            tx.commit();
            return khachHang;
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public KhachHang update(KhachHang khachHang) {
        EntityManager em = createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            KhachHang merged = em.merge(khachHang);
            tx.commit();
            return merged;
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public boolean deleteById(String maKhachHang) {
        EntityManager em = createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            KhachHang found = em.find(KhachHang.class, maKhachHang);
            if (found == null) {
                tx.rollback();
                return false;
            }
            em.remove(found);
            tx.commit();
            return true;
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }
}
