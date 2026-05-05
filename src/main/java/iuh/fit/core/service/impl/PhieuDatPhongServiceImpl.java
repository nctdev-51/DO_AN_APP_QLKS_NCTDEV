package iuh.fit.core.service.impl;

import iuh.fit.core.dto.PhieuDatPhongDTO;
import iuh.fit.core.entity.PhieuDatPhong;
import iuh.fit.core.repository.IPhieuDatPhongRepository;
import iuh.fit.core.service.IPhieuDatPhongService;
import iuh.fit.infrastructure.mapper.PhieuDatPhongMapper;

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

    @Override
    public PhieuDatPhongDTO addPhieuDatPhong(PhieuDatPhongDTO phieuDTO) throws IllegalArgumentException {
        // ĐÃ FIX LỖI FONT CHỮ
        if (phieuDTO.getMaPhieu() == null || phieuDTO.getMaPhieu().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu không được để trống");
        }
        PhieuDatPhong entity = PhieuDatPhongMapper.dtoToEntity(phieuDTO);
        PhieuDatPhong saved = phieuRepository.save(entity);
        return PhieuDatPhongMapper.entityToDTO(saved);
    }

    @Override
    public PhieuDatPhongDTO updatePhieuDatPhong(PhieuDatPhongDTO phieuDTO) throws IllegalArgumentException {
        // ĐÃ FIX LỖI FONT CHỮ
        if (phieuDTO.getMaPhieu() == null || phieuDTO.getMaPhieu().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiếu không được để trống");
        }
        PhieuDatPhong entity = PhieuDatPhongMapper.dtoToEntity(phieuDTO);
        PhieuDatPhong updated = phieuRepository.update(entity);
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

    // MAP HÀM MỚI VÀO REPOSITORY
    @Override
    public boolean bookRoomTransaction(PhieuDatPhongDTO phieuDTO) {
        PhieuDatPhong entity = PhieuDatPhongMapper.dtoToEntity(phieuDTO);
        return phieuRepository.saveBookingTransaction(entity);
    }
}