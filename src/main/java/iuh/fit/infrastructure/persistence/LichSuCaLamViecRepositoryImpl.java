package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.LichSuCaLamViec;
import iuh.fit.core.repository.ILichSuCaLamViecRepository;
import iuh.fit.infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;

public class LichSuCaLamViecRepositoryImpl implements ILichSuCaLamViecRepository {

    // Không cần hàm khởi tạo (Constructor) nữa, sử dụng mặc định 0 tham số

    @Override
    public LichSuCaLamViec findCaDangLam(String maNhanVien) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            String jpql = "SELECT l FROM LichSuCaLamViec l WHERE l.nhanVien.maNhanVien = :maNhanVien AND l.trangThai = 'DANG_LAM'";
            return em.createQuery(jpql, LichSuCaLamViec.class)
                    .setParameter("maNhanVien", maNhanVien)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public LichSuCaLamViec save(LichSuCaLamViec lichSu) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(lichSu);
            tx.commit();
            return lichSu;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public LichSuCaLamViec update(LichSuCaLamViec lichSu) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            LichSuCaLamViec merged = em.merge(lichSu);
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
    public Optional<LichSuCaLamViec> findById(String maLichSu) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            return Optional.ofNullable(em.find(LichSuCaLamViec.class, maLichSu));
        } finally {
            em.close();
        }
    }

    @Override
    public List<LichSuCaLamViec> findAll() {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            return em.createQuery("SELECT l FROM LichSuCaLamViec l", LichSuCaLamViec.class).getResultList();
        } finally {
            em.close();
        }
    }
}