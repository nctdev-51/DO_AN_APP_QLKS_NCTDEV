package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.KhachHang;
import iuh.fit.core.repository.IKhachHangRepository;
import iuh.fit.infrastructure.db.JpaConfig;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Class: KhachHangRepositoryImpl (Persistence Implementation)
 * 
 * Tầng: INFRASTRUCTURE - Persistence Layer
 * Trách nhiệm: Implement IKhachHangRepository bằng JPA/Hibernate
 */
public class KhachHangRepositoryImpl implements IKhachHangRepository {
    
    private static final Logger logger = Logger.getLogger(KhachHangRepositoryImpl.class.getName());
    
    @Override
    public Optional<KhachHang> findById(String maKhachHang) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            KhachHang khachHang = em.find(KhachHang.class, maKhachHang);
            return Optional.ofNullable(khachHang);
        } catch (Exception e) {
            logger.severe("❌ Lỗi tìm KhachHang: " + e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<KhachHang> findAll() {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            String hql = "SELECT k FROM KhachHang k";
            return em.createQuery(hql, KhachHang.class).getResultList();
        } catch (Exception e) {
            logger.severe("❌ Lỗi lấy tất cả KhachHang: " + e.getMessage());
            return List.of();
        } finally {
            em.close();
        }
    }
    
    @Override
    public KhachHang save(KhachHang khachHang) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(khachHang);
            transaction.commit();
            logger.info("✅ KhachHang được lưu thành công: " + khachHang.getMaKhachHang());
            return khachHang;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi lưu KhachHang: " + e.getMessage());
            throw new RuntimeException("Không thể lưu KhachHang", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public KhachHang update(KhachHang khachHang) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            KhachHang merged = em.merge(khachHang);
            transaction.commit();
            logger.info("✅ KhachHang được cập nhật thành công: " + khachHang.getMaKhachHang());
            return merged;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi cập nhật KhachHang: " + e.getMessage());
            throw new RuntimeException("Không thể cập nhật KhachHang", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void deleteById(String maKhachHang) {
        EntityManager em = JpaConfig.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            KhachHang khachHang = em.find(KhachHang.class, maKhachHang);
            if (khachHang != null) {
                em.remove(khachHang);
                transaction.commit();
                logger.info("✅ KhachHang được xóa thành công: " + maKhachHang);
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.severe("❌ Lỗi xóa KhachHang: " + e.getMessage());
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<KhachHang> findBySoDienThoai(String soDienThoai) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            String hql = "SELECT k FROM KhachHang k WHERE k.soDienThoai = :soDienThoai";
            TypedQuery<KhachHang> query = em.createQuery(hql, KhachHang.class);
            query.setParameter("soDienThoai", soDienThoai);
            
            return query.getResultList().stream().findFirst();
        } catch (Exception e) {
            logger.severe("❌ Lỗi tìm KhachHang theo SDT: " + e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    // Thêm hàm này vào KhachHangRepositoryImpl.java
    public String phatSinhMaKhachHangMoi() {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            // Lấy mã KH lớn nhất. Mẹo: Sắp xếp theo chiều dài chuỗi trước, sau đó sắp xếp theo chuỗi
            // để tránh lỗi chuỗi "KH99" lớn hơn "KH100"
            String jpql = "SELECT k.maKhachHang FROM KhachHang k WHERE k.maKhachHang LIKE 'KH%' " +
                    "ORDER BY LENGTH(k.maKhachHang) DESC, k.maKhachHang DESC";

            List<String> listMa = em.createQuery(jpql, String.class)
                    .setMaxResults(1) // Chỉ lấy 1 dòng lớn nhất
                    .getResultList();

            if (listMa.isEmpty()) {
                return "KH001"; // Nếu chưa có khách hàng nào trong DB
            }

            String maxMa = listMa.get(0); // VD: "KH015"
            try {
                // Cắt 2 ký tự "KH" đầu tiên, lấy phần số phía sau và cộng 1
                int so = Integer.parseInt(maxMa.substring(2));
                so++;
                // Trả về định dạng KH + số (luôn có ít nhất 3 chữ số, VD: KH016)
                return String.format("KH%03d", so);
            } catch (NumberFormatException e) {
                // Rơi vào đây nếu mã cũ bị lộn xộn (như KH2026...). Sẽ tự động lấy thời gian để không bị lỗi.
                return "KH" + (System.currentTimeMillis() % 100000);
            }
        }
    }
}

