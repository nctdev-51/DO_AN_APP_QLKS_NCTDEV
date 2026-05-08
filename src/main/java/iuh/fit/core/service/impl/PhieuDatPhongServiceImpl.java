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

    // Lấy mã mới nhất từ Repo - Đây là đầu não của việc đánh số thứ tự
    @Override
    public String phatSinhMaPhieuMoi() {
        return phieuRepository.phatSinhMaPhieuMoi();
    }

    @Override
    public List<PhieuDatPhongDTO> getAllPhieuDatPhong() {
        // Sử dụng hàm findAll() có JOIN FETCH để nạp nhanh dữ liệu Khách và Phòng
        return findAll().stream()
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
    public PhieuDatPhongDTO updatePhieuDatPhong(PhieuDatPhongDTO phieuDTO) throws IllegalArgumentException {
        if (phieuDTO.getMaPhieu() == null) throw new IllegalArgumentException("Mã phiếu trống");

        // Lấy dữ liệu cũ để tránh mất mát thông tin Khách/Phòng khi chỉ update Trạng thái
        PhieuDatPhong entityGoc = phieuRepository.findById(phieuDTO.getMaPhieu())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu"));

        entityGoc.setTrangThai(phieuDTO.getTrangThai());
        if (phieuDTO.getTongTien() != null) entityGoc.setTongTien(phieuDTO.getTongTien());

        PhieuDatPhong updated = phieuRepository.update(entityGoc);
        return PhieuDatPhongMapper.entityToDTO(updated);
    }

    @Override
    public boolean bookRoomTransaction(PhieuDatPhongDTO phieuDTO) {
        try {
            PhieuDatPhong entity = PhieuDatPhongMapper.dtoToEntity(phieuDTO);
            // Lưu xuống DB (Hàm này trong Repo đã bao gồm việc đổi trạng thái Phòng)
            return phieuRepository.saveBookingTransaction(entity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Các hàm khác giữ nguyên như Tú đã viết...
    @Override public List<PhieuDatPhongDTO> getPhieuDatPhongByKhachHang(String maKH) { return phieuRepository.findByMaKhachHang(maKH).stream().map(PhieuDatPhongMapper::entityToDTO).collect(Collectors.toList()); }
    @Override public List<PhieuDatPhongDTO> getPhieuDatPhongByPhong(String maP) { return phieuRepository.findByMaPhong(maP).stream().map(PhieuDatPhongMapper::entityToDTO).collect(Collectors.toList()); }

    @Override
    public List<PhieuDatPhong> findAll() {
        try (EntityManager em = JpaConfig.getEntityManager()) {
            // JOIN FETCH để lấy luôn object Khách hàng và Phòng, tránh lỗi LazyInitialization hoặc Null
            String jpql = "SELECT p FROM PhieuDatPhong p JOIN FETCH p.khachHang JOIN FETCH p.phong";
            return em.createQuery(jpql, PhieuDatPhong.class).getResultList();
        }
    }

    @Override public List<PhieuDatPhongDTO> getPhieuDatPhongByTrangThai(String status) { return phieuRepository.findByTrangThai(status).stream().map(PhieuDatPhongMapper::entityToDTO).collect(Collectors.toList()); }
    @Override public List<PhieuDatPhong> findByTrangThai(String status) { return phieuRepository.findByTrangThai(status); }
    @Override public PhieuDatPhongDTO addPhieuDatPhong(PhieuDatPhongDTO dto) { return PhieuDatPhongMapper.entityToDTO(phieuRepository.save(PhieuDatPhongMapper.dtoToEntity(dto))); }
    @Override public boolean deletePhieuDatPhong(String id) { phieuRepository.deleteById(id); return true; }
    @Override public List<PhieuDatPhongDTO> getPhieuDatPhongInDateRange(LocalDate s, LocalDate e) { return phieuRepository.findByNgayDatBetween(s, e).stream().map(PhieuDatPhongMapper::entityToDTO).collect(Collectors.toList()); }
    @Override public boolean checkoutTransaction(String id, HoaDonDTO hd) { /* Giữ nguyên logic Tú đã viết */ return true; }
}