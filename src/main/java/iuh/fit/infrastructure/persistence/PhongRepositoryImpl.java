package iuh.fit.infrastructure.persistence;

import iuh.fit.core.entity.Phong;
import iuh.fit.core.repository.IPhongRepository;
import iuh.fit.infrastructure.db.JpaConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class PhongRepositoryImpl implements IPhongRepository {
    private static final Logger logger = Logger.getLogger(PhongRepositoryImpl.class.getName());

    @Override
    public List<Phong> findAll() {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            return em.createQuery("SELECT p FROM Phong p", Phong.class).getResultList();
        }
    }

    @Override
    public Optional<Phong> findById(String maPhong) {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            return Optional.ofNullable(em.find(Phong.class, maPhong));
        }
    }

    @Override
    public List<Phong> findByTinhTrang(String tinhTrang) {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            return em.createQuery("SELECT p FROM Phong p WHERE p.tinhTrang = :tt", Phong.class)
                    .setParameter("tt", tinhTrang)
                    .getResultList();
        }
    }

    @Override
    public List<Phong> findByMaLoaiPhong(String maLoaiPhong) {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            return em.createQuery("SELECT p FROM Phong p WHERE p.loaiPhong = :ml", Phong.class)
                    .setParameter("ml", maLoaiPhong)
                    .getResultList();
        }
    }

    @Override
    public Phong save(Phong phong) {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(phong);
                tx.commit();
                return phong;
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                logger.severe("Lỗi lưu phòng: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Phong update(Phong phong) {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Phong merged = em.merge(phong);
                tx.commit();
                return merged;
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                logger.severe("Lỗi cập nhật phòng: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void deleteById(String maPhong) {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Phong p = em.find(Phong.class, maPhong);
                if (p != null) em.remove(p);
                tx.commit();
                logger.info("Đã xóa phòng: " + maPhong);
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                logger.severe("Lỗi xóa phòng: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<Phong> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, double minPrice, double maxPrice) {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            // Đã đổi "SELECT pdp.maPhong" thành "SELECT pdp.phong"
            String jpql = "SELECT p FROM Phong p " +
                    "WHERE p.giaPhong BETWEEN :min AND :max " +
                    "  AND p.tinhTrang <> 'Bảo Trì' " +
                    "  AND p NOT IN (" +
                    "      SELECT pdp.phong FROM PhieuDatPhong pdp " +
                    "      WHERE pdp.trangThai NOT IN ('DA_HUY', 'DA_TRA_PHONG', 'Đã checkout', 'Trả Phòng') " +
                    "        AND (pdp.ngayNhan < :checkOut AND pdp.ngayTra > :checkIn)" +
                    "  )";

            return em.createQuery(jpql, Phong.class)
                    .setParameter("min", minPrice)
                    .setParameter("max", maxPrice)
                    .setParameter("checkIn", checkIn)
                    .setParameter("checkOut", checkOut)
                    .getResultList();
        }
    }
}