package iuh.fit.core.service.impl;

import iuh.fit.core.dto.LoaiPhongDTO;
import iuh.fit.core.entity.LoaiPhong;
import iuh.fit.core.repository.ILoaiPhongRepository;
import iuh.fit.core.service.ILoaiPhongService;
import iuh.fit.infrastructure.mapper.LoaiPhongMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class: LoaiPhongServiceImpl (Service Implementation)
 * 
 * Tầng: CORE - Service Layer Implementation
 * Trách nhiệm: Implement ILoaiPhongService - xử lý logic nghiệp vụ cho Loại Phòng
 */
public class LoaiPhongServiceImpl implements ILoaiPhongService {
    
    private final ILoaiPhongRepository loaiPhongRepository;
    
    public LoaiPhongServiceImpl(ILoaiPhongRepository loaiPhongRepository) {
        this.loaiPhongRepository = loaiPhongRepository;
    }
    
    @Override
    public Optional<LoaiPhongDTO> findById(String maLoaiPhong) {
        return loaiPhongRepository.findById(maLoaiPhong)
                .map(LoaiPhongMapper::entityToDTO);
    }
    
    @Override
    public List<LoaiPhongDTO> findAll() {
        return loaiPhongRepository.findAll().stream()
                .map(LoaiPhongMapper::entityToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public LoaiPhongDTO create(LoaiPhongDTO dto) {
        if (dto.getMaLoaiPhong() == null || dto.getMaLoaiPhong().isEmpty()) {
            throw new IllegalArgumentException("Mã loại phòng không được để trống");
        }
        LoaiPhong entity = LoaiPhongMapper.dtoToEntity(dto);
        LoaiPhong saved = loaiPhongRepository.save(entity);
        return LoaiPhongMapper.entityToDTO(saved);
    }
    
    @Override
    public LoaiPhongDTO update(LoaiPhongDTO dto) {
        if (dto.getMaLoaiPhong() == null || dto.getMaLoaiPhong().isEmpty()) {
            throw new IllegalArgumentException("Mã loại phòng không được để trống");
        }
        LoaiPhong entity = LoaiPhongMapper.dtoToEntity(dto);
        LoaiPhong updated = loaiPhongRepository.update(entity);
        return LoaiPhongMapper.entityToDTO(updated);
    }
    
    @Override
    public void delete(String maLoaiPhong) {
        loaiPhongRepository.deleteById(maLoaiPhong);
    }
}

