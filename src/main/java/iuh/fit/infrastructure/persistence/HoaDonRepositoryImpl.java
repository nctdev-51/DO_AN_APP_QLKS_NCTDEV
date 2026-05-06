package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.HoaDon;
import iuh.fit.core.repository.IHoaDonRepository;
import iuh.fit.infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class HoaDonRepositoryImpl implements IHoaDonRepository {

    @Override
    public List<HoaDon> findAll() {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            Query query = em.createQuery("SELECT h FROM HoaDon h", HoaDon.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<HoaDon> findById(String maHoaDon) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            HoaDon hoaDon = em.find(HoaDon.class, maHoaDon);
            return Optional.ofNullable(hoaDon);
        } finally {
            em.close();
        }
    }

    @Override
    public HoaDon save(HoaDon hoaDon) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            em.persist(hoaDon);
            et.commit();
            return hoaDon;
        } catch (Exception e) {
            if (et.isActive()) et.rollback();
            throw new RuntimeException("Lỗi lưu hóa đơn: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public HoaDon update(HoaDon hoaDon) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            HoaDon merged = em.merge(hoaDon);
            et.commit();
            return merged;
        } catch (Exception e) {
            if (et.isActive()) et.rollback();
            throw new RuntimeException("Lỗi cập nhật hóa đơn: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(String maHoaDon) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            HoaDon hoaDon = em.find(HoaDon.class, maHoaDon);
            if (hoaDon != null) {
                em.remove(hoaDon);
            }
            et.commit();
        } catch (Exception e) {
            if (et.isActive()) et.rollback();
            throw new RuntimeException("Lỗi xóa hóa đơn: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<HoaDon> findByPhieuDatPhong(String maPhieuDat) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            Query query = em.createQuery("SELECT h FROM HoaDon h WHERE h.phieuDatPhong.maPhieu = :maPhieu", HoaDon.class);
            query.setParameter("maPhieu", maPhieuDat);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<HoaDon> findByDateRange(LocalDate startDate, LocalDate endDate) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            Query query = em.createQuery("SELECT h FROM HoaDon h WHERE h.ngayTao >= :startDate AND h.ngayTao <= :endDate ORDER BY h.ngayTao DESC", HoaDon.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public double getTotalRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            Query query = em.createQuery("SELECT SUM(h.tongTien) FROM HoaDon h WHERE h.ngayTao >= :startDate AND h.ngayTao <= :endDate");
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            Object result = query.getSingleResult();
            return result != null ? ((Number) result).doubleValue() : 0.0;
        } finally {
            em.close();
        }
    }
}

