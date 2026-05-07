package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.LoaiPhong;
import iuh.fit.core.repository.ILoaiPhongRepository;
import iuh.fit.infrastructure.db.JpaConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Class: LoaiPhongRepositoryImpl (Persistence Implementation)
 * 
 * Tầng: INFRASTRUCTURE - Persistence Layer
 * Trách nhiệm: Implement ILoaiPhongRepository bằng JPA/Hibernate
 */
public class LoaiPhongRepositoryImpl implements ILoaiPhongRepository {
    
    private static final Logger logger = Logger.getLogger(LoaiPhongRepositoryImpl.class.getName());
    
    @Override
    public Optional<LoaiPhong> findById(String maLoaiPhong) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            LoaiPhong loaiPhong = em.find(LoaiPhong.class, maLoaiPhong);
            return Optional.ofNullable(loaiPhong);
        } catch (Exception e) {
            logger.severe("❌ Lỗi tìm LoaiPhong: " + e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<LoaiPhong> findAll() {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            String hql = "SELECT l FROM LoaiPhong l";
            return em.createQuery(hql, LoaiPhong.class).getResultList();
        } catch (Exception e) {
            logger.severe("❌ Lỗi lấy tất cả LoaiPhong: " + e.getMessage());
            return List.of();
        } finally {
            em.close();
        }
    }
    
    @Override
    public LoaiPhong save(LoaiPhong loaiPhong) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(loaiPhong);
            transaction.commit();
            logger.info("✅ LoaiPhong được lưu thành công: " + loaiPhong.getMaLoaiPhong());
            return loaiPhong;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi lưu LoaiPhong: " + e.getMessage());
            throw new RuntimeException("Không thể lưu LoaiPhong", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public LoaiPhong update(LoaiPhong loaiPhong) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            LoaiPhong merged = em.merge(loaiPhong);
            transaction.commit();
            logger.info("✅ LoaiPhong được cập nhật thành công: " + loaiPhong.getMaLoaiPhong());
            return merged;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi cập nhật LoaiPhong: " + e.getMessage());
            throw new RuntimeException("Không thể cập nhật LoaiPhong", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void deleteById(String maLoaiPhong) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            LoaiPhong loaiPhong = em.find(LoaiPhong.class, maLoaiPhong);
            if (loaiPhong != null) {
                em.remove(loaiPhong);
                logger.info("✅ LoaiPhong được xóa thành công: " + maLoaiPhong);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi xóa LoaiPhong: " + e.getMessage());
            throw new RuntimeException("Không thể xóa LoaiPhong", e);
        } finally {
            em.close();
        }
    }
}

