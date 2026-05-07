package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.ChiTietHoaDon;
import iuh.fit.core.repository.IChiTietHoaDonRepository;
import iuh.fit.infrastructure.db.JpaConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class: ChiTietHoaDonRepositoryImpl (Persistence Implementation)
 * 
 * Tầng: INFRASTRUCTURE - Persistence Layer
 * Trách nhiệm: Implement IChiTietHoaDonRepository bằng JPA/Hibernate
 */
public class ChiTietHoaDonRepositoryImpl implements IChiTietHoaDonRepository {
    
    private static final Logger logger = Logger.getLogger(ChiTietHoaDonRepositoryImpl.class.getName());
    
    @Override
    public List<ChiTietHoaDon> findByMaHoaDon(String maHoaDon) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            String hql = "SELECT c FROM ChiTietHoaDon c WHERE c.maHoaDon = :maHoaDon";
            return em.createQuery(hql, ChiTietHoaDon.class)
                    .setParameter("maHoaDon", maHoaDon)
                    .getResultList();
        } catch (Exception e) {
            logger.severe("❌ Lỗi tìm ChiTietHoaDon theo hóa đơn: " + e.getMessage());
            return List.of();
        } finally {
            em.close();
        }
    }
    
    @Override
    public ChiTietHoaDon save(ChiTietHoaDon chiTiet) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(chiTiet);
            transaction.commit();
            logger.info("✅ ChiTietHoaDon được lưu thành công");
            return chiTiet;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi lưu ChiTietHoaDon: " + e.getMessage());
            throw new RuntimeException("Không thể lưu ChiTietHoaDon", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void deleteByMaHoaDon(String maHoaDon) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            String hql = "DELETE FROM ChiTietHoaDon c WHERE c.maHoaDon = :maHoaDon";
            em.createQuery(hql)
                    .setParameter("maHoaDon", maHoaDon)
                    .executeUpdate();
            transaction.commit();
            logger.info("✅ ChiTietHoaDon được xóa thành công: " + maHoaDon);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi xóa ChiTietHoaDon: " + e.getMessage());
            throw new RuntimeException("Không thể xóa ChiTietHoaDon", e);
        } finally {
            em.close();
        }
    }
}

