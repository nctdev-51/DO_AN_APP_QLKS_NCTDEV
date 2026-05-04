package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.TaiKhoan;
import iuh.fit.core.repository.ITaiKhoanRepository;
import iuh.fit.infrastructure.db.JpaConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Class: TaiKhoanRepositoryImpl (Persistence Implementation)
 * 
 * Tầng: INFRASTRUCTURE - Persistence Layer
 * Trách nhiệm: Implement ITaiKhoanRepository bằng JPA/Hibernate
 * 
 * Đây là ADAPTER pattern:
 * - Implement interface từ core.repository
 * - Sử dụng JPA/Hibernate để thao tác với database
 * - Chuyển Entity ↔ Database
 * 
 * Quy trình:
 * 1. Lấy EntityManager từ JpaConfig
 * 2. Thực hiện CRUD operations
 * 3. Handle transactions
 * 4. Đóng EntityManager
 */
public class TaiKhoanRepositoryImpl implements ITaiKhoanRepository {
    
    private static final Logger logger = Logger.getLogger(TaiKhoanRepositoryImpl.class.getName());
    
    @Override
    public Optional<TaiKhoan> findByTaiKhoan(String taiKhoan) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            // HQL Query: Tìm TaiKhoan theo tenDangNhap field
            // ✅ Sử dụng tenDangNhap (tên field trong entity)
            String hql = "SELECT t FROM TaiKhoan t WHERE t.tenDangNhap = :tenDangNhap";
            TypedQuery<TaiKhoan> query = em.createQuery(hql, TaiKhoan.class);
            query.setParameter("tenDangNhap", taiKhoan);
            
            // getSingleResult() trả về Optional tự động
            return query.getResultList().stream().findFirst();
        } catch (Exception e) {
            logger.severe("❌ Lỗi tìm TaiKhoan: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    
    @Override
    public boolean existsByTaiKhoan(String taiKhoan) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            String hql = "SELECT COUNT(t) FROM TaiKhoan t WHERE t.tenDangNhap = :tenDangNhap";
            TypedQuery<Long> query = em.createQuery(hql, Long.class);
            query.setParameter("tenDangNhap", taiKhoan);
            
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            logger.severe("❌ Lỗi kiểm tra tồn tại TaiKhoan: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
    
    @Override
    public TaiKhoan save(TaiKhoan taiKhoan) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            
            // persist(): Thêm entity vào persistence context
            em.persist(taiKhoan);
            
            transaction.commit();
            logger.info("✅ TaiKhoan được lưu thành công: " + taiKhoan.getTenDangNhap());
            
            return taiKhoan;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi lưu TaiKhoan: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Không thể lưu TaiKhoan", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<TaiKhoan> findAll() {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            String hql = "SELECT t FROM TaiKhoan t";
            return em.createQuery(hql, TaiKhoan.class).getResultList();
        } catch (Exception e) {
            logger.severe("❌ Lỗi lấy tất cả TaiKhoan: " + e.getMessage());
            return List.of();
        } finally {
            em.close();
        }
    }
    
    @Override
    public void deleteByTaiKhoan(String taiKhoan) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            
            String hql = "DELETE FROM TaiKhoan t WHERE t.taiKhoan = :taiKhoan";
            em.createQuery(hql)
                    .setParameter("taiKhoan", taiKhoan)
                    .executeUpdate();
            
            transaction.commit();
            logger.info("✅ TaiKhoan được xóa thành công: " + taiKhoan);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi xóa TaiKhoan: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    
    @Override
    public TaiKhoan update(TaiKhoan taiKhoan) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            
            // merge(): Cập nhật entity từ detached state
            TaiKhoan merged = em.merge(taiKhoan);
            
            transaction.commit();
            logger.info("✅ TaiKhoan được cập nhật thành công: " + taiKhoan.getTenDangNhap());
            
            return merged;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi cập nhật TaiKhoan: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Không thể cập nhật TaiKhoan", e);
        } finally {
            em.close();
        }
    }
}

