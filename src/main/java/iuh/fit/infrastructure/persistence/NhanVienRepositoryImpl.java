package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.NhanVien;
import iuh.fit.core.repository.INhanVienRepository;
import iuh.fit.infrastructure.db.JpaConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Class: NhanVienRepositoryImpl (Persistence Implementation)
 * 
 * Tầng: INFRASTRUCTURE - Persistence Layer
 * Trách nhiệm: Implement INhanVienRepository bằng JPA/Hibernate
 */
public class NhanVienRepositoryImpl implements INhanVienRepository {
    
    private static final Logger logger = Logger.getLogger(NhanVienRepositoryImpl.class.getName());
    
    @Override
    public Optional<NhanVien> findById(String maNhanVien) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            NhanVien nhanVien = em.find(NhanVien.class, maNhanVien);
            return Optional.ofNullable(nhanVien);
        } catch (Exception e) {
            logger.severe("❌ Lỗi tìm NhanVien: " + e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<NhanVien> findAll() {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            String hql = "SELECT n FROM NhanVien n";
            return em.createQuery(hql, NhanVien.class).getResultList();
        } catch (Exception e) {
            logger.severe("❌ Lỗi lấy tất cả NhanVien: " + e.getMessage());
            return List.of();
        } finally {
            em.close();
        }
    }
    
    @Override
    public NhanVien save(NhanVien nhanVien) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(nhanVien);
            transaction.commit();
            logger.info("✅ NhanVien được lưu thành công: " + nhanVien.getMaNhanVien());
            return nhanVien;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi lưu NhanVien: " + e.getMessage());
            throw new RuntimeException("Không thể lưu NhanVien", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public NhanVien update(NhanVien nhanVien) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            NhanVien merged = em.merge(nhanVien);
            transaction.commit();
            logger.info("✅ NhanVien được cập nhật thành công: " + nhanVien.getMaNhanVien());
            return merged;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi cập nhật NhanVien: " + e.getMessage());
            throw new RuntimeException("Không thể cập nhật NhanVien", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void deleteById(String maNhanVien) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            NhanVien nhanVien = em.find(NhanVien.class, maNhanVien);
            if (nhanVien != null) {
                em.remove(nhanVien);
                transaction.commit();
                logger.info("✅ NhanVien được xóa thành công: " + maNhanVien);
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi xóa NhanVien: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<NhanVien> findBySoDienThoai(String soDienThoai) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            String hql = "SELECT n FROM NhanVien n WHERE n.soDienThoai = :soDienThoai";
            TypedQuery<NhanVien> query = em.createQuery(hql, NhanVien.class);
            query.setParameter("soDienThoai", soDienThoai);
            
            return query.getResultList().stream().findFirst();
        } catch (Exception e) {
            logger.severe("❌ Lỗi tìm NhanVien theo SDT: " + e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}

