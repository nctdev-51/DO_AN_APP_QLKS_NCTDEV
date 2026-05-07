package iuh.fit.core.service.impl;

import iuh.fit.core.dto.PhongDTO;
import iuh.fit.core.entity.Phong;
import iuh.fit.core.repository.IPhongRepository;
import iuh.fit.core.service.IPhongService;
import iuh.fit.infrastructure.mapper.PhongMapper;

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
        // ĐÃ FIX LỖI FONT CHỮ
        if (phongDTO.getMaPhong() == null || phongDTO.getMaPhong().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phòng không được để trống");
        }
        Phong entity = PhongMapper.dtoToEntity(phongDTO);
        Phong saved = phongRepository.save(entity);
        return PhongMapper.entityToDTO(saved);
    }

    @Override
    public PhongDTO updatePhong(PhongDTO phongDTO) throws IllegalArgumentException {
        // ĐÃ FIX LỖI FONT CHỮ
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

    // MAP HÀM MỚI VÀO REPOSITORY
    @Override
    public List<PhongDTO> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, double minPrice, double maxPrice) {
        return phongRepository.findAvailableRooms(checkIn, checkOut, minPrice, maxPrice).stream()
                .map(PhongMapper::entityToDTO)
                .collect(Collectors.toList());
    }
    @Override
    public List<PhongDTO> getPhongByPhieuDat(String maPhieu) {
        // Gọi repo lấy Entity -> dùng Stream API map sang DTO
        return phongRepository.getDanhSachPhongTheoMaPhieu(maPhieu).stream()
                .map(PhongMapper::entityToDTO)
                .collect(Collectors.toList());
    }
}