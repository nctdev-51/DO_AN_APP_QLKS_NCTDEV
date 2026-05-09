package iuh.fit.core.service.impl;

import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.entity.Phong;
import iuh.fit.core.repository.IPhongRepository;
import iuh.fit.core.service.IPhongService;
import iuh.fit.infrastructure.db.JpaConfig;
import iuh.fit.infrastructure.mapper.PhongMapper;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class PhongServiceImpl implements IPhongService {
    private final IPhongRepository phongRepository;

    public PhongServiceImpl(IPhongRepository phongRepository) {
        this.phongRepository = phongRepository;
    }

    @Override
    public List<PhongDTO> getAllPhong() {
        return phongRepository.findAll().stream()
                .map(PhongMapper::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PhongDTO getPhongById(String maPhong) {
        return phongRepository.findById(maPhong)
                .map(PhongMapper::entityToDTO)
                .orElse(null);
    }

    @Override
    public List<PhongDTO> getPhongByTinhTrang(String tinhTrang) {
        return phongRepository.findByTinhTrang(tinhTrang).stream()
                .map(PhongMapper::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PhongDTO> getPhongByMaLoaiPhong(String maLoaiPhong) {
        return phongRepository.findByMaLoaiPhong(maLoaiPhong).stream()
                .map(PhongMapper::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PhongDTO addPhong(PhongDTO phongDTO) throws IllegalArgumentException {
        if (phongDTO.getMaPhong() == null || phongDTO.getMaPhong().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phòng không được để trống");
        }
        Phong entity = PhongMapper.dtoToEntity(phongDTO);
        Phong saved = phongRepository.save(entity);
        return PhongMapper.entityToDTO(saved);
    }

    @Override
    public PhongDTO updatePhong(PhongDTO phongDTO) throws IllegalArgumentException {
        if (phongDTO.getMaPhong() == null || phongDTO.getMaPhong().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phòng không được để trống");
        }
        Phong entity = PhongMapper.dtoToEntity(phongDTO);
        Phong updated = phongRepository.update(entity);
        return PhongMapper.entityToDTO(updated);
    }

    @Override
    public boolean deletePhong(String maPhong) {
        try {
            phongRepository.deleteById(maPhong);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // =========================================================================
    // 👉 FIX CỐT LÕI: LOGIC CHỐNG TRÙNG LỊCH BẰNG EntityManager (JPA Thuần)
    // =========================================================================
    @Override
    public List<PhongDTO> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, double minPrice, double maxPrice, String tinhTrang) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            // 1. Chuyển LocalDate thành LocalDateTime để khớp chuẩn hệ thống (Nhận 14h, Trả 12h)
            java.time.LocalDateTime inTime = checkIn.atTime(14, 0);
            java.time.LocalDateTime outTime = checkOut.atTime(12, 0);

            // 2. Câu lệnh JPQL quét sâu vào DB để loại bỏ các phòng bị trùng thời gian lưu trú
            String jpql = "SELECT p FROM Phong p WHERE p.tinhTrang != 'Bảo Trì' " +
                    "AND p.giaPhong >= :minPrice AND p.giaPhong <= :maxPrice " +
                    "AND p.maPhong NOT IN (" +
                    "    SELECT pdp.phong.maPhong FROM PhieuDatPhong pdp " +
                    "    WHERE pdp.trangThai NOT IN ('DA_HUY', 'DA_TRA_PHONG', 'Đã checkout', 'Trả Phòng') " +
                    "    AND pdp.ngayNhan < :outTime " +
                    "    AND pdp.ngayTra > :inTime" +
                    ")";

            List<Phong> phongTrong = em.createQuery(jpql, Phong.class)
                    .setParameter("minPrice", minPrice)
                    .setParameter("maxPrice", maxPrice)
                    .setParameter("outTime", outTime)
                    .setParameter("inTime", inTime)
                    .getResultList();

            // 3. Map sang DTO và lọc thêm trạng thái UI (nếu Lễ tân chọn)
            return phongTrong.stream()
                    .filter(p -> tinhTrang == null || tinhTrang.equals("Tất cả trạng thái") || p.getTinhTrang().equalsIgnoreCase(tinhTrang))
                    .map(PhongMapper::entityToDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Lỗi quét trùng lịch: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // Trả về rỗng để kích hoạt Fallback trên Controller
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // =========================================================================

    @Override
    public boolean updatePhongTrangThai(String maPhong, String trangThai) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            Phong phong = em.find(Phong.class, maPhong);
            if (phong != null) {
                phong.setTinhTrang(trangThai);
                em.merge(phong);
                em.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public List<PhongDTO> getPhongByPhieuDat(String maPhieu) {
        return phongRepository.getDanhSachPhongTheoMaPhieu(maPhieu).stream()
                .map(PhongMapper::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Phong> findPhongByMaPhieu(String maPhieu) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            String jpql = "SELECT p FROM Phong p WHERE p.maPhong = (SELECT pdp.phong.maPhong FROM PhieuDatPhong pdp WHERE pdp.maPhieu = :maPhieu)";
            return em.createQuery(jpql, Phong.class)
                    .setParameter("maPhieu", maPhieu)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}