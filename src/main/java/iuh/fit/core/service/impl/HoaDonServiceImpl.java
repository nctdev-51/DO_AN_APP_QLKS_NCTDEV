package iuh.fit.core.service.impl;

import iuh.fit.core.dto.HoaDonDTO;
import iuh.fit.core.entity.HoaDon;
import iuh.fit.core.repository.IHoaDonRepository;
import iuh.fit.core.service.IHoaDonService;
import iuh.fit.infrastructure.mapper.HoaDonMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class: HoaDonServiceImpl (Service Implementation)
 * 
 * Tầng: CORE - Service Layer Implementation
 * Trách nhiệm: Implement IHoaDonService - xử lý logic nghiệp vụ cho Hóa Đơn
 */
public class HoaDonServiceImpl implements IHoaDonService {
    
    private final IHoaDonRepository hoaDonRepository;
    
    public HoaDonServiceImpl(IHoaDonRepository hoaDonRepository) {
        this.hoaDonRepository = hoaDonRepository;
    }
    
    @Override
    public Optional<HoaDonDTO> findById(String maHoaDon) {
        return hoaDonRepository.findById(maHoaDon)
                .map(HoaDonMapper::entityToDTO);
    }
    
    @Override
    public List<HoaDonDTO> findAll() {
        return hoaDonRepository.findAll().stream()
                .map(HoaDonMapper::entityToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public HoaDonDTO create(HoaDonDTO dto) {
        if (dto.getMaHoaDon() == null || dto.getMaHoaDon().isEmpty()) {
            throw new IllegalArgumentException("Mã hóa đơn không được để trống");
        }
        if (dto.getTongTien() < 0) {
            throw new IllegalArgumentException("Tổng tiền không được âm");
        }
        HoaDon entity = HoaDonMapper.dtoToEntity(dto);
        HoaDon saved = hoaDonRepository.save(entity);
        return HoaDonMapper.entityToDTO(saved);
    }
    
    @Override
    public HoaDonDTO update(HoaDonDTO dto) {
        if (dto.getMaHoaDon() == null || dto.getMaHoaDon().isEmpty()) {
            throw new IllegalArgumentException("Mã hóa đơn không được để trống");
        }
        if (dto.getTongTien() < 0) {
            throw new IllegalArgumentException("Tổng tiền không được âm");
        }
        HoaDon entity = HoaDonMapper.dtoToEntity(dto);
        HoaDon updated = hoaDonRepository.update(entity);
        return HoaDonMapper.entityToDTO(updated);
    }
    
    @Override
    public void delete(String maHoaDon) {
        hoaDonRepository.deleteById(maHoaDon);
    }
    
    @Override
    public List<HoaDonDTO> findByMaKhachHang(String maKhachHang) {
        return hoaDonRepository.findByMaKhachHang(maKhachHang).stream()
                .map(HoaDonMapper::entityToDTO)
                .collect(Collectors.toList());
    }
}

