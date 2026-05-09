package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.ChiTietPhieuDatPhong;
import iuh.fit.core.entity.ChiTietPhieuDatPhongId;
import iuh.fit.core.repository.IChiTietPhieuDatPhongRepository;
import iuh.fit.infrastructure.db.JpaConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Class: ChiTietPhieuDatPhongRepositoryImpl (Persistence Implementation)
 * 
 * Tầng: INFRASTRUCTURE - Persistence Layer
 * Trách nhiệm: Implement IChiTietPhieuDatPhongRepository bằng JPA/Hibernate
 */
public class ChiTietPhieuDatPhongRepositoryImpl implements IChiTietPhieuDatPhongRepository {
    
    private static final Logger logger = Logger.getLogger(ChiTietPhieuDatPhongRepositoryImpl.class.getName());
    
    @Override
    public List<ChiTietPhieuDatPhong> findByMaPhieu(String maPhieu) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            String hql = "SELECT c FROM ChiTietPhieuDatPhong c WHERE c.id.maPhieu = :maPhieu";
            return em.createQuery(hql, ChiTietPhieuDatPhong.class)
                    .setParameter("maPhieu", maPhieu)
                    .getResultList();
        } catch (Exception e) {
            logger.severe("❌ Lỗi tìm ChiTietPhieuDatPhong theo phiếu: " + e.getMessage());
            return List.of();
        } finally {
            em.close();
        }
    }
    
    @Override
    public ChiTietPhieuDatPhong save(ChiTietPhieuDatPhong chiTiet) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(chiTiet);
            transaction.commit();
            logger.info("✅ ChiTietPhieuDatPhong được lưu thành công");
            return chiTiet;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi lưu ChiTietPhieuDatPhong: " + e.getMessage());
            throw new RuntimeException("Không thể lưu ChiTietPhieuDatPhong", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void deleteByMaPhieu(String maPhieu) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            String hql = "DELETE FROM ChiTietPhieuDatPhong c WHERE c.id.maPhieu = :maPhieu";
            em.createQuery(hql)
                    .setParameter("maPhieu", maPhieu)
                    .executeUpdate();
            transaction.commit();
            logger.info("✅ ChiTietPhieuDatPhong được xóa thành công: " + maPhieu);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi xóa ChiTietPhieuDatPhong: " + e.getMessage());
            throw new RuntimeException("Không thể xóa ChiTietPhieuDatPhong", e);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<ChiTietPhieuDatPhong> findById(ChiTietPhieuDatPhongId id) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            return Optional.ofNullable(em.find(ChiTietPhieuDatPhong.class, id));
        } finally {
            em.close();
        }
    }
}

