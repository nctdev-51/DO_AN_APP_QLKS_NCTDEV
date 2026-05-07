package iuh.fit.core.service.impl;

import iuh.fit.core.dto.KhuyenMaiDTO;
import iuh.fit.core.entity.KhuyenMai;
import iuh.fit.core.repository.IKhuyenMaiRepository;
import iuh.fit.core.service.IKhuyenMaiService;
import iuh.fit.infrastructure.mapper.KhuyenMaiMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class: KhuyenMaiServiceImpl (Service Implementation)
 * 
 * Tầng: CORE - Service Layer Implementation
 * Trách nhiệm: Implement IKhuyenMaiService - xử lý logic nghiệp vụ cho Khuyến Mại
 */
public class KhuyenMaiServiceImpl implements IKhuyenMaiService {
    
    private final IKhuyenMaiRepository khuyenMaiRepository;
    
    public KhuyenMaiServiceImpl(IKhuyenMaiRepository khuyenMaiRepository) {
        this.khuyenMaiRepository = khuyenMaiRepository;
    }
    
    @Override
    public Optional<KhuyenMaiDTO> findById(String maKhuyenMai) {
        return khuyenMaiRepository.findById(maKhuyenMai)
                .map(KhuyenMaiMapper::entityToDTO);
    }
    
    @Override
    public List<KhuyenMaiDTO> findAll() {
        return khuyenMaiRepository.findAll().stream()
                .map(KhuyenMaiMapper::entityToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public KhuyenMaiDTO create(KhuyenMaiDTO dto) {
        if (dto.getMaKhuyenMai() == null || dto.getMaKhuyenMai().isEmpty()) {
            throw new IllegalArgumentException("Mã khuyến mại không được để trống");
        }
        if (dto.getNgayKetThuc().isBefore(dto.getNgayBatDau())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
        }
        KhuyenMai entity = KhuyenMaiMapper.dtoToEntity(dto);
        KhuyenMai saved = khuyenMaiRepository.save(entity);
        return KhuyenMaiMapper.entityToDTO(saved);
    }
    
    @Override
    public KhuyenMaiDTO update(KhuyenMaiDTO dto) {
        if (dto.getMaKhuyenMai() == null || dto.getMaKhuyenMai().isEmpty()) {
            throw new IllegalArgumentException("Mã khuyến mại không được để trống");
        }
        if (dto.getNgayKetThuc().isBefore(dto.getNgayBatDau())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
        }
        KhuyenMai entity = KhuyenMaiMapper.dtoToEntity(dto);
        KhuyenMai updated = khuyenMaiRepository.update(entity);
        return KhuyenMaiMapper.entityToDTO(updated);
    }
    
    @Override
    public void delete(String maKhuyenMai) {
        khuyenMaiRepository.deleteById(maKhuyenMai);
    }
}

