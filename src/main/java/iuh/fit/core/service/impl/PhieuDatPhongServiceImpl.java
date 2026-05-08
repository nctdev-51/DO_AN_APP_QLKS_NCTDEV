package iuh.fit.core.service.impl;

import iuh.fit.core.dto.HoaDonDTO;
import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.entity.HoaDon;
import iuh.fit.core.entity.PhieuDatPhong;
import iuh.fit.core.entity.Phong;
import iuh.fit.core.repository.IPhieuDatPhongRepository;
import iuh.fit.core.service.IPhieuDatPhongService;
import iuh.fit.infrastructure.db.JpaConfig;
import iuh.fit.infrastructure.mapper.HoaDonMapper;
import iuh.fit.infrastructure.mapper.PhieuDatPhongMapper;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class PhieuDatPhongServiceImpl implements IPhieuDatPhongService {
    private final IPhieuDatPhongRepository phieuRepository;

    public PhieuDatPhongServiceImpl(IPhieuDatPhongRepository phieuRepository) {
        this.phieuRepository = phieuRepository;
    }

    @Override
    public List<PhieuDatPhongDTO> getAllPhieuDatPhong() {
        return phieuRepository.findAll().stream()
                .map(PhieuDatPhongMapper::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PhieuDatPhongDTO getPhieuDatPhongById(String maPhieu) {
        return phieuRepository.findById(maPhieu)
                .map(PhieuDatPhongMapper::entityToDTO)
                .orElse(null);
    }

    @Override
    public List<PhieuDatPhongDTO> getPhieuDatPhongByKhachHang(String maKhachHang) {
        return phieuRepository.findByMaKhachHang(maKhachHang).stream()
                .map(PhieuDatPhongMapper::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PhieuDatPhongDTO> getPhieuDatPhongByPhong(String maPhong) {
        return phieuRepository.findByMaPhong(maPhong).stream()
                .map(PhieuDatPhongMapper::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PhieuDatPhongDTO> getPhieuDatPhongInDateRange(LocalDate startDate, LocalDate endDate) {
        return phieuRepository.findByNgayDatBetween(startDate, endDate).stream()
                .map(PhieuDatPhongMapper::entityToDTO)
                .collect(Collectors.toList());
    }

    // ✅ IMPLEMENTATION: Lọc phiếu đặt phòng theo trạng thái
    @Override
    public List<PhieuDatPhongDTO> getPhieuDatPhongByTrangThai(String status) {
        return phieuRepository.findByTrangThai(status).stream()
                .map(PhieuDatPhongMapper::entityToDTO)
                .collect(Collectors.toList());
    }

    // Bổ sung method này vào class PhieuDatPhongRepositoryImpl
    @Override
    public List<PhieuDatPhong> findByTrangThai(String trangThai) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            String jpql = "SELECT p FROM PhieuDatPhong p WHERE p.trangThai = :trangThai";
            return em.createQuery(jpql, PhieuDatPhong.class)
                    .setParameter("trangThai", trangThai)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public PhieuDatPhongDTO addPhieuDatPhong(PhieuDatPhongDTO phieuDTO) throws IllegalArgumentException {
        if (phieuDTO.getMaPhieu() == null || phieuDTO.getMaPhieu().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu không được để trống");
        }
        PhieuDatPhong entity = PhieuDatPhongMapper.dtoToEntity(phieuDTO);
        PhieuDatPhong saved = phieuRepository.save(entity);
        return PhieuDatPhongMapper.entityToDTO(saved);
    }

    @Override
    public PhieuDatPhongDTO updatePhieuDatPhong(PhieuDatPhongDTO phieuDTO) throws IllegalArgumentException {
        if (phieuDTO.getMaPhieu() == null || phieuDTO.getMaPhieu().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu không được để trống");
        }

        // 👉 CÁCH SỬA: Lấy phiếu "xịn" đầy đủ thông tin từ Database lên trước
        PhieuDatPhong entityGoc = phieuRepository.findById(phieuDTO.getMaPhieu())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu trong Database"));

        // 👉 CẬP NHẬT: Chỉ đổi đúng trạng thái (và các trường cho phép đổi), giữ nguyên Khách và Phòng
        entityGoc.setTrangThai(phieuDTO.getTrangThai());

        // Lưu lại xuống DB
        PhieuDatPhong updated = phieuRepository.update(entityGoc);

        // Bắt lỗi Thất bại ngầm (Silent Fail) từ Repository
        if (updated == null) {
            throw new RuntimeException("Cập nhật thất bại tại Database (Lỗi ngầm)");
        }

        return PhieuDatPhongMapper.entityToDTO(updated);
    }

    @Override
    public boolean deletePhieuDatPhong(String maPhieu) {
        try {
            phieuRepository.deleteById(maPhieu);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean bookRoomTransaction(PhieuDatPhongDTO phieuDTO) {
        try {
            if (phieuDTO == null || phieuDTO.getMaPhieu() == null) {
                throw new IllegalArgumentException("Phiếu đặt phòng không hợp lệ");
            }
            if (phieuDTO.getMaPhong() == null || phieuDTO.getMaKhachHang() == null) {
                throw new IllegalArgumentException("Thông tin phòng hoặc khách hàng không được để trống");
            }

            PhieuDatPhong entity = PhieuDatPhongMapper.dtoToEntity(phieuDTO);

            // ❌ XÓA DÒNG NÀY ĐI NHÉ:
            // entity.setTrangThai("DA_NHAN_PHONG");

            return phieuRepository.saveBookingTransaction(entity);
        } catch (Exception e) {
            System.err.println("Lỗi trong bookRoomTransaction: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ✅ IMPLEMENTATION: Transaction (Atomicity) -> Lưu phiếu + Cập nhật phòng + Lưu hóa đơn
    @Override
    public boolean checkoutTransaction(String maPhieu, HoaDonDTO hoaDonDTO) {
        EntityManager em = JpaConfig.getEntityManager();
        try {
            em.getTransaction().begin();

            // 1. Cập nhật trạng thái Phiếu Đặt Phòng
            PhieuDatPhong phieu = em.find(PhieuDatPhong.class, maPhieu);
            if (phieu == null) throw new IllegalArgumentException("Không tìm thấy phiếu đặt phòng");
            phieu.setTrangThai("DA_THANH_TOAN");
            em.merge(phieu);

            // 2. Cập nhật trạng thái Phòng (Đang sử dụng -> Trống/Sẵn sàng)
            if (phieu.getPhong() != null) {
                Phong phong = em.find(Phong.class, phieu.getPhong().getMaPhong());
                if (phong != null) {
                    phong.setTinhTrang("TRONG");
                    em.merge(phong);
                }
            }

            // 3. Lưu Hóa Đơn
            HoaDon hoaDonEntity = HoaDonMapper.dtoToEntity(hoaDonDTO);
            em.persist(hoaDonEntity);

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Transaction Checkout thất bại: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public List<PhieuDatPhong> findAll() {
        // Sử dụng try-with-resources để tự động đóng EntityManager
        try (EntityManager em = JpaConfig.getEntityManager()) {

            // 👉 CHIÊU CUỐI: JOIN FETCH giúp nạp luôn Khách hàng và Phòng
            // Việc này giúp Mapper không bị get ra null nữa.
            String jpql = "SELECT p FROM PhieuDatPhong p " +
                    "JOIN FETCH p.khachHang " +
                    "JOIN FETCH p.phong";

            return em.createQuery(jpql, PhieuDatPhong.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Trả về danh sách rỗng nếu lỗi
        }
    }

    // 👉 BỔ SUNG HÀM NÀY VÀO LỚP SERVICE ĐỂ HẾT BÁO LỖI ĐỎ
    @Override
    public String phatSinhMaPhieuMoi() {
        // Gọi hàm sinh mã từ tầng Repository lên
        return phieuRepository.phatSinhMaPhieuMoi();
    }

}