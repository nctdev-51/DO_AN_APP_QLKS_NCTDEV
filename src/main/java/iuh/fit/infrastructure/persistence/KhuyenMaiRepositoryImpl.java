package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.KhuyenMai;
import iuh.fit.core.repository.IKhuyenMaiRepository;
import iuh.fit.infrastructure.db.JpaConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Class: KhuyenMaiRepositoryImpl (Persistence Implementation)
 * 
 * Tầng: INFRASTRUCTURE - Persistence Layer
 * Trách nhiệm: Implement IKhuyenMaiRepository bằng JPA/Hibernate
 */
public class KhuyenMaiRepositoryImpl implements IKhuyenMaiRepository {
    
    private static final Logger logger = Logger.getLogger(KhuyenMaiRepositoryImpl.class.getName());
    
    @Override
    public Optional<KhuyenMai> findById(String maKhuyenMai) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            KhuyenMai khuyenMai = em.find(KhuyenMai.class, maKhuyenMai);
            return Optional.ofNullable(khuyenMai);
        } catch (Exception e) {
            logger.severe("❌ Lỗi tìm KhuyenMai: " + e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<KhuyenMai> findAll() {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            String hql = "SELECT k FROM KhuyenMai k";
            return em.createQuery(hql, KhuyenMai.class).getResultList();
        } catch (Exception e) {
            logger.severe("❌ Lỗi lấy tất cả KhuyenMai: " + e.getMessage());
            return List.of();
        } finally {
            em.close();
        }
    }
    
    @Override
    public KhuyenMai save(KhuyenMai khuyenMai) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(khuyenMai);
            transaction.commit();
            logger.info("✅ KhuyenMai được lưu thành công: " + khuyenMai.getMaKhuyenMai());
            return khuyenMai;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi lưu KhuyenMai: " + e.getMessage());
            throw new RuntimeException("Không thể lưu KhuyenMai", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public KhuyenMai update(KhuyenMai khuyenMai) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            KhuyenMai merged = em.merge(khuyenMai);
            transaction.commit();
            logger.info("✅ KhuyenMai được cập nhật thành công: " + khuyenMai.getMaKhuyenMai());
            return merged;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi cập nhật KhuyenMai: " + e.getMessage());
            throw new RuntimeException("Không thể cập nhật KhuyenMai", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void deleteById(String maKhuyenMai) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            KhuyenMai khuyenMai = em.find(KhuyenMai.class, maKhuyenMai);
            if (khuyenMai != null) {
                em.remove(khuyenMai);
                logger.info("✅ KhuyenMai được xóa thành công: " + maKhuyenMai);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi xóa KhuyenMai: " + e.getMessage());
            throw new RuntimeException("Không thể xóa KhuyenMai", e);
        } finally {
            em.close();
        }
    }
}

