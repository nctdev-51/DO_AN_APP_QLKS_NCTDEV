package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.HoaDon;
import iuh.fit.core.repository.IHoaDonRepository;
import iuh.fit.infrastructure.db.JpaConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Class: HoaDonRepositoryImpl (Persistence Implementation)
 * 
 * Tầng: INFRASTRUCTURE - Persistence Layer
 * Trách nhiệm: Implement IHoaDonRepository bằng JPA/Hibernate
 */
public class HoaDonRepositoryImpl implements IHoaDonRepository {
    
    private static final Logger logger = Logger.getLogger(HoaDonRepositoryImpl.class.getName());
    
    @Override
    public Optional<HoaDon> findById(String maHoaDon) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            HoaDon hoaDon = em.find(HoaDon.class, maHoaDon);
            return Optional.ofNullable(hoaDon);
        } catch (Exception e) {
            logger.severe("❌ Lỗi tìm HoaDon: " + e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<HoaDon> findAll() {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            String hql = "SELECT h FROM HoaDon h";
            return em.createQuery(hql, HoaDon.class).getResultList();
        } catch (Exception e) {
            logger.severe("❌ Lỗi lấy tất cả HoaDon: " + e.getMessage());
            return List.of();
        } finally {
            em.close();
        }
    }
    
    @Override
    public HoaDon save(HoaDon hoaDon) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(hoaDon);
            transaction.commit();
            logger.info("✅ HoaDon được lưu thành công: " + hoaDon.getMaHoaDon());
            return hoaDon;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi lưu HoaDon: " + e.getMessage());
            throw new RuntimeException("Không thể lưu HoaDon", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public HoaDon update(HoaDon hoaDon) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            HoaDon merged = em.merge(hoaDon);
            transaction.commit();
            logger.info("✅ HoaDon được cập nhật thành công: " + hoaDon.getMaHoaDon());
            return merged;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi cập nhật HoaDon: " + e.getMessage());
            throw new RuntimeException("Không thể cập nhật HoaDon", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void deleteById(String maHoaDon) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            HoaDon hoaDon = em.find(HoaDon.class, maHoaDon);
            if (hoaDon != null) {
                em.remove(hoaDon);
                logger.info("✅ HoaDon được xóa thành công: " + maHoaDon);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi xóa HoaDon: " + e.getMessage());
            throw new RuntimeException("Không thể xóa HoaDon", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<HoaDon> findByMaKhachHang(String maKhachHang) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            String hql = "SELECT h FROM HoaDon h WHERE h.maKhachHang = :maKhachHang";
            return em.createQuery(hql, HoaDon.class)
                    .setParameter("maKhachHang", maKhachHang)
                    .getResultList();
        } catch (Exception e) {
            logger.severe("❌ Lỗi tìm HoaDon theo khách hàng: " + e.getMessage());
            return List.of();
        } finally {
            em.close();
        }
    }
}

