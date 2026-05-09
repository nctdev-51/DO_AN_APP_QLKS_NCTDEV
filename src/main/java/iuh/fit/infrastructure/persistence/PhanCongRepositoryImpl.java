package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.PhanCongCaLamViec;
import iuh.fit.core.repository.IPhanCongRepository;
import iuh.fit.infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PhanCongRepositoryImpl implements IPhanCongRepository {

    // Không cần hàm khởi tạo (Constructor) nữa, sử dụng mặc định 0 tham số

    @Override
    public PhanCongCaLamViec findPhanCongToday(String maNhanVien, LocalDate ngayLamViec) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            String jpql = "SELECT p FROM PhanCongCaLamViec p WHERE p.nhanVien.maNhanVien = :maNhanVien AND p.ngayLamViec = :ngayLamViec";
            return em.createQuery(jpql, PhanCongCaLamViec.class)
                    .setParameter("maNhanVien", maNhanVien)
                    .setParameter("ngayLamViec", ngayLamViec)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public PhanCongCaLamViec save(PhanCongCaLamViec phanCong) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(phanCong);
            tx.commit();
            return phanCong;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public PhanCongCaLamViec update(PhanCongCaLamViec phanCong) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            PhanCongCaLamViec merged = em.merge(phanCong);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean deleteById(String id) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            PhanCongCaLamViec pc = em.find(PhanCongCaLamViec.class, id);
            if (pc != null) {
                em.remove(pc);
                tx.commit();
                return true;
            }
            tx.rollback();
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<PhanCongCaLamViec> findById(String id) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            return Optional.ofNullable(em.find(PhanCongCaLamViec.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<PhanCongCaLamViec> findAll() {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM PhanCongCaLamViec p", PhanCongCaLamViec.class).getResultList();
        } finally {
            em.close();
        }
    }
}